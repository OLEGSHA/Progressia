package ru.windcorp.progressia.common.state;

public abstract class StateStorage {

	public abstract int getInt(int index);

	public abstract void setInt(int index, int value);

}