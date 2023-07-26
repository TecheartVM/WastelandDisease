package techeart.wastelanddisease.config;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import techeart.wastelanddisease.MainClass;
import techeart.wastelanddisease.utils.KeyValuePair;
import techeart.wastelanddisease.RegistryHandler;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class Configuration
{
    //CLIENT settings
    public static ForgeConfigSpec.EnumValue<BarSkins> barSkin;

    //SERVER settings
    /*TODO: implement this in future*/
    public static ForgeConfigSpec.BooleanValue enableRadMeter;
    public static ForgeConfigSpec.BooleanValue enableIodineTablet;
    public static ForgeConfigSpec.BooleanValue enableHazmat;

    public static ForgeConfigSpec.EnumValue<BarDisplayModes> barMode;

    public static ForgeConfigSpec.BooleanValue allowWeakness;
    public static ForgeConfigSpec.BooleanValue allowMiningFatigue;
    public static ForgeConfigSpec.BooleanValue allowNausea;
    public static ForgeConfigSpec.BooleanValue allowBlindness;

    public static ForgeConfigSpec.IntValue minRadLevel;
    public static ForgeConfigSpec.IntValue maxRadLevel;
    public static ForgeConfigSpec.IntValue defaultRadLevel;
    public static ForgeConfigSpec.IntValue minYLevel;
    public static ForgeConfigSpec.IntValue maxYLevel;
    public static ForgeConfigSpec.IntValue coverSearchRadius;
    public static ForgeConfigSpec.IntValue maxDistToNextLayer;
    public static ForgeConfigSpec.IntValue maxLayerChecks;
    public static ForgeConfigSpec.IntValue maxCoverThickness;
    public static ForgeConfigSpec.IntValue maxImpactLevel;
    public static ForgeConfigSpec.IntValue impactEffectDuration;
    public static ForgeConfigSpec.IntValue fullBarReachTime;
    public static ForgeConfigSpec.IntValue radLevelNaturalDecrDelay;
    public static ForgeConfigSpec.IntValue cdDuration;
    public static ForgeConfigSpec.IntValue cdBaseHurtChance;
    public static ForgeConfigSpec.IntValue cdSubeffectsDuration;
    public static ForgeConfigSpec.IntValue cdTickDelay;
    public static ForgeConfigSpec.IntValue hazmatDurabilityMult;

    public static ForgeConfigSpec.ConfigValue<List<Integer>> cdRequiredRadLevel;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> suitList;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> antiradList;
    public static ForgeConfigSpec.BooleanValue dimensionsBlacklist;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> dimensionsList;
    public static ForgeConfigSpec.BooleanValue biomesBlacklist;
    private static ForgeConfigSpec.ConfigValue<List<? extends String>> biomesSettings;

    private static Map<String, Integer> biomesList;
    public static Map<String, Integer> getBiomesList()
    {
        if(biomesList == null)
            biomesList = convertConfigList(biomesSettings.get(), maxImpactLevel.get());
        return biomesList;
    }

    private static Map<String, Integer> antiradEquipmentSettings;
    public static Map<String, Integer> getAntiradEquipmentSettings()
    {
        if(antiradEquipmentSettings == null)
            antiradEquipmentSettings = convertConfigList(suitList.get(), -1);
        return antiradEquipmentSettings;
    }

    private static Map<String, KeyValuePair<Integer, Integer>> antiradConsumables;
    public static Map<String, KeyValuePair<Integer, Integer>> getAntiradConsumables()
    {
        if(antiradConsumables == null)
        {
            antiradConsumables = Maps.newHashMap();
            for (String s : antiradList.get())
            {
                String[] spl = s.split(",");
                if(spl.length == 1)
                {
                    antiradConsumables.put(spl[0], new KeyValuePair<>(12000, 30));
                    continue;
                }
                try
                {
                    int i = Integer.parseInt(spl[1]);
                    if(spl.length == 2)
                    {
                        antiradConsumables.put(spl[0], new KeyValuePair<>(12000, i));
                        continue;
                    }
                    int mod = Integer.parseInt(spl[2]);
                    antiradConsumables.put(spl[0], new KeyValuePair<>(i, mod));
                }
                catch (Exception ignored) { }
            }
        }
        return antiradConsumables;
    }

    public static void init(ForgeConfigSpec.Builder serverCfg, ForgeConfigSpec.Builder clientCfg)
    {
        clientCfg.push("HUD");
        barSkin = clientCfg.comment("HUD rad level bar SKIN variant.")
                .defineEnum("bar_skin", BarSkins.RAD_YELLOW);
        clientCfg.pop();

//        serverCfg.comment("This config is per-world.\n" +
//                "To configure the mod edit the file in 'YourWorldFolder/serverconfig/"+ MainClass.CONFIG_FILE_NAME +"-server.toml'.\n" +
//                "If there is no such file in your world folder, you can copy it from the general config folder.");
        serverCfg.push("Balance");
        serverCfg.push("info");
        barMode = serverCfg.comment("HUD rad level bar DISPLAY MODE.")
                .defineEnum("bar_display_mode", BarDisplayModes.HAND);
        serverCfg.pop();

        serverCfg.push("items");
        hazmatDurabilityMult = serverCfg.comment("Hazmat suit durability MULTIPLIER.\n" +
                "Base armor durability values is [13,15,16,11].")
                .defineInRange("hazmat_durability", 8, 1, Integer.MAX_VALUE);
        suitList = serverCfg.comment("""
                        Items in this list will grant player resistance to polluton impact.
                        Affects only items that can be placed in player armor slots.
                        Player will get the resistance ONLY if full suit of such items is equipped.
                        Total suit resistance value is equals to average value of all items.
                        SYNTAX: '<modid>:<item>,<resistance>'
                        The <resistance> is value between 0 and 100 (inclusive).
                        The 0 resistance value item will not protect player from pollution impact.""")
                .defineList("antirad_equipment", ImmutableList.of(
                        MainClass.MODID + ":hazmat_helmet,80",
                        MainClass.MODID + ":hazmat_chestplate,80",
                        MainClass.MODID + ":hazmat_leggings,80",
                        MainClass.MODID + ":hazmat_boots,80"
                ), isStringIntegerPair);
        antiradList = serverCfg.comment("""
                        Items in this list will give player a rad resistance effect when used.
                        SYNTAX: '<modid>:<item>,<effect_duration>,<effect_amplifier>'
                                '<modid>:<item>,<effect_amplifier>'  (<effect_duration_ticks> will be equal to 12000)
                                '<modid>:<item>'                       (<effect_duration_ticks> will be equal to 12000 and <effect_amplifier> will be equal to 2)
                        The <effect_amplifier> is value between 0 and 9 (inclusive). Each level adds 10 points to total attribute modifier.""")
                .defineList("antirad_consumables", ImmutableList.of(MainClass.MODID + ":iodine_tablet,12000,2", "minecraft:dried_kelp,3000,1"), obj -> true);
        serverCfg.pop();

        serverCfg.push("effects");
        serverCfg.push("environmental_impact");
        maxImpactLevel = serverCfg.comment("The maximum value of env. impact for all dimensions and biomes.\n" +
                "Keeping this value about 2 LESS than 'Balance.cover.max_cover_thickness' is RECOMMENDED for proper system behaviour.")
                .defineInRange("max_impact", 5, 1, Integer.MAX_VALUE);
        impactEffectDuration = serverCfg.comment("The TIME in TICKS for which the PLAYER will get the IMPACT effect when he is in an UNCOVERED POLLUTED area.")
                .defineInRange("impact_duration", 600, 60, Integer.MAX_VALUE);
        serverCfg.pop();
        serverCfg.push("cells_destruction");
        cdDuration = serverCfg.comment("The TIME in TICKS for which the PLAYER will get the CELLS DESTRUCTION effect.\n" +
                "when reaches certain 'effects.cells_destruction.required_rad_levels' RAD LEVEL.")
                .defineInRange("duration", 1200, 60, Integer.MAX_VALUE);
        cdRequiredRadLevel = serverCfg.comment("LIST of RAD LEVELS REQUIRED for APPLYING a 'Cells Destruction' EFFECT of I, II, III and IV amplifier.")
                .comment("SYNTAX: [<min rad level for t1>,<min rad level for t2>,<min rad level for t3>,<min rad level for t4>]")
                .define("required_rad_levels", Lists.newArrayList(80, 120, 160, 190));
        cdBaseHurtChance = serverCfg.comment("BASE cells destruction SUB EFFECTS applying CHANCE.\n" +
                "Always used with the multiplier equal to the current 'Cells Destruction' effect amplifier.")
                .defineInRange("subeffects_chance", 200, 1, 10000);
        cdTickDelay = serverCfg.comment("BASE DELAY in ticks between 'Cells Destruction' effect UPDATE cycle ITERATIONS.\n" +
                "Divided by 2 for every amplifier level.")
                .defineInRange("tick_delay", 160, 1, Integer.MAX_VALUE);
        serverCfg.push("subeffects");
        allowWeakness = serverCfg.comment("If TRUE the RADIATION will be able to cause WEAKNESS effect to player.")
                .define("allow_weakness", true);
        allowMiningFatigue = serverCfg.comment("If TRUE the RADIATION will be able to cause MINING FATIGUE effect to player.")
                .define("allow_mining_fatigue", true);
        allowNausea = serverCfg.comment("If TRUE the RADIATION will be able to cause NAUSEA effect to player.")
                .define("allow_nausea", true);
        allowBlindness = serverCfg.comment("If TRUE the RADIATION will be able to cause BLINDNESS effect to player.")
                .define("allow_blindness", true);
        cdSubeffectsDuration = serverCfg.comment("The TIME in TICKS for which the PLAYER will get the CELLS DESTRUCTION effect SUB EFFECTS.")
                .defineInRange("subeffects_duration", 600, 60, Integer.MAX_VALUE);
        serverCfg.pop();
        serverCfg.pop();
        serverCfg.pop();

        serverCfg.push("measurements");
        minRadLevel = serverCfg.comment("The minimum rad value measured.")
                .defineInRange("min_rad_level", 0, 0, Integer.MAX_VALUE);
        maxRadLevel = serverCfg.comment("The maximum rad value measured.")
                .defineInRange("max_rad_level", 200, 100, Integer.MAX_VALUE);
        defaultRadLevel = serverCfg.comment("Default PLAYER rad level.")
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
        maxCoverThickness = serverCfg.comment("The maximum AMOUNT of COVER LAYERS that WILL AFFECT a total IMPACT to the player.\n" +
                "Keeping this value about 2 MORE than 'Balance.effects.max_impact' is RECOMMENDED for proper system behaviour.")
                .defineInRange("max_cover_thickness", 7, 0, 255);
        serverCfg.pop();

        serverCfg.push("misc");
        fullBarReachTime = serverCfg.comment("TIME in ticks for which the HUD rad level BAR being completely FILLED.")
                .defineInRange("full_bar_reach_time", 6000, 100, Integer.MAX_VALUE);
        radLevelNaturalDecrDelay = serverCfg.comment("The TIME in ticks between RAD level NATURAL DECREASE cycles.\n" +
                "Only works while the player is in safe zone.")
                .defineInRange("rad_decrease_delay", 100, 0, Integer.MAX_VALUE);
        serverCfg.pop();
        serverCfg.pop();

        serverCfg.push("Dimensions");
        dimensionsBlacklist = serverCfg.comment("If TRUE dimensions list will be used as BLACKLIST.")
                .define("blacklist", true);
        dimensionsList = serverCfg.comment("Dimensions with these ids will not be polluted if 'dimensions.blacklist' is true. Otherwise only they will be.")
                .defineList("polluted_dimensions", ImmutableList.of(), s -> s instanceof String);
        serverCfg.pop();

        serverCfg.push("Biomes");
        biomesBlacklist = serverCfg.comment("If TRUE biomes list will be used as BLACKLIST.")
                .define("blacklist", true);
        biomesSettings = serverCfg.comment("""
                        Biomes with these ids will not be polluted if 'biomes.blacklist' is true.
                        Otherwise only these biomes will be polluted with the specified impact level.
                        If the impact level is undefined, the 'balance.maxImpact' will be used.
                        In BLACKLIST mode unlisted biomes will use 'balance.maxImpact'.
                        FORMAT: "<modid>:<biome>,<impact_level>\"""")
                .defineList("polluted_biomes", ImmutableList.of(), isStringIntegerPair);
        serverCfg.pop();
    }

    private static final Predicate<Object> isStringIntegerPair = s -> {
        if(!(s instanceof String)) return false;
        String[] spl = ((String) s).split(",");
        if(spl.length == 1) return true;
        try
        {
            Integer.parseInt(spl[1]);
            return true;
        }
        catch (Exception e) { return false; }
    };

    /**Called immediately after loading the configuration file.*/
    public static void postLoad(UnmodifiableConfig file)
    {

    }

    private static Map<String, Integer> convertConfigList(List<? extends String> list, int defVal)
    {
        Map<String, Integer> res = Maps.newHashMap();
        for (String s : list)
        {
            String[] spl = s.split(",");
            res.put(spl[0], spl.length > 1 ? Integer.parseInt(spl[1]) : defVal);
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
                if(p.getItemInHand(hand).getItem() == RegistryHandler.ITEM_RAD_METER.get())
                    return true;
            return false;
        }),
        TOOLBAR(p -> {
            for (int i = 0; i < Inventory.getSelectionSize(); i++)
                if(p.getInventory().items.get(i).getItem() == RegistryHandler.ITEM_RAD_METER.get())
                    return true;
            return false;
        }),
        INVENTORY(p -> p.getInventory().contains(new ItemStack(RegistryHandler.ITEM_RAD_METER.get()))),
        ALWAYS(p -> true);

        private final Function<Player, Boolean> shouldDisplayCheck;
        BarDisplayModes(Function<Player, Boolean> shouldDisplayCheck) { this.shouldDisplayCheck = shouldDisplayCheck; }
        public Function<Player, Boolean> getShouldDispayCheck() { return shouldDisplayCheck; }
    }
}
