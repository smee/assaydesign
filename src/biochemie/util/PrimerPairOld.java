/*
 * Created on 05.09.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package biochemie.util;

/**
 * @author Steffen
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PrimerPairOld {
	public String leftp;
	public String rightp;
	public int leftpos;
	public int leftlen;
	public int rightpos;
	public int rightlen;
	public int productlen;
	public float gcdiff;
	public boolean okay;
	
	public int sekscore;
	public int blatscore;
	public int gcdiffscore;
	public int repseqscore;
	public int snpscore;
	public int exonscore;
	
	public PrimerPairOld(String l, String r, int leftpos, int rightpos,float gcdiff)  {
		this.leftp=l;
		this.rightp=r;
		this.leftpos=leftpos;
		this.rightpos=rightpos;
		this.leftlen=l.length();
		this.rightlen=r.length();
		this.sekscore=0;
		this.blatscore=0;
		this.gcdiffscore=0;
		this.repseqscore=0;
		this.snpscore=0;
		this.exonscore=0;
		this.productlen=this.rightpos-this.leftpos+1;
		this.gcdiff=gcdiff;
		this.okay=true;
	}
	/**
	 * Konstruktor, um einfach groﬂe Primer zum Testen zu basteln.
	 * @param leftpos
	 * @param leftlen
	 * @param rightpos
	 * @param rightlen
	 */
	public PrimerPairOld(int leftpos, int leftlen, int rightpos, int rightlen) {
		StringBuffer sb=new StringBuffer(leftlen);
		for(int i=0;i<leftlen;i++)	 {
			sb.append('A');
		}
		leftp=sb.toString();
		sb=new StringBuffer(rightlen);
		for(int i=0;i<rightlen;i++)	 {
			sb.append('A');
		}
		rightp=sb.toString();
		this.leftpos=leftpos;
		this.rightpos=rightpos;
		this.leftlen=leftlen;
		this.rightlen=rightlen;
		this.sekscore=0;
		this.blatscore=0;
		this.gcdiffscore=0;
		this.repseqscore=0;
		this.snpscore=0;
		this.exonscore=0;
		this.productlen=this.rightpos-this.leftpos+1;
		this.gcdiff=0;
		this.okay=true;
	}
	public String toString() {
		StringBuffer sb=new StringBuffer("[\tLeft primer : ");
		 sb.append(leftp+'\n');
		 sb.append("\tstart,length  : "+(leftpos+1)+", "+leftlen+'\n');
		 sb.append("\tRight primer  : "+rightp+'\n');
		 sb.append("\tstart,length  : "+(rightpos+1)+", "+rightlen+'\n');
		 sb.append("\tProduktlaenge : "+productlen+'\n');
		 sb.append("\tGC-Differenz  : "+gcdiff+'\n');
		 sb.append("\tScores :\n\t--------\n");
		 sb.append("\t\tBLAT :\t\t\t\t"+blatscore+'\n');
		 sb.append("\t\tSekundaeranalyse :\t\t"+sekscore+'\n');
		 sb.append("\t\tGC-Differenz :\t\t\t"+gcdiffscore+'\n');
 		 sb.append("\t\tRepetetive Sequenzen :\t\t"+repseqscore+'\n');
		 sb.append("\t\tExon/Intron :\t\t\t"+exonscore+'\n');
		 sb.append("\t\tSNP :\t\t\t\t"+snpscore+']');
		return sb.toString() ;		
	}
	public int getOverallScore() {
		return blatscore+gcdiffscore+repseqscore+sekscore+snpscore+exonscore;
	}
}
