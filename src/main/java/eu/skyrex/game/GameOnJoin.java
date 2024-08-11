package eu.skyrex.game;

import eu.skyrex.Main;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

public class GameOnJoin implements EventListener<PlayerSpawnEvent> {

    @Override
    public @NotNull Class<PlayerSpawnEvent> eventType() {
        return PlayerSpawnEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerSpawnEvent event) {
        if(!event.isFirstSpawn()) return Result.INVALID;

        event.getPlayer().addEffect(new Potion(PotionEffect.RESISTANCE, (byte) 127, Potion.INFINITE_DURATION));
        Main.getGameManager().addToSidebar(event.getPlayer());
        Main.getGameManager().sendMessageToAllPlayers("<yellow>"+event.getPlayer().getUsername() + " joined the game!");
        return Result.SUCCESS;
    }
}
