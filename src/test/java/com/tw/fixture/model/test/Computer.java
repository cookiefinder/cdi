package com.tw.fixture.model.test;

import javax.inject.Inject;

public class Computer {
    private Console console;
    @Inject
    private Keyboard keyboard;

    @Inject
    public Computer(Console console) {
        this.console = console;
    }

    public Computer(Keyboard keyboard) {
        this.keyboard = keyboard;
    }

    public String print() {
        return console.print();
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }
}