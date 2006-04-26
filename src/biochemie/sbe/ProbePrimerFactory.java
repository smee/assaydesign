package biochemie.sbe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import biochemie.domspec.Primer;
import biochemie.domspec.ProbePrimer;
import biochemie.util.Helper;

public class ProbePrimerFactory extends PrimerFactory {
    public static final boolean[][] ASSAYTYPES={
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
    public static String[] ASSAYTYPES_DESC=new String[]{
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
    
    public ProbePrimerFactory(SBEOptions cfg, String id, String seq5,
            String snp, String seq3, String bautEin5, String bautEin3,
            int productlen, String givenMultiplexid, int givenAssay5, int givenAssay3, boolean userGiven,
            String unwanted, boolean rememberOutput) {
        super(cfg, id, seq5, snp, seq3, bautEin5, bautEin3, productlen,
                givenMultiplexid, userGiven, unwanted, rememberOutput);
        this.givenAssay5=givenAssay5;
        this.givenAssay3=givenAssay3;
    }


    protected void createGivenPrimers() {
        System.out.println("Creating given probeprimers...");
        if(givenAssay5>=0  && givenAssay5 <ASSAYTYPES.length){
            System.out.println("Using given 5' primer.");
            List addons=generateAddons(Primer._5_,givenAssay5);
            for (Iterator it = addons.iterator(); it.hasNext();) {
                String addon = (String ) it.next();
                ProbePrimer primer=new ProbePrimer(getId(),seq5,Primer._5_,getSNP(),givenAssay5,addon,cfg.getSecStrucOptions());
                primer.addObserver(this);
                primercandidates.add(primer);
            }
        }
        if(givenAssay3>=0 && givenAssay3 <ASSAYTYPES.length){
            System.out.println("Using given 3' primer.");
            String rseq=Helper.revcomplPrimer(seq3);
            String rsnp=Helper.revcomplPrimer(snp);
            List addons=generateAddons(Primer._3_,givenAssay5);
            for (Iterator it = addons.iterator(); it.hasNext();) {
                String addon = (String ) it.next();
                ProbePrimer primer=new ProbePrimer(getId(),rseq,Primer._3_,rsnp,givenAssay3,addon,cfg.getSecStrucOptions());
                primer.addObserver(this);
                primercandidates.add(primer);
            }
        }

    }

    protected List findBestPrimers(List primers) {
        // TODO Auto-generated method stub
        return primers;
    }

    public String getCSVRow() {
        // TODO Auto-generated method stub
        return null;
    }


    public Collection createPossiblePrimers(String seq, String type) {
        //TODO kombination mit entweder cleavable oder pinpoint
        Collection result=new ArrayList();
        for (int i = 0; i < ASSAYTYPES.length; i++) {
            List addons=generateAddons(type,i);
            for (Iterator it = addons.iterator(); it.hasNext();) {
                String addon = (String ) it.next();
                result.add(new ProbePrimer(getId(),seq,type,getSNP(),i,addon,cfg.getSecStrucOptions()));
            }
        }
        return result;
    }


    List generateAddons(String type, int assay) {
        List result=new ArrayList(4);
        String right=seq3;
        String snp=getSNP();
        if(type.equals(Primer._3_)){
            snp=Helper.complPrimer(snp);
            right=Helper.revcomplPrimer(seq5);
        }
        for(int i=0;i<snp.length();i++){
            StringBuffer sb=new StringBuffer();
            sb.append(snp.charAt(i));
            if(ASSAYTYPES[assay][getPos(snp.charAt(i))]){//ist ein dd-Nukleotid
                result.add(sb.toString());
                continue;
            }
            int j=0;
            while(j<right.length()){
                sb.append(right.charAt(j));
                if(ASSAYTYPES[assay][getPos(right.charAt(j))]){//ist ein dd-Nukleotid
                    result.add(sb.toString());
                    break;
                }
                j++;
            }
        }
        return result;
    }


    public String[] getCsvheader() {
        // TODO Auto-generated method stub
        return null;
    }

}
