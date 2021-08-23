/*
 * Progressia
 * Copyright (C)  2020-2021  Wind Corporation and contributors
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
 */

package ru.windcorp.progressia.client.graphics.flat;

import java.nio.FloatBuffer;

import glm.mat._4.Mat4;
import glm.vec._2.Vec2;
import glm.vec._4.Vec4;

public class TransformedMask {

	public static final int SIZE_IN_FLOATS = 2 * 3 * 2;

	private final Vec2 origin = new Vec2();
	private final Vec2 width = new Vec2();
	private final Vec2 height = new Vec2();

	private final Vec2 counterOrigin = new Vec2();
	private final Vec2 counterWidth = new Vec2();
	private final Vec2 counterHeight = new Vec2();

	// Temporary values, objects cached for efficiency
	private Vec4 startXstartY = null;
	private Vec4 startXendY = null;
	private Vec4 endXstartY = null;
	private Vec4 endXendY = null;

	public TransformedMask(Vec2 origin, Vec2 width, Vec2 height, Vec2 counterOrigin, Vec2 counterWidth,
			Vec2 counterHeight) {
		set(origin, width, height, counterOrigin, counterWidth, counterHeight);
	}

	public TransformedMask(Mask mask, Mat4 transform) {
		set(mask, transform);
	}

	public TransformedMask() {
		// Do nothing
	}

	public TransformedMask set(Vec2 origin, Vec2 width, Vec2 height, Vec2 counterOrigin, Vec2 counterWidth,
			Vec2 counterHeight) {
		this.origin.set(origin.x, origin.y);
		this.width.set(width.x, width.y);
		this.height.set(height.x, height.y);
		this.counterOrigin.set(counterOrigin.x, counterOrigin.y);
		this.counterWidth.set(counterWidth.x, counterWidth.y);
		this.counterHeight.set(counterHeight.x, counterHeight.y);

		return this;
	}

	public TransformedMask set(Mask mask, Mat4 transform) {
		applyTransform(mask, transform);
		setFields();
		return this;
	}

	private void applyTransform(Mask mask, Mat4 transform) {
		ensureTemporaryVariablesExist();

		int relX = mask.getWidth();
		int relY = mask.getHeight();

		startXstartY.set(0, 0, 0, 1);
		startXendY.set(0, relY, 0, 1);
		endXstartY.set(relX, 0, 0, 1);
		endXendY.set(relX, relY, 0, 1);

		transform.mul(startXstartY);
		transform.mul(startXendY);
		transform.mul(endXstartY);
		transform.mul(endXendY);
	}

	private void ensureTemporaryVariablesExist() {
		if (startXstartY == null) {
			startXstartY = new Vec4();
			startXendY = new Vec4();
			endXstartY = new Vec4();
			endXendY = new Vec4();
		}
	}

	private void setFields() {
		origin.set(startXstartY.x, startXstartY.y);

		width.set(endXstartY.x - startXstartY.x, endXstartY.y - startXstartY.y);

		height.set(startXendY.x - startXstartY.x, startXendY.y - startXstartY.y);

		counterOrigin.set(endXendY.x, endXendY.y);

		counterWidth.set(startXendY.x - endXendY.x, startXendY.y - endXendY.y);

		counterHeight.set(endXstartY.x - endXendY.x, endXstartY.y - endXendY.y);
	}

	public void writeToBuffer(FloatBuffer output) {
		output.put(origin.x).put(origin.y);
		output.put(width.x).put(width.y);
		output.put(height.x).put(height.y);
		output.put(counterOrigin.x).put(counterOrigin.y);
		output.put(counterWidth.x).put(counterWidth.y);
		output.put(counterHeight.x).put(counterHeight.y);
	}

}
