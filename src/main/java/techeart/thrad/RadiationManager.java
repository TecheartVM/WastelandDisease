package techeart.thrad;

import net.minecraft.world.entity.player.Player;

public class RadiationManager
{
    public static int getRadLevel(Player player) { return (int) player.getAttributeValue(RegistryHandler.RADIATION.get()); }

    public static void setRadLevel(Player player, int value)
    {
        if(!player.getAttributes().hasAttribute(RegistryHandler.RADIATION.get())) return;

        int v = Math.min(value, Configuration.MAX_RAD_LEVEL);
        v = Math.max(v, Configuration.MIN_RAD_LEVEL);
        player.getAttribute(RegistryHandler.RADIATION.get()).setBaseValue(v);
    }
}
