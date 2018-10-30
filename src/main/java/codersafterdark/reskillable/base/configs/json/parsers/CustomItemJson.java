package codersafterdark.reskillable.base.configs.json.parsers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.lang.reflect.Type;

public class CustomItemJson implements JsonDeserializer<Item> {
    @Override
    public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(json.getAsString()));
    }
}
