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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.IntFunction;

import ru.windcorp.jputil.ArrayUtil;

public class StringUtil {

	private StringUtil() {
	}

	private static final String NULL_PLACEHOLDER = "[null]";
	private static final String EMPTY_PLACEHOLDER = "[empty]";
	private static final String DEFAULT_SEPARATOR = "; ";

	public static <T> String arrayToString(T[] array, String separator, String empty, String nullPlaceholder,
			String nullArray) {

		if (separator == null) {
			throw new IllegalArgumentException(new NullPointerException());
		}

		if (array == null) {
			return nullArray;
		}

		if (array.length == 0) {
			return empty;
		}

		StringBuilder sb = new StringBuilder(array[0] == null ? nullPlaceholder : array[0].toString());

		for (int i = 1; i < array.length; ++i) {
			sb.append(separator);
			sb.append(array[i] == null ? nullPlaceholder : array[i].toString());
		}

		return sb.toString();
	}

	public static <T> String arrayToString(T[] array, String separator) {
		return arrayToString(array, separator, EMPTY_PLACEHOLDER, NULL_PLACEHOLDER, "[null array]");
	}

	public static <T> String arrayToString(T[] array) {
		return arrayToString(array, DEFAULT_SEPARATOR);
	}

	public static String iteratorToString(Iterator<?> iterator, String separator, String empty, String nullPlaceholder,
			String nullIterator) {

		if (separator == null) {
			throw new IllegalArgumentException(new NullPointerException());
		}

		if (iterator == null) {
			return nullIterator;
		}

		if (!iterator.hasNext()) {
			return empty;
		}

		Object obj = iterator.next();
		StringBuilder sb = new StringBuilder(obj == null ? nullPlaceholder : obj.toString());

		while (iterator.hasNext()) {
			obj = iterator.next();
			sb.append(separator);
			sb.append(obj == null ? nullPlaceholder : obj.toString());
		}

		return sb.toString();
	}

	public static String iteratorToString(Iterator<?> iterator, String separator) {
		return iteratorToString(iterator, separator, EMPTY_PLACEHOLDER, NULL_PLACEHOLDER, "[null iterator]");
	}

	public static String iteratorToString(Iterator<?> iterator) {
		return iteratorToString(iterator, DEFAULT_SEPARATOR);
	}

	public static String iterableToString(Iterable<?> iterable, String separator, String empty, String nullPlaceholder,
			String nullIterable) {

		if (separator == null) {
			throw new IllegalArgumentException(new NullPointerException());
		}

		if (iterable == null) {
			return nullIterable;
		}

		return iteratorToString(iterable.iterator(), separator, empty, nullPlaceholder, nullIterable);
	}

	public static String iterableToString(Iterable<?> iterable, String separator) {
		return iterableToString(iterable, separator, EMPTY_PLACEHOLDER, NULL_PLACEHOLDER, "[null iterable]");
	}

	public static String iterableToString(Iterable<?> iterable) {
		return iterableToString(iterable, DEFAULT_SEPARATOR);
	}

	public static <T> String supplierToString(IntFunction<T> supplier, int length, String separator, String empty,
			String nullPlaceholder, String nullSupplier) {

		if (separator == null)
			throw new IllegalArgumentException(new NullPointerException());
		if (supplier == null)
			return nullSupplier;
		if (length == 0)
			return empty;

		if (length > 0) {
			return supplierToStringExactly(supplier, length, separator, nullPlaceholder);
		} else {
			return supplierToStringUntilNull(supplier, separator, empty);
		}

	}

	private static <T> String supplierToStringExactly(IntFunction<T> supplier, int length, String separator,
			String nullPlaceholder) {
		T element = supplier.apply(0);

		StringBuilder sb = new StringBuilder(element == null ? nullPlaceholder : element.toString());

		for (int i = 1; i < length; ++i) {
			sb.append(separator);
			element = supplier.apply(i);
			sb.append(element == null ? nullPlaceholder : element.toString());
		}

		return sb.toString();
	}

	private static <T> String supplierToStringUntilNull(IntFunction<T> supplier, String separator, String empty) {
		T element = supplier.apply(0);

		if (element == null) {
			return empty;
		}

		StringBuilder sb = new StringBuilder(element.toString());

		int i = 0;
		while ((element = supplier.apply(i++)) != null) {
			sb.append(separator);
			sb.append(element);
		}

		return sb.toString();
	}

