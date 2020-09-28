package ru.windcorp.progressia.common.util;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class NamespacedUtil {
	
	public static final char SEPARATOR = ':';
	public static final int MAX_PART_LENGTH = 127;
	public static final int MAX_TOTAL_LENGTH = MAX_PART_LENGTH * 2 + 1;
	
	private static final String PART_REGEX = "^[A-Z][a-zA-Z0-9]{2,}$";
	
	private static final Predicate<String> PART_CHECKER =
			Pattern.compile(PART_REGEX).asPredicate();
	
	public static String getId(String namespace, String name) {
		checkPart(namespace, "Namespace");
		checkPart(name, "Name");
		
		return namespace + SEPARATOR + name;
	}
	
	private static void checkPart(String data, String name) {
		Objects.requireNonNull(data, name);
		
		if (data.length() > MAX_PART_LENGTH) {
			throw new IllegalArgumentException(
					name + " \"" + data + "\" is too long. "
							+ "Expected at most " + MAX_PART_LENGTH
							+ " characters"
			);
		}
		
		if (!PART_CHECKER.test(name)) {
			throw new IllegalArgumentException(
					name + " \"" + data + "\" is invalid. "
							+ "Allowed is: " + PART_REGEX
			);
		}
	}
	
	private NamespacedUtil() {}

}
