import java.util.Random;

abstract class Attack {
    protected static final Random random = new Random();
    protected int roll() {
        return random.nextInt(20) + 1;
    }
    public abstract int perform();
    public abstract String special();
}

class GuardAttack extends Attack {

    @Override
    public int perform() {
        int roll = roll() + 3;
        if (roll == 23) {
            return 12;
        } else if (roll > 18) {
            return 6;
        }
        return 0;
    }

    @Override
    public String special() {
        return "Guard special action!";
    }
}

class SkulkerAttack extends Attack {
    @Override
    public int perform() {
        int roll1 = roll();
        int roll2 = roll();
        int finalRoll = Math.max(roll1, roll2) + 4;

        if (finalRoll == 24) {
            return 8;
        } else if (finalRoll > 18) {
            return 4;
        }
        return 0;
    }

    @Override
    public String special() {
        return "Skulker special action!";
    }
}

class PhalanxAttack extends Attack {

    @Override
    public int perform() {
        int roll = roll() + 5;
        if (roll == 25) {
            return 12;
        } else if (roll > 16) {
            return 6;
        }
        return 0;
    }

        @Override
        public String special() {
            return "Phalanx special action!";
        }
    }

class FighterAttack extends Attack {
    @Override
    public int perform() {
        int totalDamage = 0;
        for (int i = 0; i < 2; i++) {
            int roll = roll() + 5;

            if (roll == 25) {
                totalDamage += 12;
            } else if (roll > 18) {
                totalDamage += 6;
            }
        }
        return totalDamage;
    }

    @Override
    public String special() {
        return "Fighter special action!";
    }
}
