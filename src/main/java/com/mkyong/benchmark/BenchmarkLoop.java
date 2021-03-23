package com.mkyong.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/*
http://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
*/
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
//@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx2G"})
@Warmup(iterations = 3)
@Measurement(iterations = 8)
public class BenchmarkLoop {

    @Param({"10000"})
    private int dataSize;

    @Param({"10"})
    private int dataLoopsCount;

    @Param({"1024"})
    private int strongCollectionSize;

    private Map<Integer, Integer> integerIntegerMap;

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
            .include(BenchmarkLoop.class.getSimpleName())
            .forks(1)
            .build();
        new Runner(opt).run();
    }

    @Setup
    public void setup() {
        integerIntegerMap = createData();
    }

    @Benchmark
    public void doubleStorageCacheTest(Blackhole bh) {
        DoubleStorageCache<Integer, Integer> doubleStorageCache = new DoubleStorageCache<>(strongCollectionSize);

        for (int j = 0; j < dataLoopsCount; j++) {
            for (int i = 0; i < integerIntegerMap.size(); i++) {
                doubleStorageCache.computeIfAbsent(i, integerIntegerMap::get);
            }
        }
    }

    @Benchmark
    public void doubleStorageCacheBigStrongTest(Blackhole bh) {
        DoubleStorageCache<Integer, Integer> doubleStorageCache = new DoubleStorageCache<>(dataSize);

        for (int j = 0; j < dataLoopsCount; j++) {
            for (int i = 0; i < integerIntegerMap.size(); i++) {
                doubleStorageCache.computeIfAbsent(i, integerIntegerMap::get);
            }
        }
    }

    @Benchmark
    public void doubleStorageCacheBigStrongAtomicBooleanTest(Blackhole bh) {
        DoubleStorageCacheAtomicBoolean<Integer, Integer> doubleStorageCacheAtomicBoolean = new DoubleStorageCacheAtomicBoolean<>(dataSize);

        for (int j = 0; j < dataLoopsCount; j++) {
            for (int i = 0; i < integerIntegerMap.size(); i++) {
                doubleStorageCacheAtomicBoolean.computeIfAbsent(i, integerIntegerMap::get);
            }
        }
    }

    @Benchmark
    public void doubleStorageCacheAtomicBooleanTest(Blackhole bh) {
        DoubleStorageCacheAtomicBoolean<Integer, Integer> doubleStorageCacheAtomicBoolean = new DoubleStorageCacheAtomicBoolean<>(strongCollectionSize);

        for (int j = 0; j < dataLoopsCount; j++) {
            for (int i = 0; i < integerIntegerMap.size(); i++) {
                doubleStorageCacheAtomicBoolean.computeIfAbsent(i, integerIntegerMap::get);
            }
        }
    }

    @Benchmark
    public void doubleStorageCacheNoSizeCacheTest(Blackhole bh) {
        DoubleStorageCacheNoSizeCache<Integer, Integer> doubleStorageCacheNoSizeCache = new DoubleStorageCacheNoSizeCache<>(strongCollectionSize);

        for (int j = 0; j < dataLoopsCount; j++) {
            for (int i = 0; i < integerIntegerMap.size(); i++) {
                doubleStorageCacheNoSizeCache.computeIfAbsent(i, integerIntegerMap::get);
            }
        }
    }

    @Benchmark
    public void doubleBigStorageCacheNoSizeCacheTest(Blackhole bh) {
        DoubleStorageCacheNoSizeCache<Integer, Integer> doubleStorageCacheNoSizeCache = new DoubleStorageCacheNoSizeCache<>(dataSize);

        for (int j = 0; j < dataLoopsCount; j++) {
            for (int i = 0; i < integerIntegerMap.size(); i++) {
                doubleStorageCacheNoSizeCache.computeIfAbsent(i, integerIntegerMap::get);
            }
        }
    }

    @Benchmark
    public void concurrentHashMapTest(Blackhole bh) {
        ConcurrentHashMap<Integer, Integer> concurrentHashMap = new ConcurrentHashMap<>();
        for (int j = 0; j < dataLoopsCount; j++) {
            for (int i = 0; i < integerIntegerMap.size(); i++) {
                concurrentHashMap.computeIfAbsent(i, integerIntegerMap::get);
            }
        }
    }

    private Map<Integer, Integer> createData() {
        Map<Integer, Integer> data = new HashMap<>();
        for (int i = 0; i < dataSize; i++) {
            data.put(i, i);
        }
        return data;
    }

}
