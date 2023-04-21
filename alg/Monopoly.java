import java.util.*;

public class Monopoly {
    public enum Space {
        START(0),
        DORPSSTRAAT(1),
        ALGEMEEN_FONDS_1(2),
        BRINK(3),
        INKOMSTENBELASTING(4),
        STATION_ZUID(5),
        STEENSTRAAT(6),
        KANS_1(7),
        KETELSTRAAT(8),
        VELPERPLEIN(9),
        OP_BEZOEK(10),
        BARTELJORISSTRAAT(11),
        ELEKTRICITEITSBEDRIJF(12),
        ZIJLWEG(13),
        HOUTSTRAAT(14),
        STATION_WEST(15),
        NEUDE(16),
        ALGEMEEN_FONDS_2(17),
        BILTSTRAAT(18),
        VREEBURG(19),
        VRIJ_PARKEREN(20),
        A_KERKHOF(21),
        KANS_2(22),
        GROTE_MARKT(23),
        HEERESTRAAT(24),
        STATION_NOORD(25),
        SPUI(26),
        PLEIN(27),
        WATERLEIDING(28),
        L_POTEN(29),
        NAAR_DE_GEVANGENIS(30),
        HOFPLEIN(31),
        BLAAK(32),
        ALGEMEEN_FONDS_3(33),
        COOLSINGEL(34),
        STATION_OOST(35),
        KANS_3(36),
        LEIDSESTRAAT(37),
        EXTRA_BELASTING(38),
        KALVERSTRAAT(39),

        GEVANGENIS(10);

        private int position;

        Space(int position) {
            this.position = position;
        }

        private static final int BOARD_SIZE = 40;
        private static Map<Integer, Space> spaceByPosition = new HashMap<>();

        static {
            for (Space sp : Space.values()) {
                if (!spaceByPosition.containsKey(sp.position)) {
                    spaceByPosition.put(sp.position, sp);
                }
            }
        }

        /**
         * Get the space at the given position.
         * @param position
         * @return
         */
        static Space byPosition(int position) {
            return spaceByPosition.get((position + BOARD_SIZE) % BOARD_SIZE);
        }
    }

    /**
     * Calculate the odds of rolling n on two dice.
     * @param n
     * @return
     */
    private double diceOdds(int n) {
        if (n < 2 || n > 12) return 0.0;
        return (6 - Math.abs(n - 7)) / 36.0;
    }

    /**
     * Calculate the odds of rolling n on two dice, as a double or not as a double.
     * @param n
     * @param isDouble
     * @return
     */
    private double diceOdds(int n, boolean isDouble) {
        if (n < 2 || n > 12) return 0.0;
        if (isDouble) return (1 - n % 2) / 36.0;
        return (6 - Math.abs(n - 7) - (1 - n % 2)) / 36.0;
    }

    /**
     * Update an entry in the given table, adding the value to the value that is already there.
     * @param table
     * @param index
     * @param space
     * @param p
     */
    private void update(List<Map<Space, Double>> table, int index, Space space, double p) {
        if (p > 0) {
            table.get(index).put(space, p + table.get(index).getOrDefault(space, 0.0));
        }
    }

    /**
     * Calculate the odds of ending on the given space after the given number of turns.
     * @param space
     * @param turn
     * @return
     */
    public double chanceToVisit(Space space, int turn) {
        List<Map<Space, Double>> table = new ArrayList<>(turn + 1);
        table.add(new EnumMap<>(Space.class));
        table.get(0).put(Space.START, 1.0);

        for (int i = 1; i <= turn; i++) {
            Map<Space, Double> currentTurn = new EnumMap<>(Space.class);
            for (Space s : Space.values()) {
                double p = 0.0;
                for (int d1 = 1; d1 <= 6; d1++) {
                    for (int d2 = 1; d2 <= 6; d2++) {
                        if (d1 + d2 == 2) {  // dubbel 1 -> naar gevangenis
                            update(table, i, Space.GEVANGENIS, diceOdds(d1 + d2));
                        } else if (i == turn && s == space) {
                            p += diceOdds(d1 + d2, d1 == d2);
                        } else {
                            Space next = s;
                            int doubles = 0;
                            if (d1 == d2) doubles++;
                            next = Space.byPosition(s.position + d1 + d2);
                            if (next == Space.NAAR_DE_GEVANGENIS) {
                                next = Space.GEVANGENIS;
                                doubles = 0;
                            }
                            p += diceOdds(d1 + d2, doubles == 1) * table.get(i-1).getOrDefault(next, 0.0);
                        }
                    }
                }
                if (s == Space.GEVANGENIS && i < turn) {  // de gevangenis overslaan als niet op bezoek
                    update(table, i+2, Space.OP_BEZOEK, p);
                } else {
                    update(table, i, s, p);
                }
            }
            table.add(currentTurn);
        }

        return table.get(turn).getOrDefault(space, 0.0);
    }

