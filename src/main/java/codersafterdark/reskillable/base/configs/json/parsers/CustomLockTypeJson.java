package codersafterdark.reskillable.base.configs.json.parsers;

import codersafterdark.reskillable.base.configs.json.types.BaseLockTypeJson;
import codersafterdark.reskillable.base.configs.json.LockTypeJsonFactory;
import com.google.gson.*;

import java.lang.reflect.Type;

public class CustomLockTypeJson implements JsonDeserializer<BaseLockTypeJson>/*, JsonSerializer<BaseLockTypeJson>*/ {
    @Override
    public BaseLockTypeJson deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String s = json.getAsJsonObject().get("type").getAsString();

        return context.deserialize(json, LockTypeJsonFactory.getLockType(s));
    }

    /*
    Not needed for now
    @Override
    public JsonElement serialize(BaseLockTypeJson src, Type typeOfSrc, JsonSerializationContext context) {
        return null;
    }
    */
}

