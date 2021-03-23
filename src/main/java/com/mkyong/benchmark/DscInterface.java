package com.mkyong.benchmark;

import java.util.function.Function;

public interface DscInterface<K, V> {
  public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction);
}
