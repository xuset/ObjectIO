package objectIO.util;

public class ParamJoin {
	public static final ParamJoin INSTANCE = new ParamJoin();
	
	
	private final StringBuilder joiner = new StringBuilder();
	public String delimiter = ":";
	
	public synchronized ParamJoin add(String...arg) {
		for (int i = 0; i < arg.length; i++) {
			if (joiner.length() != 0)
				joiner.append(delimiter);
			joiner.append(arg[i]);
		}
		return this;
	}
	
	public synchronized String joinAndClear(String...arg) {
		clear();
		for (int i = 0; i < arg.length; i++) {
			if (joiner.length() != 0)
				joiner.append(delimiter);
			joiner.append(arg[i]);
		}
		String s = joiner.toString();
		clear();
		return s;
	}
	
	public synchronized String toStringAndClear() {
		String s = joiner.toString();
		joiner.delete(0, joiner.length());
		return s;
	}
	
	public synchronized void clear() {
		joiner.delete(0, joiner.length());
	}
	
	
}
