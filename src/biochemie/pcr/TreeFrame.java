/*
 * Created on 20.01.2005
 *
 */
package biochemie.pcr;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import org.biojava.bio.gui.FeatureTree;
import org.biojava.bio.seq.SequenceIterator;
import org.biojava.bio.seq.db.HashSequenceDB;
import org.biojava.bio.seq.db.SequenceDB;
import org.biojava.bio.seq.io.SeqIOTools;

public class TreeFrame extends JFrame {
  private JPanel jPanel = new JPanel();
  private JScrollPane jScrollPane1 = new JScrollPane();
  private BorderLayout borderLayout = new BorderLayout();
  private FeatureTree featureTree = new FeatureTree();

  public TreeFrame() {
    try {
      init();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * This program will read files supported by SeqIOTools and display its
   * Sequence, Annotations and Features as a Tree. It takes three
   * arguments, the first is the file name the second is the file type
   * and the third is the alphabet type
   *
   */
  public static void main(String[] args) throws Exception{

    //read the sequence flat file
    BufferedReader br = new BufferedReader(new FileReader(args[0]));
    //get the format type from the command line
    String format = args[1];
    //get the alphabet from the command line
    String alpha = args[2];

    //read the sequences into a DB that will serve as the model for the tree
    SequenceDB db = new HashSequenceDB();
    SequenceIterator iter =
        (SequenceIterator)SeqIOTools.fileToBiojava(format, alpha, br);
    while(iter.hasNext()){
      db.addSequence(iter.nextSequence());
    }
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    TreeFrame treeFrame = new TreeFrame();
    //set the SequenceDB to serve as the data model
    treeFrame.getFeatureTree().setSequenceDB(db);
    treeFrame.pack();
    treeFrame.show();
  }

  private void init() throws Exception {
    jPanel.setLayout(borderLayout);
    this.setTitle("FeatureTree Demo");
    this.getContentPane().add(jPanel, BorderLayout.CENTER);
    jPanel.add(jScrollPane1,  BorderLayout.CENTER);
    jScrollPane1.getViewport().add(featureTree, null);
  }

  public FeatureTree getFeatureTree() {
    return featureTree;
  }

  protected void processWindowEvent(WindowEvent we){
    if(we.getID() == WindowEvent.WINDOW_CLOSING){
      System.exit(0);
    }else{
      super.processWindowEvent(we);
    }
  }
}