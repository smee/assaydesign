package biochemie.pcr.io;

import biochemie.util.config.GeneralConfig;


/*
 * Created on 22.08.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
/**
 * @author Steffen
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PCRConfig extends GeneralConfig {
	/**
	 * Returns defaultvalues
	 */
	public PCRConfig() {
        super();
	}
    protected String[][] getInitializedProperties() {
        return new String[][]{
             {"INFILES","test.in"}
            ,{"OUTFILE",""}
            ,{"PARAM_GCDIFF_LOWER_BOUND","2000"}
            ,{"PARAM_GCDIFF_UPPER_BOUND","8000"}
            ,{"PARAM_REPETETIVE_SEQ","40"}
            ,{"PARAM_HAIRPIN_WINDOW_SIZE","6"}
            ,{"PARAM_HAIRPIN_MIN_BINDING","4"}
            ,{"PARAM_CROSS_WINDOW_SIZE","6"}
            ,{"PARAM_CROSS_MIN_BINDING","4"}
            ,{"PARAM_HOMO_WINDOW_SIZE","6"}
            ,{"PARAM_HOMO_MIN_BINDING","4"}
            ,{"SCORE_MAXSCORE","100"}
            ,{"SCORE_BLAT_FOUND_SEQ","100"}
            ,{"SCORE_BLAT_FOUND_PRIMER","50"}
            ,{"SCORE_SNP_END","100"}
            ,{"SCORE_SNP_DISTANCES","50 40 30 20 10"}
            ,{"SCORE_NO_EXON_INTRON_BETWEEN_PRIMER","100"}
            ,{"PARAM_LENTH_OF_5'/3'_SNP_FLANKING_SEQUENCES_TO_BE_AMPLIFIED","25"}
            ,{"MIN_ACCEPTED_MISSPRIMING_PRIMER_DISTANCE","3000"}
            ,{"PERFORM_BLAT_WITH_BOTH_SIDES_INDEPENDENTLY_IF_PCR_LARGER_THAN","500"}
            ,{"SNP_LIST",""}
            ,{"EXONS",""}
            ,{"REP_SEQ_LIST",""}
            ,{"PCR_PRODUCT_INCLUDES_EXON_INTRON_BORDER","false"}
            ,{"BLAT_SINGLE","true"}
            ,{"BLAT_BOTH","true"}
            ,{"BLAT","true"}
            ,{"HAIR","true"}
            ,{"HOMO","true"}
            ,{"CROSS","true"}
            ,{"GCDIFF","true"}
            ,{"DEBUG","false"}
            ,{"SNP","true"}
            ,{"REP","true"}
            ,{"USEPROXY","false"}
            ,{"PROXYHOST","139.18.233.236"}
            ,{"PROXYPORT","8080"}
            ,{"SEQUENCE",""}
            ,{"PRIMER_NUM_RETURN",""}
            ,{"PARAM_SNP_OF_INTEREST",""}
            ,{"OUTPUT_CSV","true"}
            ,{"ASSEMBLY","hg16"}
            ,{"BLAT_HASHCODE","-1"}
            ,{"NUM_OF_SUCCESSFUL_PAIRS","50"}
            ,{"PRIMER_NUM_RETURN","1000"}
            ,{"FESTE5SEQ",""}
            ,{"PRIMER3COMMAND","primer3.exe"}};
    }
}
