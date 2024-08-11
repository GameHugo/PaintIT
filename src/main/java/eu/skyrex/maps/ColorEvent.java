package eu.skyrex.maps;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerUseItemEvent;
import org.jetbrains.annotations.NotNull;

public class ColorEvent implements EventListener<PlayerUseItemEvent> {

    private final CanvasManager canvasManager;
    private final ColorPallete pallete;

    public ColorEvent(CanvasManager canvasManager, ColorPallete pallete) {
        this.canvasManager = canvasManager;
        this.pallete = pallete;
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

        if(intersection.x() < 1) return Result.INVALID;
        if(intersection.x() >= 17) return Result.INVALID;
        if(intersection.y() >= 43) return Result.INVALID;
        if(intersection.y() <= 41) return Result.INVALID;

        final int x = (int) ((intersection.x() - 1) * 128);
        final int y = (int) (256 - (intersection.y() - 40.5) * 128);

        pallete.use(new Pixel(x, y));

        return Result.SUCCESS;
    }
}
