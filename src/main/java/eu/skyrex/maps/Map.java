package eu.skyrex.maps;

import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.map.framebuffers.Graphics2DFramebuffer;
import net.minestom.server.network.packet.server.play.MapDataPacket;

import java.awt.*;

public class Map {

    private final ItemStack mapItem;
    private final MapDataPacket packet;

    public Map() {
        this.mapItem = ItemStack.builder(Material.FILLED_MAP)
                .set(ItemComponent.MAP_ID, 1)
                .set(ItemComponent.MAP_COLOR, TextColor.color(0, 255, 0))
                .build();

        final Graphics2DFramebuffer buf = new Graphics2DFramebuffer();
        final Graphics2D graphics2D = buf.getRenderer();
        graphics2D.setColor(new Color(0, 255, 0));
        graphics2D.drawRect(0, 0, 128, 128);

        this.packet = buf.preparePacket(1);
    }

    public ItemStack getMapItem() {
        return mapItem;
    }

    public MapDataPacket getPacket() {
        return packet;
    }

    public static Map create() {
        return new Map();
    }

}
