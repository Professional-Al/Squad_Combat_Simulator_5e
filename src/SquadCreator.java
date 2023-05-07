import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


enum UnitType {
    GUARD, SKULKER, FIGHTER, PHALANX, DEFEATED
}
class Unit {
    UnitType type;
    int count;
    int armorClass;
    int hpPoints;
    boolean isDisabled;
    int moralePoints;

    public Unit(UnitType type, int count, int armorClass, int hpPoints, boolean isDisabled, int moralePoints) {
        this.type = type;
        this.count = count;
        this.armorClass = armorClass;
        this.hpPoints = hpPoints;
        this.isDisabled = isDisabled;
        this.moralePoints = moralePoints;
    }
}


public class SquadCreator {

    private static final Random random = new Random();

    private static Unit createUnit(String[] unitData) {
        UnitType type = UnitType.valueOf(unitData[0].toUpperCase());
        int count = Integer.parseInt(unitData[1]);
        int armorClass, hpPoints, moralePoints;

        switch (type) {
            case GUARD:
                armorClass = 16;
                hpPoints = 16;
                moralePoints = 7;
                break;
            case SKULKER:
                armorClass = 16;
                hpPoints = 16;
                moralePoints = 4;
                break;
            case FIGHTER:
                armorClass = 52;
                hpPoints = 52;
                moralePoints = 5;
                break;
            case PHALANX:
                armorClass = 32;
                hpPoints = 32;
                moralePoints = 10;
                break;
            case DEFEATED:
                armorClass = 0;
                hpPoints = 0;
                moralePoints = -3;
                break;
            default:
                throw new IllegalArgumentException("Invalid unit type: " + unitData[0]);
        }

        return new Unit(type, count, armorClass, hpPoints, false, moralePoints);
    }

    private static Unit getRandomUnit(List<Unit> squad) {
        List<Unit> availableUnits = squad.stream()
                .filter(unit -> unit.type != UnitType.DEFEATED)
                .collect(Collectors.toList());

        if (availableUnits.isEmpty()) {
            return null;
        }

        int index = random.nextInt(availableUnits.size());
        return availableUnits.get(index);
    }


    private static List<Unit> readSquadFromFile(String fileName) {
        List<Unit> squad = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] unitData = line.split(",");
                squad.add(createUnit(new String[]{unitData[0], unitData[1]}));
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + fileName);
            e.printStackTrace();
        }
        return squad;
    }

    private static void writeSquadToFile(List<Unit> squad, String fileName) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (Unit unit : squad) {
                if (unit.count > 0) {
                    bw.write(unit.type + "," + unit.count);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing to the file: " + fileName);
            e.printStackTrace();
        }
    }

    private static void performCombat(List<Unit> friendlySquad, List<Unit> enemySquad) {
        String friendlySquadsFile = "FriendlySquads.txt";
        String enemySquadsFile = "EnemySquads.txt";

        boolean friendlyTurn = random.nextInt(20) + 1 > random.nextInt(20) + 1;

        while (friendlySquad.stream().mapToInt(unit -> unit.moralePoints).sum() > 0 && enemySquad.stream().mapToInt(unit -> unit.moralePoints).sum() > 0) {
            List<Unit> attackingSquad = friendlyTurn ? friendlySquad : enemySquad;
            List<Unit> defendingSquad = friendlyTurn ? enemySquad : friendlySquad;
            String attackingSquadName = friendlyTurn ? "Friendly" : "Enemy";

            System.out.println(attackingSquadName + " squad is attacking!");

            for (Unit attacker : attackingSquad) {
                if (attacker.isDisabled) {
                    continue;
                }
                Attack attack = createAttackForUnit(attacker);
                int damage = attack.perform();
                if (damage > 0) {
                    Unit defender = getRandomUnit(defendingSquad);
                    System.out.println(attacker.type + " deals " + damage + " damage to " + defender.type);
                    defender.hpPoints -= damage;
                    if (defender.hpPoints <= 0) {
                        defender.count--;
                        if (defender.count > 0) {
                            defender.hpPoints = defender.armorClass;
                        } else {
                            defendingSquad.remove(defender);
                        }
                    }
                }
            }

            // Update files
            writeSquadToFile(friendlySquad, friendlySquadsFile);
            writeSquadToFile(enemySquad, enemySquadsFile);

            // Switch turns
            friendlyTurn = !friendlyTurn;
        }

        if (friendlySquad.stream().mapToInt(unit -> unit.moralePoints).sum() <= 0) {
            System.out.println("Friendly squad is broken!");
        }
        if (enemySquad.stream().mapToInt(unit -> unit.moralePoints).sum() <= 0) {
            System.out.println("Enemy squad is broken!");
        }
    }



    private static Attack createAttackForUnit(Unit unit) {
        switch (unit.type) {
            case GUARD:
                return new GuardAttack();
            case SKULKER:
                return new SkulkerAttack();
            case FIGHTER:
                return new FighterAttack();
            case PHALANX:
                return new GuardAttack(); // Phalanx attack is the same as Guard attack
            default:
                throw new IllegalArgumentException("Invalid unit type for attack: " + unit.type);
        }
    }


    public static void main(String[] args) {
        String friendlySquadsFile = ".idea/FriendlySquads.txt";
        String enemySquadsFile = "EnemySquads.txt";

        List<Unit> friendlySquad = readSquadFromFile(friendlySquadsFile);
        List<Unit> enemySquad = readSquadFromFile(enemySquadsFile);

        performCombat(friendlySquad, enemySquad);

        writeSquadToFile(friendlySquad, friendlySquadsFile);
        writeSquadToFile(enemySquad, enemySquadsFile);
    }


    private static List<List<Unit>> readSquadsFromFile(String fileName) {
        List<List<Unit>> allSquads = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            List<Unit> currentSquad = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    allSquads.add(currentSquad);
                    currentSquad = new ArrayList<>();
                } else {
                    List<String> values = Arrays.asList(line.split(","));
                    UnitType type = UnitType.valueOf(values.get(0));
                    int count = Integer.parseInt(values.get(1).trim());
                    int armorClass = Integer.parseInt(values.get(2).trim());
                    int hpPoints = Integer.parseInt(values.get(3).trim());
                    boolean isDisabled = Boolean.parseBoolean(values.get(4).trim());
                    int moralePoints = Integer.parseInt(values.get(5).trim());

                    currentSquad.add(new Unit(type ,count, armorClass, hpPoints, isDisabled, moralePoints));
                }
            }

            if (!currentSquad.isEmpty()) {
                allSquads.add(currentSquad);
            }

        } catch (IOException e) {
            System.err.println("Error reading the file: " + fileName);
            e.printStackTrace();
        }

        return allSquads;
    }
}
