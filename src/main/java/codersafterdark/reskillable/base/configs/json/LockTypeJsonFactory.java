package codersafterdark.reskillable.base.configs.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import net.minecraft.item.Item;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class LockTypeJsonFactory {
    private static Map<Type, JsonDeserializer> deserializerMap = new HashMap<>();

    static {
        registerDeserializer(LockJson.class, new CustomLockJson());
        registerDeserializer(Item.class, new CustomItemJson());
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
