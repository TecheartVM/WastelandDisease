package techeart.thrad.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import techeart.thrad.config.Configuration;
import techeart.thrad.utils.RadiationManager;

import java.util.ArrayList;
import java.util.List;

public class EffectExposure extends MobEffect
{
    public EffectExposure() { super(MobEffectCategory.HARMFUL, 0x77ff00); }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier)
    {
        if(entity instanceof Player)
        {
            Player player = (Player)entity;
            RadiationManager.setRadLevel(player, RadiationManager.getRadLevel(player) + 1);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier)
    {
        int i = Configuration.maxRadLevel.get() - Configuration.minRadLevel.get();
        float f = i / 6000f; //6000 ticks = 5 minutes
        i = (int) Math.ceil(1/f);
        if(amplifier <= Configuration.maxExposure.get()) i = i << (Configuration.maxExposure.get() - amplifier);
        else i = i >> amplifier - Configuration.maxExposure.get();
        return i <= 0 || duration % i == i - 1;
    }

    @Override
    public List<ItemStack> getCurativeItems() { return new ArrayList<>(); }

    @Override
    public boolean isInstantenous() { return false; }
}
