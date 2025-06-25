package Classes;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
//import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeriesCollection;



public class ControlPanel {
    private static List<ExpressionFunction> expressions;
    private static Integer count = 0;
    public static void addFunction(JPanel inputPanel, ArrayList<JTextField> functionFields, XYSeriesCollection dataset)
    {   
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JTextField field = new JTextField("sin(x)", 12);
            //field.setFont(new Font("Serif", Font.ITALIC, 18));
            JButton derivativeBox = new JButton("Add f'(x)");
            JButton deleteButton = new JButton("Delete");
            derivativeBox.addActionListener(e -> {
            String expressionText = field.getText();
            ControlPanel.addDerivative(inputPanel, functionFields, expressionText);
        });
            deleteButton.addActionListener(e->ControlPanel.delete(dataset));
            row.add(new JLabel("f(x) = "));
            row.add(field);
            row.add(derivativeBox);
            row.add(deleteButton);
            inputPanel.add(row);
            functionFields.add(field);
            inputPanel.revalidate(); 
            count++;
    }

    public static void addDerivative(JPanel inputPanel, ArrayList<JTextField> functionFields,String expression)
    {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String derString = ExpressionFunction.derivative(expression);
            JTextField field = new JTextField(derString,20);
            //field.setFont(new Font("Serif", Font.ITALIC, 18));
            //JCheckBox derivativeBox = new JCheckBox("Plot Derivative");
            row.add(new JLabel("f'(x) = "));
            row.add(field);
            //row.add(derivativeBox);
            inputPanel.add(row);
            functionFields.add(field);
            inputPanel.revalidate(); 
            count++;
    }

    public static void plotAll(ArrayList<JTextField> funcFields,XYSeriesCollection dataset,JFreeChart chart)
    {
        expressions = new ArrayList<>();

        for (JTextField field : funcFields) {
            String expr = field.getText(); // always gets current text
            System.out.println(expr);
            expressions.add(new ExpressionFunction(expr));
        }

        Plotter.plotExpressions(dataset, expressions, chart);
    }

    public static void zoom(JFreeChart chart,XYSeriesCollection dataset)
    {
        Plotter.ZoomPlotExpressions(dataset, expressions, chart);
    }

    public static void resetZoom(JFreeChart chart,XYSeriesCollection dataset)
    {   
        XYPlot plot = chart.getXYPlot();

    // Set default fixed bounds (X: -10 to 10, Y: -10 to 10)
        plot.getDomainAxis().setRange(-10, 10);
        plot.getRangeAxis().setRange(-10, 10);
        Plotter.plotExpressions(dataset, expressions, chart);
    }

    public static void manualZoom(JFreeChart chart, XYSeriesCollection dataset)
    {
        JTextField xMinField = new JTextField("-10");
        JTextField xMaxField = new JTextField("10");
        JTextField yMinField = new JTextField("-10");
        JTextField yMaxField = new JTextField("10");

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.add(new JLabel("X Min:"));
        panel.add(xMinField);
        panel.add(new JLabel("X Max:"));
        panel.add(xMaxField);
        panel.add(new JLabel("Y Min:"));
        panel.add(yMinField);
        panel.add(new JLabel("Y Max:"));
        panel.add(yMaxField);

        int result = JOptionPane.showConfirmDialog(
            null, panel, "Set Zoom Bounds", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                double xMin = Double.parseDouble(xMinField.getText());
                double xMax = Double.parseDouble(xMaxField.getText());
                double yMin = Double.parseDouble(yMinField.getText());
                double yMax = Double.parseDouble(yMaxField.getText());

                XYPlot plot = chart.getXYPlot();
                plot.getDomainAxis().setRange(xMin, xMax);
                plot.getRangeAxis().setRange(yMin, yMax);
                Plotter.ZoomPlotExpressions(dataset, expressions, chart);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid numbers.");
            }
        }
    }

    public static void delete(XYSeriesCollection dataset)
    {   
       //XYSeries s1 = new XYSeries(get);
        dataset.removeSeries(0); 
    }
}
