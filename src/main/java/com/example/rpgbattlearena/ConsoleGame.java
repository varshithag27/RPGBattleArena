package com.example.rpgbattlearena;

import com.example.rpgbattlearena.model.GameResponse;
import com.example.rpgbattlearena.model.Item;
import com.example.rpgbattlearena.model.LeaderboardEntry;
import com.example.rpgbattlearena.service.GameService;

import java.util.List;
import java.util.Scanner;

/**
 * Play the game in the terminal — no browser or Postman needed.
 * Run: mvn -q exec:java
 * Or double-click: RUN-GAME.bat
 */
public class ConsoleGame {

    public static void main(String[] args) {
        GameService game = new GameService();
        Scanner scanner = new Scanner(System.in);

        printHeader();

        if (args.length > 0 && "demo".equalsIgnoreCase(args[0])) {
            runDemo(game);
            scanner.close();
            return;
        }

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Enter choice (1-6): ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> startGame(game, scanner);
                    case "2" -> printResponse(game.attack());
                    case "3" -> printInventory(game.getInventory());
                    case "4" -> useItem(game, scanner);
                    case "5" -> printLeaderboard(game.getLeaderboard());
                    case "6" -> {
                        System.out.println("\nThanks for playing! Goodbye.\n");
                        running = false;
                    }
                    default -> System.out.println("\nInvalid choice. Please enter 1 to 6.\n");
                }
            } catch (IllegalStateException | IllegalArgumentException e) {
                System.out.println("\nError: " + e.getMessage() + "\n");
            }
        }
        scanner.close();
    }

    private static void runDemo(GameService game) {
        System.out.println("=== AUTO DEMO (no typing needed) ===\n");
        printResponse(game.startGame("Hero", "Mage"));
        for (int i = 1; i <= 5; i++) {
            System.out.println("--- Turn " + i + " ---");
            printResponse(game.attack());
        }
        System.out.println("--- Inventory ---");
        printInventory(game.getInventory());
        System.out.println("--- Leaderboard ---");
        printLeaderboard(game.getLeaderboard());
        System.out.println("=== DEMO FINISHED ===\n");
    }

    private static void startGame(GameService game, Scanner scanner) {
        System.out.print("Your name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            name = "Hero";
        }
        System.out.println("Pick class: 1=Warrior  2=Mage  3=Archer");
        System.out.print("Choice (1-3, Enter = Warrior): ");
        String pick = scanner.nextLine().trim();
        String characterClass = switch (pick) {
            case "2" -> "Mage";
            case "3" -> "Archer";
            default -> "Warrior";
        };
        printResponse(game.startGame(name, characterClass));
    }

    private static void useItem(GameService game, Scanner scanner) {
        printInventory(game.getInventory());
        System.out.print("Item name to use (e.g. Health Potion): ");
        String itemName = scanner.nextLine().trim();
        if (!itemName.isEmpty()) {
            printResponse(game.useItem(itemName));
        }
    }

    private static void printHeader() {
        System.out.println();
        System.out.println("========================================");
        System.out.println("       RPG BATTLE ARENA (Console)      ");
        System.out.println("========================================");
        System.out.println();
    }

    private static void printMenu() {
        System.out.println("----------------------------------------");
        System.out.println("  1 - Start new game");
        System.out.println("  2 - Attack (fight one turn)");
        System.out.println("  3 - Show inventory");
        System.out.println("  4 - Use an item");
        System.out.println("  5 - Show leaderboard");
        System.out.println("  6 - Quit");
        System.out.println("----------------------------------------");
    }

    private static void printResponse(GameResponse r) {
        System.out.println();
        System.out.println(">> " + r.getMessage());
        System.out.println("   Player: " + r.getPlayer());
        System.out.println("   Enemy:  " + r.getEnemy());
        System.out.println("   Wave: " + r.getWave() + "  |  Status: " + r.getStatusEffect());
        printInventory(r.getInventory());
        System.out.println();
    }

    private static void printInventory(List<Item> items) {
        if (items == null || items.isEmpty()) {
            System.out.println("   Inventory: (empty)");
            return;
        }
        System.out.println("   Inventory:");
        for (Item item : items) {
            System.out.println("     - " + item.getName() + " (" + item.getType() + ", value=" + item.getValue() + ")");
        }
    }

    private static void printLeaderboard(List<LeaderboardEntry> entries) {
        System.out.println();
        if (entries == null || entries.isEmpty()) {
            System.out.println("Leaderboard is empty. Defeat an enemy first!");
            System.out.println();
            return;
        }
        System.out.println("--- LEADERBOARD (top players) ---");
        int rank = 1;
        for (LeaderboardEntry e : entries) {
            System.out.printf("  %d. %s - Level %d, Score %d%n", rank++, e.getPlayerName(), e.getLevel(), e.getScore());
        }
        System.out.println();
    }
}
