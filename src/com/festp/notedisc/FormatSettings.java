package com.festp.notedisc;

import com.festp.notebook.NoteBookParser;
import com.festp.utils.NoteUtils;

public abstract class FormatSettings {
	/*public enum Mode {
		NBS, REAL
	}
	final Mode mode;*/
	public final int tickrate;
	public final int multiplier;
	
	public FormatSettings(int tickrate) {
		if (tickrate <= 0) {
			this.tickrate = NoteBookParser.DEFAULT_TICKRATE;
		} else {
			this.tickrate = tickrate;
		}
		int multiplier = (int) Math.round(NoteBookParser.MAX_TICKRATE / (double) this.tickrate);
		if (multiplier == 0) {
			multiplier = NoteBookParser.MAX_TICKRATE / NoteBookParser.DEFAULT_TICKRATE;
		}
		this.multiplier = multiplier;
	}
	
	public abstract int getPitchShift(int instId);
	
	public int getGapLength(int pauseLength) {
		if (tickrate != NoteBookParser.MAX_TICKRATE) {
			return multiplier * (pauseLength + 1) - 1; // 10: 0,1,2,4,7(=>0,0,1,2) -> 0,2,4,8,14(=>1,1,3,5)
			// 5: 0,1,2,4,7(=>0,0,1,2) -> 0,4,8,16,28(=>3,3,7,11)
		}
		return pauseLength;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "{" + tickrate + " tps -> " + multiplier + "}";
	}
	
	public static FormatSettings getSettings(String name, int tickrate) {
		if (name.contains("real")) {
			return new RealSettings(tickrate);
		}
		return new NBSSettings(tickrate);
	}
	
	
	
	public static class NBSSettings extends FormatSettings {
		public NBSSettings(int tickrate) {
			super(tickrate);
		}

		@Override
		public int getPitchShift(int instId) {
			return - NoteUtils.INSTRUMENTS[instId].octaveShift * NoteUtils.OCTAVE;
		}
	}
	
	public static class RealSettings extends FormatSettings {
		public RealSettings(int tickrate) {
			super(tickrate);
		}

		@Override
		public int getPitchShift(int instId) {
			return 0;
		}
	}
}
