package iron;

import arc.struct.Seq;
import mindustry.content.TechTree;
import mindustry.game.Objectives;
import mindustry.game.Objectives.Objective;

import static mindustry.content.TechTree.node;
import static mindustry.content.TechTree.nodeRoot;
import static mindustry.type.ItemStack.with;

/**
 * Дерево технологий Айрона.
 *
 * Структура собрана из полей research: каждого hjson-файла:
 * parent задаёт вложенность, requirements — стоимость исследования,
 * objectives — условия разблокировки.
 *
 * ВНИМАНИЕ: у цели Produce в hjson стояло amount: 30 / 50, но у класса
 * Objectives.Produce поля amount нет — парсер его игнорировал.
 * Поэтому здесь цель тоже без количества, как и было в игре.
 */
public class IronTechTree {

    private static Seq<Objective> sector(mindustry.type.SectorPreset preset) {
        return Seq.with(new Objectives.SectorComplete(preset));
    }

    private static Seq<Objective> produce(mindustry.ctype.UnlockableContent content) {
        return Seq.with(new Objectives.Produce(content));
    }

    public static void load() {
        IronPlanets.iron.techTree = nodeRoot("iron-tree", IronBlocks.shardCore, () -> {

            // --- ресурсы ---
            node(IronItems.iron, with(IronItems.iron, 1), () -> {
                node(IronLiquids.liquidIron, with(IronItems.iron, 1), () -> {});

                node(IronItems.uranus, with(IronItems.uranus, 1), () -> {
                    node(IronItems.depletedUranus, with(IronItems.depletedUranus, 1), () -> {});

                    node(IronItems.hugeQuartz, with(IronItems.hugeQuartz, 1), () -> {
                        node(IronItems.quartz, with(IronItems.quartz, 1), () -> {});
                    });
                });
            });

            // --- плавильня и всё, что от неё ---
            node(IronBlocks.ironSmelter, with(IronItems.iron, 50), () -> {

                node(IronBlocks.bulletThrower, with(IronItems.iron, 100),
                        produce(IronItems.iron), () -> {

                    node(IronBlocks.uranusWall, with(IronItems.uranus, 50),
                            sector(IronSectorPresets.sector1), () -> {

                        node(IronBlocks.uranusWallLarge, with(IronItems.uranus, 200),
                                sector(IronSectorPresets.sector1), () -> {});
                    });

                    node(IronBlocks.uranusCannon, with(IronItems.iron, 200, IronItems.uranus, 50),
                            sector(IronSectorPresets.sector1), () -> {});
                });

                node(IronBlocks.crasher, with(IronItems.iron, 250, IronItems.uranus, 100),
                        sector(IronSectorPresets.sector2), () -> {});
            });

            // --- насос и манипуляторы ---
            node(IronBlocks.ironPump, with(IronItems.iron, 50), () -> {

                node(IronBlocks.ironManipulator, with(IronItems.iron, 15),
                        produce(IronItems.iron), () -> {

                    node(IronBlocks.fastManipulator, with(IronItems.iron, 15),
                            produce(IronItems.uranus), () -> {});
                });
            });

            // --- энергетика ---
            node(IronBlocks.uraniumReactor, with(IronItems.iron, 100, IronItems.uranus, 15),
                    sector(IronSectorPresets.sector1), () -> {

                node(IronBlocks.solarator, with(IronItems.iron, 40, IronItems.uranus, 5),
                        sector(IronSectorPresets.sector1), () -> {

                    node(IronBlocks.hugeSolarator,
                            with(IronItems.iron, 60, IronItems.uranus, 15, IronItems.quartz, 5),
                            sector(IronSectorPresets.sector2), () -> {});
                });

                node(IronBlocks.uranusCollector, with(IronItems.uranus, 25, IronItems.iron, 100), () -> {});
            });
        });
    }
}
