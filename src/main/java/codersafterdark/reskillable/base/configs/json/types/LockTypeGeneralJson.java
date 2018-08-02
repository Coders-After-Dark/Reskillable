package codersafterdark.reskillable.base.configs.json.types;

import java.util.HashMap;
import java.util.Map;

public class LockTypeGeneralJson extends BaseLockTypeJson {
    public Map<String, Object> params = new HashMap<>();

    @Override
    public String toString() {
        return "LockTypeGeneralJson{" +
                "params=" + params +
                ", requirements=" + requirements +
                ", type='" + type + '\'' +
                '}';
    }
}
