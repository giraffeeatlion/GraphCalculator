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
    static double xMaxBound = 0;
    static double xMinBound = 0;
    static double points = 0;
    static boolean EnableToolTips = false;
    public static void plotExpressions(XYSeriesCollection dataset, List<ExpressionFunction> functions, JFreeChart chart) {
        
        double xMin = chart.getXYPlot().getDomainAxis().getLowerBound();
        double xMax = chart.getXYPlot().getDomainAxis().getUpperBound();
        dataset.removeAllSeries();
        points = 0;
        xMaxBound = xMax;
        xMinBound = xMin;
        for (ExpressionFunction func : functions) {
        //Integer points = 0;
        XYSeries series = new XYSeries(func.getExpressionString());
        double resolution = (xMax - xMin)/28000;
        if(resolution==0)
            break;
            for (double x = xMin; x <= xMax; x += resolution) {
                try {
                    double y = func.evaluate(x);
                    if (!Double.isNaN(y) && !Double.isInfinite(y)) {
                        series.add(x, y);
                        points++;
                    }
                } catch (Exception ignored) {}
            }
        System.out.println(" -> " + func.getExpressionString() + ": " + points + " points");
        dataset.addSeries(series);

        xMinBound = xMin;
        xMaxBound = xMax;
    }  

        // Set different colors for each series
        XYPlot plot = chart.getXYPlot();
        //plot.getDomainAxis().setRange(-10, 10);
        //plot.getRangeAxis().setRange(-10, 10);
        Color[] colors = {
            Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA,
            Color.CYAN, Color.PINK, Color.YELLOW, Color.GRAY, Color.DARK_GRAY
        };

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        

        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            renderer.setSeriesStroke(i, new BasicStroke(0.5f)); // thicker lines
            renderer.setSeriesPaint(i, colors[i % colors.length]); // ✅ Set color explicitly
            renderer.setSeriesShapesVisible(i, false); 
        }
        if(EnableToolTips)
        {
            XYToolTipGenerator toolTipGenerator = new StandardXYToolTipGenerator(
            StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
            new DecimalFormat("0.0"),
            new DecimalFormat("0.0")
            );
            renderer.setDefaultToolTipGenerator(toolTipGenerator);
        }
        plot.setRenderer(renderer);
        chart.fireChartChanged();
    }
    
}
