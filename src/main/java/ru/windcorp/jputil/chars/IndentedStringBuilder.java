/*
 * JPUtil
 * Copyright (C)  2019-2021  OLEGSHA/Javapony and contributors
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

package ru.windcorp.jputil.chars;

public class IndentedStringBuilder {

	private final StringBuilder sb = new StringBuilder();

	private int indentLevel = 0;
	private boolean indentApplied = false;

	private String[] indentCache = new String[16];
	private String indent = "";
	private final char[] indentFill;

	public IndentedStringBuilder(char[] indentFill) {
		this.indentFill = indentFill;
	}

	public IndentedStringBuilder(String indentFill) {
		this(indentFill.toCharArray());
	}

	public IndentedStringBuilder(char indentChar, int length) {
		this(StringUtil.sequence(indentChar, length));
	}

	public IndentedStringBuilder() {
		this(new char[] { ' ' });
	}

	@Override
	public String toString() {
		return sb.toString();
	}

	public int getIndentLevel() {
		return indentLevel;
	}

	public void setIndentLevel(int level) {
		this.indentLevel = level;
		updateIndent();
	}

	public char[] getIndentFill() {
		return indentFill;
	}

	protected void updateIndent() {
		if (indentLevel < indentCache.length) {
			indent = indentCache[indentLevel];
			if (indent != null)
				return;
		}

		char[] fill = getIndentFill();
		char[] array = new char[fill.length * getIndentLevel()];
		for (int i = 0; i < array.length; i += fill.length)
			System.arraycopy(fill, 0, array, i, fill.length);
		indent = new String(array);

		if (indentLevel < indentCache.length) {
			indentCache[indentLevel] = indent;
		}
	}

	public IndentedStringBuilder indent() {
		setIndentLevel(getIndentLevel() + 1);
		return this;
	}

	public IndentedStringBuilder unindent() {
		setIndentLevel(getIndentLevel() - 1);
		return this;
	}

	public IndentedStringBuilder append(Object x) {
		if (x == null) {
			appendRaw("null");
			return this;
		}

		String str = x.toString();
		int newLines = StringUtil.count(str, '\n');

		if (newLines == 0) {
			appendRaw(str);
			return this;
		}

		String[] lines = StringUtil.split(str, '\n', newLines + 1);
		appendRaw(lines[0]);

		for (int i = 1; i < lines.length; ++i) {
			newLine();
			appendRaw(lines[i]);
		}

		return this;
	}

	public IndentedStringBuilder appendRaw(String str) {
		if (str.isEmpty())
			return this; // Do not append indent

		if (!indentApplied) {
			sb.append(indent);
			indentApplied = true;
		}

		sb.append(str);
		return this;
	}

	public IndentedStringBuilder newLine() {
		sb.append('\n');
		indentApplied = false;
		return this;
	}

}
