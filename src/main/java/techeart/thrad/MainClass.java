package techeart.thrad;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import techeart.thrad.capabilities.radcap.RadiationCapability;
import techeart.thrad.client.gui.RadiationBar;
import techeart.thrad.compat.CompatCurios;
import techeart.thrad.config.ConfigHandler;
import techeart.thrad.network.PacketHandler;
import techeart.thrad.utils.EventHandler;

@Mod("thrad")
public class MainClass
{
    public static final String MODID = "thrad";
    public static final String CONFIG_FILE_NAME = "thrad";

    public static final Logger LOGGER = LogManager.getLogger();

    public MainClass()
    {
        ConfigHandler.register();

        EventHandler eventHandler = new EventHandler();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMCs);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(eventHandler::onModifyEntityAttributes);

        ConfigHandler.load(CONFIG_FILE_NAME);

        RegistryHandler.register(FMLJavaModLoadingContext.get().getModEventBus());

        MinecraftForge.EVENT_BUS.register(eventHandler);
        MinecraftForge.EVENT_BUS.register(new RadiationBar(Minecraft.getInstance()));
    }

    public void setup(final FMLCommonSetupEvent event)
    {
        PacketHandler.register();
        RadiationCapability.register();
    }

    public void enqueueIMCs(InterModEnqueueEvent event)
    {
        new CompatCurios().sendIMC();
    }
}
