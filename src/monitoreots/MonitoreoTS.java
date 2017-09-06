/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitoreots;

/**
 *
 * @author Admin
 */
import Conexion.DB;
import File.WriteFile;
import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class MonitoreoTS extends ApplicationFrame {
    private DB db = new DB();
    public MonitoreoTS(String titel) {
        super(titel);

        final CategoryDataset dataset = createDataset();
        final JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 350));
        setContentPane(chartPanel);
    }

    private CategoryDataset createDataset() {
        
//        double[][] data = new double[][]{
//            {210, 300, 320,123,456},
//            {200, 304, 201,234,341},
//            {200, 304, 201,200,111},};
        String[] bloques = new String[] {"Max_Size", "A","B"};
        return DatasetUtilities.createCategoryDataset(bloques, db.getTableSpace_name(), db.getSize());
    }

    private JFreeChart createChart(final CategoryDataset dataset) {
        final JFreeChart chart = ChartFactory.createStackedBarChart(
                "Stacked Bar Chart ", "", "",
                dataset, PlotOrientation.HORIZONTAL, true, true, false);

//        chart.setBackgroundPaint(new Color(100, 100, 100));

        CategoryPlot plot = chart.getCategoryPlot();
        plot.getRenderer().setSeriesPaint(0, new Color(38, 201, 56));
        plot.getRenderer().setSeriesPaint(1, new Color(90, 98, 221));
        plot.getRenderer().setSeriesPaint(2, new Color(241, 48, 48));

        return chart;
    }

    public static void main(final String[] args) {

        final MonitoreoTS demo = new MonitoreoTS("Stacked Bar Chart");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);      
        demo.setVisible(true);
        WriteFile wf = new WriteFile();
        wf.read();
    }
}
