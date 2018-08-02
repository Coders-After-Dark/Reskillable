package codersafterdark.reskillable.base.configs.json.types;

public class LockTypeItem extends BaseLockTypeJson {
    String nbt;
    String itemstack;

    @Override
    public String toString() {
        return "LockTypeItem{" +
                "nbt='" + nbt + '\'' +
                ", itemstack='" + itemstack + '\'' +
                ", requirements=" + requirements +
                ", type='" + type + '\'' +
                '}';
    }
}
