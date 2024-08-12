package eu.skyrex.game;

import eu.skyrex.Main;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.UUID;

public class GameOnJoin implements EventListener<PlayerSpawnEvent> {

    UUID resourcePackId = UUID.randomUUID();

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
        event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize(
                "<blue>Welcome to PaintIT! This is a game like skribbl.io, but in Minecraft! You can draw, guess, and have fun!"));
        if(!Main.getGameManager().isGameStarted()) {
            event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize(
                    "<green>The game has not started yet. You can start it by typing /game start"));
        } else {
            event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize(
                    "<green>The game has already started, you just automatically join. Good luck!"));
        }

        URI uri = URI.create("https://download.mc-packs.net/pack/c121153069e3daabd2d369e5b3a6ebdfe04bb3c9.zip");
        ResourcePackInfo pack = ResourcePackInfo.resourcePackInfo(resourcePackId, uri, "c121153069e3daabd2d369e5b3a6ebdfe04bb3c9");
        event.getPlayer().sendResourcePacks(ResourcePackRequest.resourcePackRequest().packs(pack));
        return Result.SUCCESS;
    }
}
