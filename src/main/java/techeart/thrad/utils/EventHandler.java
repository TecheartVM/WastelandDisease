package techeart.thrad.utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import techeart.thrad.RadiationManager;
import techeart.thrad.capabilities.radcap.RadiationCapProvider;
import techeart.thrad.capabilities.radcap.RadiationCapability;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class EventHandler
{
    public void onModifyEntityAttributes(EntityAttributeModificationEvent event)
    {
        event.add(EntityType.PLAYER, RegistryHandler.RAD_RESISTANCE.get());
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

//    @SubscribeEvent
//    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
//    {
//        Player player = event.getPlayer();
//        if(!player.level.isClientSide())
//        {
//            AttributeMap attributes = player.getAttributes();
////            attributes.getInstance(RegistryHandler.RAD_RESISTANCE.get()).setBaseValue(10);
//            System.out.println(attributes.getInstance(RegistryHandler.RAD_RESISTANCE.get()));
//        }
//    }

    @SubscribeEvent
    public void onAttachEntityCaps(AttachCapabilitiesEvent<Entity> event)
    {
        if(event.getObject().getType() == EntityType.PLAYER)
        {
            event.addCapability(RadiationCapability.ID, new RadiationCapProvider());
        }
    }
}
