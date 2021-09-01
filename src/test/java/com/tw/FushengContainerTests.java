package com.tw;

import com.tw.fixture.FushengContainerFixture;
import com.tw.fixture.test.TestInstance;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FushengContainerTests {
    @Test
    void should_return_instance_when_startup_container_given_bootstrap_class() {
        FushengContainerFixture.startup(Main.class);
        List<TestInstance> components = FushengContainerFixture.getComponent(TestInstance.class);
        assertEquals(components.size(), 1);
    }
}