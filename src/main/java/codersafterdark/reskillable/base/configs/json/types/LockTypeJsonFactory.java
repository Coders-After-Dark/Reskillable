package codersafterdark.reskillable.base.configs.json.types;

import codersafterdark.reskillable.base.configs.json.parsers.CustomGeneralLockTypeJson;
import codersafterdark.reskillable.base.configs.json.parsers.CustomLockTypeJson;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class LockTypeJsonFactory {
    private static Map<String, Type> types = new HashMap<>();
    private static Map<Type, JsonDeserializer> deserializerMap = new HashMap<>();

    static {
        registerType("itemstack", LockTypeItem.class);
    }

    public static Type getLockType(String string) {
        return types.getOrDefault(string, LockTypeGeneralJson.class);
    }

    public static void registerType(String name, Type type) {
        registerType(name, type, null);
    }

    public static void registerType(String name, Type type, JsonDeserializer deserializer) {
        types.put(name, type);
        if (deserializer != null) {
            deserializerMap.put(type, deserializer);
        }
    }

    public static Gson constructGSON() {
        GsonBuilder builder = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(BaseLockTypeJson.class, new CustomLockTypeJson())
                .registerTypeAdapter(LockTypeGeneralJson.class, new CustomGeneralLockTypeJson());

        deserializerMap.forEach(builder::registerTypeAdapter);

        return builder.create();
    }
}