    public double chanceToVisitB(Space space, int turn) {
        List<Map<Space, Double>> table = new ArrayList<>(turn + 1);
        table.add(new EnumMap<>(Space.class));
        table.get(0).put(Space.START, 1.0);

        for (int i = 1; i <= turn; i++) {
            Map<Space, Double> currentTurn = new EnumMap<>(Space.class);
            for (Space s : Space.values()) {
                double p = 0.0;
                if (s == Space.GEVANGENIS) {  // zit al in de gevangenis
                    for (int d1 = 1; d1 <= 6; d1++) {
                        for (int d2 = 1; d2 <= 6; d2++) {
                            if (d1 == d2) {  // dubbel gegooid, uit de gevangenis
                                Space next = Space.byPosition(s.position + d1 + d2);
                                if (next == Space.NAAR_DE_GEVANGENIS) {
                                    next = Space.GEVANGENIS;
                                }
                                update(table, i, next, diceOdds(d1 + d2, true));
                            } else {  // niet dubbel gegooid, blijft in de gevangenis
                                update(table, i, s, diceOdds(d1 + d2, false));
                            }
                        }
                    }
                } else {  // niet in de gevangenis
                    for (int d1 = 1; d1 <= 6; d1++) {
                        for (int d2 = 1; d2 <= 6; d2++) {
                            if (d1 + d2 == 2) {  // dubbel 1 -> naar gevangenis
                                update(table, i, Space.GEVANGENIS, diceOdds(d1 + d2));
                            } else if (i == turn && s == space) {
                                p += diceOdds(d1 + d2, d1 == d2);
                            } else {
                                Space next = s;
                                int doubles = 0;
                                if (d1 == d2) doubles++;
                                next = Space.byPosition(s.position + d1 + d2);
                                if (next == Space.NAAR_DE_GEVANGENIS) {
                                    next = Space.GEVANGENIS;
                                    doubles = 0;
                                }
                                p += diceOdds(d1 + d2, doubles == 1) * table.get(i-1).getOrDefault(next, 0.0);
                            }
                        }
                    }
                    if (s == Space.GEVANGENIS && i < turn) {  // de gevangenis overslaan als niet op bezoek
                        update(table, i+2, Space.OP_BEZOEK, p);
                    } else {
                        update(table, i, s, p);
                    }
                }
            }
            table.add(currentTurn);
        }

        return table.get(turn).getOrDefault(space, 0.0);
    }

