package techeart.thrad.config;


import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Configuration
{
    /*TODO: attach config file*/

    public static ForgeConfigSpec.EnumValue<BarSkins> barSkin;

    public static ForgeConfigSpec.EnumValue<BarDisplayModes> barMode;
//    public static ForgeConfigSpec.IntValue minRadLevel;
//    public static ForgeConfigSpec.IntValue maxRadLevel;
//    public static ForgeConfigSpec.IntValue defaultRadLevel;
//    public static ForgeConfigSpec.IntValue minYLevel;
//    public static ForgeConfigSpec.IntValue maxYLevel;
//    public static ForgeConfigSpec.IntValue coverSearchRadius;
//    public static ForgeConfigSpec.IntValue maxDistToNextLayer;
//    public static ForgeConfigSpec.IntValue maxLayerChecks;
//    public static ForgeConfigSpec.IntValue maxCoverThickness;
//    public static ForgeConfigSpec.IntValue maxExposureLevel;
//    public static ForgeConfigSpec.IntValue radLevelDecreaseChance;

    public static ForgeConfigSpec.BooleanValue dimensionsBlacklist;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> dimensionsList;

    public static void init(ForgeConfigSpec.Builder serverCfg, ForgeConfigSpec.Builder clientCfg)
    {
        clientCfg.comment("Client");
        barSkin = clientCfg.comment("HUD pollution level bar SKIN variant.")
                .defineEnum("client.bar_skin", BarSkins.RAD_YELLOW);

        serverCfg.comment("Balance");
        barMode = serverCfg.comment("HUD pollution level bar DISPLAY MODE.")
                .defineEnum("balance.bar_display_mode", BarDisplayModes.HAND);

        serverCfg.comment("Dimensions");
        dimensionsBlacklist = serverCfg.comment("If TRUE dimensions list will be used as BLACKLIST.")
                .define("dimensions.blacklist", true);
        dimensionsList = serverCfg.comment("Dimensions with these ids will not be polluted if 'dimensions.blacklist' is true. Otherwise only they will be.")
                .defineList("dimensions.list", (Supplier<List<? extends String>>) ArrayList::new, s -> s instanceof String);

        //serverCfg.comment("Biomes");
    }

    /**
    * 0 - never show bar
    * 1 - show bar if player hold rad meter in hand
    * 2 - show bar if player has rad meter on his toolbar
    * 3 - show bar if player has rad meter in his inventory
    * 4 - always show bar
    */
    public static final int RAD_BAR_DISPLAY_MODE = 2;
    public static final int RAD_BAR_SKIN_ID = 2;

    public static final int MIN_RAD_LEVEL = 0;
    public static final int MAX_RAD_LEVEL = 200;
    public static final int DEFAULT_RAD_LEVEL = 10;

    public static final int MIN_Y_LEVEL_AFFECTED = 0;
    public static final int MAX_Y_LEVEL_AFFECTED = 155;

    public static final int COVER_SEARCH_RADIUS = 30;
    public static final int MAX_DIST_TO_NEXT_LAYER = 6;
    public static final int MAX_COVER_THICKNESS = 7;
    public static final int MAX_LAYER_CHECKS = 10;

    public static final int MAX_EXPOSURE_LEVEL = 10;

    public static final int RAD_LEVEL_DECREASE_CHANCE = 10;

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
        NEVER,
        HAND,
        TOOLBAR,
        INVENTORY,
        ALWAYS
    }
}
