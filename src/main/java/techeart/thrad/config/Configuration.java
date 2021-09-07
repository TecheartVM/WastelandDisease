package techeart.thrad.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import techeart.thrad.utils.RegistryHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class Configuration
{
    //CLIENT settings
    public static ForgeConfigSpec.EnumValue<BarSkins> barSkin;

    //SERVER settings
    public static ForgeConfigSpec.EnumValue<BarDisplayModes> barMode;
    public static ForgeConfigSpec.IntValue minRadLevel;
    public static ForgeConfigSpec.IntValue maxRadLevel;
    public static ForgeConfigSpec.IntValue defaultRadLevel;
    public static ForgeConfigSpec.IntValue minYLevel;
    public static ForgeConfigSpec.IntValue maxYLevel;
    public static ForgeConfigSpec.IntValue coverSearchRadius;
    public static ForgeConfigSpec.IntValue maxDistToNextLayer;
    public static ForgeConfigSpec.IntValue maxLayerChecks;
    public static ForgeConfigSpec.IntValue maxCoverThickness;
    public static ForgeConfigSpec.IntValue maxExposure;
    public static ForgeConfigSpec.IntValue exposureDuration;
    public static ForgeConfigSpec.IntValue radLevelDecreaseChance;
    public static ForgeConfigSpec.ConfigValue<List<Integer>> cdRequiredRadLevel;
    public static ForgeConfigSpec.BooleanValue allowWeakness;
    public static ForgeConfigSpec.BooleanValue allowMiningFatigue;
    public static ForgeConfigSpec.BooleanValue allowNausea;
    public static ForgeConfigSpec.BooleanValue allowBlindness;
    public static ForgeConfigSpec.IntValue cdTickDelay;

    public static ForgeConfigSpec.BooleanValue dimensionsBlacklist;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> dimensionsList;

    public static ForgeConfigSpec.BooleanValue biomesBlacklist;
    private static ForgeConfigSpec.ConfigValue<List<? extends String>> biomesSettings;
    /**This map is populated with biome data from the config file after it is loaded.*/
    public static Map<String, Integer> biomesList;

    public static void init(ForgeConfigSpec.Builder serverCfg, ForgeConfigSpec.Builder clientCfg)
    {
        clientCfg.push("Client");
        barSkin = clientCfg.comment("HUD pollution level bar SKIN variant.")
                .defineEnum("bar_skin", BarSkins.RAD_YELLOW);
        clientCfg.pop();

        serverCfg.push("Balance");
        serverCfg.push("info");
        barMode = serverCfg.comment("HUD pollution level bar DISPLAY MODE.")
                .defineEnum("bar_display_mode", BarDisplayModes.HAND);
        serverCfg.pop();

        serverCfg.push("effects");
        maxExposure = serverCfg.comment("The maximum value of exposure for all dimensions and biomes.\n" +
                "Keeping this value about 2 LESS than 'Balance.cover.max_cover_thickness' is RECOMMENDED for proper system behaviour.")
                .defineInRange("max_exposure", 5, 1, Integer.MAX_VALUE);
        exposureDuration = serverCfg.comment("The TIME in TICKS for which the PLAYER will get the EXPOSURE effect when he is in an UNCOVERED POLLUTED area.")
                .defineInRange("exposure_duration", 600, 60, Integer.MAX_VALUE);
        serverCfg.push("cells_destruction");
        cdRequiredRadLevel = serverCfg.comment("LIST of RAD LEVELS REQUIRED for APPLYING a 'Cells Destruction' EFFECT of I, II, III and IV amplifier.")
                .comment("SYNTAX: [<min rad level for t1>,<min rad level for t2>,<min rad level for t3>,<min rad level for t4>]")
                .define("required_rad_levels", Lists.newArrayList(100, 150, 170, 185));
        allowWeakness = serverCfg.comment("If TRUE the RADIATION will be able to cause WEAKNESS effect to player.")
                .define("allow_weakness", true);
        allowMiningFatigue = serverCfg.comment("If TRUE the RADIATION will be able to cause MINING FATIGUE effect to player.")
                .define("allow_mining_fatigue", true);
        allowNausea = serverCfg.comment("If TRUE the RADIATION will be able to cause NAUSEA effect to player.")
                .define("allow_nausea", true);
        allowBlindness = serverCfg.comment("If TRUE the RADIATION will be able to cause BLINDNESS effect to player.")
                .define("allow_blindness", true);
        cdTickDelay = serverCfg.comment("BASE DELAY in ticks between 'Cells Destruction' effect UPDATE cycle ITERATIONS.")
                .defineInRange("tick_delay", 100, 1, Integer.MAX_VALUE);
        serverCfg.pop();
        serverCfg.pop();

        serverCfg.push("measurements");
        minRadLevel = serverCfg.comment("The minimum pollution value.")
                .defineInRange("min_rad_level", 0, 0, Integer.MAX_VALUE);
        maxRadLevel = serverCfg.comment("The maximum pollution value.")
                .defineInRange("max_rad_level", 200, 100, Integer.MAX_VALUE);
        defaultRadLevel = serverCfg.comment("Default pollution level.")
                .defineInRange("default_rad_level", 10, 0, Integer.MAX_VALUE);
        serverCfg.pop();

        serverCfg.push("world");
        minYLevel = serverCfg.comment("The minimum Y level polluted.")
                .defineInRange("min_height", 0, 0, 255);
        maxYLevel = serverCfg.comment("The maximum Y level polluted.")
                .defineInRange("max_height", 155, 0, 255);
        serverCfg.pop();

        serverCfg.push("cover");
        coverSearchRadius = serverCfg.comment("The maximum DISTANCE from PLAYER at which the FIRST LAYER of COVER can be detected.")
                .defineInRange("cover_search_radius", 30, 3, 50);
        maxDistToNextLayer = serverCfg.comment("The maximum DISTANCE between PREVIOUS COVER LAYER found and potential NEXT LAYER.")
                .defineInRange("next_layer_distance", 6, 3, 20);
        maxLayerChecks = serverCfg.comment("How many TIMES the system will try to find ADDITIONAL LAYER of player COVER.")
                .defineInRange("max_layer_checks", 10, 1, 20);
        maxCoverThickness = serverCfg.comment("The maximum AMOUNT of COVER LAYERS that WILL AFFECT a total EXPOSURE to the player.\n" +
                "Keeping this value about 2 MORE than 'Balance.effects.max_exposure' is RECOMMENDED for proper system behaviour.")
                .defineInRange("max_cover_thickness", 7, 0, 255);
        serverCfg.pop();

        serverCfg.push("random");
        radLevelDecreaseChance = serverCfg.comment("The chance of pollution level to be decreased by 1 while the player is in safe zone.")
                .defineInRange("rad_decrease_chance", 100, 0, 10000);
        serverCfg.pop();
        serverCfg.pop();

        serverCfg.push("Dimensions");
        dimensionsBlacklist = serverCfg.comment("If TRUE dimensions list will be used as BLACKLIST.")
                .define("blacklist", true);
        dimensionsList = serverCfg.comment("Dimensions with these ids will not be polluted if 'dimensions.blacklist' is true. Otherwise only they will be.")
                .defineList("polluted_dimensions", (Supplier<List<? extends String>>) ArrayList::new, s -> s instanceof String);
        serverCfg.pop();

        serverCfg.push("Biomes");
        biomesBlacklist = serverCfg.comment("If TRUE biomes list will be used as BLACKLIST.")
                .define("blacklist", true);
        biomesSettings = serverCfg.comment("Biomes with these ids will not be polluted if 'biomes.blacklist' is true.\n" +
                "Otherwise only these biomes will be polluted with the specified exposure level.\n" +
                "If the exposure level is undefined, the 'balance.maxExposure' will be used.\n" +
                "In BLACKLIST mode unlisted biomes will use 'balance.maxExposure'.\n" +
                "FORMAT: \"<modid>:<biome>,<exposure_level>\"")
                .defineList("polluted_biomes", (Supplier<List<? extends String>>) ArrayList::new, s -> {
                    if(!(s instanceof String)) return false;
                    String[] spl = ((String) s).split(",");
                    if(spl.length == 1) return true;
                    if(spl.length < 1 || spl.length > 2) return false;
                    try
                    {
                        Integer.parseInt(spl[1]);
                        return true;
                    }
                    catch (NumberFormatException e) { return false; }
                });
        serverCfg.pop();
    }

    /**Called immediately after loading the configuration file.*/
    public static void postLoad(UnmodifiableConfig file)
    {
        biomesList = getBiomesSettings();
    }

    /**Use of {@link Configuration#biomesList} field instead of this method is recommended.*/
    public static Map<String, Integer> getBiomesSettings()
    {
        Map<String, Integer> res = Maps.newHashMap();
        for (String s : biomesSettings.get())
        {
            String[] spl = s.split(",");
            res.put(spl[0], spl.length > 1 ? Integer.valueOf(spl[1]) : maxExposure.get());
        }
        return res;
    }

    public enum BarSkins
    {
        RAD_GREEN(0),
        RAD_GRADIENT(1),
        RAD_YELLOW(2),
        BIOHAZARD(3);

        private final int id;
        BarSkins(int id) { this.id = id; }
        public int getId() { return id; }
    }

    public enum BarDisplayModes
    {
        NEVER(p -> false),
        HAND(p -> {
            for (InteractionHand hand : InteractionHand.values())
                if(p.getItemInHand(hand).getItem() == RegistryHandler.RAD_METER.get())
                    return true;
            return false;
        }),
        TOOLBAR(p -> {
            for (int i = 0; i < Inventory.getSelectionSize(); i++)
                if(p.getInventory().items.get(i).getItem() == RegistryHandler.RAD_METER.get())
                    return true;
            return false;
        }),
        INVENTORY(p -> p.getInventory().contains(new ItemStack(RegistryHandler.RAD_METER.get()))),
        ALWAYS(p -> true);

        private final Function<Player, Boolean> shouldDisplayCheck;
        BarDisplayModes(Function<Player, Boolean> shouldDisplayCheck) { this.shouldDisplayCheck = shouldDisplayCheck; }
        public Function<Player, Boolean> getShouldDispayCheck() { return shouldDisplayCheck; }
    }
}
