package com.tw.fixture.model.test;

import javax.inject.Inject;

public class A {
    private B b;

    @Inject
    public A(B b) {
        this.b = b;
    }
}
