package eu.skyrex.maps;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerUseItemEvent;
import org.jetbrains.annotations.NotNull;

public class PaintEvent implements EventListener<PlayerUseItemEvent> {

    private final CanvasManager canvasManager;

    public PaintEvent(CanvasManager canvasManager) {
        this.canvasManager = canvasManager;
    }

    @Override
    public @NotNull Class<PlayerUseItemEvent> eventType() {
        return PlayerUseItemEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerUseItemEvent event) {

        final Player player = event.getPlayer();
        if(!player.equals(canvasManager.getPainter())) return Result.INVALID;

        if(player.getPosition().z() <= 0) return Result.INVALID;

        final Vec direction = player.getPosition().direction();

        double factor = player.getPosition().z() / Math.abs(direction.z());

        final Vec intersection = player.getPosition().asVec().add(direction.mul(factor));

        if(intersection.x() < 0) return Result.INVALID;
        if(intersection.x() >= 16) return Result.INVALID;
        if(intersection.y() < 43) return Result.INVALID;
        if(intersection.y() > 54) return Result.INVALID;

        final int x = (int) (intersection.x() * 128);
        final int y = (int) (1152 - (intersection.y() - 43.5) * 128);

        canvasManager.use(new Pixel(x, y));

        return Result.SUCCESS;
    }
}
