package eu.skyrex;

import eu.skyrex.commands.ColorCommand;
import eu.skyrex.commands.StopCommand;
import eu.skyrex.commands.TestCommand;
import eu.skyrex.commands.ToolCommand;
import eu.skyrex.files.ServerProperties;
import eu.skyrex.game.GameCommand;
import eu.skyrex.game.GameManager;
import eu.skyrex.game.GameOnChat;
import eu.skyrex.maps.CanvasManager;
import eu.skyrex.maps.PaintEvent;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.ping.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    private static GameManager gameManager;
    private static CanvasManager canvasManager;

    public static void main(String[] args) throws IOException {
        Logger logger = LoggerFactory.getLogger(Main.class);

        final long startTime = System.currentTimeMillis();

        // Initialization
        MinecraftServer minecraftServer = MinecraftServer.init();
        ServerProperties serverProperties = new ServerProperties();
        gameManager = new GameManager();

        // Create the instance
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();

        // Set the ChunkGenerator
        instanceContainer.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));
        instanceContainer.setChunkSupplier(LightingChunk::new);

        canvasManager = new CanvasManager(instanceContainer);

        // Add an event callback to specify the spawning instance (and the spawn position)
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(0, 42, 0));
        });

        globalEventHandler.addListener(PlayerSpawnEvent.class, event -> {
            final Player player = event.getPlayer();
            player.setAllowFlying(true);
            player.setFlying(true);
            canvasManager.sendPackets(player);
            player.getInventory().setItemInMainHand(ItemStack.of(Material.STICK));
            canvasManager.setPainter(player);
        });

        globalEventHandler.addListener(ServerListPingEvent.class, event -> {
            ResponseData responseData = event.getResponseData();
            responseData.setMaxPlayer(69);
            responseData.setDescription(Component.text("PaintIT"));
        });

        globalEventHandler.addListener(new GameOnChat());
        globalEventHandler.addListener(new PaintEvent(canvasManager));

        MinecraftServer.setBrandName("PaintIT");

        MinecraftServer.getCommandManager().register(new StopCommand());
        MinecraftServer.getCommandManager().register(new TestCommand(instanceContainer));
        MinecraftServer.getCommandManager().register(new GameCommand());
        MinecraftServer.getCommandManager().register(new ColorCommand());
        MinecraftServer.getCommandManager().register(new ToolCommand());

        MojangAuth.init();

        // Start the server
        minecraftServer.start(serverProperties.getIp(), serverProperties.getPort());
        logger.info("Server started on {}:{} in {}ms", serverProperties.getIp(), serverProperties.getPort(), System.currentTimeMillis() - startTime);

        // Allow console input
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = reader.readLine()) != null) {
            CommandSender sender = new ConsoleSender();
            MinecraftServer.getCommandManager().execute(sender, line);
        }
    }

    public static GameManager getGameManager() {
        return gameManager;
    }

    public static CanvasManager getCanvasManager() {
        return canvasManager;
    }
}