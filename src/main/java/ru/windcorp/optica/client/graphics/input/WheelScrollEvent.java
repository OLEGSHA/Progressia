package ru.windcorp.optica.client.graphics.input;

import glm.vec._2.d.Vec2d;

public class WheelScrollEvent extends WheelEvent {

    protected double xoffset;
    protected double yoffset;

    protected WheelScrollEvent(double xoffset, double yoffset) {
        this.xoffset = xoffset;
        this.yoffset = yoffset;
    }

    protected WheelScrollEvent() {}

    public boolean isUp() { return yoffset > 0; }

    public boolean isDown() { return yoffset < 0; }

    public boolean isRight() { return xoffset > 0; }

    public boolean isLeft() { return xoffset < 0; }

    public boolean hasVerticalMovement() { return yoffset != 0; }

    public boolean hasHorizontalMovement() { return xoffset != 0; }

    public double getX() { return xoffset; }

    public double getY() { return yoffset; }

    @Override
    public WheelEvent snapshot() {
        return new StaticWheelScrollEvent(xoffset, yoffset, getTime());
    }

    private class StaticWheelScrollEvent extends WheelScrollEvent {

        private final double time;

        public StaticWheelScrollEvent(double xoffset, double yoffset, double time) {
            super(xoffset, yoffset);
            this.time = time;
        }

        @Override
        public double getTime() { return time; }

        @Override
        public WheelEvent snapshot() {
            return this;
        }

    }
}
