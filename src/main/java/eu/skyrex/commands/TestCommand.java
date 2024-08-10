package eu.skyrex.commands;

import eu.skyrex.Main;
import net.minestom.server.command.builder.Command;
import net.minestom.server.instance.InstanceContainer;

import java.awt.*;
import java.util.Random;

public class TestCommand extends Command {
    public TestCommand(InstanceContainer manager) {
        super("test");

        setDefaultExecutor((sender, context) -> {
            Main.getCanvasManager().clearCanvas();
        });

        Random random = new Random();

        addSyntax((sender, context) -> {

            final Graphics2D graphics = Main.getCanvasManager().getGraphics();
            graphics.setColor(Color.RED);
            graphics.fillOval(random.nextInt(1920), random.nextInt(1080), 100, 100);
            Main.getCanvasManager().sendPackets();

        });

    }
}
