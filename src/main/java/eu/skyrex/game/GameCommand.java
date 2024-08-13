package eu.skyrex.game;

import eu.skyrex.Main;
import net.minestom.server.command.ConsoleSender;
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
            if(sender.hasPermission("game.stop") || sender instanceof ConsoleSender) {
                Main.getGameManager().stopGame();
            } else {
                sender.sendMessage("You don't have permission to stop the game");
            }
        }, ArgumentType.Literal("stop"));
    }
}
