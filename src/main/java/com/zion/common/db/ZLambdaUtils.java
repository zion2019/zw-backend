package com.zion.common.db;

import java.beans.Introspector;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ZLambdaUtils {
    private static final Map<Class<?>, Map<String, String>> CACHE = new ConcurrentHashMap<>();

    /* 对外唯一 API：把 Lambda 变成实体字段名 */
    public static <T> String getField(ZFunction<T, ?> fn) {
        SerializedLambda lambda = getSerializedLambda(fn);
        String method = lambda.getImplMethodName();
        if (method.startsWith("get")) {
            return Introspector.decapitalize(method.substring(3));
        }
        return method;
    }

    /* 拿 SerializedLambda 对象 */
    private static SerializedLambda getSerializedLambda(Serializable fn) {
        try {
            Method m = fn.getClass().getDeclaredMethod("writeReplace");
            m.setAccessible(true);
            return (SerializedLambda) m.invoke(fn);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
