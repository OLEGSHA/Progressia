package ru.windcorp.progressia.client.graphics.backend;

import java.util.ArrayDeque;
import java.util.Deque;

public class FaceCulling {
	
	private static final Deque<Boolean> STACK = new ArrayDeque<>();
	
	public static void push(boolean useFaceCulling) {
		GraphicsBackend.setFaceCulling(useFaceCulling);
		STACK.push(Boolean.valueOf(useFaceCulling));
	}
	
	public static void pop() {
		STACK.pop();
		
		if (STACK.isEmpty()) {
			GraphicsBackend.setFaceCulling(false);
		} else {
			GraphicsBackend.setFaceCulling(STACK.getFirst());
		}
	}
	
	private FaceCulling() {}

}
