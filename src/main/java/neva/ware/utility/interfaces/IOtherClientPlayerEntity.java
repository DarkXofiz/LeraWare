package neva.ware.utility.interfaces;

import neva.ware.features.modules.combat.Aura;

public interface IOtherClientPlayerEntity {
   void resolve(Aura.Resolver var1);

   void releaseResolver();
}
