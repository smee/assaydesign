/*
 * Created on 21.11.2004
 *
 */
package biochemie.gui;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import biochemie.util.Helper;


public class DoubleValueIntervallPanel extends JPanel{
	private String tooltip;
	private String title;
	private JButton btAddabstand;
	private JButton btDelabstand;
	JList abstandList;
	private DelAbstandAction delAbstandAction;
	private AddAbstandAction addAbAction;
	Vector abstandVector;
	double[] from = {};
	double[] to   =  {};
    JTextField tfFrom;
    JTextField tfTo;

	final class AddAbstandAction extends AbstractAction {
		Icon icon;
		AddAbstandAction(String tooltip) {
			putValue(NAME, "Add");
			putValue(SHORT_DESCRIPTION, tooltip);
			java.net.URL url=this.getClass().getClassLoader().getResource("images/add.gif");
			if(null != url){
				icon=new ImageIcon(url);
				putValue(Action.SMALL_ICON,icon);
			}
		}
		public void actionPerformed(ActionEvent e) {
			String sstart=tfFrom.getText();
			tfFrom.setText("");
			String sende=tfTo.getText();
			tfTo.setText("");
			if(null != sstart && null != sende && 0 != sstart.length() && 0 != sende.length()){
				try {
					double start=Double.parseDouble(sstart);
					double ende=Double.parseDouble(sende);
					if(ende<start) {
						double t=start;
						start=ende;
						ende=t;
						abstandVector.add(sende+" - "+sstart);
					}
					else {
						abstandVector.add(sstart+" - "+sende);
					}
					double[] neu_from=new double[from.length+1];
					double[] neu_to=new double[to.length+1];
					for(int i=0;i<from.length;i++) {
						neu_from[i]=from[i];
						neu_to[i]=to[i];
					}
					neu_from[from.length]=start;
					neu_to[to.length]=ende;
					from=neu_from;
					to=neu_to;
					abstandList.setListData(abstandVector);
					delAbstandAction.setEnabled(true);
				} catch (NumberFormatException e2) {}
			}
		}
	}

	final class DelAbstandAction extends AbstractAction {
		Icon icon;
		DelAbstandAction(String tooltip) {
			putValue(NAME, "Delete");
			putValue(SHORT_DESCRIPTION, tooltip);
			java.net.URL url=this.getClass().getClassLoader().getResource("images/delete.gif");
			if(null != url){
				icon=new ImageIcon(url);
				putValue(Action.SMALL_ICON,icon);
			}
		}
		public void actionPerformed(ActionEvent e) {
			String temp=(String)abstandList.getSelectedValue();
			int index=abstandList.getSelectedIndex();
			if(null != temp) {
				abstandVector.remove(temp);
				abstandList.setListData(abstandVector);
				double[] neu_from=new double[from.length-1];
				double[] neu_to=new double[to.length-1];
				int aktx=0;
				for(int i=0;i<from.length;i++) {
					if(i!=index) {
						neu_from[aktx]=from[i];
						neu_to[aktx]=to[i];
						aktx++;
					}
				}
				from=neu_from;
				to=neu_to;
				if(0 == abstandVector.size())
					delAbstandAction.setEnabled(false);
			}
		}
	}
	public DoubleValueIntervallPanel(String title, String tooltip,double[] defaultfrom,double[] defaultto){
		this.title=title;
		this.tooltip=tooltip;
		initialize();
        reset(defaultfrom,defaultto);
	}
	public DoubleValueIntervallPanel(){
		this("","",new double[]{},new double[]{});
	}
	/**
	 * @param prop
	 */
	public String saveToString() {
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<from.length;i++){
			sb.append('\"');
			sb.append(from[i]);
			sb.append('_');
			sb.append(to[i]);
			sb.append("\" ");
		}
		return sb.toString();
	}
	/**
	 * @param string
	 */
	public void loadFromString(String string) {
		if(null == string){
			return;
		}
		StringTokenizer st=new StringTokenizer(string," \"");
		abstandVector.removeAllElements();
		from=new double[st.countTokens()];
		to=new double[st.countTokens()];
		for(int i=0;st.hasMoreTokens();i++) {
			String temp=st.nextToken();
			from[i]=Double.parseDouble(temp.substring(0,temp.indexOf('_')));
			to[i]=Double.parseDouble(temp.substring(temp.indexOf('_')+1));
			abstandVector.add(from[i]+" - "+to[i]);
		}
		update();
	}
	/**
	 *
	 */
	private void update() {
        abstandVector.clear();
        for(int i=0;i<from.length;i++){
            abstandVector.add(from[i]+" - "+to[i]);
        }
		delAbstandAction.setEnabled(0 < abstandVector.size()?true:false);
		abstandList.setListData(abstandVector);
	}
	/**
	 *
	 */
	public void reset(double[] defaultfrom, double[] defaultto) {
		from=new double[defaultfrom.length];
		System.arraycopy(defaultfrom,0,from,0,from.length);
		to=new double[defaultto.length];
		System.arraycopy(defaultto,0,to,0,to.length);
		abstandVector=new Vector();
		for(int i=0;i<defaultfrom.length;i++){
			abstandVector.add(defaultfrom[i]+" - "+defaultto[i]);
		}
		update();
	}
	protected void initialize(){
		double b=5;
		double text=28;
		double[][] abstandSize={{b,30,b,30,2*b,20,b,30,2*b,TableLayoutConstants.PREFERRED,b},{b,text,40,b,text,b}};
		setLayout(new TableLayout(abstandSize));
		setBorder( BorderFactory.createTitledBorder( title ) );
		setToolTipText(tooltip);
		abstandList = new JList( abstandVector );

		abstandList.setToolTipText(tooltip);
		abstandList.setVisibleRowCount(5);
		abstandList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		delAbstandAction= new DelAbstandAction("Delete selected value.");
		abstandList.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e) {
				if(KeyEvent.VK_DELETE == e.getKeyCode())
					delAbstandAction.actionPerformed(null);
			}
		});
		JScrollPane scrollAbstandList = new JScrollPane( );
		scrollAbstandList.getViewport().setView(abstandList);
		tfFrom = new JTextField();
		tfTo = new JTextField();
		addAbAction= new AddAbstandAction("Add new value.");
		tfTo.setAction(addAbAction);
		btAddabstand = new JButton(addAbAction);btAddabstand.setHorizontalAlignment(SwingConstants.LEFT);
		btDelabstand = new JButton(delAbstandAction);btDelabstand.setHorizontalAlignment(SwingConstants.LEFT);
		add(scrollAbstandList,"1,1,7,2");
		add(new JLabel("From:"),"1,4");
		add(new JLabel("To:"),"5,4");
		add(tfFrom,"3,4");
		add(tfTo,"7,4");
		add(btAddabstand,"9,4");
		add(btDelabstand,"9,1");
	}
    public double[] getFrom() {
    	return Helper.clone(from);
    }
    public double[] getTo() {
    	return Helper.clone(to);
    }

}