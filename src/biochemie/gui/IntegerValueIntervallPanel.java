/*
 * Created on 22.11.2004
 *
 */
package biochemie.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import biochemie.calcdalton.gui.PBSequenceField;
import biochemie.util.Helper;
/**
 * @author Steffen Dienst
 *
 */
public class IntegerValueIntervallPanel extends JPanel {
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
					int start=Integer.parseInt(sstart);
					int ende=Integer.parseInt(sende);
					abstandVector.add(sstart+" - "+sende);
					int[] neu_from=new int[from.length+1];
					int[] neu_to=new int[to.length+1];
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
				int[] neu_from=new int[from.length-1];
				int[] neu_to=new int[to.length-1];
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

	private JButton btAddAbstand = null;
	private JButton btDelAbstand = null;
	private JLabel toJlabel = null;
	private JLabel fromJlabel = null;
	private PBSequenceField tfFrom = null;
	private PBSequenceField tfTo = null;
	private JScrollPane jScrollPane = null;
	private JList abstandList = null;  //  @jve:decl-index=0:visual-constraint="10,220"
	private final DelAbstandAction delAbstandAction;
	private final AddAbstandAction addAbAction;
	private String title = null;

	int[] defaultfrom = new int[]{};
	int[] defaultto = new int[]{};
	Vector abstandVector;
	int[] from = {};
	int[] to   =  {};

