package interpreter.util;

import java.util.Iterator;
import java.util.function.BiConsumer;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Iterators {

	public static <T, U> void zip(Iterator<T> left, Iterator<U> right, BiConsumer<T, U> consumer) {
		while (left.hasNext() && right.hasNext()) {
			consumer.accept(left.next(), right.next());
		}
	}

}