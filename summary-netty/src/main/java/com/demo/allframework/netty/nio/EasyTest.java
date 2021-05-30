package com.demo.allframework.netty.nio;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author CDY
 * @date 2021/5/29
 * @description
 */
public class EasyTest {

    public static void main(String[] args) {
        ByteBuffer bb = ByteBuffer.allocate(10);
        System.out.println(bb.limit());
        bb.put("123".getBytes(StandardCharsets.UTF_8));
        System.out.println(bb.limit());
        // 切换为读
        bb.flip();
        System.out.println("position:"+bb.position());
        System.out.println("limit:"+bb.limit());
        System.out.println(String.valueOf(bb.get()));
        System.out.println(String.valueOf(bb.get()));
        System.out.println(String.valueOf(bb.get()));
        System.out.println("position:"+bb.position());
        // 切换为写
        bb.flip();
        bb.limit(10);
        System.out.println("limit:"+bb.limit());
        System.out.println("position:"+bb.position());
        bb.position(3);
        bb.put("456".getBytes(StandardCharsets.UTF_8));
        // 切换为读
        bb.flip();
        bb.position(3);
        bb.mark(); // 比较位置 3
        System.out.println("position:"+bb.position());
        System.out.println(String.valueOf(bb.get()));
        System.out.println(String.valueOf(bb.get()));
        System.out.println(String.valueOf(bb.get()));
        System.out.println("position:"+bb.position());
        System.out.println("limit:"+bb.limit());
        bb.reset();
        System.out.println(bb.position());
        System.out.println(String.valueOf(bb.get()));
    }

}
