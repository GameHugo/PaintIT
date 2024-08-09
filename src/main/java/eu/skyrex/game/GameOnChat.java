package eu.skyrex.game;

import eu.skyrex.Main;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerChatEvent;
import org.jetbrains.annotations.NotNull;

public class GameOnChat implements EventListener<PlayerChatEvent> {

    @Override
    public @NotNull Class<PlayerChatEvent> eventType() {
        return PlayerChatEvent.class;
    }

    private final GameManager gameManager = Main.getGameManager();

    @NotNull
    @Override
    public Result run(@NotNull PlayerChatEvent event) {
        Player player = event.getPlayer();
        event.setChatFormat(evt -> Component.text(evt.getPlayer().getUsername() + " > " + evt.getMessage()));
        if (!gameManager.isGameStarted()) return Result.INVALID;
        if (gameManager.playerCorrect(player)) {
            player.sendMessage(Component.text("You already guessed the word!"));
            event.setCancelled(true);
            return Result.INVALID;
        }
        event.setCancelled(gameManager.playerGuess(player, event.getMessage()));
        return Result.SUCCESS;
    }
}
