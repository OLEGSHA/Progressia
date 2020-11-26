package ru.windcorp.progressia.server.world.tasks;

@FunctionalInterface
public interface StateChange<T> {
	void change(T object);
}