	public static String supplierToString(IntFunction<?> supplier, int length, String separator) {
		return supplierToString(supplier, length, separator, EMPTY_PLACEHOLDER, NULL_PLACEHOLDER, "[null supplier]");
	}

	public static String supplierToString(IntFunction<?> supplier, String separator) {
		return supplierToString(supplier, -1, separator, EMPTY_PLACEHOLDER, NULL_PLACEHOLDER, "[null supplier]");
	}

	public static String supplierToString(IntFunction<?> supplier, int length) {
		return supplierToString(supplier, length, DEFAULT_SEPARATOR);
	}

	public static String supplierToString(IntFunction<?> supplier) {
		return supplierToString(supplier, -1, DEFAULT_SEPARATOR);
	}

	public static byte[] toJavaByteArray(String str) {
		char[] chars = str.toCharArray();
		byte[] bytes = new byte[chars.length];

		for (int i = 0; i < bytes.length; ++i) {
			bytes[i] = (byte) chars[i];
		}

		return bytes;
	}

	public static int count(String src, char target) {
		int i = 0;
		for (char c : src.toCharArray()) {
			if (c == target) {
				++i;
			}
		}

		return i;
	}

	public static String[] split(String src, char separator) {
		return split(src, separator, count(src, separator) + 1);
	}

	public static String[] split(String src, char separator, int arrayLength) {
		if (arrayLength < 0)
			throw illegalArrayLength(arrayLength);
		else if (arrayLength == 0)
			return new String[0];
		else if (arrayLength == 1)
			return new String[] { src };

		String[] result = new String[arrayLength];

		int resultIndex = 0;
		StringBuilder sb = new StringBuilder();
		for (char c : src.toCharArray()) {
			if (c == separator && (resultIndex + 1) < arrayLength) {
				result[resultIndex] = resetStringBuilder(sb);
				++resultIndex;
			} else {
				sb.append(c);
			}
		}

		result[resultIndex] = sb.toString();

		return result;
	}

	public static int count(String src, char... target) {
		int i = 0;
		for (char c : src.toCharArray()) {
			for (char t : target) {
				if (c == t) {
					++i;
					break;
				}
			}
		}

		return i;
	}

	public static String[] split(String src, char... separator) {
		return split(src, count(src, separator) + 1, separator);
	}

	public static String[] split(String src, int arrayLength, char... separator) {
		if (arrayLength < 0)
			throw illegalArrayLength(arrayLength);
		else if (arrayLength == 0)
			return new String[0];
		else if (arrayLength == 1)
			return new String[] { src };

		String[] result = new String[arrayLength];

		int resultIndex = 0;
		StringBuilder sb = new StringBuilder();

		charLoop: for (char c : src.toCharArray()) {
			if ((resultIndex + 1) < arrayLength) {
				for (char h : separator) {
					if (c == h) {
						result[resultIndex] = resetStringBuilder(sb);
						++resultIndex;
						continue charLoop;
					}
				}
			}

			sb.append(c);
		}

		result[resultIndex] = sb.toString();

		return result;
	}

	public static int count(String src, CharPredicate test) {
		int i = 0;
		for (char c : src.toCharArray()) {
			if (test.test(c))
				i++;
		}

		return i;
	}

	public static String[] split(String src, CharPredicate test) {
		return split(src, count(src, test) + 1, test);
	}

	public static String[] split(String src, int arrayLength, CharPredicate test) {
		if (arrayLength < 0)
			throw illegalArrayLength(arrayLength);
		else if (arrayLength == 0)
			return new String[0];
		else if (arrayLength == 1)
			return new String[] { src };

		String[] result = new String[arrayLength];

		int resultIndex = 0;
		StringBuilder sb = new StringBuilder();

		charLoop: for (char c : src.toCharArray()) {
			if ((resultIndex + 1) < arrayLength && test.test(c)) {
				result[resultIndex] = resetStringBuilder(sb);
				++resultIndex;
				continue charLoop;
			}

			sb.append(c);
		}

		result[resultIndex] = sb.toString();

		return result;
	}

