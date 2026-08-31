package bg.dalexiev.grumpysenior.util;

import java.util.function.Function;
import java.util.function.Supplier;

public sealed interface Either<E, T> {

    record Left<E, T>(E value) implements Either<E, T> {}

    record Right<E, T>(T value) implements Either<E, T> {}

    static <E, T> Left<E, T> left(E value) {
        return new Left<>(value);
    }

    static <E, T> Right<E, T> right(T value) {
        return new Right<>(value);
    }

    default <U> Either<E, U> map(Function<? super T, ? extends U> mapper) {
        return switch (this) {
            case Either.Left<E, T> left -> left(left.value);
            case Either.Right<E, T> right -> right(mapper.apply(right.value));
        };
    }

    default <U> Either<E, U> flatMap(Function<? super T, Either<E, U>> mapper) {
        return switch (this) {
            case Either.Left<E, T> left -> left(left.value);
            case Either.Right<E, T> right -> mapper.apply(right.value);
        };
    }

    default T rightOrElse(Supplier<T> supplier) {
        return switch (this) {
            case Either.Left<E, T> ignored -> supplier.get();
            case Either.Right<E, T> right -> right.value;
        };
    }
}
