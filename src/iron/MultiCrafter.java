package iron;

import arc.struct.ObjectSet;
import arc.util.Strings;
import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.consumers.ConsumePowerDynamic;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

/**
 * Крафтер с несколькими рецептами и автоматическим выбором по входным предметам.
 *
 * Блок берёт первый рецепт, все входные предметы которого лежат в нём,
 * и держится за него, пока хватает сырья. Всё остальное поведение —
 * прогресс, warmup, эффекты, выгрузка, жидкости на выходе, drawer —
 * достаётся от GenericCrafter без изменений.
 */
public class MultiCrafter extends GenericCrafter {

    /** Один рецепт: что съесть, что выдать и сколько энергии тратить за тик. */
    public static class Recipe {
        public ItemStack[] input;
        public ItemStack[] output;
        public float power;

        public Recipe(ItemStack[] input, ItemStack[] output, float power) {
            this.input = input;
            this.output = output;
            this.power = power;
        }

        /** Без указания энергии — рецепт бесплатный. */
        public Recipe(ItemStack[] input, ItemStack[] output) {
            this(input, output, 0f);
        }
    }

    public Seq<Recipe> recipes = new Seq<>();

    /** Все предметы, которые блок вообще согласен принимать. */
    protected ObjectSet<Item> acceptedItems = new ObjectSet<>();

    public MultiCrafter(String name) {
        super(name);
        hasItems = true;
    }

    /** Короткая запись рецепта. */
    public void recipe(ItemStack[] input, ItemStack[] output) {
        recipes.add(new Recipe(input, output));
    }

    public void recipe(ItemStack[] input, ItemStack[] output, float power) {
        recipes.add(new Recipe(input, output, power));
    }

    @Override
    public void init() {
        // какие предметы принимаем на вход
        for (Recipe r : recipes) {
            if (r.input != null) {
                for (ItemStack st : r.input) acceptedItems.add(st.item);
            }
        }

        // объединение всех выходов: нужно, чтобы работали выгрузка,
        // outputsItems() и иконки — их GenericCrafter берёт из outputItems
        Seq<ItemStack> union = new Seq<>();
        for (Recipe r : recipes) {
            if (r.output == null) continue;
            for (ItemStack st : r.output) {
                ItemStack found = union.find(u -> u.item == st.item);
                if (found == null) {
                    union.add(new ItemStack(st.item, st.amount));
                } else if (st.amount > found.amount) {
                    found.amount = st.amount;
                }
            }
        }
        if (union.size > 0) outputItems = union.toArray(ItemStack.class);

        // потребление сырья — динамическое, зависит от выбранного рецепта
        consume(new ConsumeItemDynamic((MultiCrafterBuild b) ->
                b.recipe() != null && b.recipe().input != null
                        ? b.recipe().input
                        : ItemStack.empty));

        // энергия подключается, только если её просит хоть один рецепт
        if (recipes.contains(r -> r.power > 0f)) {
            hasPower = true;
            // ConsumePowerDynamic принимает Floatf<Building>, без обобщения,
            // поэтому тип приводим внутри лямбды вручную
            consume(new ConsumePowerDynamic(b -> {
                Recipe r = ((MultiCrafterBuild) b).recipe();
                return r != null ? r.power : 0f;
            }));
        }

        super.init();
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(Stat.output, table -> {
            table.row();
            for (Recipe r : recipes) {
                table.table(t -> {
                    if (r.input != null) StatValues.items(r.input).display(t);
                    t.add(" -> ").pad(8f);
                    if (r.output != null) StatValues.items(r.output).display(t);
                    if (r.power > 0f) {
                        t.add(" (" + Strings.autoFixed(r.power * 60f, 2) + " "
                                + StatUnit.powerSecond.localized() + ")").padLeft(8f);
                    }
                }).left().row();
            }
        });
    }

    public class MultiCrafterBuild extends GenericCrafterBuild {
        /** Рецепт, выбранный сейчас. Пересчитывается, только когда сырья не хватает. */
        protected Recipe current;

        /** Хватает ли сырья на рецепт целиком. */
        public boolean hasInputs(Recipe r) {
            if (r == null) return false;
            if (r.input == null) return true;
            for (ItemStack st : r.input) {
                if (items.get(st.item) < st.amount) return false;
            }
            return true;
        }

        /** Есть ли место под результат рецепта. */
        public boolean hasRoom(Recipe r) {
            if (r == null || r.output == null) return true;
            for (ItemStack st : r.output) {
                if (items.get(st.item) + st.amount > itemCapacity) return false;
            }
            return true;
        }

        public boolean hasEnergy(Recipe r) {
            if (r.power == 0) return true;
            if (power == null) return true;
            if (power.status < 0.9) return false;
            return true;
        }

        /**
         * Автовыбор рецепта. Пока текущий обеспечен сырьём — держимся за него,
         * чтобы блок не прыгал между рецептами посреди крафта.
         */
        public Recipe recipe() {
            if (hasInputs(current) && hasEnergy(current)) return current;
            for (Recipe r : recipes) {
                if (hasInputs(r) && hasEnergy(r)) return current = r;
            }
            return current = null;
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return acceptedItems.contains(item) && items.get(item) < getMaximumAccepted(item);
        }

        @Override
        public boolean shouldConsume() {
            Recipe r = recipe();
            return r != null && hasRoom(r) && super.shouldConsume();
        }

        @Override
        public void craft() {
            Recipe r = recipe();

            consume();

            if (r != null && r.output != null) {
                for (ItemStack st : r.output) {
                    for (int i = 0; i < st.amount; i++) offload(st.item);
                }
            }

            if (wasVisible) craftEffect.at(x, y);
            progress %= 1f;
        }
    }
}
