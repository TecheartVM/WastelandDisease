package techeart.thrad.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import techeart.thrad.config.Configuration;
import techeart.thrad.utils.RegistryHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EffectCellsDestruction extends MobEffect
{
    private static Random random = new Random();

    public EffectCellsDestruction() { super(MobEffectCategory.HARMFUL, 0x803c00); }

    @Override
    public void applyEffectTick(@Nonnull LivingEntity entity, int amplifier)
    {
        if(entity instanceof Player && ((Player)entity).isCreative()) return;
        boolean flag = false;
        if(random.nextInt(10000) < (amplifier + 1) * Configuration.cdBaseHurtChance.get())
        {
            applyAdditionalEffects(entity, amplifier);
            flag = true;
        }
        float damage = entity.level.getDifficulty().getId();
        if(damage > 0 && (amplifier > 2 || (flag && amplifier == 2)))
            entity.hurt(RegistryHandler.DAMAGE_SOURCE_RADIATION, damage);
    }

    protected void applyAdditionalEffects(LivingEntity entity, int cdAmp)
    {
        int duration = Configuration.cdSubeffectsDuration.get();
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
