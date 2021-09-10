package techeart.thrad.compat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.InterModComms;
import techeart.thrad.items.HazmatSuitItem;
import techeart.thrad.utils.RegistryHandler;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Optional;
import java.util.function.Predicate;

public class CompatCurios implements ICompatModule
{
    @Override
    public void sendIMC()
    {
        InterModComms.sendTo(
                CuriosApi.MODID,
                SlotTypeMessage.REGISTER_TYPE,
                () -> SlotTypePreset.BELT.getMessageBuilder().build()
        );
    }

    public static ItemStack getRadMeter(LivingEntity entity)
    {
        return getItemInCurios(
                entity,
                SlotTypePreset.BELT,
                stack -> stack.getItem() == RegistryHandler.RAD_METER.get()
        );
    }

    public static boolean hasFullHazmatSuit(LivingEntity entity)
    {
        Predicate<ItemStack> filter = stack -> stack.getItem() instanceof HazmatSuitItem;
        return !(
                getItemInCurios(entity, SlotTypePreset.HEAD, filter).isEmpty() ||
                getItemInCurios(entity, SlotTypePreset.BODY, filter).isEmpty()
        );
    }

    public static ItemStack getItemInCurios(LivingEntity entity, SlotTypePreset slot, Predicate<ItemStack> filter)
    {
        LazyOptional<ICuriosItemHandler> lo = CuriosApi.getCuriosHelper().getCuriosHandler(entity);
        if(!lo.isPresent()) return ItemStack.EMPTY;
        ICuriosItemHandler itemHandler = lo.orElse(null);
        Optional<ICurioStacksHandler> op = itemHandler.getStacksHandler(slot.getIdentifier());
        if(!op.isPresent()) return ItemStack.EMPTY;
        ICurioStacksHandler stacksHandler = op.get();
        if(stacksHandler.isVisible())
        {
            for (int i = 0; i < stacksHandler.getSlots(); i++)
            {
                if (stacksHandler.getRenders().get(i))
                {
                    ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                    if (filter.test(stack))
                        return stack;
                }
            }
        }
        return ItemStack.EMPTY;
    }
}
