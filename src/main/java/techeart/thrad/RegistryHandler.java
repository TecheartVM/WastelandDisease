package techeart.thrad;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import techeart.thrad.attributes.AttributeRadiationLevel;

public class RegistryHandler
{
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, MainClass.MODID);
    public static final RegistryObject<Attribute> RADIATION = ATTRIBUTES.register("radiation", AttributeRadiationLevel::new);

    public static void register(IEventBus eventBus)
    {
        ATTRIBUTES.register(eventBus);
    }
}
