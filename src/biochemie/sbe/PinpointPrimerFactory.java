package biochemie.sbe;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.functor.Algorithms;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.lang.StringUtils;

import biochemie.calcdalton.CalcDalton;
import biochemie.domspec.PinpointPrimer;
import biochemie.domspec.Primer;
import biochemie.util.Helper;

public class PinpointPrimerFactory extends PrimerFactory {
    static public final String CSV_OUTPUT_HEADER = 
                    "PINPOINT Multiplex ID;"
                    +"SBE-Primer ID;"
                    +"Sequence incl. 5’tag;"
                    +"SNP allele;"
                    +"Number of dT;"
                    +"Primerlength incl 5’ tag;"
                    +"GC contents excl 5’tag;"
                    +"Tm excl. 5’ tag;"
                    +"Excluded 5\' Primers;"
                    +"Excluded 3\' Primers;"
                    +"Primer from 3' or 5';"
                    +"PCR-Product-length;"
                    +"Actual sequence excl. 5’ tag;"
                    +"Sec.struc.: position (3\');"
                    +"Sec.struc.: incorporated nucleotide;"
                    +"Sec.struc.: class";
    
    private final double maxMass;
    private final int tCount5;
    private final int tCount3;

    public PinpointPrimerFactory(SBEOptions cfg, String id, String seq5, String snp, String seq3, String bautEin5, String bautEin3, 
            int tCount5, int tCount3,
            int productlen, String givenMultiplexID, String unwanted, 
            boolean userGiven, boolean rememberOutput) {
        super(cfg, id, seq5, snp, seq3, bautEin5, bautEin3, productlen,givenMultiplexID,userGiven, unwanted, rememberOutput);
        this.maxMass=cfg.getCalcDaltonMaxMass();
        this.tCount5=tCount5;
        this.tCount3=tCount3;
    }


    protected List findBestPrimers(List primers) {
        List result=new ArrayList();
        if(primers.size()==0)
            return result;
        int maxLen=findMaxT(primers);
        for(int len=0;len<=maxLen;len++){
            final int newLen=len;
            List primersOfThisTLenList=(List) Algorithms.collect(Algorithms.select(primers.iterator(),new UnaryPredicate(){
                public boolean test(Object obj) {
                    return ((PinpointPrimer)obj).getTTail().length()==newLen;
                }
            }),new ArrayList());
            Collections.sort(primersOfThisTLenList,new TemperatureDistanceAndHairpinComparator(cfg.getOptTemperature()));
            PinpointPrimer p=(PinpointPrimer) Algorithms.detect(primersOfThisTLenList.iterator(),new UnaryPredicate(){
                public boolean test(Object obj) {
                    return ((Primer)obj).getType().equals(Primer._5_);
                }
            },null);
            if(p!=null)
                result.add(p);
            p=(PinpointPrimer)Algorithms.detect(primersOfThisTLenList.iterator(),new UnaryPredicate(){
                public boolean test(Object obj) {
                    return ((Primer)obj).getType().equals(Primer._3_);
                }
            },null);
            if(p!=null)
                result.add(p);
        }        
        return result;
    }





    private int findMaxT(List primers) {
        int max=-1;
        for (Iterator it = primers.iterator(); it.hasNext();) {
            PinpointPrimer p = (PinpointPrimer) it.next();
            if(p.getTTail().length()> max)
                max=p.getTTail().length();
        }
        return max;
    }


    protected void createGivenPrimers() {
        System.out.println("Creating given pinpoint primers...");
        if(tCount5>0){
            System.out.println("Using given 5' primer.");
            PinpointPrimer primer=new PinpointPrimer(getId(),seq5,Primer._5_,snp,StringUtils.repeat("T",tCount5),cfg);
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
            result.add(new PinpointPrimer(getId(),seq,type,snp,addOn,cfg));
            addOn+="T";
        }
        return result;
    }

    public String getCSVRow() {
        if(chosen == null && primercandidates.size()==0)
            return ";"+getId()+";;;;;;;" +invalidreason5
                    + ";" + invalidreason3
                    + ";;;;;;;;;;";

        assertPrimerChosen();
        StringBuffer sb=new StringBuffer();
        DecimalFormat df= new DecimalFormat("0.00");
        sb.append(chosen.getPlexID());
        sb.append(';');
        sb.append(getId());
        sb.append(';');
        sb.append(chosen.getCompletePrimerSeq());
        sb.append(';');
        sb.append(chosen.getSNP());
        sb.append(';');
        sb.append(((PinpointPrimer)chosen).getTTail().length());
        sb.append(';');
        sb.append(getFavSeq().length());
        sb.append(';');
        sb.append(df.format(chosen.getGCGehalt()));
        sb.append(';');
        sb.append(df.format(chosen.getTemperature()));
        sb.append(';');
        sb.append(invalidreason5);
        sb.append(';');
        sb.append(invalidreason3);
        sb.append(';');
        sb.append(getType());
        sb.append(';');
        sb.append(getProductLength());
        sb.append(';');
        sb.append(chosen.getPrimerSeq());
        sb.append(';');
        sb.append(chosen.getCSVSekStructuresSeparatedBy(";"));
        return sb.toString();
    }

    public String getFilter() {
        assertPrimerChosen();
        return chosen.getType()+"_*_"+((PinpointPrimer)chosen).getTTail().length();
    }


    public String getCsvheader() {
        return CSV_OUTPUT_HEADER;
    }

}
