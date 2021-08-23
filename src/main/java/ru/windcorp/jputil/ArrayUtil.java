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

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.Objects;

public class ArrayUtil {

	private ArrayUtil() {
	}

	public static int firstIndexOf(byte[] array, byte element) {
		for (int i = 0; i < array.length; ++i) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int lastIndexOf(byte[] array, byte element) {
		for (int i = array.length - 1; i >= 0; --i) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int occurences(byte[] array, byte element) {
		int result = 0;
		for (int i = 0; i < array.length; ++i) {
			if (array[i] == element) {
				++result;
			}
		}
		return result;
	}

	public static int hasDuplicates(byte[] array) {
		for (int i = 0; i < array.length; ++i) {
			byte a = array[i];
			for (int j = i + 1; j < array.length; ++j) {
				if (array[j] == a) {
					return i;
				}
			}
		}

		return -1;
	}

	public static boolean isSorted(byte[] array, boolean ascending) {
		for (int i = 0; i < array.length - 1; ++i) {
			if (array[i] == array[i + 1])
				continue;

			if ((array[i] < array[i + 1]) != ascending) {
				return false;
			}
		}

		return true;
	}

	public static int firstIndexOf(short[] array, short element) {
		for (int i = 0; i < array.length; ++i) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int lastIndexOf(short[] array, short element) {
		for (int i = array.length - 1; i >= 0; --i) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int occurences(short[] array, short element) {
		int result = 0;
		for (int i = 0; i < array.length; ++i) {
			if (array[i] == element) {
				++result;
			}
		}
		return result;
	}

	public static int hasDuplicates(short[] array) {
		for (int i = 0; i < array.length; ++i) {
			short a = array[i];
			for (int j = i + 1; j < array.length; ++j) {
				if (array[j] == a) {
					return i;
				}
			}
		}

		return -1;
	}

	public static boolean isSorted(short[] array, boolean ascending) {
		for (int i = 0; i < array.length - 1; ++i) {
			if (array[i] == array[i + 1])
				continue;

			if ((array[i] < array[i + 1]) != ascending) {
				return false;
			}
		}

		return true;
	}

	public static int firstIndexOf(int[] array, int element) {
		for (int i = 0; i < array.length; ++i) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int lastIndexOf(int[] array, int element) {
		for (int i = array.length - 1; i >= 0; --i) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int occurences(int[] array, int element) {
		int result = 0;
		for (int i = 0; i < array.length; ++i) {
			if (array[i] == element) {
				++result;
			}
		}
		return result;
	}

	public static int hasDuplicates(int[] array) {
		for (int i = 0; i < array.length; ++i) {
			int a = array[i];
			for (int j = i + 1; j < array.length; ++j) {
				if (array[j] == a) {
					return i;
				}
			}
		}

		return -1;
	}

	public static boolean isSorted(int[] array, boolean ascending) {
		for (int i = 0; i < array.length - 1; ++i) {
			if (array[i] == array[i + 1])
				continue;

			if ((array[i] < array[i + 1]) != ascending) {
				return false;
			}
		}

		return true;
	}

	public static int firstIndexOf(long[] array, long element) {
		for (int i = 0; i < array.length; ++i) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int lastIndexOf(long[] array, long element) {
		for (int i = array.length - 1; i >= 0; --i) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int occurences(long[] array, long element) {
		int result = 0;
		for (int i = 0; i < array.length; ++i) {
			if (array[i] == element) {
				++result;
			}
		}
		return result;
	}

	public static int hasDuplicates(long[] array) {
		for (int i = 0; i < array.length; ++i) {
			long a = array[i];
			for (int j = i + 1; j < array.length; ++j) {
				if (array[j] == a) {
					return i;
				}
			}
		}

		return -1;
	}

	public static boolean isSorted(long[] array, boolean ascending) {
		for (int i = 0; i < array.length - 1; ++i) {
			if (array[i] == array[i + 1])
				continue;

			if ((array[i] < array[i + 1]) != ascending) {
				return false;
			}
		}

		return true;
	}

	public static int firstIndexOf(float[] array, float element) {
		for (int i = 0; i < array.length; ++i) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int lastIndexOf(float[] array, float element) {
		for (int i = array.length - 1; i >= 0; --i) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int occurences(float[] array, float element) {
		int result = 0;
		for (int i = 0; i < array.length; ++i) {
			if (array[i] == element) {
				++result;
			}
		}
		return result;
	}

	public static int hasDuplicates(float[] array) {
		for (int i = 0; i < array.length; ++i) {
			float a = array[i];
			for (int j = i + 1; j < array.length; ++j) {
				if (array[j] == a) {
					return i;
				}
			}
		}

		return -1;
	}

	public static boolean isSorted(float[] array, boolean ascending) {
		for (int i = 0; i < array.length - 1; ++i) {
			if (array[i] == array[i + 1])
				continue;

			if ((array[i] < array[i + 1]) != ascending) {
				return false;
			}
		}

		return true;
	}

	public static int firstIndexOf(double[] array, double element) {
		for (int i = 0; i < array.length; ++i) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int lastIndexOf(double[] array, double element) {
		for (int i = array.length - 1; i >= 0; --i) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int occurences(double[] array, double element) {
		int result = 0;
		for (int i = 0; i < array.length; ++i) {
			if (array[i] == element) {
				++result;
			}
		}
		return result;
	}

	public static int hasDuplicates(double[] array) {
		for (int i = 0; i < array.length; ++i) {
			double a = array[i];
			for (int j = i + 1; j < array.length; ++j) {
				if (array[j] == a) {
					return i;
				}
			}
		}

		return -1;
	}

	public static boolean isSorted(double[] array, boolean ascending) {
		for (int i = 0; i < array.length - 1; ++i) {
			if (array[i] == array[i + 1])
				continue;

			if ((array[i] < array[i + 1]) != ascending) {
				return false;
			}
		}

		return true;
	}

	public static int firstIndexOf(boolean[] array, boolean element) {
		for (int i = 0; i < array.length; ++i) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int lastIndexOf(boolean[] array, boolean element) {
		for (int i = array.length - 1; i >= 0; --i) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int occurences(boolean[] array, boolean element) {
		int result = 0;
		for (int i = 0; i < array.length; ++i) {
			if (array[i] == element) {
				++result;
			}
		}
		return result;
	}

	public static int firstIndexOf(char[] array, char element) {
		for (int i = 0; i < array.length; ++i) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int lastIndexOf(char[] array, char element) {
		for (int i = array.length - 1; i >= 0; --i) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int occurences(char[] array, char element) {
		int result = 0;
		for (int i = 0; i < array.length; ++i) {
			if (array[i] == element) {
				++result;
			}
		}
		return result;
	}

	public static int hasDuplicates(char[] array) {
		for (int i = 0; i < array.length; ++i) {
			char a = array[i];
			for (int j = i + 1; j < array.length; ++j) {
				if (array[j] == a) {
					return i;
				}
			}
		}

		return -1;
	}

	public static boolean isSorted(char[] array, boolean ascending) {
		for (int i = 0; i < array.length - 1; ++i) {
			if (array[i] == array[i + 1])
				continue;

			if ((array[i] < array[i + 1]) != ascending) {
				return false;
			}
		}

		return true;
	}

	public static int firstIndexOf(Object[] array, Object element) {
		for (int i = 0; i < array.length; ++i) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int lastIndexOf(Object[] array, Object element) {
		for (int i = array.length - 1; i >= 0; --i) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int occurences(Object[] array, Object element) {
		int result = 0;
		for (int i = 0; i < array.length; ++i) {
			if (array[i] == element) {
				++result;
			}
		}
		return result;
	}

	public static int hasDuplicates(Object[] array) {
		for (int i = 0; i < array.length; ++i) {
			Object a = array[i];
			for (int j = i + 1; j < array.length; ++j) {
				if (array[j] == a) {
					return i;
				}
			}
		}

		return -1;
	}

	public static int firstIndexOfEqual(Object[] array, Object element) {
		for (int i = 0; i < array.length; ++i) {
			if (Objects.equals(array[i], element)) {
				return i;
			}
		}
		return -1;
	}

	public static int lastIndexOfEqual(Object[] array, Object element) {
		for (int i = array.length - 1; i >= 0; --i) {
			if (Objects.equals(array[i], element)) {
				return i;
			}
		}
		return -1;
	}

	public static int occurencesOfEqual(Object[] array, Object element) {
		int result = 0;
		for (int i = 0; i < array.length; ++i) {
			if (Objects.equals(array[i], element)) {
				++result;
			}
		}
		return result;
	}

	public static int hasEquals(Object[] array) {
		for (int i = 0; i < array.length; ++i) {
			Object a = array[i];
			for (int j = i + 1; j < array.length; ++j) {
				if (Objects.equals(array[j], a)) {
					return i;
				}
			}
		}

		return -1;
	}

	public static <T extends Comparable<T>> boolean isSorted(T[] array, boolean ascending) {
		for (int i = 0; i < array.length - 1; ++i) {
			if (array[i] == array[i + 1])
				continue;

			int order = array[i].compareTo(array[i + 1]);

			if ((order < 0) != ascending) {
				return false;
			}
		}

		return true;
	}

	public static long sum(byte[] array, int start, int length) {
		long s = 0;
		length += start;
		for (int i = start; i < length; ++i) {
			s += array[i];
		}
		return s;
	}

	public static long sum(short[] array, int start, int length) {
		long s = 0;
		length += start;
		for (int i = start; i < length; ++i) {
			s += array[i];
		}
		return s;
	}

	public static long sum(int[] array, int start, int length) {
		long s = 0;
		length += start;
		for (int i = start; i < length; ++i) {
			s += array[i];
		}
		return s;
	}

	public static long sum(long[] array, int start, int length) {
		long s = 0;
		length += start;
		for (int i = start; i < length; ++i) {
			s += array[i];
		}
		return s;
	}

	public static BigInteger longSum(long[] array, int start, int length) {
		BigInteger s = BigInteger.ZERO;
		length += start;
		for (int i = start; i < length; ++i) {
			s = s.add(BigInteger.valueOf(array[i]));
		}
		return s;
	}

	public static float sum(float[] array, int start, int length) {
		float s = 0;
		length += start;
		for (int i = start; i < length; ++i) {
			s += array[i];
		}
		return s;
	}

	public static double sum(double[] array, int start, int length) {
		double s = 0;
		length += start;
		for (int i = start; i < length; ++i) {
			s += array[i];
		}
		return s;
	}

	public static long sum(char[] array, int start, int length) {
		long s = 0;
		length += start;
		for (int i = start; i < length; ++i) {
			s += array[i];
		}
		return s;
	}

	public static int checkArrayOffsetLength(Object array, int offset, int length) {
		int arrayLength = Array.getLength(array);

		if (length < 0)
			length = arrayLength;

		int end = offset + length;
		if (end > arrayLength || offset < 0)
			throw new IllegalArgumentException(
					"Array contains [0; " + arrayLength + "), requested [" + offset + "; " + end + ")");

		return length;
	}

	public static int checkArrayStartEnd(Object array, int start, int end) {
		int arrayLength = Array.getLength(array);

		if (end < 0)
			end = arrayLength;

		if (start > end)
			throw new IllegalArgumentException("Start > end: " + start + " > " + end);

		if (end > arrayLength || start < 0)
			throw new IllegalArgumentException(
					"Array contains [0; " + arrayLength + "), requested [" + start + "; " + end + ")");

		return end;
	}

}
