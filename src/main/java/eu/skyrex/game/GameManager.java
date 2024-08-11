package eu.skyrex.game;

import eu.skyrex.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.TaskSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameManager {

    private final File wordsFile;
    private boolean gameStarted = false;
    private String currentWord;
    private String previewWord;
    private final List<Player> players = new ArrayList<>();
    private final List<Player> correctPlayers = new ArrayList<>();

    private int timeLeft;

    private final Random random = new Random();
    Logger logger = LoggerFactory.getLogger(GameManager.class);

    public GameManager() {

        final File file = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile();
        wordsFile = new File(file, "words.txt");

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

        final Player drawer = players.get(random.nextInt(players.size()));
        Main.getCanvasManager().setPainter(drawer);
        correctPlayers.add(drawer);

        currentWord = getWord();
        previewWord = "_ ".repeat(currentWord.length());

        sendMessageToAllPlayers("<yellow>" + drawer.getUsername() + " is drawing!");
        logger.info("The current word is: {}", currentWord);
        for (Player player : players) {
            sendGameActionBar(player);
        }
    }

    public void stopGame() {
        if (!gameStarted) return;
        gameStarted = false;
        sendMessageToAllPlayers("<red>The game has been stopped!");
        sendActionBarToAllPlayers(null);

        Main.getCanvasManager().setPainter(null);

        MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(player -> player.getInventory().clear());
    }

    public boolean playerGuess(Player player, String guess) {
        if (!gameStarted) return false;
        if (guess.equalsIgnoreCase(currentWord)) {
            correctPlayers.add(player);
            player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Correct!"));
            sendMessageToAllPlayers("<green>" + player.getUsername() + " has guessed the word!");
            sendGameActionBar(player);
            if(correctPlayers.size() == players.size()) {
                stopGame();
            }
            return true;
        }
        return false;
    }

    public void sendMessageToAllPlayers(String message) {
        Component msg = MiniMessage.miniMessage().deserialize(message);
        for (Player player : players) {
            player.sendMessage(msg);
        }
    }

    public void sendActionBarToAllPlayers(String message) {
        Component msg = message == null ? Component.empty() : MiniMessage.miniMessage().deserialize(message);
        for (Player player : players) {
            if (message == null) {
                player.sendActionBar(msg);
                return;
            }
            player.sendActionBar(msg);
        }
    }

    public void sendGameActionBar(Player player) {
        if(correctPlayers.contains(player)) {
            player.sendActionBar(MiniMessage.miniMessage().deserialize("<yellow>The word is: <green>" + currentWord + " <gray>(" + timeLeft + ")"));
            return;
        }
        player.sendActionBar(MiniMessage.miniMessage().deserialize("<yellow>Guess the word: " + previewWord + " <gray>(" + timeLeft + ")"));
    }

    public String getWord() {
        try (FileInputStream stream = new FileInputStream(wordsFile);
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            long length = reader.lines().count();
            stream.getChannel().position(0);
            return reader.lines().skip(random.nextLong(length)).findFirst().orElseThrow();
        } catch (Exception e) {
            throw new RuntimeException("Could not find a word. Please add a words.txt file to the server directory.", e);
        }
    }

    public boolean playerCorrect(Player player) {
        return correctPlayers.contains(player);
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public List<Player> getPlayers() {
        return players;
    }
}
