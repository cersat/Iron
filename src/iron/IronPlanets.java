package iron;

import arc.graphics.Color;
import mindustry.content.Planets;
import mindustry.graphics.g3d.NoiseMesh;
import mindustry.maps.planet.SerpuloPlanetGenerator;
import mindustry.type.Planet;


public class IronPlanets {
    public static Planet iron;

    public static void load() {
        iron = new Planet("iron", Planets.sun, 1f, 1) {{
            generator = new SerpuloPlanetGenerator();

            meshLoader = () -> new NoiseMesh(
                this,
                69,                         // seed
                2,                          // divisions
                1f,                         // radius
                3,                          // octaves
                0.5f,                       // persistence
                1f,                         // scale
                0.5f,                       // mag
                Color.valueOf("5a3d1f"),    // color1
                Color.valueOf("2c1810"),    // color2
                1,                          // colorOct     (по умолчанию)
                0.5f,                       // colorPersistence (по умолчанию)
                1f,                         // colorScale   (по умолчанию)
                0.5f                        // colorThreshold (по умолчанию)
            );

            accessible = true;
            startSector = 0;
            alwaysUnlocked = true;
            sectorSeed = 42;
            allowLaunchToNumbered = false;
            orbitRadius = 30f;
        }};
    }
}
