package techeart.wastelanddisease;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import techeart.wastelanddisease.client.gui.RadiationBar;
import techeart.wastelanddisease.compat.CompatCurios;
import techeart.wastelanddisease.config.ConfigHandler;
import techeart.wastelanddisease.network.PacketHandler;
import techeart.wastelanddisease.utils.EventHandler;

@Mod(MainClass.MODID)
public class MainClass
{
    public static final String MODID = "wastelanddisease";
    public static final String CONFIG_FILE_NAME = "wastelanddisease";

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
    }

    public void enqueueIMCs(InterModEnqueueEvent event)
    {
        new CompatCurios().sendIMC();
    }
}
