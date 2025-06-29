package Classes;

import java.awt.BasicStroke;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.Color;
import java.text.DecimalFormat;

public class Plotter {
    public static double xMaxBound = 0;
    public static double xMinBound = 0;
    static double points = 0;
    public static boolean EnableToolTips = false;
    public static boolean EnableZeroesSolver = false;
    private static final double THRESHOLD = 5.0;
    public static void plotExpressions(XYSeriesCollection dataset, List<ExpressionFunction> functions, JFreeChart chart,List<ExpressionFunction> derivativeFunctions) {
        
        double xMin = chart.getXYPlot().getDomainAxis().getLowerBound();
        double xMax = chart.getXYPlot().getDomainAxis().getUpperBound();
        XYSeriesCollection pointDataset = new XYSeriesCollection();
        chart.getXYPlot().getDomainAxis().setAutoRange(false); // X-axis
        chart.getXYPlot().getRangeAxis().setAutoRange(false); 
        dataset.removeAllSeries();
        points = 0;
        xMaxBound = xMax;
        xMinBound = xMin;
        for (int i = 0; i < functions.size(); i++) {
            ExpressionFunction func = functions.get(i);
            ExpressionFunction derFunc = derivativeFunctions.get(i);
            double prevY = 0;
            double prevX = xMin;
            XYSeries series = new XYSeries(func.getExpressionString());
            XYSeries pointSeries = null;
            if(EnableZeroesSolver)
                pointSeries = new XYSeries(func.getExpressionString()+": zeroes");
            double resolution = (xMax - xMin)/28000;
            if(resolution==0)
                return;
                for (double x = xMin; x <= xMax; x += resolution) {
                    try {
                        double y = func.evaluate(x);
                        if (!Double.isNaN(y) && !Double.isInfinite(y)) {
                            //if(Math.abs(prev-y)>THRESHOLD && y*prev<0)
                            /*{   
                                series.add(null, null);
                                
                            }*/
                            //System.out.println(prev + ":prev | y:" + y);
                            //prev = y;
                            if(EnableZeroesSolver)
                            {
                                if(prevY*y < 0)
                                {
                                    double intersectionPoint = Solver.zeroes_solver(func, derFunc, prevX, x);
                                    pointSeries.add(intersectionPoint,0);
                                }
                            }
                            series.add(x, y);
                            points++;
                        }
                        prevX = x;
                        prevY = y;
                    } catch (Exception ignored) {}
                }
            //System.out.println("xMin: "+ xMin + "| xMax "+xMax);
            //System.out.println("X Range: " + chart.getXYPlot().getDomainAxis().getRange());
            //System.out.println("chartxmin: "+ chart.getXYPlot().getDomainAxis().getUpperBound() + "| chartxMax "+chart.getXYPlot().getDomainAxis().getUpperBound());
            //System.out.println(" -> " + func.getExpressionString() + ": " + points + " points");
            dataset.addSeries(series);
            if(EnableZeroesSolver)
            pointDataset.addSeries(pointSeries);
            xMinBound = xMin;
            xMaxBound = xMax;
    }  

        // Set different colors for each series
        XYPlot plot = chart.getXYPlot();

        // Define colors
        Color[] colors = {
            Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA,
            Color.CYAN, Color.PINK, Color.YELLOW, Color.GRAY, Color.DARK_GRAY
        };

        // === LINE DATASET RENDERER (dataset index 0) ===
        XYLineAndShapeRenderer lineRenderer = new XYLineAndShapeRenderer();

        // Set properties for line renderer (lines only)
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            lineRenderer.setSeriesStroke(i, new BasicStroke(0.5f));
            lineRenderer.setSeriesPaint(i, colors[i % colors.length]);
            lineRenderer.setSeriesShapesVisible(i, false); // no points
        }

        if (EnableToolTips) {
            XYToolTipGenerator toolTipGenerator = new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                new DecimalFormat("0.000"),
                new DecimalFormat("0.000")
            );
            lineRenderer.setDefaultToolTipGenerator(toolTipGenerator);
        }

        // Assign line dataset and renderer
        plot.setDataset(1, dataset);
        plot.setRenderer(1, lineRenderer);

        // === POINT DATASET RENDERER (dataset index 1) ===
        XYLineAndShapeRenderer pointRenderer = new XYLineAndShapeRenderer(false, true); // points only

        // Customize point appearance (e.g. small dots)
        if(EnableZeroesSolver)
        for (int i = 0; i < pointDataset.getSeriesCount(); i++) {
            pointRenderer.setSeriesPaint(i, colors[(i + dataset.getSeriesCount()) % colors.length]);
            pointRenderer.setSeriesShape(i, new java.awt.geom.Ellipse2D.Double(-2, -2, 4, 4)); // small circle
        }

        if (EnableToolTips) {
            XYToolTipGenerator pointToolTipGenerator = new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                new DecimalFormat("0.000"),
                new DecimalFormat("0.000")
            );
            pointRenderer.setDefaultToolTipGenerator(pointToolTipGenerator);
        }

        // Assign point dataset and renderer
        if(EnableZeroesSolver)
        {plot.setDataset(0, pointDataset);
        plot.setRenderer(0, pointRenderer);}

        // Force chart refresh
        chart.fireChartChanged();
    }
    
}
