/*
 * Created on 01.04.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package biochemie.pcr.modules;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import biochemie.pcr.io.PCRConfig;
import biochemie.pcr.io.UI;
import biochemie.util.Helper;

/**
 * @author Steffen
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public abstract class SekAnalysis extends AnalyseModul {

	public static final int HAIR=0;
    public static final int HOMO=1;
    public static final int CROSS=2;
	int[] windowsize;
    int[] minbinds;
    int maxbind=Integer.MIN_VALUE;              //temp. VAr. für momentane anzahl maximaler binds
    Set posSet=new HashSet();
	
	public SekAnalysis(String windowsizes, String minbinds, String debug, int type) {
		super(null,Boolean.valueOf(debug).booleanValue());
		init(windowsizes,minbinds,debug,type);
	}
	public SekAnalysis(PCRConfig cfg,int type, boolean debug) {
		super(cfg,debug);
		if(null == cfg)
			return;
		init(null,null,null,type);
	}

	private void init(String win, String bind, String debug, int type) {
		if(null == win){
			String winparam=getWinParameterNameFor(type);
			win= config.getProperty(winparam);
		}
		if(null == bind){
			String bindparam=getBindParameterNameFor(type);
			bind= config.getProperty(bindparam);
		}
		if(null == debug){
			debug=config.getString("DEBUG","false");
		}
		windowsize= Helper.tokenizeToInt(win);
		minbinds=Helper.tokenizeToInt(bind);
		
		validateParameter();
	}

	private String getWinParameterNameFor(int type) {
		switch (type) {
		case HAIR:
			return "PARAM_HAIRPIN_WINDOW_SIZE";
		case HOMO:
			return "PARAM_HOMO_WINDOW_SIZE";
		case CROSS:
			return "PARAM_CROSS_WINDOW_SIZE";
		default:
			UI.errorDisplay("Wrong sek.analysis type: "+type);
		}
		return "";
	}
	/**
	 * @param type
	 * @return
	 */
	private String getBindParameterNameFor(int type) {
		switch (type) {
			case HAIR:
				return "PARAM_HAIRPIN_MIN_BINDING";
			case HOMO:
				return "PARAM_HOMO_MIN_BINDING";
			case CROSS:
				return "PARAM_CROSS_MIN_BINDING";
			default:
				UI.errorDisplay("Wrong sek.analysis type: "+type);
			}
			return "";
	}
	protected int[] parseIntList(String intlist) {
		if(null == intlist || 0 == intlist.length()) {
            return new int[0];
		}
		StringTokenizer st=new StringTokenizer(intlist);
        int[] ints=new int[st.countTokens()];
        for(int i=0;st.hasMoreTokens();i++)	 {
        	ints[i]=Integer.parseInt(st.nextToken());
        }		
		return ints;
	}
	
    protected void validateParameter() {
		if (windowsize.length != minbinds.length)
			UI.errorDisplay("Es muessen genausoviele Werte bei PARAM_WINDOW_SIZE wie bei PARAM_MIN_BINDING angegeben werden!");
		for (int i = 0; i < minbinds.length; i++) {
			if (0 > windowsize[i]) {
				this.windowsize[i] = 0;
				if (0 > minbinds[i]) {
					this.minbinds[i] = 0;
				} else if (minbinds[i] > windowsize[i]) {
					this.minbinds[i] = this.windowsize[i];
				}
			}
		}
	}

}
