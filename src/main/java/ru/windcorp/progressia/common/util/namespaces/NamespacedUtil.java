package ru.windcorp.progressia.common.util.namespaces;

import java.util.Objects;

import ru.windcorp.jputil.chars.StringUtil;

public class NamespacedUtil {
	
	public static final char SEPARATOR = ':';
	
	public static final int MAX_ID_LENGTH = 255;
	private static final int MAX_PART_LENGTH = (MAX_ID_LENGTH - 1) / 2;
	
	public static final int MAX_NAMESPACE_LENGTH = MAX_PART_LENGTH;
	public static final int MAX_NAME_LENGTH = MAX_PART_LENGTH;
	
	private static final int MIN_PART_LENGTH = 3;
	
	public static final int MIN_NAMESPACE_LENGTH = MIN_PART_LENGTH;
	public static final int MIN_NAME_LENGTH = MIN_PART_LENGTH;
	
	/*
	 * This is the definition of the accepted pattern, but the value of
	 * these constants is not actually consulted in the check* methods.
	 */
	private static final String PART_CORE_REGEX = "[A-Z][a-zA-Z0-9]{2," + (MAX_PART_LENGTH - 1) + "}";
	private static final String PART_REGEX = "^" + PART_CORE_REGEX + "$";
	
	public static final String NAMESPACE_REGEX = PART_REGEX;
	public static final String NAME_REGEX = PART_REGEX;
	public static final String ID_REGEX = "^" + PART_CORE_REGEX + ":" + PART_CORE_REGEX + "$";
	
	public static String getName(String id) {
		checkId(id);
		return id.substring(id.indexOf(':') + 1);
	}
	
	public static String getNamespace(String id) {
		checkId(id);
		return id.substring(0, id.indexOf(':'));
	}
	
	public static String getId(String namespace, String name) {
		checkPart(namespace, 0, namespace.length(), "Namespace");
		checkPart(name, 0, name.length(), "Name");
		
		return namespace + SEPARATOR + name;
	}
	
	public static void checkId(String id) {
		Objects.requireNonNull(id, "id");
		
		int firstSeparator = id.indexOf(SEPARATOR);
		
		boolean areSeparatorsInvalid = (firstSeparator < 0) || (id.indexOf(SEPARATOR, firstSeparator + 1) >= 0);
		
		if (areSeparatorsInvalid) {
			int separators = StringUtil.count(id, SEPARATOR);
			throw new IllegalIdException(
					"ID \"" + id + "\" is invalid. "
							+ (separators == 0 ? "No " : "Too many (" + separators + ") ")
							+ "separators '" + SEPARATOR + "' found, exactly one required"
			);
		}
		
		checkPart(id, 0, firstSeparator, "namespace");
		checkPart(id, firstSeparator + 1, id.length() - firstSeparator - 1, "name");
	}
	
	private static void checkPart(String data, int offset, int length, String nameForErrors) {
		Objects.requireNonNull(data, nameForErrors);
		
		if (length > MAX_PART_LENGTH) {
			throw new IllegalIdException(
					nameForErrors + " \"" + data.substring(offset, offset + length) + "\" is too long. "
							+ "Expected at most " + MAX_PART_LENGTH
							+ " characters"
			);
		} else if (length < MIN_PART_LENGTH) {
			throw new IllegalIdException(
					nameForErrors + " \"" + data.substring(offset, offset + length) + "\" is too short. "
							+ "Expected at lest " + MIN_PART_LENGTH
							+ " characters"
			);
		}
		
		// Don't actually use *_REGEX for speed
		
		for (int i = 0; i < length; ++i) {
			char c = data.charAt(i + offset);
			if (!(
					(          c >= 'A' && c <= 'Z') ||
					(i != 0 && c >= 'a' && c <= 'z') ||
					(i != 0 && c >= '0' && c <= '9')
			)) {
				throw new IllegalIdException(
						nameForErrors + " \"" + data.substring(offset, offset + length) + "\" is invalid. "
								+ "Allowed is: " + PART_REGEX
				);
			}
		}
	}
	
	private NamespacedUtil() {}

}
