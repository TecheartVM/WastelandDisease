package techeart.thrad.utils;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import techeart.thrad.MainClass;
import techeart.thrad.attributes.AttributeRadiationLevel;

public class RegistryHandler
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MainClass.MODID);
    public static final RegistryObject<Item> RAD_METER = ITEMS.register("rad_meter", () -> new Item(
            new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS)
    ));

    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, MainClass.MODID);
    public static final RegistryObject<Attribute> RADIATION = ATTRIBUTES.register("radiation", AttributeRadiationLevel::new);

    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
        ATTRIBUTES.register(eventBus);
    }
}
