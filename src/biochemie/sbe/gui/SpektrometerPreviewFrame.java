package biochemie.sbe.gui;
import java.awt.Color;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.ToolTipManager;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.CustomXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import biochemie.calcdalton.CalcDalton;
import biochemie.sbe.SBECandidate;

/*
 * Created on 21.12.2004
 *
 */

/**
 * @author sdienst
 *
 */
public class SpektrometerPreviewFrame extends JFrame{
    CustomXYToolTipGenerator ttgen;
    
    public SpektrometerPreviewFrame(List sbec,String title, String subtitle)
    {
        super(title);
        
        IntervalXYDataset massen = createDataset(sbec);
        JFreeChart jfreechart = createChart(massen,subtitle,ttgen);
        ChartPanel chartpanel = new ChartPanel(jfreechart);
        chartpanel.setPreferredSize(new Dimension(500, 300));
        setContentPane(chartpanel);
        ToolTipManager.sharedInstance().setInitialDelay(0);//???
        ToolTipManager.sharedInstance().setReshowDelay(0);
    }

    /**
     * @param sbec
     * @return
     */

    private JFreeChart createChart(IntervalXYDataset intervalxydataset,String subtitle,XYToolTipGenerator ttgen)
    {
        JFreeChart jfreechart = ChartFactory.createXYBarChart("MALDI Preview", "Calcdaltonmasses", false, "rel. units", intervalxydataset, PlotOrientation.VERTICAL, true, true, false);
        jfreechart.addSubtitle(new TextTitle(subtitle));
        jfreechart.setBackgroundPaint(Color.white);
        XYPlot xyplot = jfreechart.getXYPlot();
        XYItemRenderer xyitemrenderer = xyplot.getRenderer();
        xyplot.setBackgroundPaint(Color.lightGray);
        xyplot.setRangeGridlinePaint(Color.white);
        xyitemrenderer.setToolTipGenerator(ttgen);
//        NumberAxis axis = new NumberAxis();
//        xyplot.setDomainAxis(axis);
        return jfreechart;
    }
    
    private IntervalXYDataset createDataset(List sbec)
    {
        final double LEN = 4000.0;

        final DecimalFormat df = new DecimalFormat("00.00");
        ttgen = new CustomXYToolTipGenerator();
        XYSeriesCollection collection = new XYSeriesCollection();
        for (Iterator it = sbec.iterator(); it.hasNext();) {
            SBECandidate s = (SBECandidate) it.next();
            XYSeries masse = new XYSeries(s.getId(),false,true);
            List l = new ArrayList(3*5);
            double[] m = CalcDalton.calcSBEMass(new String[]{s.getFavSeq(),"A","C","G","T"},s.getBruchstelle());
            String id = s.getId();
            
            //TODO tooltips und positionen stimmen nicht ueberein
            masse.add(m[0],LEN);
            l.add(id+": "+df.format(m[0])+"D");
            masse.add(m[0]+22,LEN*1/7f);
            l.add(id+", Na+ Peak: "+df.format(m[0]+22)+"D");
            masse.add(m[0]+38,LEN*1/10f);
            l.add(id+", K+ Peak: "+df.format(m[0]+38)+"D");
            
            String snp = s.getSNP();            
            final String foo="ACGT";
            
            for (int i = 0; i < snp.length(); i++) {
                int pos = foo.indexOf(snp.charAt(i));
                if(pos==-1) {
                    System.err.println("invalid nucleotide in snp!");
                    continue;
                }
                String name = id+"+"+snp.charAt(i); 
                masse.add(m[pos+1],LEN*5/4);
                l.add(name+": "+df.format(m[pos+1])+"D");
                masse.add(m[pos+1]+22,LEN*5/4f*1/7f);
                l.add(name+", Na+ Peak: "+df.format(m[pos+1]+22)+"D");
                masse.add(m[pos+1]+38,LEN*5/4f*1/10f);
                l.add(name+", K+ Peak: "+df.format(m[pos+1]+38)+"D");
            }
            collection.addSeries(masse);
            ttgen.addToolTipSeries(l);
        }
        
        return collection;
    }

}
