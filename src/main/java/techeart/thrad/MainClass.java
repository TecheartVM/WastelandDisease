package techeart.thrad;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import techeart.thrad.capabilities.radcap.RadiationCapability;
import techeart.thrad.capabilities.radcap.RadiationManager;

@Mod("thrad")
public class MainClass
{
    public static final String MODID = "thrad";

    private static final Logger LOGGER = LogManager.getLogger();

    public MainClass()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModifyEntityAttributes);

        RegistryHandler.register(FMLJavaModLoadingContext.get().getModEventBus());

        MinecraftForge.EVENT_BUS.register(new EventHandler());
        MinecraftForge.EVENT_BUS.register(new RadiationBar(Minecraft.getInstance()));
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        RadiationCapability.register();
    }

    private void setupClient(final FMLClientSetupEvent event)
    {

    }

    public void onModifyEntityAttributes(EntityAttributeModificationEvent event)
    {
        event.add(EntityType.PLAYER, RegistryHandler.RADIATION.get());
    }
}
