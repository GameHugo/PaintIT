package eu.skyrex.commands;

import eu.skyrex.Main;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentColor;

import java.awt.*;

public class ColorCommand extends Command {
    public ColorCommand() {
        super("color");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage("Usage: /color <color>");
        });

        ArgumentColor color = ArgumentType.Color("color");

        addSyntax((sender, context) -> {
            final Style style = context.get("color");
            final TextColor color1 = style.color();
            Main.getCanvasManager().setCurrentColor(new Color(color1.red(), color1.green(), color1.blue()));
            sender.sendMessage("Color: " + context.get("color"));
        }, color);
    }
}