	/**
	 * Splits {@code src} at index {@code at} discarding the character at that
	 * index.
	 * <p>
	 * Indices {@code 0} and {@code src.length() - 1} produce {@code str}
	 * excluding the specified character and {@code ""}.
	 * <p>
	 * 
	 * @param src
	 *            the String to split
	 * @param at
	 *            index to split at
	 * @throws IllegalArgumentException
	 *             if the index is out of bounds for {@code src}
	 * @return an array containing the substrings, in order of encounter in
	 *         {@code src}. Its length is always 2.
	 */
	public static String[] splitAt(String src, int at) {
		Objects.requireNonNull(src, "src");

		if (at < 0) {
			throw new StringIndexOutOfBoundsException(at);
		} else if (at >= src.length()) {
			throw new StringIndexOutOfBoundsException(at);
		}

		if (at == 0) {
			return new String[] { "", src.substring(1) };
		} else if (at == src.length()) {
			return new String[] { src.substring(0, src.length() - 1), "" };
		}

		return new String[] { src.substring(0, at), src.substring(at + 1) };
	}

	/**
	 * Splits {@code src} at indices {@code at} discarding characters at those
	 * indices.
	 * <p>
	 * Indices {@code 0} and {@code src.length() - 1} produce extra zero-length
	 * outputs. Duplicate indices produce extra zero-length outputs.
	 * <p>
	 * Examples:
	 * 
	 * <pre>
	 * splitAt("a.b.c", 1, 3)    -> {"a", "b", "c"}
	 * splitAt("a..b",  1, 2)    -> {"a", "", "b"}
	 * splitAt(".b.",   0, 2)    -> {"", "b", ""}
	 * splitAt("a.b",   1, 1, 1) -> {"a", "", "", "b"}
	 * </pre>
	 * 
	 * @param src
	 *            the String to split
	 * @param at
	 *            indices to split at, in any order
	 * @throws IllegalArgumentException
	 *             if some index is out of bounds for {@code src}
	 * @return an array containing the substrings, in order of encounter in
	 *         {@code src}. Its length is always {@code at.length + 1}.
	 */
	public static String[] splitAt(String src, int... at) {
		Objects.requireNonNull(src, "src");
		Objects.requireNonNull(at, "at");

		if (at.length == 0)
			return new String[] { src };
		if (at.length == 1)
			return splitAt(src, at[0]);

		int[] indices; // Always sorted

		if (ArrayUtil.isSorted(at, true)) {
			indices = at;
		} else {
			indices = at.clone();
			Arrays.sort(indices);
		}

		if (indices[0] < 0) {
			throw new StringIndexOutOfBoundsException(indices[0]);
		} else if (indices[indices.length - 1] >= src.length()) {
			throw new StringIndexOutOfBoundsException(indices[indices.length - 1]);
		}

		String[] result = new String[at.length + 1];

		int start = 0;
		int resultIndex = 0;
		for (int index : indices) {
			int end = index;

			String substring;

			if (end <= start) {
				// Duplicate or successive index
				substring = "";
			} else {
				substring = src.substring(start, end);
			}

			result[resultIndex] = substring;
			resultIndex++;
			start = end + 1;
		}

		result[resultIndex] = src.substring(start);

		return result;
	}

	private static IllegalArgumentException illegalArrayLength(int length) {
		return new IllegalArgumentException("arrayLength must be non-negative (" + length + ")");
	}

	public static String remove(String src, char... remove) {
		char[] result = new char[src.length() - count(src, remove)];

		char current;
		int resultIndex = 0;

		mainLoop: for (int srcIndex = 0; srcIndex < src.length(); ++srcIndex) {
			current = src.charAt(srcIndex);

			for (char c : remove) {
				if (current == c) {
					continue mainLoop;
				}
			}

			result[resultIndex++] = current;
		}

		return new String(result);
	}

	public static String resetStringBuilder(StringBuilder sb) {
		String result = sb.toString();
		sb.setLength(0);
		sb.ensureCapacity(10);
		return result;
	}