    public double chanceToVisitC(Space space, int turn) {
        List<Map<Space, Double>> table = new ArrayList<>(turn + 1);
        table.add(new EnumMap<>(Space.class));
        table.get(0).put(Space.START, 1.0);

        for (int i = 1; i <= turn; i++) {
            Map<Space, Double> currentTurn = new EnumMap<>(Space.class);
            for (Space s : Space.values()) {
                double p = 0.0;
                for (int d1 = 1; d1 <= 6; d1++) {
                    for (int d2 = 1; d2 <= 6; d2++) {
                        if (d1 + d2 == 2) {  // dubbel 1 -> naar gevangenis
                            update(table, i, Space.GEVANGENIS, diceOdds(d1 + d2));
                        } else if (i == turn && s == space) {
                            p += diceOdds(d1 + d2, d1 == d2);
                        } else {
                            Space next = s;
                            int doubles = 0;
                            if (d1 == d2) doubles++;
                            if (next == Space.GEVANGENIS) {
                                if (doubles == 1 && i >= 3) {
                                    next = Space.OP_BEZOEK;
                                } else if (doubles == 2) {
                                    next = Space.GEVANGENIS;
                                }
                            } else {
                                next = Space.byPosition(s.position + d1 + d2);
                                if (next == Space.NAAR_DE_GEVANGENIS) {
                                    next = Space.GEVANGENIS;
                                    doubles = 0;
                                }
                            }
                            p += diceOdds(d1 + d2, doubles == 1) * table.get(i-1).getOrDefault(next, 0.0);
                        }
                    }
                }
                if (s == Space.GEVANGENIS) {
                    if (i == 1) {  // eerste beurt na gevangenis -> altijd dubbel
                        update(table, i + 1, Space.GEVANGENIS, p);
                    } else if (i == turn) {  // laatste beurt -> geen bezoek mogelijk
                        update(table, i, s, p);
                    } else {  // andere beurten -> kans op op bezoek gaan
                        update(table, i + 1, Space.OP_BEZOEK, p);
                    }
                } else {
                    update(table, i, s, p);
                }
            }
            table.add(currentTurn);
        }

        return table.get(turn).getOrDefault(space, 0.0);
    }

    private static void assertDouble(double expected, double actual) {
        if (expected * 0.99999 > actual || expected * 1.00001 < actual) {
            new AssertionError("Expected: " + expected + ", actual: " + actual).printStackTrace(System.err);
        }
    }

    public static void main(String[] args) {
        Monopoly m = new Monopoly();

        // exercise A
        assertDouble(0.0, m.chanceToVisit(Space.GEVANGENIS, 1));
        assertDouble(0.16666666666666666, m.chanceToVisit(Space.KANS_1, 1));
        assertDouble(0.027777777777777776, m.chanceToVisit(Space.ELEKTRICITEITSBEDRIJF, 1));
        assertDouble(0.09645061728395062, m.chanceToVisit(Space.ELEKTRICITEITSBEDRIJF, 2));
        assertDouble(0.0, m.chanceToVisit(Space.VRIJ_PARKEREN, 1));
        assertDouble(0.02700617283950617, m.chanceToVisit(Space.VRIJ_PARKEREN, 2));
        assertDouble(0.022939611963679888, m.chanceToVisit(Space.START, 1000));
        assertDouble(0.026863264333236637, m.chanceToVisit(Space.GEVANGENIS, 1000));

        // exercise B
        assertDouble(0.0, m.chanceToVisitB(Space.GEVANGENIS, 1));
        assertDouble(0.16666666666666666, m.chanceToVisitB(Space.KANS_1, 1));
        assertDouble(0.027777777777777776, m.chanceToVisitB(Space.ELEKTRICITEITSBEDRIJF, 1));
        assertDouble(0.09645061728395062, m.chanceToVisitB(Space.ELEKTRICITEITSBEDRIJF, 2));
        assertDouble(0.0, m.chanceToVisitB(Space.VRIJ_PARKEREN, 1));
        assertDouble(0.02700617283950617, m.chanceToVisitB(Space.VRIJ_PARKEREN, 2));
        assertDouble(0.020205991616564566, m.chanceToVisitB(Space.START, 1000));
        assertDouble(0.14291379111329078, m.chanceToVisitB(Space.GEVANGENIS, 1000));

        // exercise C
        assertDouble(0.0050154320987654336, m.chanceToVisitC(Space.GEVANGENIS, 1));
        assertDouble(0.17133916323731138, m.chanceToVisitC(Space.KANS_1, 1));
        assertDouble(0.009645061728395061, m.chanceToVisitC(Space.ELEKTRICITEITSBEDRIJF, 1));
        assertDouble(0.07771768320707285, m.chanceToVisitC(Space.ELEKTRICITEITSBEDRIJF, 2));
        assertDouble(0.005958504801097394, m.chanceToVisitC(Space.VRIJ_PARKEREN, 1));
        assertDouble(0.03618953291616566, m.chanceToVisitC(Space.VRIJ_PARKEREN, 2));
        assertDouble(0.019454302036506436, m.chanceToVisitC(Space.START, 1000));
        assertDouble(0.18341792428441364, m.chanceToVisitC(Space.GEVANGENIS, 1000));
    }
}
