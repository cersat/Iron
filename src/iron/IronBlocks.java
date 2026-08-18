package iron;

import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.ArtilleryBulletType;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.distribution.MassDriver;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.StaticWall;
import mindustry.world.blocks.power.SolarGenerator;
import mindustry.world.blocks.production.AttributeCrafter;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.Pump;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.Attribute;

import static mindustry.type.ItemStack.with;

/** Все блоки планеты Айрон. */
public class IronBlocks {
    /** Кастомный атрибут поверхности, используется конденсатором и урановым газом. */
    public static Attribute uraniumPresence;
    // окружение
    public static Block ironPlanetFloor, ironPlanetLavaFloor, liron, uraniumGasFloor, ironWall;
    // производство
    public static Block ironPump, ironSmelter, crasher, uranusCollector, ironPress;
    // энергия
    public static Block uraniumReactor, solarator, hugeSolarator;
    // логистика
    public static Block ironManipulator, fastManipulator;
    // оборона
    public static Block uranusWall, uranusWallLarge, bulletThrower, uranusCannon;
    // ядро
    public static Block shardCore;

    public static void load() {
        uraniumPresence = Attribute.add("uranium-presence");

        // ------------------------------------------------------------------
        // ОКРУЖЕНИЕ
        // ------------------------------------------------------------------

        ironPlanetFloor = new Floor("iron-planet-floor") {{
            variants = 2;
            isLiquid = false;
            speedMultiplier = 1f;
            albedo = 0f;
        }};

        ironPlanetLavaFloor = new Floor("iron-planet-lava-floor") {{
            variants = 0;
            isLiquid = false;
            speedMultiplier = 0.7f;
            cacheLayer = mindustry.graphics.CacheLayer.water;
            albedo = 0.1f;
            damageTaken = 0.3f;
        }};

        liron = new Floor("liron") {{
            liquidDrop = IronLiquids.liquidIron;
            isLiquid = true;
            speedMultiplier = 0.5f;
            variants = 2;
            cacheLayer = mindustry.graphics.CacheLayer.water;
        }};

        uraniumGasFloor = new Floor("uranium-gas-floor") {{
            attributes.set(uraniumPresence, 1f);
            emitLight = true;
            lightRadius = 40f;
            variants = 0;
        }};

        ironWall = new StaticWall("iron-wall") {{
            variants = 2;
            size = 1;
            solid = true;
            alwaysReplace = false;
            category = Category.effect;
        }};

        // ------------------------------------------------------------------
        // ЯДРО
        // ------------------------------------------------------------------

        shardCore = new CoreBlock("shard-core") {{
            requirements(Category.effect, with(IronItems.iron, 500));
            size = 3;
            health = 2000;
            itemCapacity = 1500;
            unitType = IronUnitTypes.ironStone;
            acceptsItems = true;
        }};

        // ------------------------------------------------------------------
        // ПРОИЗВОДСТВО
        // ------------------------------------------------------------------

        ironPump = new Pump("iron-pump") {{
            requirements(Category.liquid, with(IronItems.iron, 35));
            health = 150;
            size = 2;
            pumpAmount = 0.05f;
            liquidCapacity = 30f;
        }};

        ironSmelter = new GenericCrafter("iron-smelter") {{
            requirements(Category.crafting, with(IronItems.iron, 60));
            size = 2;
            health = 200;
            craftTime = 60f;
            outputItem = new ItemStack(IronItems.iron, 1);
            consumeLiquid(IronLiquids.liquidIron, 0.1f);
        }};

        crasher = new GenericCrafter("crasher") {{
            requirements(Category.crafting, with(IronItems.iron, 100, IronItems.uranus, 50));
            size = 2;
            hasItems = true;
            itemCapacity = 20;
            craftTime = 60f;
            consumeItem(IronItems.hugeQuartz, 1);
            outputItem = new ItemStack(IronItems.quartz, 4);
        }};

        uranusCollector = new AttributeCrafter("uranus-collector") {{
            requirements(Category.production, with(IronItems.iron, 60));
            size = 3;
            hasItems = true;
            hasPower = true;
            itemCapacity = 20;
            health = 250;
            craftTime = 120f;

            attribute = uraniumPresence;
            baseEfficiency = 0f;
            boostScale = 1f;

            consumePower(0.3f);
            outputItem = new ItemStack(IronItems.uranus, 1);
        }};

        ironPress = new MultiCrafter("iron-press") {{
            requirements(Category.production, with(IronItems.iron, 45, IronItems.uranus, 20, IronItems.depletedUranus, 5));
            health = 150;
            size = 2;
            itemCapacity = 20;
            craftTime = 60f;
            craftEffect = Fx.pulverizeMedium;

            recipe(with(IronItems.iron, 1), with(IronItems.ironPlate, 1));
            recipe(with(IronItems.uranus, 1), with(IronItems.uranusPlate, 1));
        }};

        // ------------------------------------------------------------------
        // ЭНЕРГИЯ
        // ------------------------------------------------------------------

        // Реактор перерабатывает уран в отходы И вырабатывает энергию.
        // В hjson блок generator: { powerProduction: 5 } не работал: у
        // GenericCrafter такого поля нет, а парсер молча пропускает неизвестные
        // поля. Здесь используется свой класс IronReactor, который добавляет
        // выработку поверх обычного крафта.
        uraniumReactor = new IronReactor("uranium-reactor") {{
            requirements(Category.power, with(IronItems.iron, 60, IronItems.uranus, 10));
            size = 2;
            hasItems = true;
            itemCapacity = 10;
            craftTime = 60f;
            powerProduction = 0.2f;
            consumeItem(IronItems.uranus, 1);
            outputItem = new ItemStack(IronItems.depletedUranus, 1);
        }};

        solarator = new SolarGenerator("solarator") {{
            requirements(Category.power, with(IronItems.iron, 25, IronItems.uranus, 5));
            size = 1;
            hasPower = true;
            powerProduction = 0.05f;
        }};

        hugeSolarator = new SolarGenerator("huge-solarator") {{
            requirements(Category.power, with(IronItems.iron, 40, IronItems.uranus, 10, IronItems.quartz, 5));
            size = 2;
            hasPower = true;
            powerProduction = 0.3f;
        }};

        // ------------------------------------------------------------------
        // ЛОГИСТИКА — манипуляторы
        // ------------------------------------------------------------------

        ironManipulator = new MassDriver("iron-manipulator") {{
            requirements(Category.distribution, with(IronItems.iron, 10));
            size = 1;
            range = 24f;
            rotateSpeed = 6f;
            translation = 0f;
            minDistribute = 1;
            knockback = 0f;
            reload = 30f;
            itemCapacity = 1;
            hasPower = false;
        }};

        fastManipulator = new MassDriver("fast-manipulator") {{
            requirements(Category.distribution, with(IronItems.iron, 10, IronItems.uranus, 5));
            size = 1;
            range = 28f;
            rotateSpeed = 9f;
            translation = 0f;
            minDistribute = 1;
            knockback = 0f;
            reload = 20f;
            itemCapacity = 2;
            hasPower = false;
        }};

        // ------------------------------------------------------------------
        // ОБОРОНА
        // ------------------------------------------------------------------

        uranusWall = new Wall("uranus-wall") {{
            requirements(Category.defense, with(IronItems.uranus, 5, IronItems.depletedUranus, 1));
            health = 225;
            size = 1;
        }};

        uranusWallLarge = new Wall("uranus-wall-large") {{
            requirements(Category.defense, with(IronItems.uranus, 20, IronItems.depletedUranus, 4));
            health = 900;
            size = 2;
        }};

        bulletThrower = new ItemTurret("bullet-thrower") {{
            requirements(Category.turret, with(IronItems.iron, 80));
            health = 635;
            size = 2;
            reload = 15f;
            range = 90f;
            rotateSpeed = 4f;

            ammo(
                IronItems.iron, new BasicBulletType(4f, 6f) {{
                    ammoMultiplier = 5;
                    lifetime = 70f;
                    sprite = "bullet";
                    hitEffect = Fx.none;
                    shootEffect = Fx.hitBulletSmall;
                    shootSound = Sounds.shoot;
                    status = StatusEffects.none;
                    statusDuration = 0f;
                }},
                IronItems.quartz, new BasicBulletType(5f, 8f) {{
                    ammoMultiplier = 3;
                    lifetime = 70f;
                    sprite = "bullet";
                    hitEffect = Fx.none;
                    shootEffect = Fx.hitBulletSmall;
                    shootSound = Sounds.shoot;
                    status = StatusEffects.none;
                    statusDuration = 0f;
                }}
            );
        }};

        uranusCannon = new ItemTurret("uranus-cannon") {{
            requirements(Category.turret, with(IronItems.uranus, 10, IronItems.iron, 120));
            health = 835;
            size = 3;
            reload = 90f;
            range = 150f;
            rotateSpeed = 2.5f;

            ammo(
                IronItems.uranus, new ArtilleryBulletType(3f, 50f) {{
                    splashDamage = 40f;
                    splashDamageRadius = 20f;
                    lifetime = 90f;
                    sprite = "bullet";
                    hitEffect = Fx.flakExplosion;
                    shootEffect = Fx.shootBig;
                    shootSound = Sounds.shootArtillery;
                    status = IronStatusEffects.radioactivity;
                    statusDuration = 180f;
                }},
                IronItems.depletedUranus, new ArtilleryBulletType(3f, 60f) {{
                    splashDamage = 35f;
                    splashDamageRadius = 15f;
                    lifetime = 105f;
                    sprite = "bullet";
                    hitEffect = Fx.flakExplosion;
                    shootEffect = Fx.shootBig;
                    shootSound = Sounds.shootArtillery;
                    status = IronStatusEffects.radioactivity;
                    statusDuration = 100f;
                }}
            );
        }};
    }
}
