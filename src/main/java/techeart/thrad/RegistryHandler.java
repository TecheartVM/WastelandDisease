package techeart.thrad;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import techeart.thrad.commands.CommandRadLevelAction;
import techeart.thrad.effects.EffectCellsDestruction;
import techeart.thrad.effects.EffectExposure;
import techeart.thrad.effects.EffectRadResistance;
import techeart.thrad.items.HazmatSuitItem;

public class RegistryHandler
{
    /*----------Attributes----------*/
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, MainClass.MODID);
    public static final RegistryObject<Attribute> ATTR_RAD_RESISTANCE = ATTRIBUTES.register("rad_resistance", () ->
            new RangedAttribute(MainClass.MODID + ".rad_resistance", 10, 0, 100)
                    .setSyncable(true)
    );

    /*-------------Items-------------*/
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MainClass.MODID);
    public static final RegistryObject<Item> ITEM_RAD_METER = ITEMS.register("rad_meter", () -> new Item(
            new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS)
    ));
    public static final RegistryObject<Item> ITEM_IODINE_TABLET = ITEMS.register("iodine_tablet", () -> new Item(
            new Item.Properties().tab(CreativeModeTab.TAB_FOOD).food(new FoodProperties.Builder().fast().nutrition(0).saturationMod(0f).alwaysEat().build())
    ));
    public static final RegistryObject<Item> ITEM_HAZMAT_HELMET = ITEMS.register("hazmat_helmet", () -> new HazmatSuitItem(EquipmentSlot.HEAD));
    public static final RegistryObject<Item> ITEM_HAZMAT_CHESTPLATE = ITEMS.register("hazmat_chestplate", () -> new HazmatSuitItem(EquipmentSlot.CHEST));
    public static final RegistryObject<Item> ITEM_HAZMAT_LEGGINGS = ITEMS.register("hazmat_leggings", () -> new HazmatSuitItem(EquipmentSlot.LEGS));
    public static final RegistryObject<Item> ITEM_HAZMAT_BOOTS = ITEMS.register("hazmat_boots", () -> new HazmatSuitItem(EquipmentSlot.FEET));

    /*------------Effects------------*/
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MainClass.MODID);
    public static final RegistryObject<MobEffect> EFFECT_EXPOSURE = EFFECTS.register("exposure", EffectExposure::new);
    public static final RegistryObject<MobEffect> EFFECT_CELLS_DESTRUCTION = EFFECTS.register("cells_destruction", EffectCellsDestruction::new);
    public static final RegistryObject<MobEffect> EFFECT_RAD_RESISTANCE = EFFECTS.register("rad_resistance", () -> new EffectRadResistance().addAttributeModifier(
            ATTR_RAD_RESISTANCE.get(), "8522920c-69d5-4d0b-8341-d2dae6774534", 10.0d, AttributeModifier.Operation.ADDITION
    ));

    /*--------Damage sources--------*/
    public static final DamageSource DAMAGE_SOURCE_RADIATION = (new DamageSource("cells_destruction")).bypassArmor();


    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
        ATTRIBUTES.register(eventBus);
        EFFECTS.register(eventBus);
    }

    public static void registerCustomCommands(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        CommandRadLevelAction.register(dispatcher);
    }
}
