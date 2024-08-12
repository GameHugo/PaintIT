package eu.skyrex;

import eu.skyrex.commands.*;
import eu.skyrex.files.ServerProperties;
import eu.skyrex.game.*;
import eu.skyrex.maps.CanvasManager;
import eu.skyrex.maps.ColorEvent;
import eu.skyrex.maps.ColorPallete;
import eu.skyrex.maps.PaintEvent;
import eu.skyrex.maps.tools.ToolLoadout;
import eu.skyrex.util.Resources;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
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

        instanceContainer.setTimeRate(0);
        // Set the ChunkGenerator
        instanceContainer.setChunkLoader(new AnvilLoader(String.valueOf(Resources.getFolderFromZipResource("/world.zip"))));
        //instanceContainer.setGenerator(unit -> unit.modifier().fillHeight(0, 1, Block.AIR));
        instanceContainer.setChunkSupplier(LightingChunk::new);

        canvasManager = new CanvasManager(instanceContainer);
        final ColorPallete pallete = new ColorPallete(canvasManager, instanceContainer);

        // Add an event callback to specify the spawning instance (and the spawn position)
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(10, 45, 5, 180, 0));
        });

        globalEventHandler.addListener(PlayerSpawnEvent.class, event -> {
            final Player player = event.getPlayer();
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlying(true);
            player.setFlying(true);
            canvasManager.sendPackets(player);
            pallete.sendPackets(player);
        });

        globalEventHandler.addListener(ServerListPingEvent.class, event -> {
            ResponseData responseData = event.getResponseData();
            responseData.setMaxPlayer(69);
            responseData.setDescription(Component.text("PaintIT"));
        });

        globalEventHandler.addListener(new GameOnChat());
        globalEventHandler.addListener(new GameOnJoin());
        globalEventHandler.addListener(new GameOnLeave());
        globalEventHandler.addListener(new PaintEvent(canvasManager));
        globalEventHandler.addListener(new ToolLoadout());
        globalEventHandler.addListener(new ColorEvent(canvasManager, pallete));

        MinecraftServer.setBrandName("PaintIT");

        MinecraftServer.getCommandManager().register(new StopCommand());
        MinecraftServer.getCommandManager().register(new TestCommand(instanceContainer));
        MinecraftServer.getCommandManager().register(new GameCommand());
        MinecraftServer.getCommandManager().register(new ColorCommand());
        MinecraftServer.getCommandManager().register(new ToolCommand());
        MinecraftServer.getCommandManager().register(new HeapCommand());

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