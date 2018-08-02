package codersafterdark.reskillable.base.configs.json.parsers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.item.Item;

import java.lang.reflect.Type;

public class CustomItemJson implements JsonDeserializer<Item> {
    @Override
    public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Item.getByNameOrId(json.getAsString());
    }
}
