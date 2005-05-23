/*
 * Created on 25.10.2003
 *
 */
package biochemie.pcr;

/**
 *
 * @author Steffen
 *
 */
public class PrimerPair {
    public static final int BLAT=0;
    public static final int REPSEQ=1;
    public static final int GCDIFF=2;
    public static final int SNP=3;
    public static final int EXON=4;
    public static final int HAIR=5;
    public static final int HOMO=6;
    public static final int CROSS=7;
    public static final String[] scorenames= {"BLAT","Repetetive Sequenzen"
                                             ,"GC-Differenz","SNP","Exon/Intron"
                                             ,"Hairpin-Analyse","Homodimer-Analyse"
                                             ,"Crossdimer-Analyse"};

    public final String leftp;
    public final String rightp;
    public final int leftpos;
    public final int leftlen;
    public final int rightpos;
    public final int rightlen;
    public final int productlen;
    public final float gcdiff;
    /**
     * Enthält die Punkte an den jeweiligen Positionen.
     */
    public final int[] scores=new int[scorenames.length];
    private final boolean[] flags=new boolean[scorenames.length];
	private final int posinfile;
    private final int anhangsize;
    
    /**
     * Erstellt ein neues Primerpair. 
     * @param l
     * @param r
     * @param leftpos ACHTUNG: rightpos ist die position, an der der rechte Primer endet!
     * @param rightpos
     * @param gcdiff
     */
    public PrimerPair(String l, String r, int leftpos, int rightpos,float gcdiff, int posinfile, int anhangsize)  {
        this.leftp=l.toUpperCase();
        this.rightp=r.toUpperCase();
        this.leftpos=leftpos;
        this.rightpos=rightpos;
        this.leftlen=l.length();
        this.rightlen=r.length();
        this.productlen=this.rightpos-this.leftpos+1;
        this.gcdiff=gcdiff;
        this.posinfile = posinfile;
        this.anhangsize=anhangsize;
    }
    public int getOverallScore() {
        int sum=0;
        for (int i= 0; i < this.scores.length; i++) {
            sum+=scores[i];
        }
        return sum;
    }
    public int getOriginalPosition(){
    	return posinfile;
    }
    private void setScoreString(StringBuffer sb,int id) {
        sb.append(' '+scorenames[id]);
        for(int i=0;i<25-scorenames[id].length();i++)
            sb.append(' ');
        sb.append(": "+this.scores[id]);
        sb.append('\n');
    }
    public String toString() {
        StringBuffer sb=new StringBuffer("[Left primer : ");
         sb.append(leftp+'\n');
         sb.append("start,length  : "+(leftpos+1)+", "+leftlen+'\n');
         sb.append("Right primer  : "+rightp+'\n');
         sb.append("start,length  : "+(rightpos+1)+", "+rightlen+'\n');
         sb.append("Produktlaenge : "+productlen+'\n');
         sb.append("Pos in primer3: "+posinfile+'\n');
         sb.append("GC-Differenz  : "+gcdiff+'\n');
         sb.append("Anhangsize    : "+anhangsize+'\n');
         sb.append("Scores :\n--------\n");
         if(flags[BLAT]) {
             setScoreString(sb,BLAT);
         }
        if(flags[EXON]) {
            setScoreString(sb,EXON);
        }
        if(flags[GCDIFF]) {
            setScoreString(sb,GCDIFF);
        }
        if(flags[REPSEQ]) {
            setScoreString(sb,REPSEQ);
        }
        if(flags[HAIR]) {
            setScoreString(sb,HAIR);
        }
        if(flags[HOMO]) {
            setScoreString(sb,HOMO);
        }
        if(flags[CROSS]) {
            setScoreString(sb,CROSS);
        }
        if(flags[SNP]) {
            setScoreString(sb,SNP);
        }
         sb.append(']');
         return sb.toString() ;      
    }

    /**
     * Konstruktor, um einfach große Primer zum Testen zu basteln.
     * @param leftpos
     * @param leftlen
     * @param rightpos
     * @param rightlen
     */
    public PrimerPair(int leftpos, int leftlen, int rightpos, int rightlen) {
        StringBuffer sb=new StringBuffer(leftlen);
        for(int i=0;i<leftlen;i++)   {
            sb.append('A');
        }
        leftp=sb.toString();
        sb=new StringBuffer(rightlen);
        for(int i=0;i<rightlen;i++)  {
            sb.append('A');
        }
        rightp=sb.toString();
        this.leftpos=leftpos;
        this.rightpos=rightpos;
        this.leftlen=leftlen;
        this.rightlen=rightlen;
        this.productlen=this.rightpos-this.leftpos+1;
        this.gcdiff=0;
        this.anhangsize=0;
        this.posinfile=0;
    }
    
    public void addBlatScore(int n) {
    	flags[BLAT]=true;
        scores[BLAT]+=n;
    }
    public void addExonScore(int n) {
    	flags[EXON]=true;
    	scores[EXON]+=n;
    }
    public void addSNPScore(int n) {
    	flags[SNP]=true;
    	scores[SNP]+=n;
    }
    public void addRepSeqScore(int n) {
    	flags[REPSEQ]=true;
        scores[REPSEQ]+=n;
    }
    public void addGCScore(int n) {
    	flags[GCDIFF]=true;
    	scores[GCDIFF]+=n;
    }
    public void addHairScore(int n) {
    	flags[HAIR]=true;
    	scores[HAIR]+=n;
    }
    public void addHomoScore(int n) {
    	flags[HOMO]=true;
    	scores[HOMO]+=n;
    }
    public void addCrossScore(int n) {
    	flags[CROSS]=true;
    	scores[CROSS]+=n;
    }
	/**
	 * @return
	 */
	public String toCSVString(int num,int cyclenum) {
        StringBuffer sb=new StringBuffer(Integer.toString(num));
        sb.append(';');
        sb.append(posinfile);
        sb.append(';');
        sb.append(leftp);
        sb.append(';');
        sb.append(leftpos+1);
		sb.append(';');
        sb.append(leftlen);
        sb.append(';');
        sb.append(rightp);
        sb.append(';');
        sb.append(rightpos+1);
        sb.append(';');
		sb.append(rightlen);
		sb.append(';');
        sb.append(productlen);
        sb.append(';');
        sb.append(productlen+2*anhangsize);
        sb.append(';');
        sb.append(gcdiff);
        sb.append(';');
        int sum=0;
        for(int i=0;7 >= i;i++){
            sum+=scores[i];
        	sb.append(scores[i]);
        	sb.append(';');
        }
        sb.append(sum);
        sb.append(';');
        sb.append(cyclenum);
        return sb.toString() ;      
	}
	
	public static String getCSVHeaderLine(){
        StringBuffer sb=new StringBuffer("Nr.;");
        sb.append("Original position in primer3 list;");
        sb.append("Left primer;");
        sb.append("Start;length;");
		sb.append("Right primer;");
		sb.append("Start;length;");
		sb.append("Productlength;");
		sb.append("Productlength incl. Tags;");
		sb.append("GC difference;");
        for(int i=0;7 >= i;i++){
        	sb.append(scorenames[i]);
        	sb.append(';');
        }
		sb.append("Sum;");
		sb.append("no. of cycle;");
        return sb.toString();
	}
	public boolean okay(){
		return getOverallScore()<=PCR.maxscore;
	}
}
