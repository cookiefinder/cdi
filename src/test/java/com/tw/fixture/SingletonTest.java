package com.tw.fixture;

import com.thoughtworks.fusheng.integration.junit5.FuShengTest;
import com.tw.FushengContainer;
import com.tw.exception.ContainerStartupException;

@FuShengTest
public class SingletonTest {
    private FushengContainer fushengContainer;

    public void startup(String className) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        fushengContainer = FushengContainer.startup(clazz);
    }

    public String getInstance(String className) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        try {
            return fushengContainer.getComponent(clazz).get(0).toString();
        } catch (ContainerStartupException e) {
            return e.getMessage();
        }
    }
}
