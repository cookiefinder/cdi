package com.tw.fixture.model.test;

import javax.inject.Inject;

public class Computer {
    private final Console console;

    @Inject
    public Computer(Console console) {
        this.console = console;
    }

    public String print() {
        return console.print();
    }
}
