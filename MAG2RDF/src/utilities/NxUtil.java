package utilities;

import java.net.URI;

/**
 * Some utility methods that used to be spread over a couple of other classes.
 * 
 * @author Tobias Kaefer
 * @author others that wrote the methods in the first place
 */
public class NxUtil {

	private NxUtil() {

	}

	/**
	 * Escapes strings to unicode. Note that this method does not all the work
	 * required by the spec for processing URIs. {@link URI#toASCIIString()}
	 * could be your friend here.
	 * 
	 * @see <a href="http://www.w3.org/TR/rdf-testcases/#ntrip_strings">What the
	 *      spec says about the encoding of strings</a>
	 * @see <a href="http://www.w3.org/TR/rdf-testcases/#sec-uri-encoding">What
	 *      the spec says on the encoding of URIs</a>
	 */
	public static String escapeForNx(String lit) {
		StringBuffer result = new StringBuffer();

		for (int i = 0; i < lit.length(); i++) {

			int cp = lit.codePointAt(i);
			char c;

			if (!Character.isSupplementaryCodePoint(cp)) {
				c = (char) (cp);
				switch (c) {
				case '\\':
					result.append("\\\\");
					break;
				case '"':
					result.append("\\\"");
					break;
				case '\n':
					result.append("\\n");
					break;
				case '\r':
					result.append("\\r");
					break;
				case '\t':
					result.append("\\t");
					break;
				default:
					if (c >= 0x0 && c <= 0x8 || c == 0xB || c == 0xC
							|| c >= 0xE && c <= 0x1F || c >= 0x7F
							&& c <= 0xFFFF) {
						result.append("\\u");
						result.append(toHexString(c, 4));
					} else {
						result.append(c);
					}
				}
			} else {
				result.append("\\U");
				result.append(toHexString(cp, 8));
				++i;
			}

		}

		return result.toString();
	}

	/**
	 * Escapes strings for markup.
	 * 
	 */
	public static String escapeForMarkup(String lit) {
		String unescaped = unescape(lit);

		StringBuffer result = new StringBuffer();

		for (int i = 0; i < unescaped.length(); i++) {
			int cp = unescaped.codePointAt(i);
			char c;

			if (!Character.isSupplementaryCodePoint(cp)) {
				c = (char) (cp);

				switch (c) {
				case '&':
					result.append("&amp;");
					break;
				case '<':
					result.append("&lt;");
					break;
				case '>':
					result.append("&gt;");
					break;
				case '\"':
					result.append("&quot;");
					break;
				case '\'':
					result.append("&#039;");
					break;
				case '\\':
					result.append("&#092;");
					break;
				default:
					if (c >= 0x0 && c <= 0x8 || c == 0xB || c == 0xC
							|| c >= 0xE && c <= 0x1F || c >= 0x7F
							&& c <= 0xFFFF) {
						result.append("&#x");
						result.append(toHexString(c, 4));
						result.append(";");
					} else {
						result.append(c);
					}
				}
			} else {
				result.append("&#x");
				result.append(toHexString(cp, 8));
				result.append(";");
				++i;
			}
		}
		return result.toString();
	}

	/**
	 * Converts a decimal value to a hexadecimal string represention of the
	 * specified length. For unicode escaping.
	 * 
	 * @param decimal
	 *            A decimal value.
	 * @param stringLength
	 *            The length of the resulting string.
	 **/
	private static String toHexString(int decimal, int stringLength) {
		return String.format("%0" + stringLength + "X", decimal);

		// StringBuffer result = new StringBuffer(stringLength);
		//
		// String hexVal = Integer.toHexString(decimal).toUpperCase();
		//
		// // insert zeros if hexVal has less than stringLength characters:
		// int nofZeros = stringLength - hexVal.length();
		// for (int i = 0; i < nofZeros; i++) {
		// result.append('0');
		// }
		//
		// result.append(hexVal);
		//
		// return result.toString();
	}

	/**
	 * Unescape special characters in literal by removing excess backslashes.
	 * 
	 * @param str
	 *            The string to escape
	 */
	public static String unescape(String str) {
		return unescape(str, false);
	}

	/**
	 * Unescape special characters in literal by removing excess backslashes.
	 * 
	 * @param str
	 *            The string to escape
	 * @param clean
	 *            If true, cleans up excess slashes
	 */
	public static String unescape(String str, boolean clean) {
		if (clean)
			str = cleanSlashes(str);
		int sz = str.length();

		StringBuffer buffer = new StringBuffer(sz);
		StringBuffer unicode = new StringBuffer(6);

		boolean hadSlash = false;
		boolean inUnicode = false;
		boolean inSpecialUnicode = false;

		for (int i = 0; i < sz; i++) {
			char ch = str.charAt(i);
			if (inUnicode) {
				// if in unicode, then we're reading unicode
				// values in somehow

				if (unicode.length() < 4) {
					unicode.append(ch);

					if (unicode.length() == 4) {
						// unicode now contains the four hex digits
						// which represents our unicode chacater
						try {
							int value = Integer
									.parseInt(unicode.toString(), 16);
							buffer.append((char) value);
							unicode = new StringBuffer(4);
							inUnicode = false;
							inSpecialUnicode = false;
							hadSlash = false;
						} catch (NumberFormatException nfe) {
							buffer.append(unicode.toString());
							continue;
						}
						continue;
					}
					continue;
				}

			}

			if (inSpecialUnicode) {
				// if in unicode, then we're reading unicode
				// values in somehow

				if (unicode.length() < 8) {
					unicode.append(ch);

					if (unicode.length() == 8) {
						// unicode now contains the six hex digits
						// which represents our code point
						try {
							buffer.appendCodePoint(Integer.parseInt(
									unicode.toString(), 16));

							unicode = new StringBuffer(8);
							inUnicode = false;
							inSpecialUnicode = false;
							hadSlash = false;
						} catch (NumberFormatException nfe) {
							buffer.append(unicode.toString());
							continue;
						}
						continue;
					}
					continue;
				}

			}

			if (hadSlash) {
				// handle an escaped value
				hadSlash = false;
				switch (ch) {
				case '\\':
					buffer.append('\\');
					break;
				case '\'':
					buffer.append('\'');
					break;
				case '\"':
					buffer.append('"');
					break;
				case 'r':
					buffer.append('\r');
					break;
				case 'f':
					buffer.append('\f');
					break;
				case 't':
					buffer.append('\t');
					break;
				case 'n':
					buffer.append('\n');
					break;
				case 'b':
					buffer.append('\b');
					break;
				case 'u': {
					// uh-oh, we're in unicode country....
					inUnicode = true;
					break;

				}
				case 'U': {
					// even more uh-oh, we're in special unicode land...
					inSpecialUnicode = true;
					break;
				}

				default:
					buffer.append(ch);
					break;
				}
				continue;
			}

			else if (ch == '\\') {
				hadSlash = true;
				continue;
			}

			buffer.append(ch);
		}

		if (hadSlash) {
			// then we're in the weird case of a \ at the end of the
			// string, let's output it anyway.
			buffer.append('\\');
		}
		return buffer.toString();
	}

	/**
	 * Remove multiples of \\ for cleaning data escaped multiple times.
	 */
	private static String cleanSlashes(String str) {
		while (str.indexOf("\\\\") != -1)
			str = str.replaceAll("\\\\\\\\", "\\\\");
		return str;
	}
}
