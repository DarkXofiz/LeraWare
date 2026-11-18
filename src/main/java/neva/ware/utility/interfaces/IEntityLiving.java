package neva.ware.utility.interfaces;

import java.util.List;
import neva.ware.features.modules.combat.Aura;

public interface IEntityLiving {
   double getPrevServerX();

   double getPrevServerY();

   double getPrevServerZ();

   List<Aura.Position> getPositionHistory();
}
