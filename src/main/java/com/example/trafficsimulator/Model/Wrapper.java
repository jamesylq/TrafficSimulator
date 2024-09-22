package com.example.trafficsimulator.Model;

import java.io.Serializable;

public abstract class Wrapper implements Serializable {
    public int index;

    Wrapper(int ind) {
        this.index = ind;
    }
}
