package ru.windcorp.progressia.common.state;

public interface StateFieldBuilder {
	
	public static interface Int {
		IntStateField build();
	}
	
	Int ofInt();
	
	StateFieldBuilder setLocal(boolean isLocal);
	
	default StateFieldBuilder setLocal() {
		return setLocal(true);
	}
	
	default StateFieldBuilder setShared() {
		return setLocal(false);
	}
	
	void setOrdinal(int ordinal);
	

}
