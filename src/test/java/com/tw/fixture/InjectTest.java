package com.tw.fixture;

import com.thoughtworks.fusheng.integration.junit5.FuShengTest;
import com.tw.FushengContainer;
import com.tw.fixture.model.test.Computer;
import java.util.List;
import java.util.Objects;

@FuShengTest
public class InjectTest {
    private FushengContainer fushengContainer;

    public void startup(String className) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        fushengContainer = FushengContainer.startup(clazz);
    }

    public String print(String className) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        List<?> component = fushengContainer.getComponent(clazz);
        Computer computer = (Computer) component.get(0);
        return computer.print();
    }

    public Boolean checkDependencyExist(String className) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        List<?> component = fushengContainer.getComponent(clazz);
        Computer computer = (Computer) component.get(0);
        return Objects.nonNull(computer.getKeyboard());
    }
}
