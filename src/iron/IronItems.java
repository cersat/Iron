package iron;

import arc.graphics.Color;
import mindustry.type.Item;

/**
 * Предметы планеты Айрон.
 *
 * ВНИМАНИЕ: у iron, quartz и huge-quartz поле hardness намеренно НЕ задано.
 * В hjson-версии строки "type: material" и "hardness: N" были склеены в одну
 * (пропущен перенос строки), из-за чего hjson читал значение как часть строки
 * "materialhardness: 2", а поле hardness оставалось нулевым.
 * Здесь это поведение сохранено один в один. См. комментарий в IronMod.
 */
public class IronItems {
    public static Item iron, uranus, depletedUranus, hugeQuartz, quartz, ironPlate, uranusPlate;

    public static void load() {
        iron = new Item("iron", Color.valueOf("a9a9a9")) {{
            cost = 1.5f;
            hardness = 2;
        }};

        uranus = new Item("uranus", Color.valueOf("33b81e")) {{
            radioactivity = 3f;
            hardness = 1;
            cost = 1.5f;
        }};

        depletedUranus = new Item("depleted-uranus", Color.valueOf("a9a9a9")) {{
            radioactivity = 0.7f;
            hardness = 1;
            cost = 1.5f;
        }};

        hugeQuartz = new Item("huge-quartz", Color.valueOf("a9a9a9")) {{
            cost = 1.5f;
            hardness = 3;
        }};

        quartz = new Item("quartz", Color.valueOf("a9a9a9")) {{
            cost = 0.5f;
            hardness = 2;
        }};

        ironPlate = new Item("iron-plate", Color.valueOf("a9a9a9")) {{
            cost = 1.5f;
            hardness = 2;
        }};

        uranusPlate = new Item("uranus-plate", Color.valueOf("a9a9a9")) {{
            cost = 1.5f;
            hardness = 2;
        }};
    }
}
