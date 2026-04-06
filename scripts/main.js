// Создаем новый тип блока на базе GenericCrafter
const UniversalCrafter = extend(GenericCrafter, "UniversalCrafter", {
    // Этот метод позволяет блоку принимать ЛЮБОЙ предмет из списка рецептов
    acceptItem(item, source) {
        return this.block.plans.some(p => p.input.item == item) && this.items.get(item) < this.block.itemCapacity;
    },

    updateTile() {
        // Ищем рецепт под предмет, который сейчас внутри
        const plan = this.block.plans.find(p => this.items.has(p.input.item));

        if (plan) {
            this.setupRecipe(plan); // Подменяем потребление и выход на лету
            this.super$updateTile();
        }
    },

    setupRecipe(plan) {
        this.outputItem = plan.output;
        // Тут можно добавить кастомную скорость из плана
    }
});

// Регистрируем, чтобы HJSON его видел
UniversalCrafter.plans = []; 