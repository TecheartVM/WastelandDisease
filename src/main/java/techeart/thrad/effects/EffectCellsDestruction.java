package techeart.thrad.effects;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import techeart.thrad.config.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EffectCellsDestruction extends MobEffect
{
    public static final DamageSource DAMAGE_SOURCE_RADIATION = (new DamageSource("cells_destruction")).bypassArmor();

    private static Random random = new Random();

    public EffectCellsDestruction() { super(MobEffectCategory.HARMFUL, 0x803c00); }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier)
    {
        if(entity instanceof Player && ((Player)entity).isCreative()) return;

        float damage = entity.level.getDifficulty().getId();
        if(random.nextInt((int) (100 - Math.pow(4, amplifier))) == 0)
        {
            applyAdditionalEffects(entity, amplifier);
            if(damage > 0 && amplifier == 2) entity.hurt(DAMAGE_SOURCE_RADIATION, damage);
        }
        if(damage > 0 && amplifier == 3) entity.hurt(DAMAGE_SOURCE_RADIATION, damage);
    }

    protected void applyAdditionalEffects(LivingEntity entity, int cdAmp)
    {
        int difficulty = entity.level.getDifficulty().getId();
        int duration = (600 * Math.max(1, difficulty)) >> Math.min(cdAmp, 2);
        int amplifier = Math.max(cdAmp - 1, 0);
        if(Configuration.allowWeakness.get())
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, amplifier, false, false, false));
        if(Configuration.allowMiningFatigue.get())
            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, duration, amplifier, false, false, false));

        if(cdAmp == 1)
        {
            if(Configuration.allowNausea.get())
                entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, duration, 0, false, false, false));
        }
        else if(cdAmp == 2)
        {
            if(Configuration.allowNausea.get())
                entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, duration, 1, false, false, false));
            if(Configuration.allowBlindness.get())
                entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, duration, 0, false, false, false));
        }
        else if(cdAmp > 2)
        {
            if(Configuration.allowNausea.get())
                entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, duration, 1, false, false, false));
            if(Configuration.allowBlindness.get())
                entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, duration, 1, false, false, false));
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier)
    {
        int i = Configuration.cdTickDelay.get() >> amplifier;
        return i <= 0 || duration % i == i - 1;
    }

    @Override
    public List<ItemStack> getCurativeItems() { return new ArrayList<>(); }

    @Override
    public boolean isInstantenous() { return false; }
}
