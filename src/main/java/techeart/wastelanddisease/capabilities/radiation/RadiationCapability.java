package techeart.wastelanddisease.capabilities.radiation;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import techeart.wastelanddisease.config.Configuration;
import techeart.wastelanddisease.MainClass;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RadiationCapability implements IRadiation
{
    public static final ResourceLocation ID = new ResourceLocation(MainClass.MODID, "radiation_level");
    private static final String NBT_KEY = MainClass.MODID + ".radiation";

    public static Capability<IRadiation> RADIATION_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

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

    public static class Provider implements ICapabilitySerializable<CompoundTag>
    {
        private RadiationCapability capability = null;
        private final LazyOptional<IRadiation> lazyOptional = LazyOptional.of(this::getOrCreateCapability);

        private RadiationCapability getOrCreateCapability()
        {
            if(capability == null)
                capability = new RadiationCapability();
            return capability;
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
        {
            if(cap == RADIATION_CAPABILITY)
                return lazyOptional.cast();
            return LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT()
        {
            return getOrCreateCapability().serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) { getOrCreateCapability().deserializeNBT(nbt); }
    }
}
