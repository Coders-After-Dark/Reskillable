package codersafterdark.reskillable.base.configs.json;

import codersafterdark.reskillable.api.data.ItemInfo;
import codersafterdark.reskillable.base.configs.json.parsers.CustomItemInfoJson;
import codersafterdark.reskillable.base.configs.json.parsers.CustomItemJson;
import codersafterdark.reskillable.base.configs.json.parsers.CustomLockJson;
import codersafterdark.reskillable.base.configs.json.parsers.CustomNBTJson;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class LockTypeJsonFactory {
    private static Map<Type, JsonDeserializer> deserializerMap = new HashMap<>();

    static {
        registerDeserializer(LockJson.class, new CustomLockJson());
        registerDeserializer(Item.class, new CustomItemJson());
        registerDeserializer(ItemInfo.class, new CustomItemInfoJson());
        registerDeserializer(NBTTagCompound.class, new CustomNBTJson());
    }

    public static void registerDeserializer(Type type, JsonDeserializer deserializer) {
        deserializerMap.put(type, deserializer);
    }

    public static Gson constructGSON() {
        GsonBuilder builder = new GsonBuilder()
                .setPrettyPrinting();

        deserializerMap.forEach(builder::registerTypeAdapter);

        return builder.create();
    }
}
