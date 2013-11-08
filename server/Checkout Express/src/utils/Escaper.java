package utils;

public class Escaper {

	public static String url(String s)
	{
		StringBuffer sb = new StringBuffer(s.length()*3);
		for(char c : s.toCharArray()) {
			if(		(c >= ' ') && (c < '0') && (c != '-') ||
					(c > '9') && (c < 'A') ||
					(c > 'Z') && (c < 'a') || (c > 'z')) {
				sb.append('%');
				sb.append(Integer.toHexString(c));
			} else
				sb.append(c);
		}
		return sb.toString();
	}

	public static String xml(String s)
	{
		return s.replace("&", "&amp;").replace("\"", "&quot;").replace("'", "&apos;").replace("<", "&lt;").replace(">", "&gt;");
	}

	public static String general(CharSequence seq, boolean [] table)
	{
		int oldLen = seq.length();//Might not be constant time operation
		int len = oldLen;
		for(int i = 0; i < oldLen; i++) {
			char c = seq.charAt(i);
			if((c < table.length) && (table[c]))
				len++;
		}
		char [] s = new char[len];
		int i = 0;
		int j = 0;
		while(i < oldLen) {
			char c = seq.charAt(i++);
			if((c < table.length) && (table[c])) {
				s[j++] = '\\';
				switch(c) {
					case '\b': s[j++] = 'b'; break;
					case '\f': s[j++] = 'f'; break;
					case '\r': s[j++] = 'r'; break;
					case '\n': s[j++] = 'n'; break;
					case '\t': s[j++] = 't'; break;
					case '\13': s[j++] = 'v'; break;
					default: s[j++] = c;
				}
			} else
				s[j++] = c;
		}
		return new String(s);
	}

	private static boolean [] dqTable = null;
	public static String dq(String s)
	{
		if(dqTable == null) {
			dqTable = new boolean['\\'+1];// '\\' is the highest on the ascii table
			dqTable['\b'] = true;
			dqTable['\f'] = true;
			dqTable['\r'] = true;
			dqTable['\n'] = true;
			dqTable['\t'] = true;
			dqTable['\13'] = true;
			dqTable['\"'] = true;
			dqTable['\\'] = true;
		}
		return general(s, dqTable);
	}

	private static boolean [] sqTable = null;
	public static String sq(String s)
	{
		if(sqTable == null) {
			sqTable = new boolean['\\'+1];// '\\' is the highest on the ascii table
			sqTable['\b'] = true;
			sqTable['\f'] = true;
			sqTable['\r'] = true;
			sqTable['\n'] = true;
			sqTable['\t'] = true;
			sqTable['\13'] = true;
			sqTable['\''] = true;
			sqTable['\\'] = true;
		}
		return general(s, sqTable);
	}
}