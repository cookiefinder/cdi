package com.tw.fixture;

import com.tw.FushengContainer;
import java.util.List;

public class FushengContainerFixture {
    private static FushengContainer fushengContainer;

    public static String startup(Class<?> clazz) {
        fushengContainer = FushengContainer.startup(clazz);
        return "ok";
    }

    public static <T> List<T> getComponent(Class<T> clazz) {
        return fushengContainer.getComponent(clazz);
    }
}
