package Classes;

import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.eval.ExprEvaluator;
import Interfaces.FunctionModel;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

public class ExpressionFunction implements FunctionModel {
    Function sec = new Function("sec", 1) {
    @Override
    public double apply(double... args) {
        return 1.0 / Math.cos(args[0]);
    }
};
    private Expression expression;
    private String expressionString;
    //private final Expression expression;

    private static ExprEvaluator evaluator = new ExprEvaluator();
    public ExpressionFunction(String exprStr) {
        this.expressionString = exprStr;
        ExpressionBuilder builder = new ExpressionBuilder(exprStr).variable("x");
        for (Function f : CustomFunctions.customTrigFunctions) builder.function(f);
        this.expression = builder.build();
    }

    public String getExpressionString() {
        return expressionString;
    }

    @Override
    public double evaluate(double x) {
        return expression.setVariable("x", x).evaluate();
    }

    //@Override
    public static String derivative(String exp) {
        //EvalUtilities util = new EvalUtilities();

        String expr = "D(" + exp.trim() + " , x)";  // Symbolic derivative
        try {
            IExpr result = evaluator.evaluate(expr);
            System.out.println("Derivative: " + result.toString().toLowerCase());
            return result.toString().toLowerCase();  // prints 2*x*cos(x^2)
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public double integral(double a, double b) {
        // Approximate using trapezoidal rule
        int n = 1000;
        double h = (b - a) / n;
        double sum = 0.5 * (evaluate(a) + evaluate(b));
        for (int i = 1; i < n; i++) {
            sum += evaluate(a + i * h);
        }
        return sum * h;
    }
}

