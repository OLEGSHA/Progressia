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

package ru.windcorp.jputil;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

public class CSVWriter {

	private String columnSeparator = ";";
	private String rowSeparator = "\n";

	private boolean shouldAddSeparator = false;

	private final PrintWriter parent;

	public CSVWriter(PrintWriter output) {
		this.parent = output;
	}

	public CSVWriter(Writer output) {
		this(new PrintWriter(output));
	}

	public CSVWriter(OutputStream output) {
		this(new PrintWriter(output));
	}

	public PrintWriter getParent() {
		return parent;
	}

	public String getColumnSeparator() {
		return columnSeparator;
	}

	public CSVWriter setColumnSeparator(String columnSeparator) {
		this.columnSeparator = columnSeparator;
		return this;
	}

	public String getRowSeparator() {
		return rowSeparator;
	}

	public CSVWriter setRowSeparator(String rowSeparator) {
		this.rowSeparator = rowSeparator;
		return this;
	}

	public void print(Object object) {
		skip();
		getParent().print(String.valueOf(object));
	}

	public void skip() {
		if (shouldAddSeparator) {
			getParent().print(getColumnSeparator());
		} else {
			shouldAddSeparator = true;
		}
	}

	public void skip(int amount) {
		for (int i = 0; i < amount; ++i) {
			skip();
		}
	}

	public void endRow() {
		getParent().print(getRowSeparator());
		shouldAddSeparator = false;
	}

	public void endRow(Object object) {
		print(object);
		endRow();
	}

	public void flush() {
		getParent().flush();
	}

	public void close() {
		getParent().close();
	}

}
