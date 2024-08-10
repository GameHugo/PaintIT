package eu.skyrex.maps;

import net.minestom.server.coordinate.Pos;
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
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class CanvasManager {

    private final int width = 16;
    private final int height = 9;
    public static final Logger logger = LoggerFactory.getLogger(CanvasManager.class);


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
    }

    public void setupCanvas() {
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                Entity itemFrame = new Entity(EntityType.ITEM_FRAME);
                final Pos pos = new Pos(x, 45 + y, 0);

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
    }

    public Graphics2D getGraphics() {
        return buf.getRenderer();
    }

    public void sendPackets() {
        Set<SendablePacket> packets = new HashSet<>();
        for(int x = 0; x < width; x++) {
            for(int y = height - 1; y >= 0; y--) {
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
        for(int x = 0; x < width; x++) {
            for(int y = height - 1; y >= 0; y--) {
                final int mapId = width * y + x + 1;
                final MapDataPacket packet = buf.preparePacket(mapId, x * 128, y * 128);
                packets.add(packet);
            }
        }
        player.sendPackets(packets);
    }

}
