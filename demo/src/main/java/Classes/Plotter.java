package Classes;

import java.awt.BasicStroke;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.Color;

public class Plotter {
    static double xMaxBound = 0;
    static double xMinBound = 0;
    static double points = 0;

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
        double resolution = (xMax - xMin)/2000;
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
            renderer.setSeriesStroke(i, new BasicStroke(2.0f)); // thicker lines
            renderer.setSeriesPaint(i, colors[i % colors.length]); // ✅ Set color explicitly
            renderer.setSeriesShapesVisible(i, false); 
        }

        plot.setRenderer(renderer);
        chart.fireChartChanged();
    }

    public static void ZoomPlotExpressions(XYSeriesCollection dataset, List<ExpressionFunction> functions, JFreeChart chart) {
        
        double xMin = chart.getXYPlot().getDomainAxis().getLowerBound();
        double xMax = chart.getXYPlot().getDomainAxis().getUpperBound();

        if(xMaxBound>= xMax && xMin >= xMinBound){
            dataset.removeAllSeries();
            points = 0;
            xMaxBound = xMax;
            xMinBound = xMin;
            for (ExpressionFunction func : functions) {
            //Integer points = 0;
            XYSeries series = new XYSeries(func.getExpressionString());
            double resolution = (xMax - xMin)/2000;
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
        }
        }
        else {
            for (ExpressionFunction func : functions) {
                XYSeries series = null;
                for (int i = 0; i < dataset.getSeriesCount(); i++) {
                    if (dataset.getSeries(i).getKey().equals(func.getExpressionString())) {
                        series = dataset.getSeries(i);
                        break;
                    }
                }
                if (series == null) {
                    series = new XYSeries(func.getExpressionString(), false, true);
                    dataset.addSeries(series);
                }
                // Left expansion
                if (xMin < xMinBound) {
                    double resolution = (xMinBound - xMin) / 1000;
                    if(resolution==0)
                        break;
                    for (double x = xMin; x < xMinBound; x += resolution) {
                        try {
                            double y = func.evaluate(x);
                            if (!Double.isNaN(y) && !Double.isInfinite(y)) {
                                series.add(x, y);
                                points++;
                            }
                        } catch (Exception ignored) {}
                    }
                   
                }
                System.out.println(" -> " + func.getExpressionString() + ": " + points + " points");
                // Right expansion
                if (xMax > xMaxBound) {
                    double resolution = (xMax - xMaxBound) / 1000;
                    if(resolution==0)
                        break;
                    for (double x = xMaxBound; x <= xMax; x += resolution) {
                        try {
                            double y = func.evaluate(x);
                            if (!Double.isNaN(y) && !Double.isInfinite(y)) {
                                series.add(x, y);
                                points ++;
                            }
                        } catch (Exception ignored) {}
                    }
                    
                }
                System.out.println(" -> " + func.getExpressionString() + ": " + points + " points");
                
            }
            if (xMin < xMinBound)
                xMinBound = xMin;
            if (xMax > xMaxBound)
                xMaxBound = xMax;
                
                
            
        }
        System.out.println(xMin + " "+ xMax);
        System.out.println(xMinBound + " " + xMaxBound);
        

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
            renderer.setSeriesStroke(i, new BasicStroke(2.0f)); // thicker lines
            renderer.setSeriesPaint(i, colors[i % colors.length]); // ✅ Set color explicitly
            renderer.setSeriesShapesVisible(i, false); 
        }

        plot.setRenderer(renderer);
        chart.fireChartChanged();
    }
}
