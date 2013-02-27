package com.gmail.zariust.otherbounds.common;

public enum Verbosity {
	LOW(1), NORMAL(2), HIGH(3), HIGHEST(4), EXTREME(5);
	private int level;
	
	private Verbosity(int lvl) {
		level = lvl;
	}
	
	public boolean exceeds(Verbosity other) {
		if(level >= other.level) return true;
		return false;
	}
}
