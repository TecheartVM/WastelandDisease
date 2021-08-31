package techeart.thrad.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import techeart.thrad.utils.RadiationManager;

import java.util.function.Supplier;

public class PacketSyncRadCap
{
    private final int radLevel;

    public PacketSyncRadCap(int radLevel) { this.radLevel = radLevel; }

    public static void encode(PacketSyncRadCap msg, FriendlyByteBuf buf) { buf.writeInt(msg.radLevel); }

    public static PacketSyncRadCap decode(FriendlyByteBuf buf) { return new PacketSyncRadCap(buf.readInt()); }

    public static void handle(PacketSyncRadCap msg, Supplier<NetworkEvent.Context> ctx)
    {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        ctx.get().enqueueWork(() -> RadiationManager.setRadLevel(player, msg.radLevel));
        ctx.get().setPacketHandled(true);
    }
}
