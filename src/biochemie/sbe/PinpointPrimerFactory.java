package biochemie.sbe;

import java.util.List;

public class PinpointPrimerFactory extends PrimerFactory {

    private final double maxMass;

    public PinpointPrimerFactory(SBEOptions cfg, String id, String seq5, String snp, String seq3, String bautEin5, String bautEin3, 
            int productlen, String givenMultiplexID, String unwanted, 
            boolean userGiven, boolean rememberOutput, double maxMass) {
        super(cfg, id, seq5, snp, seq3, bautEin5, bautEin3, productlen,givenMultiplexID,userGiven, unwanted, rememberOutput);
        this.maxMass=maxMass;
    }


    protected List findBestPrimers(List primers) {
        // TODO Auto-generated method stub
        return primers;
    }


    public String getCSVRow() {
        // TODO Auto-generated method stub
        return null;
    }


    protected void createValidPrimerCandidates() {
        // TODO Auto-generated method stub
        
    }


    protected void createGivenPrimers() {
        // TODO Auto-generated method stub
        
    }

}
