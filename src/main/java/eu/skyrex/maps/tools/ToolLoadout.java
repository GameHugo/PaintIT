package eu.skyrex.maps.tools;

import eu.skyrex.Main;
import eu.skyrex.maps.PaintTool;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.DyedItemColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ToolLoadout implements EventListener<PlayerChangeHeldSlotEvent> {

    public static final ItemStack BRUSH = ItemStack.builder(Material.LEATHER_HORSE_ARMOR)
            .customModelData(1)
            .set(ItemComponent.ITEM_NAME, Component.text("Brush"))
            .build();

    public static final ItemStack LINE = ItemStack.builder(Material.LEATHER_HORSE_ARMOR)
            .customModelData(2)
            .set(ItemComponent.ITEM_NAME, Component.text("Line"))
            .build();

    public static final ItemStack RECT = ItemStack.builder(Material.LEATHER_HORSE_ARMOR)
            .customModelData(3)
            .set(ItemComponent.ITEM_NAME, Component.text("Rectangle"))
            .build();

    public static final ItemStack ELLIPSE = ItemStack.builder(Material.LEATHER_HORSE_ARMOR)
            .customModelData(4)
            .set(ItemComponent.ITEM_NAME, Component.text("Ellipse"))
            .build();

    public static final ItemStack UNDO = ItemStack.builder(Material.LEATHER_HORSE_ARMOR)
            .customModelData(5)
            .set(ItemComponent.ITEM_NAME, Component.text("Undo"))
            .build();

    public static final ItemStack REDO = ItemStack.builder(Material.LEATHER_HORSE_ARMOR)
            .customModelData(6)
            .set(ItemComponent.ITEM_NAME, Component.text("Redo"))
            .build();

    public static void add(Player player) {
        player.getInventory().addItemStack(BRUSH);
        player.getInventory().addItemStack(LINE);
        player.getInventory().addItemStack(RECT);
        player.getInventory().addItemStack(ELLIPSE);
        player.getInventory().addItemStack(UNDO);
        player.getInventory().addItemStack(REDO);

    }

    public static void recolor(Player player, Color color) {
        player.getInventory().clear();
        player.getInventory().addItemStack(BRUSH.with(ItemComponent.DYED_COLOR, new DyedItemColor(TextColor.color(color.getRGB()))));
        player.getInventory().addItemStack(LINE.with(ItemComponent.DYED_COLOR, new DyedItemColor(TextColor.color(color.getRGB()))));
        player.getInventory().addItemStack(RECT.with(ItemComponent.DYED_COLOR, new DyedItemColor(TextColor.color(color.getRGB()))));
        player.getInventory().addItemStack(ELLIPSE.with(ItemComponent.DYED_COLOR, new DyedItemColor(TextColor.color(color.getRGB()))));
        player.getInventory().addItemStack(UNDO.with(ItemComponent.DYED_COLOR, new DyedItemColor(TextColor.color(color.getRGB()))));
        player.getInventory().addItemStack(REDO.with(ItemComponent.DYED_COLOR, new DyedItemColor(TextColor.color(color.getRGB()))));
    }

    @Override
    public @NotNull Class<PlayerChangeHeldSlotEvent> eventType() {
        return PlayerChangeHeldSlotEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerChangeHeldSlotEvent event) {

        if(!event.getPlayer().equals(Main.getCanvasManager().getPainter())) return Result.SUCCESS;

        ItemStack itemStack = event.getPlayer().getInventory().getItemStack(event.getSlot());
        if (itemStack == null) return Result.SUCCESS;

        Integer customModelData = itemStack.get(ItemComponent.CUSTOM_MODEL_DATA);
        if(customModelData == null) return Result.SUCCESS;

        final PaintTool tool = switch (customModelData) {
                case 1:
                    yield new BrushTool();
                case 2:
                    yield new LineTool();
                case 3:
                    yield new RectTool();
                case 4:
                    yield new EllipseTool();
                case 5:
                    yield new UndoTool();
                case 6:
                    yield new RedoTool();
            default:
                    yield null;
            };

        if(tool == null) return Result.SUCCESS;

        Main.getCanvasManager().setTool(tool);

        return Result.SUCCESS;
    }


}
