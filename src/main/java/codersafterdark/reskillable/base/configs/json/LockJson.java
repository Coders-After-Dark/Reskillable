package codersafterdark.reskillable.base.configs.json;

import codersafterdark.reskillable.api.data.LockKey;
import codersafterdark.reskillable.api.data.RequirementHolder;

public class LockJson {
    private RequirementHolder requirements;
    private LockKey lockKey;

    public LockJson(RequirementHolder requirements, LockKey lockKey) {
        this.requirements = requirements;
        this.lockKey = lockKey;
    }

    public RequirementHolder getRequirements() {
        return requirements;
    }

    public LockKey getLockKey() {
        return lockKey;
    }

    @Override
    public String toString() {
        return "LockJson{" +
                "requirements=" + requirements +
                ", lockKey=" + lockKey +
                '}';
    }
}
