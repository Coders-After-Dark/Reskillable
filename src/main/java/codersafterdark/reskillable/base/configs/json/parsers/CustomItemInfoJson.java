package codersafterdark.reskillable.base.configs.json.parsers;

import codersafterdark.reskillable.api.data.ItemInfo;
import com.google.gson.*;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import java.lang.reflect.Type;

public class CustomItemInfoJson implements JsonDeserializer<ItemInfo> {
    @Override
    public ItemInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        JsonElement itemElem = obj.get("item");

        if (itemElem == null)
            throw new JsonParseException("No key 'item' given in ItemInfo json.");

        Item item = context.deserialize(itemElem, Item.class);
        int meta = !obj.has("metadata") ? 0 : obj.get("metadata").getAsString().equals("*") ? OreDictionary.WILDCARD_VALUE : obj.get("metadata").getAsInt();
        JsonElement nbtJson = obj.get("nbt");
        NBTTagCompound tagCompound = null;
        if (nbtJson != null) {
            tagCompound = context.deserialize(nbtJson, NBTTagCompound.class);
        }

        return new ItemInfo(item, meta, tagCompound);
    }
}
