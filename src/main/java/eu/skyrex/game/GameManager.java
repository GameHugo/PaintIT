package eu.skyrex.game;

import eu.skyrex.Main;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.scoreboard.Sidebar;
import net.minestom.server.sound.SoundEvent;
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
import java.util.Map;
import java.util.Random;

public class GameManager {

    private final File wordsFile;
    private boolean gameStarted = false;
    private String currentWord;
    private String previewWord;
    private final List<Player> correctPlayers = new ArrayList<>();
    private final Map<Player, Integer> scores = new java.util.HashMap<>();

    private final Sidebar sidebar = new Sidebar(Component.text("PaintIT"));

    private int timeLeft;
    private final List<Player> playersDrawn = new ArrayList<>();

    private final Random random = new Random();
    Logger logger = LoggerFactory.getLogger(GameManager.class);

    public GameManager() {
        final File file = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile();
        wordsFile = new File(file, "words.txt");

        Scheduler scheduler = MinecraftServer.getSchedulerManager();

        scheduler.buildTask(() -> {
            List<Player> players = new ArrayList<>(MinecraftServer.getConnectionManager().getOnlinePlayers());
            if (gameStarted) {
                int revealInterval = Math.max(1, 150 / currentWord.length())+1;
                if (timeLeft == 0) {
                    sendMessageToAllPlayers("<red>Time's up! The word was: " + currentWord);
                    nextGame();
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
                        playSoundEffect(SoundEvent.BLOCK_NOTE_BLOCK_CHIME);
                    }
                }
                for (Player player : players) {
                    sendGameActionBar(player);
                }
            }
            // Update the sidebar
            for (Player player : players) {
                int score = scores.getOrDefault(player, 0);
                if (sidebar.getLine(player.getUuid().toString()) == null) {
                    Sidebar.ScoreboardLine line = new Sidebar.ScoreboardLine(
                            player.getUuid().toString(), Component.text(player.getUsername()), score);
                    sidebar.createLine(line);
                } else {
                    sidebar.updateLineScore(player.getUuid().toString(), score);
                }
            }
        }).repeat(TaskSchedule.tick(20)).schedule();
    }

    public void startGame() {
        if (gameStarted) return;
        playersDrawn.clear();
        sendMessageToAllPlayers("<green>The game has started!");
        nextGame();
    }

    public void nextGame() {
        timeLeft = 120;
        gameStarted = true;
        correctPlayers.clear();

        List<Player> players = new ArrayList<>(MinecraftServer.getConnectionManager().getOnlinePlayers());

        Player drawer = null;
        for (Player player : players) {
            if (!playersDrawn.contains(player)) {
                drawer = player;
                break;
            }
        }
        if (drawer == null) {
            stopGame();
            return;
        }
        Main.getCanvasManager().setPainter(drawer);
        correctPlayers.add(drawer);
        playersDrawn.add(drawer);

        Main.getCanvasManager().clearCanvas();

        currentWord = getWord();
        StringBuilder previewWordSB = new StringBuilder();  // Initialize a StringBuilder
        for (int i = 0; i < currentWord.length(); i++) {
            if (currentWord.charAt(i) == ' ') {
                previewWordSB.append("  ");  // Append two spaces for the space in the word
            } else {
                previewWordSB.append("_ ");  // Append underscore followed by a space
            }
        }

        previewWord = previewWordSB.toString();

        sendMessageToAllPlayers("<yellow>" + drawer.getUsername() + " is drawing!");
        logger.info("The current word is: {}", currentWord);
        for (Player player : players) {
            sendGameActionBar(player);
        }
        playSoundEffect(SoundEvent.ENTITY_ARROW_HIT_PLAYER);
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
            Main.getGameManager().playSoundEffect(SoundEvent.ENTITY_PLAYER_LEVELUP);
            int score = scores.getOrDefault(player, 0);
            if (correctPlayers.isEmpty()) {
                score += 3;
            } else if (correctPlayers.size() == 1) {
                score += 2;
            } else {
                score += 1;
            }
            scores.put(player, scores.getOrDefault(player, 0) + score);
            if(correctPlayers.size() == MinecraftServer.getConnectionManager().getOnlinePlayers().size()) {
                sendMessageToAllPlayers("<green>Everyone has guessed the word! The word was: " + currentWord);
                nextGame();
            }
            return true;
        }
        return false;
    }

    public void sendMessageToAllPlayers(String message) {
        Component msg = MiniMessage.miniMessage().deserialize(message);
        for (Player player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            player.sendMessage(msg);
        }
    }

    public void sendActionBarToAllPlayers(String message) {
        Component msg = message == null ? Component.empty() : MiniMessage.miniMessage().deserialize(message);
        for (Player player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
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

    public void playSoundEffect(SoundEvent sound) {
        for (Player player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            player.playSound(Sound.sound(sound, Sound.Source.RECORD, 1f, 1f));
        }
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

    public void addToSidebar(Player player) {
        if(sidebar.getViewers().contains(player)) return;
        sidebar.addViewer(player);
    }

    public void removeFromSidebar(Player player) {
        if(sidebar.getViewers().contains(player))
            sidebar.removeViewer(player);
        if(sidebar.getLine(player.getUuid().toString()) != null)
            sidebar.removeLine(player.getUuid().toString());
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

}
