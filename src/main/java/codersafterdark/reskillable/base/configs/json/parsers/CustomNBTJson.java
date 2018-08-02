package codersafterdark.reskillable.base.configs.json.parsers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

import java.lang.reflect.Type;

public class CustomNBTJson implements JsonDeserializer<NBTTagCompound> {
    @Override
    public NBTTagCompound deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            return JsonToNBT.getTagFromJson(json.toString());
        } catch (NBTException e) {
            e.printStackTrace();
        }

        return null;
    }
}
