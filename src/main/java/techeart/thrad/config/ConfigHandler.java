package techeart.thrad.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import techeart.thrad.MainClass;

import java.io.File;

public class ConfigHandler
{
    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SERVER_CONFIG;
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec CLIENT_CONFIG;

    static
    {
        Configuration.init(SERVER_BUILDER, CLIENT_BUILDER);
        SERVER_CONFIG = SERVER_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    public static void register()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG);
    }

    public static void load(String configFileName)
    {
        loadConfig(SERVER_CONFIG, configFileName + "-server");
        loadConfig(CLIENT_CONFIG, configFileName + "-client");
    }

    public static void loadConfig(ForgeConfigSpec cfg, String fileName)
    {
        String path = FMLPaths.CONFIGDIR.get().resolve(fileName + ".toml").toString();
        final CommentedFileConfig file = CommentedFileConfig
                .builder(new File(path))
                .sync()
                .autosave()
//                .autoreload()
                .writingMode(WritingMode.REPLACE)
                .build();
        file.load();
        MainClass.LOGGER.info("Loaded config: " + path);
        cfg.setConfig(file);

        Configuration.postLoad(file);
    }
}
