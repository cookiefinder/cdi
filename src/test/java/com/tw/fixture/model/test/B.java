package com.tw.fixture.model.test;

import javax.inject.Inject;

public class B {
    private A a;

    @Inject
    public B(A a) {
        this.a = a;
    }
}
