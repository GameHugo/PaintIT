package eu.skyrex;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.ExecutionType;
import net.minestom.server.timer.TaskSchedule;

public class PerformanceBar {

    private final BossBar bossBar = BossBar.bossBar(Component.text("hoi"), 1f, BossBar.Color.GREEN, BossBar.Overlay.PROGRESS);

    public PerformanceBar() {
        MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            bossBar.name(Component.text(
                    "Memory: " + getMemoryUsage()
            ));
            for (Player player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                player.showBossBar(bossBar);
            }
        }, TaskSchedule.nextTick(), TaskSchedule.nextTick(), ExecutionType.TICK_END);
    }

    public String getMemoryUsage() {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024 + "MB";
    }

}
