package techeart.wastelanddisease.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import techeart.wastelanddisease.MainClass;
import techeart.wastelanddisease.network.packets.PacketSyncRadCap;

import java.util.List;

public class PacketHandler
{
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(MainClass.MODID, "main_channel"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

    private static int lastId = 0;

    public static void register()
    {
        CHANNEL.registerMessage(lastId++, PacketSyncRadCap.class, PacketSyncRadCap::encode, PacketSyncRadCap::decode, PacketSyncRadCap::handle);
    }

    public static void sendToClient(Object msg, ServerPlayer player)
    {
        if(!(player instanceof FakePlayer))
            CHANNEL.sendTo(msg, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToServer(Object msg) { CHANNEL.sendToServer(msg); }

    public static void sendToAll(Object msg, MinecraftServer server)
    {
        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        for (ServerPlayer player : players)
            sendToClient(msg, player);
    }
}
