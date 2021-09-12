package techeart.thrad.utils;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import techeart.thrad.RadiationManager;
import techeart.thrad.RegistryHandler;
import techeart.thrad.capabilities.radcap.RadiationCapProvider;
import techeart.thrad.capabilities.radcap.RadiationCapability;
import techeart.thrad.config.Configuration;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class EventHandler
{
    @SubscribeEvent
    public void onPlayerFinishUseItem(LivingEntityUseItemEvent.Finish event)
    {
        Item used = event.getItem().getItem();
        KeyValuePair<Integer, Integer> durAndMod = Configuration.getAntiradConsumables().get(used.getRegistryName().toString());
        if(durAndMod == null) return;
        event.getEntityLiving().addEffect(new MobEffectInstance(
                    RegistryHandler.EFFECT_RAD_RESISTANCE.get(),
                    durAndMod.getKey(),
                    durAndMod.getValue(),
                    false,
                    false,
                    false
                )
        );
    }

    public void onModifyEntityAttributes(EntityAttributeModificationEvent event)
    {
        event.add(EntityType.PLAYER, RegistryHandler.ATTR_RAD_RESISTANCE.get());
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(!event.player.level.isClientSide())
            RadiationManager.tickRadiation(event.player);
    }

    @SubscribeEvent
    public void onEntityEquipmentChanged(LivingEquipmentChangeEvent event)
    {
        if(event.getEntityLiving() instanceof Player player)
            RadiationManager.manageRadResistModifiers(player);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event)
    {
        RegistryHandler.registerCustomCommands(event.getDispatcher());
    }

    @SubscribeEvent
    public void onAttachEntityCaps(AttachCapabilitiesEvent<Entity> event)
    {
        if(event.getObject().getType() == EntityType.PLAYER)
        {
            event.addCapability(RadiationCapability.ID, new RadiationCapProvider());
        }
    }
}
