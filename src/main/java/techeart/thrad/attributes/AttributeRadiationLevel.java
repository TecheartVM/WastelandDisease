package techeart.thrad.attributes;

import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import techeart.thrad.Configuration;
import techeart.thrad.MainClass;

public class AttributeRadiationLevel extends RangedAttribute
{
    public static final String ID = MainClass.MODID + ".radiation";

    public AttributeRadiationLevel()
    {
        super(
                ID,
                Configuration.DEFAULT_RAD_LEVEL,
                Configuration.MIN_RAD_LEVEL,
                Configuration.MAX_RAD_LEVEL
        );
    }
}