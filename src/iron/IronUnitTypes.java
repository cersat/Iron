package iron;

import mindustry.ai.types.BuilderAI;
import mindustry.gen.UnitEntity;
import mindustry.type.UnitType;

/** Юниты планеты Айрон. */
public class IronUnitTypes {
    public static UnitType ironStone;

    public static void load() {
        ironStone = new UnitType("iron-stone") {{
            constructor = UnitEntity::create;
            controller = u -> new BuilderAI();

            flying = true;
            drag = 0.08f;
            speed = 3f;
            rotateSpeed = 15f;
            accel = 0.1f;

            health = 150f;
            armor = 1f;
            hitSize = 8f;

            itemCapacity = 30;
            mineSpeed = 1f;
            mineTier = 1;
            buildSpeed = 0.5f;

            engineOffset = 5f;
            engineSize = 2f;

            alwaysUnlocked = true;
        }};
    }
}
