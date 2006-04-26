package biochemie.sbe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import biochemie.calcdalton.CalcDalton;
import biochemie.domspec.PinpointPrimer;
import biochemie.domspec.Primer;
import biochemie.util.Helper;

public class PinpointPrimerFactory extends PrimerFactory {

    private final double maxMass;
    private final int tCount5;
    private final int tCount3;

    public PinpointPrimerFactory(SBEOptions cfg, String id, String seq5, String snp, String seq3, String bautEin5, String bautEin3, 
            int tCount5, int tCount3,
            int productlen, String givenMultiplexID, String unwanted, 
            boolean userGiven, boolean rememberOutput, double maxMass) {
        super(cfg, id, seq5, snp, seq3, bautEin5, bautEin3, productlen,givenMultiplexID,userGiven, unwanted, rememberOutput);
        this.maxMass=maxMass;
        this.tCount5=tCount5;
        this.tCount3=tCount3;
    }


    protected List findBestPrimers(List primers) {
        // TODO Auto-generated method stub
        return primers;
    }


    public String getCSVRow() {
        // TODO Auto-generated method stub
        return null;
    }



    protected void createGivenPrimers() {
        System.out.println("Creating given pinpoint primers...");
        if(tCount5>0){
            System.out.println("Using given 5' primer.");
            PinpointPrimer primer=new PinpointPrimer(getId(),seq5,Primer._5_,getSNP(),StringUtils.repeat("T",tCount5),cfg);
            primer.addObserver(this);
            primercandidates.add(primer);
        }
        if(tCount3>0){
            System.out.println("Using given 3' primer.");
            String rseq=Helper.revcomplPrimer(seq3);
            String rsnp=Helper.revcomplPrimer(snp);
            PinpointPrimer primer=new PinpointPrimer(getId(),rseq,Primer._3_,rsnp,StringUtils.repeat("T",tCount3),cfg);
            primer.addObserver(this);
            primercandidates.add(primer);
        }
    }


    public Collection createPossiblePrimers(String seq, String type) {
        Collection result=new ArrayList();
        CalcDalton cd=Helper.getCalcDalton();
        double mass=cd.calcPrimerMasse(seq);
        String addOn="";
        while(cd.calcPrimerAddonMasse(mass,addOn)<maxMass){
            result.add(new PinpointPrimer(getId(),type,seq,snp,addOn,cfg));
            addOn+="T";
        }
        return result;
    }


}
