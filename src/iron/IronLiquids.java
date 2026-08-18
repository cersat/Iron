package iron;

import arc.graphics.Color;
import mindustry.type.Liquid;

/** Жидкости планеты Айрон. */
public class IronLiquids {
    public static Liquid liquidIron;

    public static void load() {
        liquidIron = new Liquid("liquid-iron", Color.valueOf("ff7a38")) {{
            temperature = 0.9f;
            viscosity = 0.7f;
            heatCapacity = 0.8f;
        }};
    }
}
