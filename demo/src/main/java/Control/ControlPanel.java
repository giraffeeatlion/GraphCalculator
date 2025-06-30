package Control;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeriesCollection;

import Classes.ExpressionFunction;
import Classes.Plotter;



public class ControlPanel {
    private static List<ExpressionFunction> expressions;
    private static List<ExpressionFunction> derivativeExpressions;
    private static List<ExpressionFunction> doubleDerExpressions;
   // private static int count = 0;
   public static boolean zeroesSolver = false;
   public static boolean saddleSolver = false;
    
    public static class FunctionRow {
        private  JTextField functionField;
        private  JButton derivativeButton;
        private  JButton deleteButton;
        private JTextField derivativeField;
        private boolean hasDerivative = false;
        
        public FunctionRow() {
            functionField = new JTextField("sin(x)", 12);
            derivativeButton = new JButton("Add f'(x)");
            deleteButton = new JButton("Delete");
        }
        
        public JPanel createRow(JPanel inputPanel, 
                               List<JTextField> functionFields, 
                               XYSeriesCollection dataset) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            
            // Set up components
            row.add(new JLabel("f(x) = "));
            row.add(functionField);
            row.add(derivativeButton);
            row.add(deleteButton);
            
            // Set up event listeners
            deleteButton.addActionListener(e -> deleteRow(dataset, row, inputPanel, functionFields));
            derivativeButton.addActionListener(e -> toggleDerivative(row, functionFields, inputPanel));
            
            functionFields.add(functionField);
            inputPanel.add(row);
            
            return row;
        }
        
        private void toggleDerivative(JPanel row, 
                                    List<JTextField> functionFields,
                                    JPanel inputPanel) {
            if (!hasDerivative) {
                addDerivative(row, functionFields, inputPanel);
            } else {
                updateDerivative();
            }
        }
        
        private void addDerivative(JPanel row, 
                                  List<JTextField> functionFields,
                                  JPanel inputPanel) {
            JPanel derivativePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            String derivativeText = ExpressionFunction.derivative(functionField.getText());
            
            derivativeField = new JTextField(derivativeText, 20);
            derivativeField.setEditable(false);
            
            derivativePanel.add(new JLabel("f'(x) = "));
            derivativePanel.add(derivativeField);
            row.add(derivativePanel);
            
            functionFields.add(derivativeField);
            hasDerivative = true;
            
            inputPanel.revalidate();
            inputPanel.repaint();
        }
        
        private void updateDerivative() {
            String newDerivative = ExpressionFunction.derivative(functionField.getText());
            derivativeField.setText(newDerivative);
        }
        
        private void deleteRow(XYSeriesCollection dataset, 
                             JPanel row, 
                             JPanel inputPanel,
                             List<JTextField> functionFields) {
            functionFields.remove(functionField);
            if (hasDerivative) {
                functionFields.remove(derivativeField);
            }
            inputPanel.remove(row);
            inputPanel.revalidate();
            inputPanel.repaint();
            ControlPanel.plotAll(functionFields, dataset, GUI_init.chart);
           // count--;
        }
    }
    public static void addToPanel(JPanel inputPanel, 
                               List<JTextField> functionFields, 
                               XYSeriesCollection dataset)
    {   
        FunctionRow functionRow = new FunctionRow();
        inputPanel.add(functionRow.createRow(inputPanel,functionFields,dataset));
        inputPanel.revalidate();
        inputPanel.repaint();
    }

    

    public static void plotAll(List<JTextField> functionFields,XYSeriesCollection dataset,JFreeChart chart)
    {
        expressions = new ArrayList<>();
        derivativeExpressions = new ArrayList<>();
        doubleDerExpressions = new ArrayList<>();
        for (JTextField field : functionFields) {
            String expr = field.getText(); // always gets current text
            System.out.println(expr);
            ExpressionFunction function = new ExpressionFunction(expr);
            expressions.add(function);
            expr = ExpressionFunction.derivative(expr);
            derivativeExpressions.add(new ExpressionFunction(expr));
            expr = ExpressionFunction.derivative(expr);
            doubleDerExpressions.add(new ExpressionFunction(expr));
        }
        
        Plotter.plotExpressions(dataset, expressions, chart,derivativeExpressions,doubleDerExpressions);
    }

    public static void zoom(JFreeChart chart,XYSeriesCollection dataset)
    {
        Plotter.plotExpressions(dataset, expressions, chart,derivativeExpressions,doubleDerExpressions);
    }

    public static void resetZoom(JFreeChart chart,XYSeriesCollection dataset)
    {   
        XYPlot plot = chart.getXYPlot();

    // Set default fixed bounds (X: -10 to 10, Y: -10 to 10)
        plot.getDomainAxis().setRange(-10, 10);
        plot.getRangeAxis().setRange(-10, 10);
        Plotter.plotExpressions(dataset, expressions, chart,derivativeExpressions,doubleDerExpressions);
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
                Plotter.plotExpressions(dataset, expressions, chart,derivativeExpressions,doubleDerExpressions);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid numbers.");
            }
        }
    }

    public static void delete(XYSeriesCollection dataset,JPanel row,JPanel inputPanel,ArrayList<JTextField> functionFields,JTextField field)
    {   
       //XYSeries s1 = new XYSeries(get);
       //XYSeries series_to_delete = new XYSeries(row.)
       inputPanel.remove(row);
       
       inputPanel.revalidate(); 
       inputPanel.repaint();
       functionFields.remove(field);
       ControlPanel.plotAll(functionFields, dataset, GUI_init.chart);

    }
    public static void toggleZeroes(List<JTextField> functionFields,XYSeriesCollection dataset,JFreeChart chart)
    {   
        Plotter.EnableZeroesSolver = !Plotter.EnableZeroesSolver;
        ControlPanel.zeroesSolver = !ControlPanel.zeroesSolver;
        ControlPanel.plotAll(functionFields, dataset, GUI_init.chart);
    }
    public static void toggleSaddle(List<JTextField> functionFields,XYSeriesCollection dataset,JFreeChart chart)
    {   
        Plotter.EnableSaddlePointSolver = !Plotter.EnableSaddlePointSolver;
        ControlPanel.saddleSolver = !ControlPanel.saddleSolver;
        ControlPanel.plotAll(functionFields, dataset, GUI_init.chart);
    }
}
