package com.demo.allframework.netty.tools;

import io.netty.util.Recycler;

/**
 * @Author YUDI-Corgi
 * @Description Recycler 对象池使用测试
 */
public class RecyclerTest {

    private static final Recycler<User> RECYCLER = new Recycler<User>() {
        /**
         * 自定义对象的创建方式
         * @param handle 池化对象的处理实现
         * @return 池化对象 - User
         */
        @Override
        protected User newObject(Handle<User> handle) {
            return new User(handle);
        }
    };

    private static class User {
        private final Recycler.Handle<User> handle;

        public User(Recycler.Handle<User> handle) {
            this.handle = handle;
        }

        public void recycle() {
            handle.recycle(this);
        }
    }

    public static void main(String[] args) {
        User user = RECYCLER.get();
        user.recycle();
        User user0 = RECYCLER.get();

        System.out.println(user == user0); // true
    }
}
