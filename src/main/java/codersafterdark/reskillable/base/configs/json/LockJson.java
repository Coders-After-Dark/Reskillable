package codersafterdark.reskillable.base.configs.json;

import codersafterdark.reskillable.api.data.LockKey;
import codersafterdark.reskillable.api.data.RequirementHolder;

public class LockJson {
    private RequirementHolder requirements;
    private LockKey modLockKey;

    public LockJson(RequirementHolder requirements, LockKey modLockKey) {
        this.requirements = requirements;
        this.modLockKey = modLockKey;
    }

    public RequirementHolder getRequirements() {
        return requirements;
    }

    public LockKey getModLockKey() {
        return modLockKey;
    }

    @Override
    public String toString() {
        return "LockJson{" +
                "requirements=" + requirements +
                ", modLockKey=" + modLockKey +
                '}';
    }
}
