package iron;

import mindustry.Vars;
import mindustry.mod.Mods;
import mindustry.type.SectorPreset;

public class IronSectorPresets {
    public static SectorPreset sector1, sector2;

    public static void load() {
        Mods.LoadedMod mod = Vars.mods.getMod(IronMod.class);

        sector1 = new SectorPreset("1", mod);
        sector1.initialize(IronPlanets.iron, 0);
        sector1.alwaysUnlocked = true;
        sector1.difficulty = 1f;

        sector2 = new SectorPreset("2", mod);
        sector2.initialize(IronPlanets.iron, 1);
        sector2.difficulty = 1f;
    }
}
