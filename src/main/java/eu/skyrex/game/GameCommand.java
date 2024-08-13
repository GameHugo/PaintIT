package eu.skyrex.game;

import eu.skyrex.Main;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentLiteral;
import net.minestom.server.command.builder.arguments.ArgumentType;

public class GameCommand extends Command {

    public GameCommand() {
        super("game");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage("Game command");
        });

        ArgumentLiteral startArg = ArgumentType.Literal("start");

        addSyntax((sender, context) -> {
            final String start = context.get(startArg);
            if (start.equals("start")) {
                Main.getGameManager().startGame();
            } else {
                sender.sendMessage("Unknown argument");
            }
        }, startArg);
        addSyntax((sender, context) -> {
            Main.getGameManager().stopGame();
        }, ArgumentType.Literal("stop"));
    }
}
