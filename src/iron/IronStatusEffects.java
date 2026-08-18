package iron;

import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.type.StatusEffect;

/**
 * Статус-эффекты мода.
 *
 * localizedName и description задаются явно: в hjson-версии эти поля
 * прописывались прямо в файле контента и перезаписывали значения из бандла
 * (ContentParser выставляет их уже после конструктора). Бандл для статусов
 * записей не содержит, поэтому текст берётся отсюда.
 */
public class IronStatusEffects {
    public static StatusEffect radioactivity;

    public static void load() {
        radioactivity = new StatusEffect("radioactivity") {{
            color = Color.valueOf("84F442");
            damage = 0.2f;
            speedMultiplier = 0.85f;
            healthMultiplier = 0.75f;
            effect = Fx.wet;
            effectChance = 0.05f;

            localizedName = "Радиоактивность";
            description = "Наносит небольшой урон и снижает максимальный уровегнь здоровья. Накладывается урановой пушкой";
        }};
    }
}
