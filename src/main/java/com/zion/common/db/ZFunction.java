package com.zion.common.db;

import java.io.Serializable;
import java.util.function.Function;

/**
 * ZDao函数式接口基础
 *
 * @param <T>
 * @param <R>
 */
@FunctionalInterface
public interface ZFunction<T, R> extends Function<T, R>, Serializable { }