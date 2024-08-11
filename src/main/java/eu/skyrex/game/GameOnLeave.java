package eu.skyrex.game;

import eu.skyrex.Main;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import org.jetbrains.annotations.NotNull;

public class GameOnLeave implements EventListener<PlayerDisconnectEvent> {

    @Override
    public @NotNull Class<PlayerDisconnectEvent> eventType() {
        return PlayerDisconnectEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerDisconnectEvent event) {
        Main.getGameManager().removeFromSidebar(event.getPlayer());
        Main.getGameManager().sendMessageToAllPlayers("<yellow>"+event.getPlayer().getUsername() + " left the game!");
        return Result.SUCCESS;
    }
}
