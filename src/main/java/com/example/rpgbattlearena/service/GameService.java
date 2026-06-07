package com.example.rpgbattlearena.service;

import com.example.rpgbattlearena.model.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class GameService {
    private PlayerSession session;
    private final List<LeaderboardEntry> leaderboard = new ArrayList<>();

    public GameResponse startGame(String playerName, String characterClass) {
        PlayerCharacter player = createPlayer(playerName, characterClass);
        Enemy enemy = createEnemy(1, player.getLevel());
        session = new PlayerSession(player, enemy);
        session.addItem(new Item("Health Potion", ItemType.POTION, 30));
        session.addItem(new Item("Iron Sword", ItemType.WEAPON_UPGRADE, 5));
        session.addItem(new Item("Leather Armor", ItemType.ARMOR_UPGRADE, 5));

        String classLabel = formatClass(player);
        List<String> intro = List.of(
            "The arena falls silent…",
            player.getName() + " steps onto the blood-stained sand, gripping their weapon as a " + classLabel + ".",
            enemy.getName() + " roars from the opposite side — the real battle begins NOW!"
        );
        String message = String.join(" ", intro);

        return new GameResponse(message, player, enemy, session.getInventory(), StatusEffect.HEALED, session.getWaveNumber(),
            new BattleDetails(intro, 0, 0, false, false, false, false));
    }

    public GameResponse attack() {
        if (session == null) {
            throw new IllegalStateException("Game has not started. Call /start-game first.");
        }

        PlayerCharacter player = session.getPlayer();
        Enemy enemy = session.getCurrentEnemy();
        List<String> lines = new ArrayList<>();

        lines.add("— Round " + session.getWaveNumber() + " —");

        int playerDamage = player.attack();
        boolean critical = player instanceof Archer && playerDamage > player.getAttackPower() + 5;
        if (Math.random() < 0.15 && !(player instanceof Archer)) {
            critical = true;
            playerDamage += 8;
            session.setEffect(StatusEffect.BURNED);
        } else if (Math.random() < 0.12) {
            session.setEffect(StatusEffect.BURNED);
            playerDamage += 5;
        }

        lines.add(describePlayerAttack(player, enemy, playerDamage, critical));
        enemy.takeDamage(playerDamage);

        if (!enemy.isAlive()) {
            lines.add(enemy.getName() + " stumbles backward and crashes into the arena wall — DEFEATED!");
            explodeLoot();
            int xpGain = 20 + session.getWaveNumber() * 5;
            player.gainExperience(xpGain);
            lines.add("The crowd erupts! " + player.getName() + " gains " + xpGain + " XP.");
            session.incrementWave();
            Enemy nextEnemy = createEnemy(session.getWaveNumber(), player.getLevel());
            session.setCurrentEnemy(nextEnemy);
            lines.add("A gate creaks open… " + nextEnemy.getName() + " charges in, hungry for revenge!");
            updateLeaderboard(player);

            BattleDetails details = new BattleDetails(lines, playerDamage, 0, critical, true, false, false);
            return new GameResponse(String.join(" ", lines), player, nextEnemy, session.getInventory(),
                session.getEffect(), session.getWaveNumber(), details);
        }

        if (session.getEffect() == StatusEffect.STUNNED) {
            lines.add(enemy.getName() + " is dazed from your last blow and cannot fight back this turn!");
            session.setEffect(StatusEffect.HEALED);
            BattleDetails details = new BattleDetails(lines, playerDamage, 0, critical, false, false, false);
            return new GameResponse(String.join(" ", lines), player, enemy, session.getInventory(),
                session.getEffect(), session.getWaveNumber(), details);
        }

        boolean dodged = Math.random() < 0.1;
        int enemyDamage = 0;
        if (dodged) {
            lines.add(player.getName() + " rolls aside — " + enemy.getName() + "'s strike whistles past!");
        } else {
            enemyDamage = enemy.attack();
            lines.add(describeEnemyAttack(enemy, player, enemyDamage));
            player.takeDamage(enemyDamage);
        }

        if (!player.isAlive()) {
            lines.add(player.getName() + " falls to one knee… then collapses. The arena goes quiet.");
            lines.add("DEFEAT. Medics drag you from the sand. Train harder and return!");
            updateLeaderboard(player);
            BattleDetails details = new BattleDetails(lines, playerDamage, enemyDamage, critical, false, true, dodged);
            return new GameResponse(String.join(" ", lines), player, enemy, session.getInventory(),
                session.getEffect(), session.getWaveNumber(), details);
        }

        applyStatusEffect(player, lines);
        if (player.getHealth() < player.getMaxHealth() * 0.3) {
            lines.add(player.getName() + " is bleeding badly — one wrong move could end it all!");
        }

        BattleDetails details = new BattleDetails(lines, playerDamage, enemyDamage, critical, false, false, dodged);
        return new GameResponse(String.join(" ", lines), player, enemy, session.getInventory(),
            session.getEffect(), session.getWaveNumber(), details);
    }

    public List<Item> getInventory() {
        verifySession();
        return new ArrayList<>(session.getInventory());
    }

    public GameResponse useItem(String itemName) {
        verifySession();

        Predicate<Item> matchesName = item -> item.getName().equalsIgnoreCase(itemName);
        Optional<Item> optionalItem = session.getInventory().stream()
            .filter(matchesName)
            .findFirst();

        if (optionalItem.isEmpty()) {
            throw new IllegalArgumentException("Item not found in inventory: " + itemName);
        }

        Item item = optionalItem.get();
        session.removeItem(item.getName());
        PlayerCharacter player = session.getPlayer();
        String message;
        List<String> lines = new ArrayList<>();

        switch (item.getType()) {
            case POTION -> {
                int healAmount = Math.min(item.getValue(), player.getMaxHealth() - player.getHealth());
                player.heal(healAmount);
                session.setEffect(StatusEffect.HEALED);
                lines.add(player.getName() + " gulps the " + item.getName() + " — wounds close for " + healAmount + " HP!");
                message = lines.get(0);
            }
            case WEAPON_UPGRADE -> {
                player.increaseAttackPower(item.getValue());
                lines.add(player.getName() + " sharpens their blade on the " + item.getName() + " (+ " + item.getValue() + " attack)!");
                message = lines.get(0);
            }
            case ARMOR_UPGRADE -> {
                player.increaseMaxHealth(item.getValue());
                lines.add(player.getName() + " straps on the " + item.getName() + " — armor clanks, defense rises!");
                message = lines.get(0);
            }
            default -> {
                message = "Used item.";
                lines.add(message);
            }
        }

        return new GameResponse(message, player, session.getCurrentEnemy(), session.getInventory(),
            session.getEffect(), session.getWaveNumber(),
            new BattleDetails(lines, 0, 0, false, false, false, false));
    }

    public List<LeaderboardEntry> getLeaderboard() {
        return leaderboard.stream()
            .sorted(Comparator.comparingInt(LeaderboardEntry::getLevel).reversed()
                .thenComparingInt(LeaderboardEntry::getScore).reversed())
            .limit(10)
            .collect(Collectors.toList());
    }

    private PlayerCharacter createPlayer(String playerName, String characterClass) {
        return switch (characterClass.toLowerCase()) {
            case "warrior" -> new Warrior(playerName);
            case "mage" -> new Mage(playerName);
            case "archer" -> new Archer(playerName);
            default -> new Warrior(playerName);
        };
    }

    private String formatClass(PlayerCharacter player) {
        if (player instanceof Warrior) return "Warrior";
        if (player instanceof Mage) return "Mage";
        if (player instanceof Archer) return "Archer";
        return "Fighter";
    }

    private String describePlayerAttack(PlayerCharacter player, Enemy enemy, int damage, boolean critical) {
        String opener = critical ? "CRITICAL STRIKE! " : "";
        String body;
        if (player instanceof Warrior) {
            body = player.getName() + " rushes in with a war cry and drives a steel blade into " + enemy.getName();
        } else if (player instanceof Mage) {
            body = player.getName() + " channels blazing arcane fire that explodes against " + enemy.getName();
        } else if (player instanceof Archer) {
            body = player.getName() + " looses a whistling arrow that thuds deep into " + enemy.getName();
        } else {
            body = player.getName() + " strikes " + enemy.getName();
        }
        return opener + body + " for " + damage + " damage!";
    }

    private String describeEnemyAttack(Enemy enemy, PlayerCharacter player, int damage) {
        String name = enemy.getName().toLowerCase();
        if (name.contains("goblin")) {
            return enemy.getName() + " snarls and slashes with a jagged dagger — " + player.getName() + " takes " + damage + " damage!";
        }
        if (name.contains("mage")) {
            return enemy.getName() + " hurls a vile curse; dark energy tears into " + player.getName() + " for " + damage + " damage!";
        }
        if (name.contains("wolf")) {
            return enemy.getName() + " leaps and sinks fangs into " + player.getName() + "'s shoulder — " + damage + " damage!";
        }
        return enemy.getName() + " counterattacks viciously — " + player.getName() + " suffers " + damage + " damage!";
    }

    private void verifySession() {
        if (session == null) {
            throw new IllegalStateException("Game has not started. Use /start-game first.");
        }
    }

    private Enemy createEnemy(int wave, int playerLevel) {
        int health = 50 + wave * 12 + playerLevel * 5;
        int attack = 8 + wave * 2 + playerLevel;
        String name = switch (wave % 3) {
            case 1 -> "Goblin Warrior";
            case 2 -> "Cursed Mage";
            default -> "Dire Wolf";
        };
        return new Enemy(name, health, attack);
    }

    private void explodeLoot() {
        if (Math.random() < 0.5) {
            session.addItem(new Item("Health Potion", ItemType.POTION, 25));
        }
        if (Math.random() < 0.3) {
            session.addItem(new Item("Steel Sword", ItemType.WEAPON_UPGRADE, 7));
        }
    }

    private void applyStatusEffect(PlayerCharacter player, List<String> lines) {
        switch (session.getEffect()) {
            case POISONED -> {
                player.takeDamage(5);
                lines.add("Poison burns through " + player.getName() + "'s veins (-5 HP)!");
            }
            case BURNED -> {
                player.takeDamage(7);
                lines.add("Flames scorch " + player.getName() + "'s armor (-7 HP)!");
            }
            case HEALED -> player.gainExperience(2);
            default -> { }
        }
    }

    private void updateLeaderboard(PlayerCharacter player) {
        int score = player.getLevel() * 100 + player.getExperience();
        leaderboard.removeIf(entry -> entry.getPlayerName().equalsIgnoreCase(player.getName()));
        leaderboard.add(new LeaderboardEntry(player.getName(), player.getLevel(), score));
    }
}
