package objectIO.util;

public class StringUtil {
	public static StringBuilder clearBuilder(StringBuilder b) {
		b.delete(0, b.length());
		return b;
	}
}
