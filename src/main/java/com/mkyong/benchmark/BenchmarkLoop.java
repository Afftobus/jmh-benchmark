package com.mkyong.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/*
http://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
*/
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3)
@Measurement(iterations = 8)
public class BenchmarkLoop {
    private final List<Class<?>> dataMap = createClassData();
    private final int dataSize = dataMap.size();
    private final int strongCollectionSize = dataSize / 3;
    private final int dataSetLoopSize = 4;


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(BenchmarkLoop.class.getSimpleName())
            .forks(1)
            .build();
        new Runner(opt).run();
    }


    ExecutorService executorService = Executors.newFixedThreadPool(8);

    @Benchmark
    public void doubleStorageCacheBigTest() {
        DoubleStorageCache<Class<?>, Object> doubleStorageCache = new DoubleStorageCache<>(dataSize);
        process(doubleStorageCache);
    }

//    @Benchmark
    public void doubleStorageCacheAtomicBigTest() {
        DoubleStorageCacheAtomicBoolean<Class<?>, Object> doubleStorageCache = new DoubleStorageCacheAtomicBoolean<>(dataSize);
        process(doubleStorageCache);
    }

    @Benchmark
    public void doubleStorageCacheNoBooleanBigTest() {
        DoubleStorageCacheNoSizeCache<Class<?>, Object> doubleStorageCache = new DoubleStorageCacheNoSizeCache<>(dataSize);
        process(doubleStorageCache);
    }


//    @Benchmark
    public void doubleStorageCacheTest() {
        DoubleStorageCache<Class<?>, Object> doubleStorageCache = new DoubleStorageCache<>(strongCollectionSize);
        process(doubleStorageCache);
    }

//    @Benchmark
    public void doubleStorageCacheAtomicTest() {
        DoubleStorageCacheAtomicBoolean<Class<?>, Object> doubleStorageCache = new DoubleStorageCacheAtomicBoolean<>(strongCollectionSize);
        process(doubleStorageCache);
    }

//    @Benchmark
    public void doubleStorageCacheNoBooleanTest() {
        DoubleStorageCacheNoSizeCache<Class<?>, Object> doubleStorageCache = new DoubleStorageCacheNoSizeCache<>(strongCollectionSize);
        process(doubleStorageCache);
    }


//    @Benchmark
    public void concurrentHashMapTest() {
        ConcurrentHashMap<Class<?>, Object> concurrentHashMap = new ConcurrentHashMap<>();
        process(concurrentHashMap);
    }


    private void process(DscInterface<Class<?>, Object> data) {
        List<Callable<Object>> tasks = new ArrayList<>();
        for (int i = 0; i < dataSetLoopSize; i++) {
            for (Class<?> key : dataMap) {
                tasks.add(() -> data.computeIfAbsent(key, k -> computeNewObject()));
            }
        }
        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {

        }
    }

    private void process(ConcurrentHashMap<Class<?>, Object> data) {
        List<Callable<Object>> tasks = new ArrayList<>();
        for (int i = 0; i < dataSetLoopSize; i++) {
            for (Class<?> key : dataMap) {
                tasks.add(() -> data.computeIfAbsent(key, k -> computeNewObject()));
            }
        }
        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {

        }
    }

    @TearDown
    public void stop() {
        executorService.shutdownNow();
    }

    private List<Class<?>> createClassData() {
        Reflections reflectionsRu = new Reflections("ru", new SubTypesScanner(false));
        final Set<String> allTypes = reflectionsRu.getAllTypes();
        final List<Class<?>> collect = allTypes.stream()
            .map(BenchmarkLoop::toClass)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        Reflections reflectionsOrg = new Reflections("org", new SubTypesScanner(false));
        final Set<String> allTypesOrg = reflectionsOrg.getAllTypes();
        final List<Class<?>> collectOrg = allTypesOrg.stream()
            .map(BenchmarkLoop::toClass)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        collect.addAll(collectOrg);

        return collect;
    }

    private static Class<?> toClass(String s) {
        try {
            return Class.forName(s);
        } catch (Throwable e) {
            return null;
        }
    }

    private Object computeNewObject() {
//        System.out.println("---->>> " + Thread.currentThread().getId());
//        try {
//            Thread.sleep(2);
//        } catch (InterruptedException e) {
//            return new Object();
//        }
//        System.out.println("<<<----- " + Thread.currentThread().getId());
        return new Object();
    }

}
