package com.tw.fixture;

import com.thoughtworks.fusheng.integration.junit5.FuShengTest;
import com.tw.FushengContainer;

@FuShengTest
public class FushengContainerTest {
    private FushengContainer fushengContainer;

    public void startup(String className) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        fushengContainer = FushengContainer.startup(clazz);
    }

    public Integer getComponentSize(String className) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        return fushengContainer.getComponent(clazz).size();
    }
}
