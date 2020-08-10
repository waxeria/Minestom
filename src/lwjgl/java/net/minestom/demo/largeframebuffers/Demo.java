package net.minestom.demo.largeframebuffers;

import fr.themode.demo.MainDemo;
import net.minestom.server.MinecraftServer;
import net.minestom.server.chat.ColoredText;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.type.decoration.EntityItemFrame;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.MapMeta;
import net.minestom.server.map.Framebuffer;
import net.minestom.server.map.LargeFramebuffer;
import net.minestom.server.map.MapColors;
import net.minestom.server.map.framebuffers.LargeDirectFramebuffer;
import net.minestom.server.map.framebuffers.LargeGLFWFramebuffer;
import net.minestom.server.map.framebuffers.LargeGraphics2DFramebuffer;
import net.minestom.server.network.packet.server.play.MapDataPacket;
import net.minestom.server.utils.Position;
import net.minestom.server.utils.time.TimeUnit;

import java.awt.*;
import java.util.Arrays;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11.*;

public class Demo {

    public static void main(String[] args) {
        MainDemo.main(args); // used to avoid code duplication
        initDemo();
    }

    private static void initDemo() {
        MinecraftServer.getConnectionManager().addPlayerInitialization(player -> {
            player.setGameMode(GameMode.CREATIVE);
        });

        InstanceManager instances = MinecraftServer.getInstanceManager();
        Instance instance = instances.getInstances().stream().findAny().get();

        LargeDirectFramebuffer directFramebuffer = new LargeDirectFramebuffer(512, 512);
        LargeGraphics2DFramebuffer graphics2DFramebuffer = new LargeGraphics2DFramebuffer(512, 512);
        LargeGLFWFramebuffer glfwFramebuffer = new LargeGLFWFramebuffer(512, 512);

        renderingLoop(0, directFramebuffer, Demo::directRendering);
        renderingLoop(101, graphics2DFramebuffer, Demo::graphics2DRendering);
        renderingLoop(201, glfwFramebuffer, f -> {});

        glfwFramebuffer.setupRenderLoop(30, TimeUnit.MILLISECOND, Demo::openGLRendering);

        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                instance.loadChunk(x, z);
            }
        }
        setupMaps(instance, 0, 10);
        setupMaps(instance, 101, 20);
        setupMaps(instance, 201, 30);
    }

    private static void createFrame(Instance instance, int id, int x, int y, int z) {
        EntityItemFrame itemFrame = new EntityItemFrame(new Position(x, y, z), EntityItemFrame.ItemFrameOrientation.NORTH);
        itemFrame.getPosition().setYaw(180f);
        ItemStack map = new ItemStack(Material.FILLED_MAP, (byte)1);
        map.setItemMeta(new MapMeta(id));
        itemFrame.setItemStack(map);
        itemFrame.setInstance(instance);
        itemFrame.setCustomNameVisible(true);
        itemFrame.setCustomName(ColoredText.of("MapID: "+id));
    }

    private static void setupMaps(Instance instance, int mapIDStart, int zCoordinate) {
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                createFrame(instance, mapIDStart+y*4+x, 2-x, 45-y, zCoordinate);
            }
        }
    }

    private static <T extends LargeFramebuffer> void renderingLoop(int mapIDStart, T framebuffer, Consumer<T> renderingCode) {
        final Framebuffer[] subviews = new Framebuffer[4*4];
        for (int i = 0; i < subviews.length; i++) {
            int x = (i % 4)*128;
            int y = (i / 4)*128;
            subviews[i] = framebuffer.createSubView(x, y);
        }
        MinecraftServer.getSchedulerManager().buildTask(() -> {
            renderingCode.accept(framebuffer);
            for (int i = 0; i < subviews.length; i++) {
                final MapDataPacket packet = new MapDataPacket();
                packet.mapId = mapIDStart + i;

                Framebuffer f = subviews[i];
                f.preparePacket(packet);
                sendPacket(packet);
            }
        }).repeat(30, TimeUnit.MILLISECOND).schedule();
    }

    private static void sendPacket(MapDataPacket packet) {
        MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(p -> p.getPlayerConnection().sendPacket(packet));
    }

    private static void directRendering(LargeDirectFramebuffer framebuffer) {
        Arrays.fill(framebuffer.getColors(), 0, 512*40+128, MapColors.COLOR_CYAN.baseColor());
        Arrays.fill(framebuffer.getColors(), 512*40+128, framebuffer.getColors().length, MapColors.COLOR_RED.baseColor());
    }

    private static void graphics2DRendering(LargeGraphics2DFramebuffer framebuffer) {
        Graphics2D renderer = framebuffer.getRenderer();
        renderer.setColor(Color.BLACK);
        renderer.clearRect(0,0,512,512);
        renderer.setColor(Color.WHITE);
        renderer.drawString("Here's a very very long string that needs multiple maps to fit", 0, 100);
    }

    private static void openGLRendering() {
        glClearColor(0f, 0f, 0f, 1f);
        glClear(GL_COLOR_BUFFER_BIT);

        glBegin(GL_TRIANGLES);

        glVertex2f(0, -0.75f);
        glColor3f(1f, 0f, 0f);

        glVertex2f(0.75f, 0.75f);
        glColor3f(0f, 1f, 0f);

        glVertex2f(-0.75f, 0.75f);
        glColor3f(0f, 0f, 1f);

        glEnd();
    }
}