	public static String readToString(InputStream is, Charset encoding, int bufferSize) throws IOException {
		char[] buffer = new char[bufferSize];
		StringBuilder result = new StringBuilder();

		Reader reader = new InputStreamReader(is, encoding);
		while (true) {
			int readChars = reader.read(buffer, 0, buffer.length);

			if (readChars == -1) {
				break;
			}

			result.append(buffer, 0, readChars);
		}

		return result.toString();
	}

	public static boolean equalsPart(char[] a, char[] b, int beginPos, int endPos) {
		if (beginPos < 0) {
			throw new IllegalArgumentException("beginPos must be non-negative (" + beginPos + ")");
		}

		if (endPos < beginPos) {
			throw new IllegalArgumentException("endPos must be greater than or equal to beginPos (endPos=" + endPos
					+ ", beginPos=" + beginPos + ")");
		}

		if (endPos >= Math.min(a.length, b.length)) {
			return false; // At least one of the arrays does not contain at
							// least one of the required elements
		}

		for (int i = beginPos; i < endPos; ++i) {
			if (a[i] != b[i]) {
				return false;
			}
		}

		return true;
	}

	// Java 8 is for pussies
	public static char[] join(char[]... srcs) {
		int tmp = 0;
		for (int i = 0; i < srcs.length; ++i) {
			tmp += srcs[i].length;
		}

		char[] result = new char[tmp];
		tmp = 0;
		for (int i = 0; i < srcs.length; ++i) {
			System.arraycopy(srcs[i], 0, result, tmp, srcs[i].length);
			tmp += srcs[i].length;
		}

		return result;
	}

	/**
	 * Finds and returns the index of the specified appearance of the specified
	 * character in the given array. The search starts at index 0.
	 * <p>
	 * Examples:
	 * <p>
	 * <table border="1">
	 * <tr>
	 * <th align="center"><code>src</code></th>
	 * <th align="center"><code>target</code></th>
	 * <th align="center"><code>skip</code></th>
	 * <th align="center">returns</th>
	 * </tr>
	 * <tr align="center">
	 * <td><code>a<u>.</u>b.c</code></td>
	 * <td><code>'.'</code></td>
	 * <td><code>0</code></td>
	 * <td><code>1</code></td>
	 * </tr>
	 * <tr align="center">
	 * <td><code>a.b<u>.</u>c</code></td>
	 * <td><code>'.'</code></td>
	 * <td><code>1</code></td>
	 * <td><code>3</code></td>
	 * </tr>
	 * <tr align="center">
	 * <td><code>a.b.c</code></td>
	 * <td><code>'.'</code></td>
	 * <td><code>2</code></td>
	 * <td><code>-1</code></td>
	 * </tr>
	 * <tr align="center">
	 * <td><code>a.b.c</code></td>
	 * <td><code>'d'</code></td>
	 * <td><i>any</i></td>
	 * <td><code>-1</code></td>
	 * </tr>
	 * </table>
	 * 
	 * @param src
	 *            - the array to search in.
	 * @param target
	 *            - the character to search for.
	 * @param skip
	 *            - the amount of <code>target</code> characters to be skipped.
	 * @return The index of the <code>skip+1</code>th <code>target</code>
	 *         character or -1, if none found.
	 * @see StringUtil#indexFromEnd(char[], char, int)
	 */
	public static int indexFromBeginning(char[] src, char target, int skip) {
		for (int i = 0; i < src.length; ++i) {
			if (src[i] == target) {
				if (skip == 0) {
					return i;
				}

				--skip;
			}
		}
		return -1;
	}

