package Classes;

import java.awt.BasicStroke;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.DatasetRenderingOrder;
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
    public static boolean EnableSaddlePointSolver = false;
    public static double total_points = 500;
    private static XYSeriesCollection pointDataset = new XYSeriesCollection();
    private static XYLineAndShapeRenderer lineRenderer = new XYLineAndShapeRenderer(true,false);
    private static XYLineAndShapeRenderer pointRenderer = new XYLineAndShapeRenderer(false, true); // points only
    private static final double THRESHOLD = 5.0;
    static Color[] colors = {
            Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA,
            Color.CYAN, Color.PINK, Color.YELLOW, Color.GRAY, Color.DARK_GRAY
        };
    private static Map<String, Boolean> seriesCheck = new HashMap<>();
    public static void plotExpressions(XYSeriesCollection dataset, List<ExpressionFunction> functions, JFreeChart chart,List<ExpressionFunction> derivativeFunctions,List<ExpressionFunction> doubleDerExprresions) {

        double xMin = chart.getXYPlot().getDomainAxis().getLowerBound();
        double xMax = chart.getXYPlot().getDomainAxis().getUpperBound();
        chart.getXYPlot().getDomainAxis().setAutoRange(false); // X-axis
        chart.getXYPlot().getRangeAxis().setAutoRange(false); 
        dataset.removeAllSeries();
        pointDataset.removeAllSeries();
        points = 0;
        xMaxBound = xMax;
        xMinBound = xMin;
        seriesCheck.clear();
        for (int i = 0; i < functions.size(); i++) {
            ExpressionFunction func = functions.get(i);
            if(seriesCheck.get(func.getExpressionString()) != null)
                continue;
            seriesCheck.put(func.getExpressionString(),true);
            ExpressionFunction derFunc = derivativeFunctions.get(i);
            ExpressionFunction doubleDerFunc = doubleDerExprresions.get(i);
            double prevY = 0;
            double prevX = xMin;
            double prevY_prime = 0;
            XYSeries series = new XYSeries(func.getExpressionString());
            XYSeries pointSeries = null;
            if(EnableZeroesSolver || EnableSaddlePointSolver)
                pointSeries = new XYSeries(func.getExpressionString()+": zeroes");
            double resolution = (xMax - xMin)/total_points;
            if(resolution==0)
                return;
                for (double x = xMin; x <= xMax; x += resolution) {
                    try {
                        double y = func.evaluate(x);
                        double y_prime = derFunc.evaluate(x);
                        if (!Double.isNaN(y) && !Double.isInfinite(y)) {
                            //if(Math.abs(prev-y)>THRESHOLD && y*prev<0)
                            /*{   
                                series.add(null, null);
                                
                            }*/
                            //System.out.println(prev + ":prev | y:" + y);
                            //prev = y;
                            if(EnableZeroesSolver && THRESHOLD > Math.abs(y-prevY))
                            {
                                if(prevY*y <= 0)
                                {
                                    double intersectionPoint = Solver.solve(func, derFunc, prevX, x);
                                    if(Math.abs(func.evaluate(intersectionPoint))<0.01)
                                        pointSeries.add(intersectionPoint,0);
                                }
                            }
                            if(EnableSaddlePointSolver && THRESHOLD > Math.abs(y_prime-prevY_prime))
                            {
                                if(prevY_prime*y_prime<=0)
                                {
                                    double intersectionPoint = Solver.solve(derFunc, doubleDerFunc, prevX, x);
                                    if(Math.abs(y - func.evaluate(intersectionPoint))<0.01)
                                        pointSeries.add(intersectionPoint,y);
                                }
                            }
                            series.add(x, y);
                            points++;
                        }
                        prevY_prime = y_prime;
                        prevX = x;
                        prevY = y;
                    } catch (Exception ignored) {}
                }
            //System.out.println("xMin: "+ xMin + "| xMax "+xMax);
            //System.out.println("X Range: " + chart.getXYPlot().getDomainAxis().getRange());
            //System.out.println("chartxmin: "+ chart.getXYPlot().getDomainAxis().getUpperBound() + "| chartxMax "+chart.getXYPlot().getDomainAxis().getUpperBound());
            //System.out.println(" -> " + func.getExpressionString() + ": " + points + " points");
            dataset.addSeries(series);
            if(EnableZeroesSolver || EnableSaddlePointSolver)
                pointDataset.addSeries(pointSeries);
            xMinBound = xMin;
            xMaxBound = xMax;
    }  

        // Set different colors for each series
        XYPlot plot = chart.getXYPlot();

        // Define colors
        
        // === LINE DATASET RENDERER (dataset index 0) ===
        

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
        plot.setDataset(0, dataset);
        plot.setRenderer(0, lineRenderer);

        // === POINT DATASET RENDERER (dataset index 2) ===
       

        // Customize point appearance (e.g. small dots)
        if(EnableZeroesSolver || EnableSaddlePointSolver)
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
        else
            pointRenderer.setDefaultToolTipGenerator(null);

        if(EnableZeroesSolver || EnableSaddlePointSolver)
        {plot.setDataset(1, pointDataset);
        plot.setRenderer(1, pointRenderer);}
        else
        {
            {
                plot.setDataset(1, null);     
                plot.setRenderer(1, null);    
            }
        }
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        chart.fireChartChanged();
    }
    
}
