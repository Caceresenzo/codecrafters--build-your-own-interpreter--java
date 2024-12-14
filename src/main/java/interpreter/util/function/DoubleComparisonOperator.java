package interpreter.util.function;

@FunctionalInterface
public interface DoubleComparisonOperator {

	boolean applyAsDouble(double left, double right);

}