	/**
	 * Finds and returns the index of the specified appearance of the specified
	 * character in the given array. The search starts at index
	 * <code>src.length - 1</code>.
	 * <p>
	 * Examples:
	 * <p>
	 * <table border="1">
	 * <tr>
	 * <th align="center"><code>src</code></th>
	 * <th align="center"><code>target</code></th>
	 * <th align="center"><code>skip</code></th>
	 * <th align="center">returns</th>
	 * </tr>
	 * <tr align="center">
	 * <td><code>a.b<u>.</u>c</code></td>
	 * <td><code>'.'</code></td>
	 * <td><code>0</code></td>
	 * <td><code>3</code></td>
	 * </tr>
	 * <tr align="center">
	 * <td><code>a<u>.</u>b.c</code></td>
	 * <td><code>'.'</code></td>
	 * <td><code>1</code></td>
	 * <td><code>1</code></td>
	 * </tr>
	 * <tr align="center">
	 * <td><code>a.b.c</code></td>
	 * <td><code>'.'</code></td>
	 * <td><code>2</code></td>
	 * <td><code>-1</code></td>
	 * </tr>
	 * <tr align="center">
	 * <td><code>a.b.c</code></td>
	 * <td><code>'d'</code></td>
	 * <td><i>any</i></td>
	 * <td><code>-1</code></td>
	 * </tr>
	 * </table>
	 * 
	 * @param src
	 *            - the array to search in.
	 * @param target
	 *            - the character to search for.
	 * @param skip
	 *            - the amount of <code>target</code> characters to be skipped.
	 * @return The index of the <code>skip+1</code>th
	 *         <code>target</code>character from the end of the array or -1, if
	 *         none found.
	 * @see StringUtil#indexFromBeginning(char[], char, int)
	 */
	public static int indexFromEnd(char[] src, char target, int skip) {
		for (int i = src.length - 1; i >= 0; --i) {
			if (src[i] == target) {
				if (skip == 0) {
					return i;
				}

				--skip;
			}
		}

		return -1;
	}

	public static String padToLeft(String src, int length, char c) {
		if (length <= 0) {
			throw new IllegalArgumentException("length must be positive (" + length + ")");
		}

		if (length <= src.length()) {
			return src;
		}

		char[] result = new char[length];

		int i = 0;
		for (; i < src.length(); ++i) {
			result[i] = src.charAt(i);
		}

		for (; i < length; ++i) {
			result[i] = c;
		}

		return new String(result);
	}

	public static String padToLeft(String src, int length) {
		return padToLeft(src, length, ' ');
	}

	public static String padToRight(String src, int length, char c) {
		if (length <= 0) {
			throw new IllegalArgumentException("length must be positive (" + length + ")");
		}

		if (length <= src.length()) {
			return src;
		}

		char[] result = new char[length];

		int i = 0;
		int srcLength = src.length();

		for (; i < length - srcLength; ++i) {
			result[i] = c;
		}

		for (; i < length; ++i) {
			result[i] = src.charAt(i - (length - srcLength));
		}

		return new String(result);
	}

	public static String padToRight(String src, int length) {
		return padToRight(src, length, ' ');
	}

	public static String center(String src, int length) {
		return center(src, length, ' ');
	}

	public static String center(String src, int length, char filler) {
		if (length <= 0) {
			throw new IllegalArgumentException("length must be positive (" + length + ")");
		}

		if (src == null || length <= src.length()) {
			return src;
		}

		char[] result = new char[length];

		int leftPaddingLength = (length - src.length()) / 2;

		Arrays.fill(result, 0, leftPaddingLength, filler);

		for (int i = 0; i < src.length(); ++i) {
			result[i + leftPaddingLength] = src.charAt(i);
		}

		Arrays.fill(result, leftPaddingLength + src.length(), result.length, filler);

		return new String(result);
	}

	public static int countWords(String src) {
		int i = 0;
		boolean isWord = false;

		for (char c : src.toCharArray()) {
			if (Character.isWhitespace(c)) {
				if (isWord) {
					isWord = false;
					i++;
				}
			} else {
				isWord = true;
			}
		}

		if (isWord) {
			i++;
		}

		return i;
	}

	public static String[] splitWords(String src) {
		String[] result = new String[countWords(src)];

		int i = 0;
		StringBuilder sb = new StringBuilder();
		for (char c : src.toCharArray()) {
			if (Character.isWhitespace(c)) {
				if (sb.length() != 0) {
					result[i++] = resetStringBuilder(sb);
				}
			} else {
				sb.append(c);
			}
		}

		if (sb.length() != 0) {
			result[i] = resetStringBuilder(sb);
		}

		return result;
	}

	public static char[] sequence(char c, int length) {
		char[] result = new char[length];
		Arrays.fill(result, c);
		return result;
	}

	public static String stripPrefix(String string, String prefix) {
		if (prefix != null && string.startsWith(prefix)) {
			return string.substring(prefix.length());
		}

		return string;
	}

