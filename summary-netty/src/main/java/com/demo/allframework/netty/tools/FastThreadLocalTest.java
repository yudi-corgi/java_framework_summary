package com.demo.allframework.netty.tools;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.InternalThreadLocalMap;

/**
 * @Author YUDI-Corgi
 * @Description FastThreadLocal 使用测试
 */
public class FastThreadLocalTest {

    private static final FastThreadLocal<Object> THREAD_LOCAL = new FastThreadLocal<Object>() {
        @Override
        protected Object initialValue() {
            return new Object();
        }
    };

    private static final FastThreadLocal<Object> THREAD_LOCAL0 = new FastThreadLocal<Object>() {
        @Override
        protected Object initialValue() {
            return new Object();
        }
    };

    public static void main(String[] args) {
        new Thread(() -> {
            Object o = THREAD_LOCAL.get();
            // do something...
            System.out.println(o);
            THREAD_LOCAL.set(InternalThreadLocalMap.UNSET);
            Object o1 = THREAD_LOCAL.get();
            System.out.println(o == o1); // false
        }).start();

        new Thread(() -> {
            Object o = THREAD_LOCAL.get();
            // do something...
            System.out.println(o); // 两个线程获取的 o 不同
        }).start();

        FastThreadLocal<Object> threadLocal = new FastThreadLocal<Object>() {
            @Override
            protected Object initialValue() {
                return new Object();
            }
        };

        System.out.println();
    }
}
