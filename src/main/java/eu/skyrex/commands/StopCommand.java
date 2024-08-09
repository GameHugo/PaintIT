package eu.skyrex.commands;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

public class StopCommand extends Command {

    public StopCommand() {
        super("stop");

        //setCondition((sender, commandString) -> sender.hasPermission("server.stop") || sender instanceof ConsoleSender);

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage("Stopping the server...");
            for (@NotNull Instance instance : MinecraftServer.getInstanceManager().getInstances()) {
                for (@NotNull Player player : instance.getPlayers()) {
                    player.kick("Server is restarting");
                }
            }

            MinecraftServer.stopCleanly();
            System.exit(0);
        });
    }
}
