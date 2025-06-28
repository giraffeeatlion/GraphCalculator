package Classes;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartMouseEvent;
import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
//import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;


public class GUI_init {
    //private boolean dragging = false;
        public static JFreeChart chart;
    public GUI_init(){
        //XYSeries series = new XYSeries("f(x)");
        XYSeriesCollection dataset = new XYSeriesCollection();

        // 2. Create chart
        chart = ChartFactory.createXYLineChart(
            "Function Plot", "X", "Y", dataset
        );
        //JFreeChart("Overlayed Transparent Curves", JFreeChart.DEFAULT_TITLE_FONT, plot, true);

        chart.getXYPlot().getDomainAxis().setRange(-10, 10); // X-axis
        chart.getXYPlot().getRangeAxis().setRange(-10, 10);
        //Plotter.xMaxBound = 10;
        //Plotter.xMinBound = -10;
        chart.setAntiAlias(true); // Enables chart anti-aliasing
        chart.setTextAntiAlias(true); // Smoother text rendering

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseZoomable(false); // enables zoom by mouse drag
        chartPanel.setMouseWheelEnabled(true);   // Zoom with scroll
        //chartPanel.setMouseZoomable(true);        // Drag to zoom
        chartPanel.setDomainZoomable(true);       // X-axis
        chartPanel.setRangeZoomable(true);
        chartPanel.setDisplayToolTips(true);
        chartPanel.setInitialDelay(0);
        

        XYPlot plot = chart.getXYPlot();
        plot.setDomainGridlinesVisible(true); // X-axis grid lines
        plot.setRangeGridlinesVisible(true);
        plot.setDomainPannable(true);
        plot.setRangePannable(true);
        ValueAxis xAxis = plot.getDomainAxis();
        ValueAxis yAxis = plot.getRangeAxis();

        xAxis.setVisible(true);
        yAxis.setVisible(true);
        // 3. Top control panel with buttons
        //JPanel controlPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        JButton plotGraphBtn = new JButton("Plot Graph");

        //JButton setZoomBtn = new JButton("Set Zoom Manually");
        JPanel controlPanel = new JPanel();
        /*controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
  
        
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(addFunctionBtn);
        controlPanel.add(Box.createVerticalStrut(10));
        
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(resetZoomBtn);*/
        JCheckBox pointToolTips = new JCheckBox("Point Tooltips");
        

        // 4. Bottom input panel (for text input)
        JPanel functionInputPanel = new JPanel();
        functionInputPanel.setLayout(new BoxLayout(functionInputPanel, BoxLayout.Y_AXIS));
        ArrayList<JTextField> functionFields = new ArrayList<>();
        JButton actionMenuButton = new JButton("Actions");
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem addItem = new JMenuItem("Add Function");
        JMenuItem plotItem = new JMenuItem("Plot Graph");
        JMenuItem resetItem = new JMenuItem("Reset Zoom");
        JMenuItem setZoomManual = new JMenuItem("Set Zoom Manually");
        popupMenu.add(addItem);
        popupMenu.add(plotItem);
        popupMenu.add(resetItem);
        popupMenu.add(setZoomManual);

        setZoomManual.addActionListener(e->ControlPanel.manualZoom(chart,dataset));
        addItem.addActionListener(e -> ControlPanel.addToPanel(functionInputPanel, functionFields,dataset));
        plotItem.addActionListener(e -> ControlPanel.plotAll(functionFields, dataset, chart));
        resetItem.addActionListener(e -> ControlPanel.resetZoom(chart, dataset));
        pointToolTips.addActionListener((ActionListener) new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(pointToolTips.isSelected())
                    Plotter.EnableToolTips = true;
                else
                    Plotter.EnableToolTips = false;
                ControlPanel.plotAll(functionFields, dataset, chart);
            }
        });
        
        //deleteBtn.addActionListener(e->ControlPanel.delete(chart, dataset));

        actionMenuButton.addActionListener(e -> popupMenu.show(actionMenuButton, 0, actionMenuButton.getHeight()));
        
        
        controlPanel.add(actionMenuButton);
        controlPanel.add(plotGraphBtn);
        controlPanel.add(pointToolTips);
       // controlPanel.add(deleteBtn);
        //controlPanel.add(setZoomBtn);
/*controlPanel.add(actionMenuButton);

        addFunctionBtn.addActionListener(e -> ControlPanel.addFunction(functionInputPanel, functionFields));
        plotGraphBtn.addActionListener(e->ControlPanel.plotAll(functionFields,dataset,chart));
        resetZoomBtn.addActionListener(e->ControlPanel.resetZoom(chart,dataset));*/
        plotGraphBtn.addActionListener(e->ControlPanel.plotAll(functionFields,dataset,chart));
        chartPanel.addChartMouseListener(new ChartMouseListener() {
            
            @Override
            public void chartMouseClicked(ChartMouseEvent event) {
                //ControlPanel.zoom(functionFields,dataset,chart);
                
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent event) {
                ControlPanel.zoom(chart,dataset);   
                //
            }
        });
        


        chartPanel.addMouseWheelListener(e -> {
            int rotation = e.getWheelRotation(); // +1 for scroll down, -1 for scroll up

            ControlPanel.zoom(chart, dataset);
            double xMin = chartPanel.getChart().getXYPlot().getDomainAxis().getLowerBound();
            double xMax = chartPanel.getChart().getXYPlot().getDomainAxis().getUpperBound();
            double yMin = chartPanel.getChart().getXYPlot().getRangeAxis().getLowerBound();
            double yMax = chartPanel.getChart().getXYPlot().getRangeAxis().getUpperBound();
            System.out.println("Scrolled: " + rotation);
            System.out.println("X Range: [" + xMin + ", " + xMax + "]");
            System.out.println("Y Range: [" + yMin + ", " + yMax + "]");
        });

        // 5. Wrap top + bottom into a leftPanel
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(controlPanel, BorderLayout.NORTH);
        leftPanel.add(functionInputPanel, BorderLayout.CENTER);

        // 6. Split the UI into left and right
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            leftPanel,
            chartPanel
        );
        splitPane.setDividerLocation(350);
        splitPane.setResizeWeight(0);

        // 7. Main frame
        JFrame frame = new JFrame("Graphing Calculator and Equation Solver");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(splitPane);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
