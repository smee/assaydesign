package biochemie.sbe.gui;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
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
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.StandardGradientPaintTransformer;
import org.jfree.ui.TextAnchor;

import biochemie.calcdalton.CalcDalton;
import biochemie.calcdalton.CalcDaltonOptions;
import biochemie.calcdalton.SBETable;
import biochemie.sbe.SBECandidate;
import biochemie.util.Helper;

/*
 * Created on 21.12.2004
 *
 */

/**
 * @author sdienst
 *
 */
public class SpektrometerPreviewFrame extends JFrame{
    final CustomXYToolTipGenerator ttgen;
    
    public SpektrometerPreviewFrame(List sbec,String title, String subtitle,CalcDaltonOptions cfg)
    {
        super(title);
        ttgen = new CustomXYToolTipGenerator();
        IntervalXYDataset massen = createDataset(sbec,cfg);
        initialize(subtitle, massen,cfg);
    }
    public SpektrometerPreviewFrame(SBETable table, String title, String subtitle,CalcDaltonOptions cfg) {
        super(title);
        ttgen = new CustomXYToolTipGenerator();
        IntervalXYDataset massen = createDataset(table,cfg);
        initialize(subtitle, massen,cfg);
    }
    /**
     * @param subtitle
     * @param massen
     * @param forbTo 
     * @param forbFrom 
     */
    private void initialize(String subtitle, IntervalXYDataset massen, CalcDaltonOptions cfg) {
        JFreeChart jfreechart = createChart(massen,subtitle,ttgen,cfg);

        ChartPanel chartpanel = new ChartPanel(jfreechart);
        chartpanel.setPreferredSize(new Dimension(500, 300));
        setContentPane(chartpanel);
        ToolTipManager.sharedInstance().setInitialDelay(0);//???
        ToolTipManager.sharedInstance().setReshowDelay(0);
    }


    /**
     * @param forbTo 
     * @param forbFrom 
     * @param sbec
     * @return
     */

    private JFreeChart createChart(IntervalXYDataset intervalxydataset,String subtitle,XYToolTipGenerator ttgen, CalcDaltonOptions cfg)
    {
        JFreeChart jfreechart = ChartFactory.createXYBarChart("MALDI Preview", "Calcdaltonmasses", false, "rel. units", intervalxydataset, PlotOrientation.VERTICAL, true, true, false);
        jfreechart.addSubtitle(new TextTitle(subtitle));
        jfreechart.setBackgroundPaint(Color.white);
        XYPlot xyplot = jfreechart.getXYPlot();
        XYItemRenderer xyitemrenderer = xyplot.getRenderer();
        xyplot.setBackgroundPaint(Color.lightGray);
//        GradientPaint gradientpaint = new GradientPaint(0.0F, 0.0F, Color.LIGHT_GRAY, 1.0F, 1.0F, Color.DARK_GRAY,true);
//        double[] forbFrom=cfg.getCalcDaltonVerbFrom();
//        double[] forbTo=cfg.getCalcDaltonVerbTo();
//        for (int i = 0; i < forbFrom.length; i++) {
//            IntervalMarker im=new IntervalMarker(forbFrom[i],forbTo[i],gradientpaint,new BasicStroke(2.0F), null, null, 1.0F);
//            im.setLabel("Forbidden mass range");
//            im.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.HORIZONTAL));
//            im.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
//            im.setLabelTextAnchor(TextAnchor.BASELINE_RIGHT);
//            xyplot.addDomainMarker(im,Layer.BACKGROUND);
//        }
        xyplot.setRangeGridlinePaint(Color.white);
        xyitemrenderer.setToolTipGenerator(ttgen);
//        NumberAxis axis = new NumberAxis();
//        xyplot.setDomainAxis(axis);
        return jfreechart;
    }
    private IntervalXYDataset createDataset(SBETable table, CalcDaltonOptions cfg) {
        XYSeriesCollection collection = new XYSeriesCollection();
        int numofprimers=table.getColumnCount();
        for(int i=1; i<numofprimers;i++) {
            String id=table.getColumnName(i);
            if(id == null || id.length() == 0)
                id=Integer.toString(i);
            double[] m = table.getMassenOfColumn(i);
            String snp = table.getAnbauOfColumn(i);
            addDataset(m,id,snp,collection,cfg.getCalcDaltonShowIons());
        }
        return collection;
    }
    private IntervalXYDataset createDataset(List sbec, CalcDaltonOptions cfg)
    {
        XYSeriesCollection collection = new XYSeriesCollection();
        CalcDalton cd=Helper.getCalcDalton(cfg);
        for (Iterator it = sbec.iterator(); it.hasNext();) {
            SBECandidate s = (SBECandidate) it.next();
            double[] m = cd.calcSBEMass(new String[]{s.getFavSeq(),"A","C","G","T"},s.getBruchstelle(),true);
            String id = s.getId();
            String snp = s.getSNP();            
            addDataset(m,id,snp,collection,cfg.getCalcDaltonShowIons());
        }
        return collection;
    }
    /**
     * m muss aus 5 Massen bestehen, der Masse des Primers und die vier Massen, die entstehen, wenn ACGT angehaengt werden.
     * @param m
     * @param id
     * @param snp
     * @param collection
     * @param showions 
     */
    private void addDataset(double[] m, String id, String snp, XYSeriesCollection collection, boolean showions) {
        final double LEN = 4000.0;
        final DecimalFormat df = new DecimalFormat("00.00");
        
        XYSeries masse = new XYSeries(id,false,true);
        List l = new ArrayList(3*5);
        
        masse.add(m[0],LEN);
        l.add(id+": "+df.format(m[0])+"D");
        if(showions){
            masse.add(m[0]+22,LEN*1/7f);
            l.add(id+", Na+ Peak: "+df.format(m[0]+22)+"D");
            masse.add(m[0]+38,LEN*1/10f);
            l.add(id+", K+ Peak: "+df.format(m[0]+38)+"D");
        }
        final String foo="ACGT";
        for (int i = 0; i < snp.length(); i++) {
            int pos = foo.indexOf(snp.charAt(i));
            if(pos==-1) {
                System.err.println("invalid nucleotide in snp!");
                continue;
            }
            if(m[pos+1]==0) {
                continue;
            }
            String name = id+"+"+snp.charAt(i); 
            masse.add(m[pos+1],LEN*5/4);
            l.add(name+": "+df.format(m[pos+1])+"D");
            if(showions){
                masse.add(m[pos+1]+22,LEN*5/4f*1/7f);
                l.add(name+", Na+ Peak: "+df.format(m[pos+1]+22)+"D");
                masse.add(m[pos+1]+38,LEN*5/4f*1/10f);
                l.add(name+", K+ Peak: "+df.format(m[pos+1]+38)+"D");
            }
        }
        collection.addSeries(masse);
        ttgen.addToolTipSeries(l);
    }
    


}
