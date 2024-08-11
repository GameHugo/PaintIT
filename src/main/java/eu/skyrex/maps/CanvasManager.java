package eu.skyrex.maps;

import eu.skyrex.maps.tools.BrushTool;
import eu.skyrex.maps.tools.ToolLoadout;
import eu.skyrex.util.Stack;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.ItemFrameMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.map.framebuffers.LargeGraphics2DFramebuffer;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.packet.server.play.MapDataPacket;
import net.minestom.server.timer.ExecutionType;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

public class CanvasManager {

    private final int width = 16;
    private final int height = 9;
    public static final Logger logger = LoggerFactory.getLogger(CanvasManager.class);

    private Color currentColor = Color.BLACK;

    private WeakReference<Player> player = new WeakReference<>(null);
    private boolean drawn = false;
    private Stack<BufferedImage> renderedImage;
    private PaintTool tool = new BrushTool();
    private int strokeWidth = 1;

    private final Instance instance;
    private final LargeGraphics2DFramebuffer buf = new LargeGraphics2DFramebuffer(128 * width, 128 * height);

    public CanvasManager(final Instance instance) {
        this.instance = instance;
        setupCanvas();
    }

    public void clearCanvas() {
        getGraphics().setColor(Color.WHITE);
        getGraphics().fillRect(0, 0, 128 * width, 128 * height);
        sendPackets();

        renderedImage = null;

        snapshotImage(true);
    }

    public void setupCanvas() {

        new HashSet<>(instance.getEntities()).forEach(e -> {
            if(e.getEntityMeta() instanceof ItemFrameMeta) e.remove();
        });

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Entity itemFrame = new Entity(EntityType.ITEM_FRAME);
                final Pos pos = new Pos(x + 1, 45 + y, 0);

                itemFrame.setInstance(instance, pos);

                int mapId = width * (height - 1 - y) + x + 1;

                final ItemStack mapItem = ItemStack.builder(Material.FILLED_MAP)
                        .set(ItemComponent.MAP_ID, mapId)
                        .build();

                itemFrame.editEntityMeta(ItemFrameMeta.class, meta -> {


                    meta.setItem(mapItem);
                });
            }
        }

        getGraphics().setColor(Color.WHITE);
        getGraphics().fillRect(0, 0, 128 * width, 128 * height);

        snapshotImage(true);

        MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            if (!drawn) return;
            sendPackets();
            drawn = false;
            buf.getRenderer().drawImage(renderedImage.getValue(), 0, 0, null);
        }, TaskSchedule.nextTick(), TaskSchedule.nextTick(), ExecutionType.TICK_END);

        MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            final Pixel target = getTargetPosition();
            if (target == null) {
                return;
            }
            tool.tick(this, target);
        }, TaskSchedule.nextTick(), TaskSchedule.tick(10), ExecutionType.TICK_START);
    }

    public Graphics2D getGraphics() {
        drawn = true;
        return buf.getRenderer();
    }

    public void sendPackets() {
        Set<SendablePacket> packets = new HashSet<>();
        for (int x = 0; x < width; x++) {
            for (int y = height - 1; y >= 0; y--) {
                final int mapId = width * y + x + 1;
                final MapDataPacket packet = buf.preparePacket(mapId, x * 128, y * 128);
                packets.add(packet);
            }
        }
        for (@NotNull Player player : instance.getPlayers()) {
            player.sendPackets(packets);
        }
    }

    public void sendPackets(Player player) {
        Set<SendablePacket> packets = new HashSet<>();
        for (int x = 0; x < width; x++) {
            for (int y = height - 1; y >= 0; y--) {
                final int mapId = width * y + x + 1;
                final MapDataPacket packet = buf.preparePacket(mapId, x * 128, y * 128);
                packets.add(packet);
            }
        }
        player.sendPackets(packets);
    }

    private Pixel getTargetPosition() {
        final Player player = this.player.get();
        if (player == null) return null;

        final Pos pos = player.getPosition();
        final Vec direction = pos.direction();
        final double factor = pos.z() / Math.abs(direction.z());
        final Pos intersection = pos.add(direction.mul(factor));

        if (intersection.x() < 1) return null;
        if (intersection.x() >= width + 1) return null;
        if (intersection.y() < 43) return null;
        if (intersection.y() > 54) return null;

        final int x = (int) ((intersection.x() - 1) * 128);
        final int y = (int) (1152 - (intersection.y() - 43.5) * 128);

        return new Pixel(x, y);
    }

    public Player getPainter() {
        return player.get();
    }

    public void setPainter(Player painter) {
        this.player = new WeakReference<>(painter);
        if(painter != null) ToolLoadout.add(painter);
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(Color currentColor) {
        this.currentColor = currentColor;
    }

    public void setTool(PaintTool tool) {
        this.tool = tool;
    }

    public void use(Pixel pos) {
        snapshotImage(tool.onUse(this, pos));
    }

    public Stack<BufferedImage> getChangeStack() {
        return renderedImage;
    }

    private void snapshotImage(boolean createNew) {
        if(renderedImage == null) {
            renderedImage = new Stack<>(new BufferedImage(width * 128, height * 128, BufferedImage.TYPE_INT_RGB));
        } else if(createNew) {
            renderedImage = renderedImage.append(new BufferedImage(width * 128, height * 128, BufferedImage.TYPE_INT_RGB));
        } else {
            renderedImage.setValue(new BufferedImage(width * 128, height * 128, BufferedImage.TYPE_INT_RGB));
        }
        final Graphics2D graphics = renderedImage.getValue().createGraphics();
        graphics.drawImage(buf.getBackingImage(), 0, 0, null);
        graphics.dispose();
    }

    public boolean undo() {
        if(renderedImage.getParent() == null) return false;
        renderedImage = renderedImage.getParent();
        getGraphics().drawImage(renderedImage.getValue(), 0, 0, null);
        return true;
    }

    public boolean redo() {
        if(renderedImage.getChild() == null) return false;
        renderedImage = renderedImage.getChild();
        getGraphics().drawImage(renderedImage.getValue(), 0, 0, null);
        return true;
    }
}
