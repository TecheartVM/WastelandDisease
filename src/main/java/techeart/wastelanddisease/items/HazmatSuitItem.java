package techeart.wastelanddisease.items;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import techeart.wastelanddisease.MainClass;
import techeart.wastelanddisease.config.Configuration;

import javax.annotation.Nonnull;
import java.util.UUID;

public class HazmatSuitItem extends ArmorItem
{
    public static final ArmorMaterial MATERIAL = new Material();
    public static final UUID FULL_SUIT_ATTRIBUTE_MOD_UUID = UUID.fromString("9bcdce8e-fb3e-4577-8cd0-3a6c8f23fbd2");
    protected static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
    private static final String NAME = MainClass.MODID + ":hazmat";

    public HazmatSuitItem(EquipmentSlot type)
    {
        super(MATERIAL, type, new Properties().stacksTo(1).tab(CreativeModeTab.TAB_COMBAT));
    }

    private static class Material implements ArmorMaterial
    {
        @Override
        public int getDurabilityForSlot(@Nonnull EquipmentSlot slot)
        {
            return HEALTH_PER_SLOT[slot.getIndex()] * Configuration.hazmatDurabilityMult.get();
        }

        @Override
        public int getDefenseForSlot(@Nonnull EquipmentSlot slot)
        {
            return switch (slot) {
                case HEAD, CHEST, LEGS -> 2;
                default -> 1;
            };
        }

        @Override
        public int getEnchantmentValue() { return 0; }

        @Override
        @NotNull
        public SoundEvent getEquipSound() { return SoundEvents.ARMOR_EQUIP_LEATHER; }

        @Override
        @NotNull
        public Ingredient getRepairIngredient() { return Ingredient.of(Tags.Items.LEATHER); }

        @Override
        @NotNull
        public String getName() { return NAME; }

        @Override
        public float getToughness() { return 0; }

        @Override
        public float getKnockbackResistance() { return 0; }
    }
}
