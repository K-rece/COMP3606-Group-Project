package com.example.comp.model;

import java.io.Serializable;

public class Milestone implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private boolean done;

    public Milestone(String name) {
        this.name = name;
        this.done = false;
    }

    public String getName() {
        return name;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}