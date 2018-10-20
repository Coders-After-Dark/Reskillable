package codersafterdark.reskillable.base.configs;

import codersafterdark.reskillable.Reskillable;
import codersafterdark.reskillable.base.LevelLockHandler;
import codersafterdark.reskillable.base.configs.json.LockJson;
import codersafterdark.reskillable.base.configs.json.LockTypeJsonFactory;
import codersafterdark.reskillable.lib.LibMisc;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static codersafterdark.reskillable.base.configs.ConfigUtilities.loadPropBool;

public class ConfigHandler {

    /////////////
    // Configs //
    /////////////
    public static Configuration mainConfig;

    /// Main Config ///
    public static boolean disableSheepWool = true;
    public static boolean enforceFakePlayers = true;

    ////////////////////
    // Default Values //
    ////////////////////
    public static boolean enableTabs = true;
    public static boolean enableLevelUp = true;
    public static boolean hideRequirements = true;
    public static Map<String, Configuration> cachedConfigs = new HashMap<>();
    /////////////////
    // Directories //
    /////////////////
    private static File configDir;
    private static File jsonDir;

    public static void init(File file) {
        generateFolder(file);
        mainConfig = new Configuration(new File(configDir.getPath(), "reskillable.cfg"));
        mainConfig.load();
        loadData();
        // loadJSONLocks();
        cachedConfigs.put(LibMisc.MOD_ID, mainConfig);
        MinecraftForge.EVENT_BUS.register(ConfigListener.class);
    }

    public static void loadData() {
        disableSheepWool = loadPropBool(mainConfig, "Disable Sheep Dropping Wool on Death", "", disableSheepWool);
        enforceFakePlayers = loadPropBool(mainConfig, "Enforce requirements on Fake Players", "", true);
        enableTabs = loadPropBool(mainConfig, "Enable Reskillable Tabs", "Set this to false if you don't want to use skills, just the advancement locks", true);
        enableLevelUp = loadPropBool(mainConfig, "Enable Level-Up Button", "Set this to false to remove the level-up button if you don't want to use another means to leveling-up skills!", true);
        hideRequirements = loadPropBool(mainConfig, "Hide Requirements", "Set this to false to not require holding down the shift key to view requirements!", true);

        String desc = "Set requirements for items in this list. Each entry is composed of the item key and the requirements\n"
                + "The item key is in the simple mod:item_id format. Optionally, it can be in mod:item_id:metadata, if you want to match metadata.\n"
                + "The requirements are in a comma separated list, each in a key|value format. For example, to make an iron pickaxe require 5 mining\n"
                + "and 5 building, you'd use the following string:\n"
                + "\"minecraft:iron_pickaxe=mining|5,building|5\"\n\n"
                + "Item usage can also be locked behind an advancement, by using adv|id. For example, to make the elytra require the \"Acquire Hardware.\" advancement\n"
                + "you'd use the following string:\n"
                + "\"minecraft:elytra=adv|minecraft:story/smelt_iron\"\n\n"
                + "Skill requirements and advancements can be mixed and matched, so you can make an item require both, if you want.\n"
                + "You can also lock placed blocks from being used or broken, in the same manner.\n\n"
                + "Locks defined here apply to all the following cases: Right clicking an item, placing a block, breaking a block, using a block that's placed,\n"
                + "left clicking an item, using an item to break any block, and equipping an armor item.\n\n"
                + "You can lock entire mods by just using their name as the left argument. You can then specify specific items to not be locked,\n"
                + "by defining their lock in the normal way. If you want an item to not be locked in this way, use \"none\" after the =";
        String[] locks = mainConfig.getStringList("Skill Locks", Configuration.CATEGORY_GENERAL, LevelLockHandler.DEFAULT_SKILL_LOCKS, desc);

        LevelLockHandler.loadFromConfig(locks);

        if (mainConfig.hasChanged()) {
            mainConfig.save();
        }
    }

    public static void loadJSONLocks() throws IOException {
        Reskillable.logger.info("Starting to load json");
        if (!jsonDir.exists() && jsonDir.mkdir()) {
            Reskillable.logger.warn("Couldn't create json lock file directory");
        }

        File[] list = jsonDir.listFiles();
        if (list != null && list.length == 0) {
            Files.copy(ConfigHandler.class.getResourceAsStream("/defaultLocks.json"), Paths.get(jsonDir.getAbsolutePath(), "defaultLocks.json"));
        }

        Files.walk(jsonDir.toPath())
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".json"))
                .sorted()
                .forEachOrdered(path -> {
                    Reskillable.logger.info("Starting to load from file " + path);

                    try (FileReader reader = new FileReader(path.toFile())) {
                        List<LockJson> obj = LockTypeJsonFactory.constructGSON().fromJson(reader, new TypeToken<List<LockJson>>() {
                        }.getType());
                        Reskillable.logger.info("Locks loaded " + obj.size() + ": " + obj);

                        for (LockJson lockJson : obj) {
                            LevelLockHandler.addLockByKey(lockJson.getLockKey(), lockJson.getRequirements());
                        }

                    } catch (IOException | JsonParseException e) {
                        Reskillable.logger.error("Couldn't load json from " + path, e);
                    }
                });
    }


    public static void generateFolder(File file) {
        File dir = new File(file, "codersafterdark");
        File dir2 = new File(dir, "locks");
        if (!dir.exists()) {
            dir.mkdirs();
            if (!dir2.exists()) {
                dir2.mkdirs();
            }
        }
        configDir = dir;
        jsonDir = dir2;
    }
}
