package techeart.thrad.utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
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
        /*TODO: tick radiation*/

        //System.out.println(RadiationManager.getRadLevel(event.player));

        //Utils.raycastSearchForBlock(event.player.level, event.player.blockPosition().below(), new Vec3(0,-1,0), 10);

        if(!event.player.level.isClientSide()) RadiationManager.tickRadiation(event.player);
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        Player player = event.getPlayer();
        if(!player.level.isClientSide())
        {
            //AttributeMap attributes = player.getAttributes();
            //attributes.getInstance(AttributeRadiationLevel.RADIATION_LEVEL).setBaseValue(Configuration.DEFAULT_RAD_LEVEL);
            /*TODO: Remove this. It used only for testing*/
            RadiationManager.setRadLevel(player, 100);
        }
    }

    @SubscribeEvent
    public void onAttachEntityCaps(AttachCapabilitiesEvent<Entity> event)
    {
        if(event.getObject().getType() == EntityType.PLAYER)
        {
            event.addCapability(RadiationCapability.ID, new RadiationCapProvider());
        }
    }

//    @SubscribeEvent
//    public void onModifyEntityAttributes(EntityAttributeModificationEvent event)
//    {
//        System.out.println("Modifying attributes");
//        event.add(EntityType.PLAYER, AttributeRadiationLevel.RADIATION_LEVEL);
//    }

//    @SubscribeEvent
//    public void onServerStarting(FMLServerStartingEvent event)
//    {
//
//    }

//    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
//    public static class RegistryEvents {
//        @SubscribeEvent
//        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
//            // register a new block here
//            LOGGER.info("HELLO from Register Block");
//        }
//    }
}
