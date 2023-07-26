package techeart.wastelanddisease.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class EffectRadResistance extends MobEffect
{
    public EffectRadResistance() { super(MobEffectCategory.BENEFICIAL, 0xfacebe); }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int p_19468_) { }

    @Override
    public boolean isDurationEffectTick(int p_19455_, int p_19456_) { return true; }
}
