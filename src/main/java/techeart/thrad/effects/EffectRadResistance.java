package techeart.thrad.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class EffectRadResistance extends MobEffect
{
    public EffectRadResistance() { super(MobEffectCategory.BENEFICIAL, 0xfacebe); }

    @Override
    public void applyEffectTick(LivingEntity p_19467_, int p_19468_) { }

    @Override
    public boolean isDurationEffectTick(int p_19455_, int p_19456_) { return true; }

    @Override
    public boolean isInstantenous() { return false; }
}
