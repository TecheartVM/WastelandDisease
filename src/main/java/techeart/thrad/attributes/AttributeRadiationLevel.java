package techeart.thrad.attributes;

import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import techeart.thrad.config.Configuration;
import techeart.thrad.MainClass;

@Deprecated
public class AttributeRadiationLevel extends RangedAttribute
{
    public static final String ID = MainClass.MODID + ".radiation";

    public AttributeRadiationLevel()
    {
        super(
                ID,
                Configuration.defaultRadLevel.get(),
                Configuration.minRadLevel.get(),
                Configuration.maxRadLevel.get()
        );
    }
}
