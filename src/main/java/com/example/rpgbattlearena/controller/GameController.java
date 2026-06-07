package com.example.rpgbattlearena.controller;

import com.example.rpgbattlearena.model.GameResponse;
import com.example.rpgbattlearena.model.LeaderboardEntry;
import com.example.rpgbattlearena.model.Item;
import com.example.rpgbattlearena.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/start-game")
    public ResponseEntity<GameResponse> startGame(@RequestBody StartGameRequest request) {
        return ResponseEntity.ok(gameService.startGame(request.getName(), request.getCharacterClass()));
    }

    @PostMapping("/attack")
    public ResponseEntity<GameResponse> attack() {
        return ResponseEntity.ok(gameService.attack());
    }

    @GetMapping("/inventory")
    public ResponseEntity<List<Item>> getInventory() {
        return ResponseEntity.ok(gameService.getInventory());
    }

    @PostMapping("/inventory/use")
    public ResponseEntity<GameResponse> useItem(@RequestBody UseItemRequest request) {
        return ResponseEntity.ok(gameService.useItem(request.getItemName()));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardEntry>> getLeaderboard() {
        return ResponseEntity.ok(gameService.getLeaderboard());
    }
}
