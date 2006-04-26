package biochemie.sbe;

import java.util.Collection;

public interface PrimerCreatorCallback {
    public Collection createPossiblePrimers(String seq, String type);
}
