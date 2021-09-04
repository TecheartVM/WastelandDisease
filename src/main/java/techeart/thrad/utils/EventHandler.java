package techeart.thrad.utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import techeart.thrad.capabilities.radcap.RadiationCapProvider;
import techeart.thrad.capabilities.radcap.RadiationCapability;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class EventHandler
{
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(!event.player.level.isClientSide())
            RadiationManager.tickRadiation(event.player);
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        //TODO: check the capability behaviour after player respawn
//        Player player = event.getPlayer();
//        if(!player.level.isClientSide())
//        {
//            //AttributeMap attributes = player.getAttributes();
//            //attributes.getInstance(AttributeRadiationLevel.RADIATION_LEVEL).setBaseValue(Configuration.DEFAULT_RAD_LEVEL);
//        }
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
