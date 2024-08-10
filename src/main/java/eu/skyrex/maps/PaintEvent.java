package eu.skyrex.maps;

import eu.skyrex.Main;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerUseItemEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class PaintEvent implements EventListener<PlayerUseItemEvent> {
    @Override
    public @NotNull Class<PlayerUseItemEvent> eventType() {
        return PlayerUseItemEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerUseItemEvent event) {

        final Player player = event.getPlayer();

        if(player.getPosition().z() <= 0) return Result.INVALID;

        final Vec direction = player.getPosition().direction();

        double factor = player.getPosition().z() / Math.abs(direction.z());

        final Vec intersection = player.getPosition().asVec().add(direction.mul(factor));

        if(intersection.x() < 0) return Result.INVALID;
        if(intersection.x() >= 16) return Result.INVALID;
        if(intersection.y() < 43) return Result.INVALID;
        if(intersection.y() > 54) return Result.INVALID;

        final int x = (int) (intersection.x() * 128) - 64;
        final int y = (int) (1152 - (intersection.y() - 43) * 128);

        final Graphics2D graphics = Main.getCanvasManager().getGraphics();
        graphics.setColor(Color.RED);
        graphics.fillOval(x, y, 100, 100);
        Main.getCanvasManager().sendPackets();
        return Result.SUCCESS;
    }
}
