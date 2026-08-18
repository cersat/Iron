package iron;

import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

/**
 * Крафтер, который вдобавок вырабатывает энергию.
 *
 * Штатный GenericCrafter энергию не производит, а ConsumeGenerator умеет
 * выдавать только жидкость — предмет на выходе он не поддерживает.
 * Поэтому нужен свой класс: берём GenericCrafter (уран -> обработанный уран)
 * и переопределяем у постройки getPowerProduction().
 *
 * Именно это и подразумевалось блоком generator: { powerProduction: 5 }
 * в старом hjson, который парсер молча игнорировал.
 */
public class IronReactor extends GenericCrafter {
    /** Выработка энергии за тик. Умножается на 60 для показа в статистике. */
    public float powerProduction = 0f;

    public IronReactor(String name) {
        super(name);
        hasPower = true;
        outputsPower = true;
        consumesPower = false;
        buildType = IronReactorBuild::new;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.basePowerGeneration, powerProduction * 60f, StatUnit.powerSecond);
    }

    public class IronReactorBuild extends GenericCrafterBuild {
        @Override
        public float getPowerProduction() {
            // efficiency — доля от полной мощности: пока в блоке нет урана,
            // она равна нулю, и реактор ничего не даёт в сеть.
            return powerProduction * efficiency;
        }
    }
}
