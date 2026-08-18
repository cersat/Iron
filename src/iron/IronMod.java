package iron;

import arc.util.Log;
import mindustry.content.TechTree;
import mindustry.content.UnitTypes;
import mindustry.mod.Mod;

import static mindustry.type.ItemStack.with;

/**
 * Мод "Планета Iron" — Java-версия.
 *
 * Порядок загрузки важен: предметы и жидкости должны существовать до того,
 * как на них сошлются блоки, блоки — до планеты и техдрева.
 *
 * ------------------------------------------------------------------------
 * НАСТРОЙКИ ИЗ HJSON, КОТОРЫЕ НИКОГДА НЕ РАБОТАЛИ
 * ------------------------------------------------------------------------
 * ContentParser в Mindustry молча пропускает поля, которых нет у класса
 * (ignoreUnknownFields = true). В hjson-версии из-за этого тихо терялись:
 *
 *  1. hardness у iron (2), quartz (2.5) и huge-quartz (3.5) — в файлах
 *     пропущен перенос строки после "type: material", и hjson читал значение
 *     как часть строки. Все три предмета имеют твёрдость 0, то есть их
 *     добывает даже дрон с mineTier 1.
 *
 *  2. generator: { powerProduction: 5 } у uranium-reactor — у GenericCrafter
 *     нет поля generator. ИСПРАВЛЕНО: см. класс IronReactor, реактор теперь
 *     выдаёт и обработанный уран, и 5 энергии за тик.
 *
 *  3. startingItems: [iron/500] у планеты — у класса Planet нет такого поля.
 *     Стартовые ресурсы задаются лоадаутом ядра или пресетом сектора.
 *
 *  4. amount у целей Produce в техдреве — у Objectives.Produce нет поля
 *     amount, засчитывается сам факт производства ресурса.
 *
 *  5. updateBuffer у солараторов и shadowAlpha у iron-wall — таких полей
 *     у Block нет.
 *
 *  6. shootSound: artillery у урановой пушки — звук переименован в
 *     Sounds.shootArtillery. ИСПРАВЛЕНО, звук вернулся.
 *
 *  7. Ключи полов в бандле начинались с floor., а игра ищет block.
 *     ИСПРАВЛЕНО в assets/bundles — полы теперь переводятся.
 *
 * Пункты 1, 3, 4, 5 воспроизведены как есть — поведение не меняется.
 * Пункты 2, 6, 7 исправлены по просьбе автора мода.
 * ------------------------------------------------------------------------
 */
public class IronMod extends Mod {

    public IronMod() {
        Log.info("[Iron] конструктор мода загружен");
    }

    @Override
    public void loadContent() {
        Log.info("[Iron] загрузка контента");

        IronItems.load();
        IronLiquids.load();
        IronStatusEffects.load();
        IronUnitTypes.load();
        IronBlocks.load();
        IronPlanets.load();
        IronSectorPresets.load();
        IronTechTree.load();

        // Дрон Айрона в hjson висел узлом под mono в дереве Серпуло
        // (research: { parent: mono, alwaysUnlocked: true }).
        TechTree.TechNode mono = TechTree.all.find(n -> n.content == UnitTypes.mono);
        if (mono != null) {
            TechTree.TechNode node = new TechTree.TechNode(mono, IronUnitTypes.ironStone,
                    with(IronItems.iron, 1));
            node.objectives.clear();
        }

        // Планета в hjson имела research с требованиями copper/5000 + lead/5000.
        TechTree.TechNode serpuloRoot = TechTree.roots.find(n -> n.planet == mindustry.content.Planets.serpulo);
        if (serpuloRoot != null) {
            new TechTree.TechNode(serpuloRoot, IronPlanets.iron,
                    with(mindustry.content.Items.copper, 5000, mindustry.content.Items.lead, 5000));
        }

        Log.info("[Iron] контент загружен");
    }
}
