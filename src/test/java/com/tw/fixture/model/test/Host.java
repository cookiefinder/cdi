package com.tw.fixture.model.test;

import javax.inject.Inject;

public class Host {
    private Screen screen;

    @Inject
    public Host(Screen screen) {
        this.screen = screen;
    }
}
