package eu.skyrex.game;

import eu.skyrex.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
        if (player.equals(Main.getCanvasManager().getPainter())) {
            player.sendMessage(Component.text("You can't guess the word!", NamedTextColor.RED));
            event.setCancelled(true);
            return Result.INVALID;
        }
        if (gameManager.playerCorrect(player)) {
            player.sendMessage(Component.text("You already guessed the word!", NamedTextColor.GREEN));
            event.setCancelled(true);
            return Result.INVALID;
        }
        if(gameManager.playerGuess(player, event.getMessage())) {
            event.setCancelled(true);
        }
        return Result.SUCCESS;
    }
}
