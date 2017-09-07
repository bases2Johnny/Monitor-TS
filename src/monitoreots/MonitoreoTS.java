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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import File.WriteFile;
import java.awt.Color;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.RefineryUtilities;

public class MonitoreoTS extends JFrame {

    private DB db = new DB();
    private JFrame ventana;
    CategoryDataset dataset;
    JFreeChart chart;
    ChartPanel chartPanel;
    JPanel panel;
    JPanel panel2;

    public MonitoreoTS(String titel) {
        this.ventana = new JFrame(titel);
        ventana.setLayout(new BorderLayout());
        createMenu();
        addTable();
        dataset = createDataset();
        chart = createChart(dataset);
        chartPanel = new ChartPanel(chart);
        panel = new JPanel();
        chartPanel.setPreferredSize(new Dimension(500, 350));
        panel.add(chartPanel);
        panel.setBackground(Color.white);
        ventana.add(panel, BorderLayout.LINE_START);
        ventana.setDefaultCloseOperation(EXIT_ON_CLOSE);
        ventana.setResizable(false);
        //System.out.println(ventana.getComponentCount());
    }

    private void createMenu() {
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;
        menuBar = new JMenuBar();
        menu = new JMenu("Edit");
        menuItem = new JMenuItem("Change HWM");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String HWM = JOptionPane.showInputDialog(
                        "Insert new value.", null);
                db.setHWT(Double.valueOf(HWM));
                pintar();
            }
        });
        menu.add(menuItem);
        menuBar.add(menu);
        ventana.add(menuBar, BorderLayout.PAGE_START);
    }

    private void addTable() {
        String[] columnNames = {"Table Space",
            "Time A (Blue)",
            "Time B (Red)"
        };
        Object[][] data = {};
        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        panel2 = new JPanel();
        panel2.add(scrollPane);
        panel2.setBackground(Color.white);
        ventana.add(panel2,BorderLayout.LINE_END);
    }
    
    private void addCheckBoxes(){
        
    }

    private void pintar() {
        this.dataset = createDataset();
        this.chart = createChart(dataset);
        this.chartPanel = new ChartPanel(chart);
        panel.remove(0);
        panel.add(chartPanel);
        panel.repaint();
        panel.revalidate();
        ventana.pack();
        //ventana.add(chartPanel);
        //ventana.repaint();
    }

    private CategoryDataset createDataset() {
        
//        double[][] data = new double[][]{
//            {210, 300, 320,123,456},
//            {200, 304, 201,234,341},
//            {200, 304, 201,200,111},};
        String[] bloques = new String[]{"Max_Size", "A", "B"};
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
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JFrame.setDefaultLookAndFeelDecorated(true);
        } catch (ClassNotFoundException |
                InstantiationException |
                IllegalAccessException |
                UnsupportedLookAndFeelException e) {
            System.err.println(e.getMessage());
        }

        final MonitoreoTS demo = new MonitoreoTS("Stacked Bar Chart");
        demo.ventana.pack();
        RefineryUtilities.centerFrameOnScreen(demo.ventana);
        demo.ventana.setVisible(true);

        WriteFile wf = new WriteFile();
        wf.read();
    }
}
