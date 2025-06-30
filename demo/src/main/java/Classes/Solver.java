package Classes;

import org.jfree.data.xy.XYSeriesCollection;
import org.matheclipse.core.eval.ExprEvaluator;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class Solver {
    static ExpressionFunction X_axis = new ExpressionFunction("0");
    private static ExprEvaluator intersectionEvaluator = new ExprEvaluator();
    public static double solve(ExpressionFunction f, ExpressionFunction df, double xMin,double xMax)
    {   
        int maxSteps = 30;
        double x = xMin+(xMax-xMin)/2;
        for(int i = 0; i < maxSteps;i++)
        {
            double y = f.evaluate(x);
            if(y == 0)
                break;
            double y_prime = df.evaluate(x);
            x = x - y/y_prime;
            if (Double.isNaN(x) || Double.isInfinite(x)) break;
        }
        return x;
    }
    public static void intersectionSolver()
    {

    }
}
