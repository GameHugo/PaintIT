package eu.skyrex.game;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.TaskSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameManager {

    private final List<String> words = List.of("apple", "banana", "cherry", "date", "elderberry", "fig", "grape", "honeydew", "kiwi", "lemon", "mango", "nectarine", "orange", "papaya", "quince", "raspberry", "strawberry", "tangerine", "watermelon");
    private boolean gameStarted = false;
    private String currentWord;
    private String previewWord;
    private final List<Player> players = new ArrayList<>();
    private final List<Player> correctPlayers = new ArrayList<>();

    private int timeLeft;

    private final Random random = new Random();
    Logger logger = LoggerFactory.getLogger(GameManager.class);

    public GameManager() {
        Scheduler scheduler = MinecraftServer.getSchedulerManager();

        scheduler.buildTask(() -> {
            if (gameStarted) {
                int revealInterval = Math.max(1, 150 / currentWord.length())+1;
                if (timeLeft == 0) {
                    stopGame();
                    return;
                }
                timeLeft--;
                if (timeLeft % revealInterval == 0) {
                    List<Integer> indexes = new ArrayList<>();
                    String wordWithoutSpaces = previewWord.replace(" ", "");
                    for (int i = 0; i < wordWithoutSpaces.length(); i++) {
                        if (wordWithoutSpaces.charAt(i) == '_') {
                            indexes.add(i);
                        }
                    }
                    if (!indexes.isEmpty()) {
                        int index = indexes.get(random.nextInt(indexes.size()));
                        previewWord = previewWord.substring(0, index * 2) + currentWord.charAt(index) + previewWord.substring(index * 2 + 1);
                    }
                }
                for (Player player : players) {
                    sendGameActionBar(player);
                }
            }
        }).repeat(TaskSchedule.tick(20)).schedule();
    }

    public void startGame() {
        if (gameStarted) return;
        timeLeft = 120;
        gameStarted = true;
        players.clear();
        correctPlayers.clear();
        players.addAll(MinecraftServer.getConnectionManager().getOnlinePlayers());
        currentWord = words.get(random.nextInt(words.size()));
        previewWord = "_ ".repeat(currentWord.length());
        sendMessageToAllPlayers("The game has started!");
        logger.info("The current word is: {}", currentWord);
        for (Player player : players) {
            sendGameActionBar(player);
        }
    }

    public void stopGame() {
        if (!gameStarted) return;
        gameStarted = false;
        sendMessageToAllPlayers("The game has been stopped!");
        sendActionBarToAllPlayers(null);
    }

    public boolean playerGuess(Player player, String guess) {
        if (!gameStarted) return false;
        if (guess.equalsIgnoreCase(currentWord)) {
            correctPlayers.add(player);
            player.sendMessage("Correct!");
            sendMessageToAllPlayers(player.getUsername() + " has guessed the word!");
            sendGameActionBar(player);
            if(correctPlayers.size() == players.size()) {
                stopGame();
            }
            return true;
        }
        return false;
    }

    public void sendMessageToAllPlayers(String message) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }

    public void sendActionBarToAllPlayers(String message) {
        for (Player player : players) {
            if (message == null) {
                player.sendActionBar(Component.empty());
                return;
            }
            player.sendActionBar(Component.text(message));
        }
    }

    public void sendGameActionBar(Player player) {
        if(correctPlayers.contains(player)) {
            player.sendActionBar(Component.text("Guess the word: " + currentWord + " (" + timeLeft + ")"));
            return;
        }
        player.sendActionBar(Component.text("Guess the word: " + previewWord + " (" + timeLeft + ")"));
    }

    public boolean playerCorrect(Player player) {
        return correctPlayers.contains(player);
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public List<String> getWordsList() {
        return words;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public List<Player> getPlayers() {
        return players;
    }
}
