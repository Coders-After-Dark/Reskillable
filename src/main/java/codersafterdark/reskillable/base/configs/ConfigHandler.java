package codersafterdark.reskillable.base.configs;

import codersafterdark.reskillable.api.data.LockKey;
import codersafterdark.reskillable.base.LevelLockHandler;
import codersafterdark.reskillable.base.configs.json.parsers.CustomGeneralLockTypeJson;
import codersafterdark.reskillable.base.configs.json.parsers.CustomLockTypeJson;
import codersafterdark.reskillable.base.configs.json.types.BaseLockTypeJson;
import codersafterdark.reskillable.base.configs.json.types.LockTypeGeneralJson;
import codersafterdark.reskillable.base.configs.json.LockTypeJsonFactory;
import codersafterdark.reskillable.lib.LibMisc;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static codersafterdark.reskillable.base.configs.ConfigUtilities.loadPropBool;

public class ConfigHandler {

    /////////////
    // Configs //
    /////////////
    public static Configuration mainConfig;
    public static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(BaseLockTypeJson.class, new CustomLockTypeJson())
            .registerTypeAdapter(LockTypeGeneralJson.class, new CustomGeneralLockTypeJson())
            .create();
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
        loadJSONLocks();
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

    public static void loadJSONLocks() {
        File mainLocks = new File(jsonDir, "defaultLocks.json");
        String json = gson.toJson(LevelLockHandler.DEFAULT_SKILL_LOCKS);
        ConfigUtilities.writeStringToFile(json, mainLocks);
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

    public static void main(String[] args) {
        String s = "[{\n" +
                "\t\t\"type\": \"modid\",\n" +
                "\t\t\"modid\": \"modid\",\n" +
                "\t\t\"ntb\": \"string\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"string\",\n" +
                "\t\t\t\"string2\",\n" +
                "\t\t\t\"string3\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"mod:stack:2\",\n" +
                "\t\t\"ntb\": \"string\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"string\",\n" +
                "\t\t\t\"string2\",\n" +
                "\t\t\t\"string3\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"locktype\",\n" +
                "\t\t\"target\": \"target\",\n" +
                "\t\t\"additionalparam\": \"primitiveType\",\n" +
                "\t\t\"additionalparams2\": \"primitiveType2\",\n" +
                "\t\t\"additionalparams3\": \"primitiveType3\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"string\",\n" +
                "\t\t\t\"string2\",\n" +
                "\t\t\t\"string3\"\n" +
                "\t\t]\n" +
                "\t}\n" +
                "]";

        List<BaseLockTypeJson> obj = LockTypeJsonFactory.constructGSON().fromJson(s, new TypeToken<List<BaseLockTypeJson>>() {}.getType());

        for (BaseLockTypeJson baseLockTypeJson : obj) {
            LevelLockHandler.addLockByKey(LockKey);
        }
        System.out.println("obj = " + obj);
    }
}
