package techeart.wastelanddisease.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import techeart.wastelanddisease.config.Configuration;
import techeart.wastelanddisease.RadiationManager;

import java.util.ArrayList;
import java.util.List;

public class EffectEnvironmentalImpact extends MobEffect
{
    public EffectEnvironmentalImpact() { super(MobEffectCategory.HARMFUL, 0x77ff00); }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier)
    {
        if(entity instanceof Player player)
            RadiationManager.setRadLevel(player, RadiationManager.getRadLevel(player) + 1);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier)
    {
        int i = Configuration.maxRadLevel.get() - Configuration.minRadLevel.get();
        float f = i / (float)Configuration.fullBarReachTime.get();
        i = (int) Math.ceil(1/f);
        if(amplifier <= Configuration.maxImpactLevel.get()) i = i << (Configuration.maxImpactLevel.get() - amplifier);
        else i = i >> amplifier - Configuration.maxImpactLevel.get();
        return i <= 0 || duration % i == i - 1;
    }

    @Override
    public List<ItemStack> getCurativeItems() { return new ArrayList<>(); }
}
