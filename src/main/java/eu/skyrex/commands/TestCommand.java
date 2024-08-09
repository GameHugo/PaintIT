package eu.skyrex.commands;

import eu.skyrex.maps.Map;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.ItemFrameMeta;
import net.minestom.server.instance.InstanceContainer;

public class TestCommand extends Command {
    public TestCommand(InstanceContainer manager) {
        super("test");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage("Hello, World!");
        });

        ArgumentInteger widthArg = ArgumentType.Integer("width");
        ArgumentInteger heightArg = ArgumentType.Integer("height");

        addSyntax((sender, context) -> {
            final int width = context.get(widthArg);
            final int height = context.get(heightArg);

            final Player player = (Player) sender;

            final Map map = new Map();

            player.sendPacket(map.getPacket());

            for(int x = player.getPosition().blockX(); x < player.getPosition().blockX() + width; x++) {
                for(int y = player.getPosition().blockY(); y < player.getPosition().blockY() + height; y++) {
                    Entity itemFrame = new Entity(EntityType.ITEM_FRAME);
                    final Pos pos = new Pos(x, y, player.getPosition().blockZ());

                    itemFrame.setInstance(player.getInstance(), pos);

                    itemFrame.editEntityMeta(ItemFrameMeta.class, meta -> {
                        meta.setItem(map.getMapItem());
                    });
                }
            }
            sender.sendMessage("Width: " + width + ", Height: " + height);
        }, widthArg, heightArg);

    }
}
