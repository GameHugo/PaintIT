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
import net.minestom.server.map.MapColors;
import net.minestom.server.map.framebuffers.LargeGraphics2DFramebuffer;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.packet.server.play.MapDataPacket;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ColorPallete {

    private final int width = 16;
    private final int height = 2;

    private final CanvasManager canvasManager;
    private final Instance instance;
    private final LargeGraphics2DFramebuffer buf = new LargeGraphics2DFramebuffer(128 * width, 128 * height);
    private final Color[] colors = List.of(MapColors.COLOR_RED,
            MapColors.COLOR_ORANGE,
            MapColors.COLOR_YELLOW,
            MapColors.COLOR_LIGHT_GREEN,
            MapColors.COLOR_GREEN,
            MapColors.COLOR_CYAN,
            MapColors.COLOR_BLUE,
            MapColors.COLOR_LIGHT_BLUE,
            MapColors.COLOR_PINK,
            MapColors.COLOR_MAGENTA,
            MapColors.COLOR_PURPLE,
            MapColors.COLOR_BROWN,
            MapColors.COLOR_BLACK,
            MapColors.COLOR_GRAY,
            MapColors.COLOR_LIGHT_GRAY,
            MapColors.SNOW
            ).stream().map(c -> new Color(c.red(), c.green(), c.blue())).toArray(Color[]::new);

    public ColorPallete(CanvasManager canvasManager, final Instance instance) {
        this.instance = instance;
        this.canvasManager = canvasManager;
        setup();
    }

    public void setup() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Entity itemFrame = new Entity(EntityType.ITEM_FRAME);
                final Pos pos = new Pos(x + 1, 42 + y, 0);

                itemFrame.setInstance(instance, pos);

                int mapId = 1000 + width * (height - 1 - y) + x + 1;

                final ItemStack mapItem = ItemStack.builder(Material.FILLED_MAP)
                        .set(ItemComponent.MAP_ID, mapId)
                        .build();

                itemFrame.editEntityMeta(ItemFrameMeta.class, meta -> {


                    meta.setItem(mapItem);
                });
            }
        }

        final Graphics2D graphics2D = buf.getRenderer();

        for(int i = 0; i < colors.length && i < 32; i++) {
            graphics2D.setColor(colors[i]);
            int mod = i > 16 ? 128 : 0;
            graphics2D.fillRect(i * 128 - mod * 16, mod, 128, 128);
        }

    }

    public void sendPackets() {
        Set<SendablePacket> packets = new HashSet<>();
        for (int x = 0; x < width; x++) {
            for (int y = height - 1; y >= 0; y--) {
                final int mapId = 1000 + width * y + x + 1;
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
                final int mapId = 1000 + width * y + x + 1;
                final MapDataPacket packet = buf.preparePacket(mapId, x * 128, y * 128);
                packets.add(packet);
            }
        }
        player.sendPackets(packets);
    }


    public void use(Pixel pixel) {
        final int x = pixel.x() / 128;
        final int y = pixel.y() / 128;

        final int colorIndex = (y * 16 + x);

        if(colorIndex >= colors.length) return;

        canvasManager.setCurrentColor(colors[colorIndex]);
    }
}
