package eu.skyrex.commands;

import eu.skyrex.util.HeapDump;
import net.minestom.server.command.builder.Command;

import java.io.IOException;

public class HeapCommand extends Command {
    public HeapCommand() {
        super("heap");

        setDefaultExecutor((sender, context) -> {
            try {
                HeapDump.dumpHeap("live", true);
                HeapDump.dumpHeap("dead", false);
                sender.sendMessage("Heap dump created");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
