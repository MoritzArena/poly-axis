package io.polyaxis.api.utils.math;

import java.io.Serial;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

/// [RandomNumGen] is a utility extending [SecureRandom] that provides convenient methods to
/// generate random values of various types (e.g., int, float, double, char, enums, strings),
/// with optional seeding from strings or numbers. It supports reproducible randomness and is
/// suitable for secure or parallel random generation.
///
/// @author github.com/MoritzArena
/// @date 2025/07/05
/// @since 1.0
public class RandomNumGen extends SecureRandom {

    @Serial
    private static final long serialVersionUID = 5222938581174415179L;

    private static final char[] CHAR_GEN =
            "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-=!@#$%^&*()_+`~[];',./<>?:\\\"{}|\\\\"
                    .toCharArray();

    private final long sx;

    // Constructor with no seed
    public RandomNumGen() {
        super();
        sx = 0;
    }

    public RandomNumGen(long seed) {
        super();
        this.setSeed(seed);
        this.sx = seed;
    }

    // Constructor with a string seed
    public RandomNumGen(String seed) {
        this(UUID.nameUUIDFromBytes(seed.getBytes(StandardCharsets.UTF_8)).getLeastSignificantBits() +
                UUID.nameUUIDFromBytes(seed.getBytes(StandardCharsets.UTF_8)).getMostSignificantBits() +
                (seed.length() * 32564L));
    }

    public RandomNumGen nextParallelRNG(int signature) {
        return new RandomNumGen(sx + signature);
    }

    public RandomNumGen nextParallelRNG(long signature) {
        return new RandomNumGen(sx + signature);
    }

    public String s(int length) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(c());
        }

        return sb.toString();
    }

    public char c() {
        return CHAR_GEN[i(CHAR_GEN.length - 1)];
    }

    // Pick a random enum
    public <T> T e(Class<T> t) {
        T[] c = t.getEnumConstants();
        return c[i(c.length)];
    }

    public boolean b() {
        return nextBoolean();
    }

    public boolean b(double percent) {
        return d() > percent;
    }

    public short si(int lowerBound, int upperBound) {
        return (short) (lowerBound + (nextFloat() * ((upperBound - lowerBound) + 1)));
    }

    public short si(int upperBound) {
        return si(0, upperBound);
    }

    public short si() {
        return si(1);
    }

    public float f(float lowerBound, float upperBound) {
        return lowerBound + (nextFloat() * ((upperBound - lowerBound)));
    }

    public float f(float upperBound) {
        return f(0, upperBound);
    }

    public float f() {
        return f(1);
    }

    public double d(double lowerBound, double upperBound) {
        return lowerBound + (nextDouble() * (upperBound - lowerBound));
    }

    public double d(double upperBound) {
        return d(0, upperBound);
    }

    public double d() {
        return nextDouble();
    }

    public int i(int lowerBound, int upperBound) {
        if (lowerBound >= upperBound) {
            throw new IllegalArgumentException("Upper bound must be greater than lower bound");
        }
        return lowerBound + this.nextInt(upperBound - lowerBound + 1);
    }

    public int i(int upperBound) {
        return i(0, upperBound);
    }

    public long l(long lowerBound, long upperBound) {
        return Math.round(d(lowerBound, upperBound));
    }

    public long l(long upperBound) {
        return l(0, upperBound);
    }

    public int imax() {
        return i(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public long lmax() {
        return l(Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public float fmax() {
        return f(Float.MIN_VALUE, Float.MAX_VALUE);
    }

    public double dmax() {
        return d(Double.MIN_VALUE, Double.MAX_VALUE);
    }

    public short simax() {
        return si(Short.MIN_VALUE, Short.MAX_VALUE);
    }

    public boolean chance(double chance) {
        return nextDouble() <= chance;
    }

    public <T> T pick(List<T> pieces) {
        if (pieces.isEmpty()) {
            return null;
        }

        if (pieces.size() == 1) {
            return pieces.get(0);
        }

        return pieces.get(this.nextInt(pieces.size()));
    }

    public long getSeed() {
        return sx;
    }
}
