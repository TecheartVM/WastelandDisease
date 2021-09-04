package techeart.thrad.capabilities.radcap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import techeart.thrad.config.Configuration;
import techeart.thrad.MainClass;

public class RadiationCapability implements IRadiation
{
    public static final ResourceLocation ID = new ResourceLocation(MainClass.MODID, "radiation_level");
    private static final String NBT_KEY = "thrad.rad";

    @CapabilityInject(IRadiation.class)
    public static Capability<IRadiation> RADIATION_CAPABILITY = null;
    public static void register() { CapabilityManager.INSTANCE.register(IRadiation.class); }

    private int radLevel;

    public RadiationCapability() { this(Configuration.defaultRadLevel.get()); }
    public RadiationCapability(int radLevel) { this.radLevel = radLevel; }

    @Override
    public int getRadLevel() { return radLevel; }

    @Override
    public void setRadLevel(int value) { radLevel = value; }

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(NBT_KEY, getRadLevel());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        setRadLevel(nbt.getInt(NBT_KEY));
    }
}
