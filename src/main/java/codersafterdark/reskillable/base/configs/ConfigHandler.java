package codersafterdark.reskillable.base.configs;

import codersafterdark.reskillable.Reskillable;
import codersafterdark.reskillable.base.LevelLockHandler;
import codersafterdark.reskillable.base.configs.json.LockJson;
import codersafterdark.reskillable.base.configs.json.LockTypeJsonFactory;
import codersafterdark.reskillable.lib.LibMisc;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
    public static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
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
                            LevelLockHandler.addLockByKey(lockJson.getModLockKey(), lockJson.getRequirements());
                        }

                    } catch (IOException | JsonParseException e) {
                        Reskillable.logger.error("Couldn't load json from " + path, e);
                    }
                });


        // File mainLocks = new File(jsonDir, "defaultLocks.json");
        // String json = gson.toJson(LevelLockHandler.DEFAULT_SKILL_LOCKS);
        // ConfigUtilities.writeStringToFile(json, mainLocks);

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

        String s2 = "[{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:iron_shovel\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:gathering|5\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:iron_axe\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:gathering|5\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:iron_sword\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:attack|5\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:iron_pickaxe\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:mining|5\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:iron_hoe\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:farming|5\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:iron_helmet\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:defense|5\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:iron_chestplate\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:defense|5\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:iron_leggings\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:defense|5\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:iron_boots\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:defense|5\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:golden_shovel\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:gathering|5\",\n" +
                "\t\t\t\"reskillable:magic|5\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:golden_axe\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:gathering|5\",\n" +
                "\t\t\t\"reskillable:magic|5\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:golden_sword\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:attack|5\",\n" +
                "\t\t\t\"reskillable:magic|5\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:golden_pickaxe\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:mining|5\",\n" +
                "\t\t\t\"reskillable:magic|5\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:golden_hoe\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:farming|5\",\n" +
                "\t\t\t\"reskillable:magic|5\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:golden_helmet\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:defense|5\",\n" +
                "\t\t\t\"reskillable:magic|5\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:golden_chestplate\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:defense|5\",\n" +
                "\t\t\t\"reskillable:magic|5\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:golden_leggings\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:defense|5\",\n" +
                "\t\t\t\"reskillable:magic|5\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:golden_boots\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:defense|5\",\n" +
                "\t\t\t\"reskillable:magic|5\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:diamond_shovel\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:gathering|16\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:diamond_axe\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:gathering|16\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:diamond_sword\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:attack|16\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:diamond_pickaxe\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:mining|16\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:diamond_hoe\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:farming|16\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:diamond_helmet\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:defense|16\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:diamond_chestplate\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:defense|16\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:diamond_leggings\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:defense|16\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:diamond_boots\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:defense|16\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:shears\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:farming|5\",\n" +
                "\t\t\t\"reskillable:gathering|5\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:fishing_rod\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:gathering|8\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:shield\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:defense|8\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:bow\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:attack|8\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:ender_pearl\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:magic|8\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:ender_eye\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:magic|16\",\n" +
                "\t\t\t\"reskillable:building|8\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:elytra\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:defense|16\",\n" +
                "\t\t\t\"reskillable:agility|24\",\n" +
                "\t\t\t\"reskillable:magic|16\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:lead\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:farming|5\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:end_crystal\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:building|24\",\n" +
                "\t\t\t\"reskillable:magic|32\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:iron_horse_armor\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:defense|5\",\n" +
                "\t\t\t\"reskillable:agility|5\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:golden_horse_armor\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:defense|5\",\n" +
                "\t\t\t\"reskillable:magic|5\",\n" +
                "\t\t\t\"reskillable:agility|5\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:diamond_horse_armor\",\n" +
                "\t\t\"meta\": \"*\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:defense|16\",\n" +
                "\t\t\t\"reskillable:agility|16\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:fireworks\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:agility|24\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:dye\",\n" +
                "\t\t\"meta\": \"15\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:farming|12\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:saddle\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:agility|12\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:redstone\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:building|5\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:redstone_torch\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:building|5\"\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"type\": \"itemstack\",\n" +
                "\t\t\"stack\": \"minecraft:skull\",\n" +
                "\t\t\"meta\": \"1\",\n" +
                "\t\t\"requirements\": [\n" +
                "\t\t\t\"reskillable:building|20\",\n" +
                "\t\t\t\"reskillable:attack|20\",\n" +
                "\t\t\t\"reskillable:defense|20\"\n" +
                "\t\t]\n" +
                "\t}\n" +
                "]";

        List<LockJson> obj = LockTypeJsonFactory.constructGSON().fromJson(s2, new TypeToken<List<LockJson>>() {
        }.getType());

        System.out.println("obj = " + obj);
    }
}
