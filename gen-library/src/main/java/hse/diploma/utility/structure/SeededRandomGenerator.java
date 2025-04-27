package hse.diploma.utility.structure;

import lombok.Getter;

import java.util.Random;

public class SeededRandomGenerator {
    private final ThreadLocal<Random> randomThreadLocal;
    @Getter
    private final long seed;

    public SeededRandomGenerator(long seed) {
        randomThreadLocal = ThreadLocal.withInitial(() -> new Random(seed));
        this.seed = seed;
    }

    public long nextIntInclusive(long left, long right) {
        if (left > right) {
            throw new IllegalArgumentException("Left bound must be <= right bound");
        }
        return left + randomThreadLocal.get().nextLong(right - left + 1);
    }

    public long nextIntInclusive(Pair<Long, Long> boundaries) {
        long left = boundaries.first;
        long right = boundaries.second;
        if (left > right) {
            throw new IllegalArgumentException("Left bound must be <= right bound");
        }
        return left + randomThreadLocal.get().nextLong(right - left + 1);
    }
}
