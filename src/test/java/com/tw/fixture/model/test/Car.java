package com.tw.fixture.model.test;

import javax.inject.Inject;
import javax.inject.Named;

public class Car {
    private Tire tire;

    @Inject
    @Named("michelin")
    public Car(Tire tire) {
        this.tire = tire;
    }

    public Tire getTire() {
        return tire;
    }
}
