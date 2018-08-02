package codersafterdark.reskillable.base.configs.json.parsers;

import codersafterdark.reskillable.base.configs.json.types.LockTypeGeneralJson;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class CustomGeneralLockTypeJson implements JsonDeserializer<LockTypeGeneralJson>/*, JsonSerializer<BaseLockTypeJson>*/ {
    @Override
    public LockTypeGeneralJson deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        LockTypeGeneralJson lock = new LockTypeGeneralJson();
        lock.requirements = new ArrayList<>();

        for (Map.Entry<String, JsonElement> stringJsonElementEntry : json.getAsJsonObject().entrySet()) {
            switch (stringJsonElementEntry.getKey()) {
                case "type":
                    lock.type = stringJsonElementEntry.getValue().getAsString();
                    break;
                case "requirements":
                    stringJsonElementEntry.getValue().getAsJsonArray().forEach(it -> lock.requirements.add(it.getAsString()));
                    break;
                default:
                    lock.params.put(stringJsonElementEntry.getKey(),
                            context.deserialize(stringJsonElementEntry.getValue(), Object.class));
            }
        }

        return lock;
    }

    /*
    Not needed for now
    @Override
    public JsonElement serialize(BaseLockTypeJson src, Type typeOfSrc, JsonSerializationContext context) {
        return null;
    }
    */
}
