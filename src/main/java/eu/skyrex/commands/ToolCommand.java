package eu.skyrex.commands;

import eu.skyrex.Main;
import eu.skyrex.maps.tools.Tool;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;

public class ToolCommand extends Command {
    public ToolCommand() {
        super("tool");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage("Usage: /tool <tool>");
        });

        ArgumentEnum<Tool> toolArgumentEnum = ArgumentType.Enum("tool", Tool.class);
        addSyntax((sender, context) -> {
            Tool tool = context.get(toolArgumentEnum);
            Main.getCanvasManager().setTool(tool.create());
            sender.sendMessage("Selected tool: " + tool.name());
        }, toolArgumentEnum);

    }
}
