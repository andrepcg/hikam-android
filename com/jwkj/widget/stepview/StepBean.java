package com.jwkj.widget.stepview;

public class StepBean {
    public static final int STEP_COMPLETED = 1;
    public static final int STEP_CURRENT = 0;
    public static final int STEP_UNDO = -1;
    private String name;
    private int state;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public StepBean(String name, int state) {
        this.name = name;
        this.state = state;
    }
}
