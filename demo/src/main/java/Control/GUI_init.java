package Control;

import org.jfree.chart.ChartPanel;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeriesCollection;

import Classes.Plotter;



public class GUI_init {
    //private boolean dragging = false;
        public static JFreeChart chart;
        static boolean isInMotion = false;
        static boolean highResPending = false;
        static boolean zeroPlot = false;
        static boolean saddlePlot = false;
        
    public GUI_init(){
        //XYSeries series = new XYSeries("f(x)");
        XYSeriesCollection dataset = new XYSeriesCollection();

        // 2. Create chart
        chart = ChartFactory.createXYLineChart(
            "Function Plot", "X", "Y", dataset
        );
        

        chart.getXYPlot().getDomainAxis().setRange(-10, 10); // X-axis
        chart.getXYPlot().getRangeAxis().setRange(-10, 10);

        chart.setAntiAlias(true); // Enables chart anti-aliasing
        chart.setTextAntiAlias(true); // Smoother text rendering

        ChartPanel chartPanel = new ChartPanel(chart);
        
        chartPanel.setMouseWheelEnabled(true);   
        chartPanel.setMouseZoomable(true);       
        chartPanel.setDomainZoomable(true);      
        chartPanel.setRangeZoomable(true);
        chartPanel.setDisplayToolTips(true);
        chartPanel.setInitialDelay(0);
        

        XYPlot plot = chart.getXYPlot();
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinesVisible(true);
        plot.setDomainPannable(true);
        plot.setRangePannable(true);
        ValueAxis xAxis = plot.getDomainAxis();
        ValueAxis yAxis = plot.getRangeAxis();

        xAxis.setVisible(true);
        yAxis.setVisible(true);

        JButton plotGraphBtn = new JButton("Plot Graph");


        JPanel controlPanel = new JPanel();

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
        JMenuItem setZoomManual = new JMenuItem("Set Bounds Manually");
        JMenuItem toggleZeroesSolver = new JMenuItem("Toggle Zero Solver");
        JMenuItem toggleSaddleSolver = new JMenuItem("Toggle Saddle Solver");
        popupMenu.add(addItem);
        popupMenu.add(plotItem);
        popupMenu.add(resetItem);
        popupMenu.add(setZoomManual);
        popupMenu.add(toggleZeroesSolver);
        popupMenu.add(toggleSaddleSolver);

        setZoomManual.addActionListener(e->ControlPanel.manualZoom(chart,dataset));
        addItem.addActionListener(e -> ControlPanel.addToPanel(functionInputPanel, functionFields,dataset));
        plotItem.addActionListener(e -> ControlPanel.plotAll(functionFields, dataset, chart));
        resetItem.addActionListener(e -> ControlPanel.resetZoom(chart, dataset));
        toggleZeroesSolver.addActionListener(e -> ControlPanel.toggleZeroes(functionFields, dataset, chart));
        toggleSaddleSolver.addActionListener(e -> ControlPanel.toggleSaddle(functionFields, dataset, chart));
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
        
        actionMenuButton.addActionListener(e -> popupMenu.show(actionMenuButton, 0, actionMenuButton.getHeight()));
        
        
        controlPanel.add(actionMenuButton);
        controlPanel.add(plotGraphBtn);
        controlPanel.add(pointToolTips);

        
        plotGraphBtn.addActionListener(e->{
            Plotter.total_points = chartPanel.getWidth();
            ControlPanel.plotAll(functionFields,dataset,chart);});
         


        Timer highResTimer;
        highResTimer = new Timer(100,e->{
            if(highResPending)
            {   
                Plotter.total_points = chartPanel.getWidth();
                Plotter.EnableToolTips = pointToolTips.isSelected();
                Plotter.EnableZeroesSolver = ControlPanel.zeroesSolver;
                Plotter.EnableSaddlePointSolver = ControlPanel.saddleSolver;
                ControlPanel.plotAll(functionFields, dataset, chart);
                highResPending = false;
            }
        });
        highResTimer.setRepeats(false);



        chart.getPlot().addChangeListener(event -> {
            //XYPlot Plot = (XYPlot) chart.getPlot();
            double xMin = plot.getDomainAxis().getLowerBound();
            double xMax = plot.getDomainAxis().getUpperBound();
            if(xMax-xMin>50)
            {
                Plotter.EnableZeroesSolver = false;
                Plotter.EnableSaddlePointSolver = false;
            }
            if (xMin != Plotter.xMinBound || xMax != Plotter.xMaxBound) {
                Plotter.xMinBound = xMin;
                Plotter.xMaxBound = xMax;
                highResPending = true;
                highResTimer.restart();
                Plotter.total_points = 500;
                Plotter.EnableToolTips = false;
                Plotter.EnableZeroesSolver = false;
                Plotter.EnableSaddlePointSolver = false;
                System.out.println(chartPanel.getWidth());
                ControlPanel.zoom(chart, dataset);
            }
        });
        /*highResTimer.addActionListener(e -> {
            lowResTimer.stop(); // Stop low-res updates
        });*/



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
