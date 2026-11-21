package neva.ware.utility.interfaces;

import neva.ware.features.modules.combat.AttackAura;

public interface IOtherClientPlayerEntity {
    void resolve(AttackAura.Resolver var1);

    void releaseResolver();
}
