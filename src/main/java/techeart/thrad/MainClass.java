package techeart.thrad;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.RenderProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import techeart.thrad.capabilities.radcap.RadiationCapability;
import techeart.thrad.client.RadiationBar;
import techeart.thrad.compat.CompatCurios;
import techeart.thrad.config.ConfigHandler;
import techeart.thrad.network.PacketHandler;
import techeart.thrad.utils.EventHandler;
import techeart.thrad.utils.RegistryHandler;

@Mod("thrad")
public class MainClass
{
    public static final String MODID = "thrad";
    private static final String CONFIG_FILE_NAME = "thrad";

    public static final Logger LOGGER = LogManager.getLogger();

    public MainClass()
    {
        ConfigHandler.register();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMCs);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModifyEntityAttributes);

        ConfigHandler.load(CONFIG_FILE_NAME);

        RegistryHandler.register(FMLJavaModLoadingContext.get().getModEventBus());

        MinecraftForge.EVENT_BUS.register(new EventHandler());
        MinecraftForge.EVENT_BUS.register(new RadiationBar(Minecraft.getInstance()));
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        PacketHandler.register();
        RadiationCapability.register();
    }

    private void enqueueIMCs(InterModEnqueueEvent event)
    {
        new CompatCurios().sendIMC();
    }

    public void onModifyEntityAttributes(EntityAttributeModificationEvent event)
    {
        event.add(EntityType.PLAYER, RegistryHandler.RADIATION.get());
    }
}
