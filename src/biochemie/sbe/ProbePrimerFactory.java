package biochemie.sbe;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.functor.Algorithms;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.core.IsNull;

import biochemie.domspec.Primer;
import biochemie.domspec.ProbePrimer;
import biochemie.util.Helper;

public class ProbePrimerFactory extends PrimerFactory {
    public static final boolean[][] PROBEASSAYTYPES={
        {true,true,true,true},
        {false,true,true,true},
        {true,false,true,true},
        {true,true,false,true},
        {true,true,true,false},
        {true,true,false,false},
        {true,false,true,false},
        {true,false,false,true},
        {false,true,true,false},
        {false,true,false,true},
        {false,false,true,true},
        {true,false,false,false},
        {false,true,false,false},
        {false,false,true,false},
        {false,false,false,true},
    };
    public final static String[] ASSAYTYPES_DESC=new String[]{
        "ddA,ddC,ddG,ddT",
        "dA,ddC,ddG,ddT",
        "ddA,dC,ddG,ddT",
        "ddA,ddC,dG,ddT",
        "ddA,ddC,ddG,dT",
        "ddA,ddC,dG,dT",
        "ddA,dC,ddG,dT",
        "ddA,dC,dG,ddT",
        "dA,ddC,ddG,dT",
        "dA,ddC,dG,ddT",
        "dA,dC,ddG,ddT",
        "ddA,dC,dG,dT",
        "dA,ddC,dG,dT",
        "dA,dC,ddG,dT",
        "dA,dC,dG,ddT"
    };
    public static int getPos(char c){
        switch (c) {
        case 'A':
        case 'a':
            return 0;
        case 'C':
        case 'c':
            return 1;
        case 'G':
        case 'g':
            return 2;
        case 'T':
        case 't':
            return 3;
        default:
            throw new IllegalArgumentException("Invalid nucleotide '"+c+"'!");
        }
    }
    private final int givenAssay5;
    private final int givenAssay3;
    final PrimerFactory otherFactory;
    
    public ProbePrimerFactory(SBEOptions cfg, String id, String seq5,
            String snp, String seq3, String bautEin5, String bautEin3,
            int productlen, String givenMultiplexid, int givenAssay5, int givenAssay3, boolean userGiven,
            String unwanted, boolean rememberOutput){
        this(cfg,id,seq5,snp,seq3,bautEin5,bautEin3,productlen,givenMultiplexid,givenAssay5,givenAssay3,userGiven,unwanted,rememberOutput,null);
    }
    
    public ProbePrimerFactory(SBEOptions cfg, String id, String seq5,
            String snp, String seq3, String bautEin5, String bautEin3,
            int productlen, String givenMultiplexid, int givenAssay5, int givenAssay3, boolean userGiven,
            String unwanted, boolean rememberOutput, PrimerFactory otherFactory) {
        super(cfg, id, seq5, snp, seq3, bautEin5, bautEin3, productlen,
                givenMultiplexid, userGiven, unwanted, rememberOutput);
        this.givenAssay5=givenAssay5;
        this.givenAssay3=givenAssay3;
        this.otherFactory=otherFactory;
    }


    protected void createGivenPrimers() {
        System.out.println("Creating given probeprimers...");
        Collection otherPrimers=Collections.EMPTY_LIST;
        if(otherFactory!=null){
            otherFactory.createGivenPrimers();
            otherPrimers=otherFactory.getMultiplexables();
        }
        if(givenAssay5>=0  && givenAssay5 <PROBEASSAYTYPES.length && seq5!=null && seq5.length()!=-1){
            System.out.println("Using given 5' primer.");
            List addons=generateAddons(Primer._5_,givenAssay5);
            Collection other5Primers=Algorithms.collect(Algorithms.select(otherPrimers.iterator(),new UnaryPredicate(){
                public boolean test(Object obj) {
                    return ((Primer)obj).getType().equals(Primer._5_);
                }
                
            }));
            for (Iterator it = addons.iterator(); it.hasNext();) {
                String addon = (String ) it.next();
                if(other5Primers.size()>0){
                    for (Iterator it2 = other5Primers.iterator(); it2.hasNext();) {
                        Primer p = (Primer) it2.next();
                        ProbePrimer pp=new ProbePrimer(p,givenAssay5,addon);
                        pp.addObserver(this);
                        primercandidates.add(pp);
                    }
                }else{
                    ProbePrimer primer=new ProbePrimer(getId(),seq5,Primer._5_,snp,givenAssay5,addon,productlen,cfg.getSecStrucOptions(),cfg.getMinProductLenDiff());
                    primer.addObserver(this);
                    primercandidates.add(primer);
                }
            }
        }
        if(givenAssay3>=0 && givenAssay3 <PROBEASSAYTYPES.length && seq3!=null && seq3.length()!=-1){
            System.out.println("Using given 3' primer.");
            String rseq=Helper.revcomplPrimer(seq3);
            String rsnp=Helper.revcomplPrimer(snp);
            List addons=generateAddons(Primer._5_,givenAssay5);
            Collection other3Primers=Algorithms.collect(Algorithms.select(otherPrimers.iterator(),new UnaryPredicate(){
                public boolean test(Object obj) {
                    return ((Primer)obj).getType().equals(Primer._3_);
                }
                
            }));
            for (Iterator it = addons.iterator(); it.hasNext();) {
                String addon = (String ) it.next();
                if(other3Primers.size()>0){
                    for (Iterator it2 = other3Primers.iterator(); it2.hasNext();) {
                        Primer p = (Primer) it2.next();
                        ProbePrimer pp=new ProbePrimer(p,givenAssay5,addon);
                        pp.addObserver(this);
                        primercandidates.add(pp);
                    }
                }else{
                    ProbePrimer primer=new ProbePrimer(getId(),rseq,Primer._3_,rsnp,givenAssay5,addon,productlen,cfg.getSecStrucOptions(),cfg.getMinProductLenDiff());
                    primer.addObserver(this);
                    primercandidates.add(primer);
                }
            }
        }
            
    }

