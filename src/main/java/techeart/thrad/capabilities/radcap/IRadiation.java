package techeart.thrad.capabilities.radcap;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IRadiation extends INBTSerializable<CompoundTag>
{
    int getRadLevel();
    void setRadLevel(int value);
}
