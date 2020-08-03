/*******************************************************************************
 * Optica
 * Copyright (C) 2020  Wind Corporation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package ru.windcorp.optica.client.graphics.input;

public class WheelScrollEvent extends WheelEvent {

    protected double xOffset;
    protected double yOffset;

    protected WheelScrollEvent(double xOffset, double yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    protected WheelScrollEvent() {}

    public boolean isUp() {
    	return yOffset > 0;
    }

    public boolean isDown() {
    	return yOffset < 0;
    }

    public boolean isRight() {
    	return xOffset > 0;
    }

    public boolean isLeft() {
    	return xOffset < 0;
    }

    public boolean hasVerticalMovement() {
    	return yOffset != 0;
    }

    public boolean hasHorizontalMovement() {
    	return xOffset != 0;
    }

    public double getX() {
    	return xOffset;
    }

    public double getY() {
    	return yOffset;
    }

    @Override
    public WheelEvent snapshot() {
        return new StaticWheelScrollEvent(xOffset, yOffset, getTime());
    }

    private class StaticWheelScrollEvent extends WheelScrollEvent {

        private final double time;

        public StaticWheelScrollEvent(
        		double xOffset, double yOffset, double time
        ) {
            super(xOffset, yOffset);
            this.time = time;
        }

        @Override
        public double getTime() {
        	return time;
        }

        @Override
        public WheelEvent snapshot() {
            return this;
        }

    }
}