    protected List findBestPrimers(List liste) {

        List l=new ArrayList();
        for (int i = 0; i < PROBEASSAYTYPES.length; i++) {
           final int var=i;
           l.add(Algorithms.detect(liste.iterator(),new UnaryPredicate() {
                    public boolean test(Object obj) {
                        ProbePrimer p=((ProbePrimer)obj);
                        return p.getAssayType() == var && p.getType().equals(Primer._5_);
                    }
                },null));
           l.add(Algorithms.detect(liste.iterator(),new UnaryPredicate() {
                    public boolean test(Object obj) {
                        ProbePrimer p=((ProbePrimer)obj);
                        return p.getAssayType() == var && p.getType().equals(Primer._3_);
                    }
                },null));
        }
        Algorithms.remove(l.iterator(),IsNull.instance());
        chosen=null;
        return l;

    }

    public Collection createPossiblePrimers(String seq, String type) {
        Collection result=new ArrayList();
        String snp=this.snp;
        if(type.equals(Primer._3_))
            snp=Helper.complPrimer(snp);
        int start = getStartAssayType(type);
        if(otherFactory!=null){
            Collection otherPrimers=otherFactory.createPossiblePrimers(seq,type);
            for (int i=start+1; i < PROBEASSAYTYPES.length; i++) {
                List addons=generateAddons(type,i);
                for (Iterator it = otherPrimers.iterator(); it.hasNext();) {
                    Primer primer = (Primer) it.next();                    
                    for (Iterator itaddon = addons.iterator(); itaddon.hasNext();) {
                        String addon = (String ) itaddon.next();
                        result.add(new ProbePrimer(primer,i,addon));
                    }
                }
                if(start!=-1)
                    break;
            }
        }else
            for (int i = start+1; i < PROBEASSAYTYPES.length; i++) {
                List addons=generateAddons(type,i);
                for (Iterator it = addons.iterator(); it.hasNext();) {
                    String addon = (String ) it.next();
                    result.add(new ProbePrimer(getId(),seq,type,snp,i,addon,productlen,cfg.getSecStrucOptions(),cfg.getMinProductLenDiff()));
                }
                if(start!=-1)
                    break;
            }
        return result;
    }


    private int getStartAssayType(String type) {
        if(type.equals(Primer._3_))
            return givenAssay3;
        else
            return givenAssay5;
    }

    List generateAddons(String type, int assay) {
        List result=new ArrayList(4);
        String right=seq3;
        String snp=this.snp;
        if(type.equals(Primer._3_)){
            snp=Helper.complPrimer(snp);
            right=Helper.revcomplPrimer(seq5);
        }
        for(int i=0;i<snp.length();i++){
            StringBuffer sb=new StringBuffer();
            sb.append(snp.charAt(i));
            if(PROBEASSAYTYPES[assay][getPos(snp.charAt(i))]){//ist ein dd-Nukleotid
                result.add(sb.toString());
                continue;
            }
            int j=0;
            while(j<right.length()){
                sb.append(right.charAt(j));
                if(PROBEASSAYTYPES[assay][getPos(right.charAt(j))]){//ist ein dd-Nukleotid
                    result.add(sb.toString());
                    break;
                }
                j++;
            }
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
        sb.append(((ProbePrimer)chosen).getAssayType());
        sb.append(';');
        if(otherFactory!=null){
            sb.append(otherFactory.getCSVRow().split(";")[4]);
            sb.append(';');
        }
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
        return chosen.getType()+"_*_"+((ProbePrimer)chosen).getAssayType();
    }

    protected static final String CSV_OUTPUT_HEADER1 = 
        "PROBE Multiplex ID;"
        +"SBE-Primer ID;"
        +"Sequence;"
        +"SNP allele;"
        +"Probe assay type;";
protected static final String CSV_OUTPUT_HEADER2 = 
        "Primerlength;"
        +"GC contents;"
        +"Tm;"
        +"Excluded 5\' Primers;"
        +"Excluded 3\' Primers;"
        +"Primer from 3' or 5';"
        +"PCR-Product-length;"
        +"Actual sequence;"
        +"Sec.struc.: position (3\');"
        +"Sec.struc.: incorporated nucleotide;"
        +"Sec.struc.: class";

    public String getCsvheader() {
        String header=CSV_OUTPUT_HEADER1;
        if(otherFactory!=null){
            header += otherFactory.getCsvheader().split(";")[4]+";";//XXX dirty, uses knowledge of header
        }
        return header+CSV_OUTPUT_HEADER2;
    }

    protected void choose(Primer struct) {
        super.choose(struct);
        if(otherFactory!=null){
            otherFactory.choose(((ProbePrimer)struct).getIncludedPrimer());
        }
    }
}
