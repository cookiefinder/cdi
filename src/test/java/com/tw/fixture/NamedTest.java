package com.tw.fixture;

import com.thoughtworks.fusheng.integration.junit5.FuShengTest;
import com.tw.FushengContainer;
import com.tw.exception.ContainerStartupException;

@FuShengTest
public class NamedTest {
    private FushengContainer fushengContainer;

    public void startup(String className) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        fushengContainer = FushengContainer.startup(clazz);
    }

    public String getComponent(String className) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        try {
            return fushengContainer.getComponent(clazz).get(0).getClass().getName();
        } catch (ContainerStartupException e) {
            return e.getMessage();
        }
    }
}