	public static String stripSuffix(String string, String suffix) {
		if (suffix != null && string.endsWith(suffix)) {
			return string.substring(suffix.length());
		}

		return string;
	}

	@SafeVarargs
	public static Collection<String> allCombinations(Iterable<String>... parts) {
		StringBuilder sb = new StringBuilder();
		Collection<String> result = new ArrayList<>();
		buildCombinations(sb, result, parts, 0);
		return result;
	}

	private static void buildCombinations(StringBuilder sb, Collection<String> result, Iterable<String>[] parts,
			int index) {
		if (index >= parts.length) {
			result.add(sb.toString());
		} else {
			int startLength = sb.length();
			for (String part : parts[index]) {
				sb.append(part);
				buildCombinations(sb, result, parts, index + 1);
				sb.setLength(startLength);
			}
		}
	}

	@SafeVarargs
	public static String[] allCombinations(String[]... parts) {
		StringBuilder sb = new StringBuilder();

		int length = 1;
		for (String[] array : parts)
			length *= array.length;
		String[] result = new String[length];

		buildCombinations(sb, result, new int[] { 0 }, parts, 0);
		return result;
	}

	private static void buildCombinations(StringBuilder sb, String[] result, int[] resultIndex, String[][] parts,
			int index) {
		if (index >= parts.length) {
			result[resultIndex[0]++] = sb.toString();
		} else {
			int startLength = sb.length();
			for (String part : parts[index]) {
				sb.append(part);
				buildCombinations(sb, result, resultIndex, parts, index + 1);
				sb.setLength(startLength);
			}
		}
	}

	public static String toUnsignedHexString(byte b) {
		int unsigned = b;
		if (b < 0) {
			unsigned += 0x100;
		}

		char[] chars = new char[2];

		chars[0] = Character.forDigit(unsigned >>> 4, 0x10);
		chars[1] = Character.forDigit(unsigned & 0x0F, 0x10);

		return new String(chars);
	}

	public static String toUnsignedHexString(byte[] bytes, String separator, int size) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < bytes.length; ++i) {
			sb.append(toUnsignedHexString(bytes[i]));
			if (i < bytes.length - 1 && ((i + 1) % size == 0)) {
				sb.append(separator);
			}
		}

		return sb.toString();
	}

	public static String toUnsignedHexString(byte[] bytes) {
		return toUnsignedHexString(bytes, ", ", 1);
	}

	public static char[] toFullHex(byte x) {
		return toFullHex(x, Byte.BYTES);
	}

	public static char[] toFullHex(short x) {
		return toFullHex(x, Short.BYTES);
	}

	public static char[] toFullHex(int x) {
		return toFullHex(x, Integer.BYTES);
	}

	public static char[] toFullHex(long x) {
		return toFullHex(x, Long.BYTES);
	}

	private static char[] toFullHex(long x, int bytes) {
		final int digits = bytes * 2;

		char[] result = new char[digits + 2];
		result[0] = '0';
		result[1] = 'x';

		for (int digit = 0; digit < digits; ++digit) {
			result[(digits - digit - 1) + 2] = hexDigit(x, digit);
		}

		return result;
	}

	private static char hexDigit(long value, int digit) {
		return hexDigit((int) (value >>> (4 * digit)) & 0xF);
	}

	public static char hexDigit(int value) {
		if (value < 0xA)
			return (char) ('0' + value);
		else
			return (char) ('A' - 0xA + value);
	}

	public static String replaceAll(String source, String substring, String replacement) {
		Objects.requireNonNull(source, "source");
		Objects.requireNonNull(substring, "substring");

		if (substring.isEmpty()) {
			throw new IllegalArgumentException("substring is empty");
		}

		if (!source.contains(substring)) { // also passes if source is empty
			return source;
		}

		if (substring.equals(replacement)) { // null-safe
			return source;
		}

		StringBuilder sb = new StringBuilder(2 * source.length());

		for (int i = 0; i < source.length() - substring.length() + 1; ++i) {
			if (source.startsWith(substring, i)) {
				if (replacement != null) {
					sb.append(replacement);
				}
			} else {
				sb.append(source.charAt(i));
			}
		}

		return sb.toString();
	}

}
