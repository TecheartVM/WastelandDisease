package techeart.thrad.capabilities.radcap;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RadiationCapProvider implements ICapabilitySerializable<CompoundTag>
{
    private final Capability<IRadiation> capability = RadiationCapability.RADIATION_CAPABILITY;
    private final RadiationCapability instance = new RadiationCapability();
    private final LazyOptional<IRadiation> lazyOptional = LazyOptional.of(()->instance);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
        if(cap == capability) return lazyOptional.cast();
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() { return instance.serializeNBT(); }

    @Override
    public void deserializeNBT(CompoundTag nbt) { instance.deserializeNBT(nbt); }
}