	/**
	 * This is the default constructor
	 */
	public IntegerValueIntervallPanel() {
		super();
		this.delAbstandAction = new DelAbstandAction("Delete selected values.");
		this.addAbAction = new AddAbstandAction("Add values to list.");
		initialize();
		reset();
	}
	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private  void initialize() {
		fromJlabel = new JLabel();
		toJlabel = new JLabel();
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		this.setSize(300,200);
		gridBagConstraints2.gridx = 4;
		gridBagConstraints2.gridy = 2;
		gridBagConstraints3.gridx = 4;
		gridBagConstraints3.gridy = 0;
		gridBagConstraints4.gridx = 2;
		gridBagConstraints4.gridy = 2;
		toJlabel.setText("To:");
		gridBagConstraints2.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints3.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints4.insets = new java.awt.Insets(10,10,10,0);
		gridBagConstraints5.insets = new java.awt.Insets(10,10,10,0);
		gridBagConstraints6.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints7.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints8.insets = new java.awt.Insets(10,10,10,10);
		gridBagConstraints5.gridx = 0;
		gridBagConstraints5.gridy = 2;
		fromJlabel.setText("From:");
		gridBagConstraints6.gridx = 1;
		gridBagConstraints6.gridy = 2;
		gridBagConstraints6.weightx = 1.0;
		gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints7.gridx = 3;
		gridBagConstraints7.gridy = 2;
		gridBagConstraints7.weightx = 1.0;
		gridBagConstraints7.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints8.gridx = 0;
		gridBagConstraints8.gridy = 0;
		gridBagConstraints8.weightx = 1.0;
		gridBagConstraints8.weighty = 1.0;
		gridBagConstraints8.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints8.gridwidth = 4;
		this.setBorder(javax.swing.BorderFactory.createTitledBorder(null, getTitle(), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
		this.add(getBtAddAbstand(), gridBagConstraints2);
		this.add(getBtDelAbstand(), gridBagConstraints3);
		this.add(toJlabel, gridBagConstraints4);
		this.add(fromJlabel, gridBagConstraints5);
		this.add(getTfFrom(), gridBagConstraints6);
		this.add(getTfTo(), gridBagConstraints7);
		this.add(getJScrollPane(), gridBagConstraints8);
	}
	/**
	 * This method initializes jButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getBtAddAbstand() {
		if (btAddAbstand == null) {
			btAddAbstand = new JButton();
			btAddAbstand.setAction(addAbAction);
		}
		return btAddAbstand;
	}
	/**
	 * This method initializes jButton1
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getBtDelAbstand() {
		if (btDelAbstand == null) {
			btDelAbstand = new JButton();
			btDelAbstand.setAction(delAbstandAction);
		}
		return btDelAbstand;
	}
	/**
	 * This method initializes PBSequenceField
	 *
	 * @return biochemie.calcdalton.gui.PBSequenceField
	 */
	private PBSequenceField getTfFrom() {
		if (tfFrom == null) {
			tfFrom = new PBSequenceField();
			tfFrom.setColumns(5);
			tfFrom.setValidChars(PBSequenceField.NUMBERS);
		}
		return tfFrom;
	}
	/**
	 * This method initializes PBSequenceField1
	 *
	 * @return biochemie.calcdalton.gui.PBSequenceField
	 */
	private PBSequenceField getTfTo() {
		if (tfTo == null) {
			tfTo = new PBSequenceField();
			tfTo.setColumns(5);
			tfTo.setValidChars(PBSequenceField.NUMBERS);
			tfTo.setAction(addAbAction);
		}
		return tfTo;
	}
	/**
	 * This method initializes jScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.getViewport().setView(getAbstandList());
		}
		return jScrollPane;
	}
	private JList getAbstandList(){
		if(abstandList == null){
			abstandList= new JList();
			abstandList.setListData(abstandVector);
			abstandList.setVisibleRowCount(5);
			abstandList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			abstandList.setSize(59, 50);
			abstandList.addKeyListener(new KeyAdapter(){
				public void keyPressed(KeyEvent e) {
					if(KeyEvent.VK_DELETE == e.getKeyCode())
						delAbstandAction.actionPerformed(null);
				}
			});
		}
		return abstandList;
	}
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
	public void loadFromString(String string) {
		if(null == string){
			reset();
			return;
		}
		abstandVector.clear();

		StringTokenizer st=new StringTokenizer(string," \"");
		abstandVector.removeAllElements();
		from=new int[st.countTokens()];
		to=new int[st.countTokens()];
		for(int i=0;st.hasMoreTokens();i++) {
			String temp=st.nextToken();
			from[i]=Integer.parseInt(temp.substring(0,temp.indexOf('_')));
			to[i]=Integer.parseInt(temp.substring(temp.indexOf('_')+1));
			abstandVector.add(from[i]+" - "+to[i]);
		}
		update();
	}
	public void loadFromString(String sfrom, String sto){
		StringTokenizer st=new StringTokenizer(sfrom);
		StringTokenizer st2=new StringTokenizer(sto);
		if(st.countTokens() != st2.countTokens())
			return;

		abstandVector.clear();
		from = new int[st.countTokens()];
		to = new int[st2.countTokens()];
		for (int i = 0; i < from.length; i++) {
			from[i]=Integer.parseInt(st.nextToken());
			to[i]=Integer.parseInt(st2.nextToken());
			abstandVector.add(from[i]+" - "+to[i]);
		}
		update();
	}
	private void update() {
		delAbstandAction.setEnabled(0 < abstandVector.size()?true:false);
		abstandList.setListData(abstandVector);
	}
	/**
	 *
	 */
	public void reset() {
		from=new int[defaultfrom.length];
		System.arraycopy(defaultfrom,0,from,0,from.length);
		to=new int[defaultto.length];
		System.arraycopy(defaultto,0,to,0,to.length);
		abstandVector=new Vector();
		for(int i=0;i<defaultfrom.length;i++){
			abstandVector.add(defaultfrom[i]+" - "+defaultto[i]);
		}
		update();
	}

    public int[] getFrom() {
    	return Helper.clone(from);
    }
    public int[] getTo() {
    	return Helper.clone(to);
    }
	public void setFromLabel(String fromLabel) {
		fromJlabel.setText(fromLabel);
	}
	public void setToLabel(String toLabel) {
		toJlabel.setText(toLabel);
	}
	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return title;
	}
	public void setTooltip(String t){
		setToolTipText(t);
		getAbstandList().setToolTipText(t);
	}
	/**
	 * @param title The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
		setBorder(javax.swing.BorderFactory.createTitledBorder( title ));
	}
	public void setDefaultValues(int[] f, int[] t){
		this.defaultfrom=Helper.clone(f);
		this.defaultto=Helper.clone(t);
		reset();
	}
}
