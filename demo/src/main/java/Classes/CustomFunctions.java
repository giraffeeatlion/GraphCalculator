package Classes;

import java.util.List;

import net.objecthunter.exp4j.function.Function;


public class CustomFunctions {
    private static final Function sec = new Function("sec", 1) {
        @Override
        public double apply(double... args) {
            return 1.0 / Math.cos(args[0]);
        }
    };

    private static final Function csc = new Function("csc", 1) {
        @Override
        public double apply(double... args) {
            return 1.0 / Math.sin(args[0]);
        }
    };

    private static final Function cot = new Function("cot", 1) {
        @Override
        public double apply(double... args) {
            return 1.0 / Math.tan(args[0]);
        }
    };
    public static final List<Function> customTrigFunctions = List.of(sec, csc, cot);
}
