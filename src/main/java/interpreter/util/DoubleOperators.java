package interpreter.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DoubleOperators {

	public static double substract(double a, double b) {
		return a - b;
	}

	public static double add(double a, double b) {
		return a + b;
	}

	public static double divide(double a, double b) {
		return a / b;
	}

	public static double multiply(double a, double b) {
		return a * b;
	}

	public static boolean greaterThan(double a, double b) {
		return a > b;
	}

	public static boolean greaterThanOrEqual(double a, double b) {
		return a >= b;
	}

	public static boolean lessThan(double a, double b) {
		return a < b;
	}

	public static boolean lessThanOrEqual(double a, double b) {
		return a <= b;
	}

}