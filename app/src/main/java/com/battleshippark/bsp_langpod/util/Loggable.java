package com.battleshippark.bsp_langpod.util;

/**
 */

public interface Loggable {
    void v(String msg);

    void v(String msg, Object... args);

    void d(String msg);

    void d(String msg, Object... args);

    void i(String msg);

    void i(String msg, Object... args);

    void w(String msg);

    void w(String msg, Object... args);

    void w(Throwable t);

    void e(String msg);

    void e(String msg, Object... args);
}
