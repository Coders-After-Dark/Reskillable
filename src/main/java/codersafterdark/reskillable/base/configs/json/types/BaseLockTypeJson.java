package codersafterdark.reskillable.base.configs.json.types;

import java.util.List;

public abstract class BaseLockTypeJson {
    public List<String> requirements;
    public String type;

    @Override
    public String toString() {
        return "BaseLockTypeJson{" +
                "requirements=" + requirements +
                ", type='" + type + '\'' +
                '}';
    }
}
