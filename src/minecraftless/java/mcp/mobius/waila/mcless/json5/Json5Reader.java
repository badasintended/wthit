// This file was a part of Quilt Parsers, modified to remove unused members,
// and to directly extends GSON's JsonReader.
// https://github.com/QuiltMC/quilt-parsers/blob/00803c4e70fb0cf93765593eaae5c781b1505bee/json/src/main/java/org/quiltmc/parsers/json/JsonReader.java
// @formatter:off

/*
 * Copyright 2010 Google Inc.
 * Copyright 2021-2023 QuiltMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mcp.mobius.waila.mcless.json5;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;

/*
 * The following changes have been applied from the original in GSON:
 * - The lenient mode has been removed.
 * - Now supports parsing JSONC and JSON5
 *
 * You may view the original, including its license header, here:
 * https://github.com/google/gson/blob/530cb7447089ccc12dc2009c17f468ddf2cd61ca/gson/src/main/java/com/google/gson/stream/JsonReader.java
 */

/**
 * Reads a {@linkplain JsonFormat JSON-family}
 * encoded value as a stream of tokens. This stream includes both literal
 * values (strings, numbers, booleans, and nulls) as well as the begin and
 * end delimiters of objects and arrays. The tokens are traversed in
 * depth-first order, the same order that they appear in the JSON document.
 * Within JSON objects, name/value pairs are represented by a single token.
 *
 * <h3>Parsing JSON</h3>
 * To create a recursive descent parser for your own JSON streams, first create
 * an entry point method that creates a {@code JsonReader}.
 *
 * <p>Next, create handler methods for each structure in your JSON text. You'll
 * need a method for each object type and for each array type.
 * <ul>
 *   <li>Within <strong>array handling</strong> methods, first call {@link
 *       #beginArray} to consume the array's opening bracket. Then create a
 *       while loop that accumulates values, terminating when {@link #hasNext}
 *       is false. Finally, read the array's closing bracket by calling {@link
 *       #endArray}.
 *   <li>Within <strong>object handling</strong> methods, first call {@link
 *       #beginObject} to consume the object's opening brace. Then create a
 *       while loop that assigns values to local variables based on their name.
 *       This loop should terminate when {@link #hasNext} is false. Finally,
 *       read the object's closing brace by calling {@link #endObject}.
 * </ul>
 * <p>When a nested object or array is encountered, delegate to the
 * corresponding handler method.
 *
 * <p>When an unknown name is encountered, strict parsers should fail with an
 * exception. Lenient parsers should call {@link #skipValue()} to recursively
 * skip the value's nested tokens, which may otherwise conflict.
 *
 * <p>If a value may be null, you should first check using {@link #peek()}.
 * Null literals can be consumed using either {@link #nextNull()} or {@link
 * #skipValue()}.
 *
 * <h3>Example</h3>
 * Suppose we'd like to parse a stream of messages such as the following: <pre> {@code
 * [
 *   {
 *     id: 912345678901,
 *     text: "How do I read a JSON stream in Java?",
 *     geo: null,
 *     user: {
 *       "name": "json_newb",
 *       followers_count: 41,
 *      }
 *   },
 *   {
 *   /*
 *    * Look mom, block comments!
 *    *\/
 *     "id": 912345678902,
 *     "text": "@json_newb just use JsonReader!",
 *     "geo": [-Infinity, NaN], // wow, broken floating point types!
 *     "user": {
 *       "name": "jesse",
 *       "followers_count": 2
 *     }
 *   }
 * ]}</pre>
 * This code implements the parser for the above structure: <pre>   {@code
 *
 *   public List<Message> readJsonStream(InputStream in) throws IOException {
 *     JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
 *     try {
 *       return readMessagesArray(reader);
 *     } finally {
 *       reader.close();
 *     }
 *   }
 *
 *   public List<Message> readMessagesArray(JsonReader reader) throws IOException {
 *     List<Message> messages = new ArrayList<Message>();
 *
 *     reader.beginArray();
 *     while (reader.hasNext()) {
 *       messages.add(readMessage(reader));
 *     }
 *     reader.endArray();
 *     return messages;
 *   }
 *
 *   public Message readMessage(JsonReader reader) throws IOException {
 *     long id = -1;
 *     String text = null;
 *     User user = null;
 *     List<Double> geo = null;
 *
 *     reader.beginObject();
 *     while (reader.hasNext()) {
 *       String name = reader.nextName();
 *       if (name.equals("id")) {
 *         id = reader.nextLong();
 *       } else if (name.equals("text")) {
 *         text = reader.nextString();
 *       } else if (name.equals("geo") && reader.peek() != JsonToken.NULL) {
 *         geo = readDoublesArray(reader);
 *       } else if (name.equals("user")) {
 *         user = readUser(reader);
 *       } else {
 *         reader.skipValue();
 *       }
 *     }
 *     reader.endObject();
 *     return new Message(id, text, user, geo);
 *   }
 *
 *   public List<Double> readDoublesArray(JsonReader reader) throws IOException {
 *     List<Double> doubles = new ArrayList<Double>();
 *
 *     reader.beginArray();
 *     while (reader.hasNext()) {
 *       doubles.add(reader.nextDouble());
 *     }
 *     reader.endArray();
 *     return doubles;
 *   }
 *
 *   public User readUser(JsonReader reader) throws IOException {
 *     String username = null;
 *     int followersCount = -1;
 *
 *     reader.beginObject();
 *     while (reader.hasNext()) {
 *       String name = reader.nextName();
 *       if (name.equals("name")) {
 *         username = reader.nextString();
 *       } else if (name.equals("followers_count")) {
 *         followersCount = reader.nextInt();
 *       } else {
 *         reader.skipValue();
 *       }
 *     }
 *     reader.endObject();
 *     return new User(username, followersCount);
 *   }}</pre>
 *
 * <h3>Number Handling</h3>
 * This reader permits numeric values to be read as strings and string values to
 * be read as numbers. For example, both elements of the JSON array {@code
 * [1, "1"]} may be read using either {@link #nextInt} or {@link #nextString}.
 * This behavior is intended to prevent lossy numeric conversions: double is
 * JavaScript's only numeric type and very large values like {@code
 * 9007199254740993} cannot be represented exactly on that platform. To minimize
 * precision loss, extremely large values should be written and read as strings
 * in JSON.
 *
 * <a id="nonexecuteprefix"/><h3>Non-Execute Prefix</h3>
 * Web servers that serve private data using JSON may be vulnerable to <a
 * href="http://en.wikipedia.org/wiki/JSON#Cross-site_request_forgery">Cross-site
 * request forgery</a> attacks. In such an attack, a malicious site gains access
 * to a private JSON file by executing it with an HTML {@code <script>} tag.
 *
 * <p>Prefixing JSON files with <code>")]}'\n"</code> makes them non-executable
 * by {@code <script>} tags, disarming the attack. Since the prefix is malformed
 * JSON, strict parsing fails when it is encountered. This class permits the
 * non-execute prefix unless {@link #allowNonExecutePrefix()} is
 * called before parsing occurs.
 *
 * <p>Each {@code JsonReader} may be used to read a single JSON stream. Instances
 * of this class are not thread safe.
 */
@SuppressWarnings({"JavadocBlankLines", "RedundantExplicitVariableType", "CommentedOutCode", "JavadocReference", "EnhancedSwitchMigration", "DataFlowIssue"})
public final class Json5Reader extends JsonReader {
	private static final int PEEKED_NONE = 0;
	private static final int PEEKED_BEGIN_OBJECT = 1;
	private static final int PEEKED_END_OBJECT = 2;
	private static final int PEEKED_BEGIN_ARRAY = 3;
	private static final int PEEKED_END_ARRAY = 4;
	private static final int PEEKED_TRUE = 5;
	private static final int PEEKED_FALSE = 6;
	private static final int PEEKED_NULL = 7;
	private static final int PEEKED_SINGLE_QUOTED = 8;
	private static final int PEEKED_DOUBLE_QUOTED = 9;
	private static final int PEEKED_UNQUOTED = 10;
	/** When this is returned, the string value is stored in peekedString. */
	private static final int PEEKED_BUFFERED = 11;
	private static final int PEEKED_SINGLE_QUOTED_NAME = 12;
	private static final int PEEKED_DOUBLE_QUOTED_NAME = 13;
	private static final int PEEKED_UNQUOTED_NAME = 14;
	private static final int PEEKED_NUMBER = 15;
	private static final int PEEKED_HEXADECIMAL = 16;
	private static final int PEEKED_NaN = 17;
	private static final int PEEKED_INF = 18;
	private static final int PEEKED_NEGATIVE_INF = 19;
	private static final int PEEKED_EOF = 20;

	/* State machine when parsing numbers */
	private static final int NUMBER_CHAR_NONE = 0;
	private static final int NUMBER_CHAR_SIGN = 1;
	private static final int NUMBER_CHAR_DIGIT = 2;
	private static final int NUMBER_CHAR_DECIMAL = 3;
	private static final int NUMBER_CHAR_FRACTION_DIGIT = 4;
	private static final int NUMBER_CHAR_EXP_E = 5;
	private static final int NUMBER_CHAR_EXP_SIGN = 6;
	private static final int NUMBER_CHAR_EXP_DIGIT = 7;
	private static final int NUMBER_CHAR_ZERO = 8;
	private static final int NUMBER_CHAR_HEXADECIMAL = 9;
	/** The input JSON. */
	private final Reader in;

	/**
	 * Use a manual buffer to easily read and unread upcoming characters, and
	 * also so we can create strings without an intermediate StringBuilder.
	 * We decode literals directly out of this buffer, so it must be at least as
	 * long as the longest token that can be reported as a number.
	 */
	private final char[] buffer = new char[1024];
	private int pos = 0;
	private int limit = 0;

	private int lineNumber = 0;
	private int lineStart = 0;

	int peeked = PEEKED_NONE;

	/**
	 * The number of characters in a peeked number literal. Increment 'pos' by
	 * this after reading a number.
	 */
	private int peekedNumberLength;

	/**
	 * A peeked string that should be parsed on the next double, long or string.
	 * This is populated before a numeric value is parsed and used if that parsing
	 * fails.
	 */
	private String peekedString;
	/*
	 * The nesting stack. Using a manual array rather than an ArrayList saves 20%.
	 */
	private int[] stack = new int[32];
	private int stackSize = 0;
	{
		stack[stackSize++] = Json5Scope.EMPTY_DOCUMENT;
	}

	/*
	 * The path members. It corresponds directly to stack: At indices where the
	 * stack contains an object (EMPTY_OBJECT, DANGLING_NAME or NONEMPTY_OBJECT),
	 * pathNames contains the name at this scope. Where it contains an array
	 * (EMPTY_ARRAY, NONEMPTY_ARRAY) pathIndices contains the current index in
	 * that array. Otherwise the value is undefined, and we take advantage of that
	 * by incrementing pathIndices when doing so isn't useful.
	 */
	private String[] pathNames = new String[32];
	private int[] pathIndices = new int[32];

	// API methods

	public Json5Reader(Reader in) {
		super(Reader.nullReader());
		Objects.requireNonNull(in, "Reader cannot be null");
		this.in = in;
	}

	public boolean hasNext() throws IOException {
		int p = peeked;
		if (p == PEEKED_NONE) {
			p = doPeek();
		}
		return p != PEEKED_END_OBJECT && p != PEEKED_END_ARRAY;
	}

	/**
	 * Returns the type of the next token without consuming it.
	 */
	public JsonToken peek() throws IOException {
		int p = peeked;
		if (p == PEEKED_NONE) {
			p = doPeek();
		}

		switch (p) {
			case PEEKED_BEGIN_OBJECT:
				return JsonToken.BEGIN_OBJECT;
			case PEEKED_END_OBJECT:
				return JsonToken.END_OBJECT;
			case PEEKED_BEGIN_ARRAY:
				return JsonToken.BEGIN_ARRAY;
			case PEEKED_END_ARRAY:
				return JsonToken.END_ARRAY;
			case PEEKED_SINGLE_QUOTED_NAME:
			case PEEKED_DOUBLE_QUOTED_NAME:
			case PEEKED_UNQUOTED_NAME:
				return JsonToken.NAME;
			case PEEKED_TRUE:
			case PEEKED_FALSE:
				return JsonToken.BOOLEAN;
			case PEEKED_NULL:
				return JsonToken.NULL;
			case PEEKED_SINGLE_QUOTED:
			case PEEKED_DOUBLE_QUOTED:
			case PEEKED_UNQUOTED:
			case PEEKED_BUFFERED:
				return JsonToken.STRING;
			case PEEKED_NUMBER:
			case PEEKED_HEXADECIMAL:
			case PEEKED_NaN:
			case PEEKED_INF:
			case PEEKED_NEGATIVE_INF:
				return JsonToken.NUMBER;
			case PEEKED_EOF:
				return JsonToken.END_DOCUMENT;
			default:
				throw new AssertionError();
		}
	}

	/**
	 * Returns the next token, a {@link JsonToken#NAME property name}, and
	 * consumes it.
	 *
	 * @throws IOException if the next token in the stream is not a property
	 *     name.
	 */
	public String nextName() throws IOException {
		int p = peeked;
		if (p == PEEKED_NONE) {
			p = doPeek();
		}
		String result;
		if (p == PEEKED_UNQUOTED_NAME) {
			result = nextUnquotedValue();
		} else if (p == PEEKED_SINGLE_QUOTED_NAME) {
			result = nextQuotedValue('\'');
		} else if (p == PEEKED_DOUBLE_QUOTED_NAME) {
			result = nextQuotedValue('"');
		} else {
			throw new IllegalStateException("Expected a name but was " + peek() + locationString());
		}
		peeked = PEEKED_NONE;
		pathNames[stackSize - 1] = result;
		return result;
	}

	/**
	 * Consumes the next token from the JSON stream and asserts that it is the
	 * beginning of a new object.
	 */
	public void beginObject() throws IOException {
		int p = peeked;
		if (p == PEEKED_NONE) {
			p = doPeek();
		}
		if (p == PEEKED_BEGIN_OBJECT) {
			push(Json5Scope.EMPTY_OBJECT);
			peeked = PEEKED_NONE;
		} else {
			throw new IllegalStateException("Expected BEGIN_OBJECT but was " + peek() + locationString());
		}
	}

	/**
	 * Consumes the next token from the JSON stream and asserts that it is the
	 * end of the current object.
	 */
	public void endObject() throws IOException {
		int p = peeked;
		if (p == PEEKED_NONE) {
			p = doPeek();
		}
		if (p == PEEKED_END_OBJECT) {
			stackSize--;
			pathNames[stackSize] = null; // Free the last path name so that it can be garbage collected!
			pathIndices[stackSize - 1]++;
			peeked = PEEKED_NONE;
		} else {
			throw new IllegalStateException("Expected END_OBJECT but was " + peek() + locationString());
		}
	}

	/**
	 * Consumes the next token from the JSON stream and asserts that it is the
	 * beginning of a new array.
	 */
	public void beginArray() throws IOException {
		int p = peeked;
		if (p == PEEKED_NONE) {
			p = doPeek();
		}
		if (p == PEEKED_BEGIN_ARRAY) {
			push(Json5Scope.EMPTY_ARRAY);
			pathIndices[stackSize - 1] = 0;
			peeked = PEEKED_NONE;
		} else {
			throw new IllegalStateException("Expected BEGIN_ARRAY but was " + peek() + locationString());
		}
	}

	/**
	 * Consumes the next token from the JSON stream and asserts that it is the
	 * end of the current array.
	 */
	public void endArray() throws IOException {
		int p = peeked;
		if (p == PEEKED_NONE) {
			p = doPeek();
		}
		if (p == PEEKED_END_ARRAY) {
			stackSize--;
			pathIndices[stackSize - 1]++;
			peeked = PEEKED_NONE;
		} else {
			throw new IllegalStateException("Expected END_ARRAY but was " + peek() + locationString());
		}
	}

	/**
	 * Returns the {@link JsonToken#STRING string} value of the next token,
	 * consuming it. If the next token is a number, this method will return its
	 * string form.
	 *
	 * @throws IllegalStateException if the next token is not a string or if
	 *     this reader is closed.
	 */
	public String nextString() throws IOException {
		int p = peeked;
		if (p == PEEKED_NONE) {
			p = doPeek();
		}
		String result;
		if (p == PEEKED_UNQUOTED) {
			result = nextUnquotedValue();
		} else if (p == PEEKED_SINGLE_QUOTED) {
			result = nextQuotedValue('\'');
		} else if (p == PEEKED_DOUBLE_QUOTED) {
			result = nextQuotedValue('"');
		} else if (p == PEEKED_BUFFERED) {
			result = peekedString;
			peekedString = null;
		}  else if (p == PEEKED_NUMBER || p == PEEKED_HEXADECIMAL || p == PEEKED_NaN || p == PEEKED_INF || p == PEEKED_NEGATIVE_INF) {
			result = new String(buffer, pos, peekedNumberLength);
			pos += peekedNumberLength;
		} else {
			throw new IllegalStateException("Expected a string but was " + peek() + locationString());
		}
		peeked = PEEKED_NONE;
		pathIndices[stackSize - 1]++;
		return result;
	}

	/**
	 * Returns the {@link JsonToken#BOOLEAN boolean} value of the next token,
	 * consuming it.
	 *
	 * @throws IllegalStateException if the next token is not a boolean or if
	 *     this reader is closed.
	 */
	public boolean nextBoolean() throws IOException {
		int p = peeked;
		if (p == PEEKED_NONE) {
			p = doPeek();
		}
		if (p == PEEKED_TRUE) {
			peeked = PEEKED_NONE;
			pathIndices[stackSize - 1]++;
			return true;
		} else if (p == PEEKED_FALSE) {
			peeked = PEEKED_NONE;
			pathIndices[stackSize - 1]++;
			return false;
		}
		throw new IllegalStateException("Expected a boolean but was " + peek() + locationString());
	}

	/**
	 * Returns the {@link JsonToken#NUMBER long} value of the next token,
	 * consuming it. If the next token is a string, this method will attempt to
	 * parse it as a long. If the next token's numeric value cannot be exactly
	 * represented by a Java {@code long}, this method throws.
	 *
	 * @throws IllegalStateException if the next token is not a literal value.
	 * @throws NumberFormatException if the next literal value cannot be parsed
	 *     as a number, or exactly represented as a long.
	 */
	public Number nextNumber() throws IOException {
		int p = peeked;
		if (p == PEEKED_NONE) {
			p = doPeek();
		}

		boolean negateHex = false;
		if (p == PEEKED_NUMBER) {
			if (buffer[pos] == '+') {
				peekedString = new String(buffer, pos + 1, peekedNumberLength - 1);
			} else {
				peekedString = new String(buffer, pos, peekedNumberLength);
			}
		} else if (p == PEEKED_HEXADECIMAL) {
			if (buffer[pos] == '+') {
				peekedString = new String(buffer, pos + 3, peekedNumberLength - 3);
			} else if (buffer[pos] == '-') {
				negateHex = true;
				peekedString = new String(buffer, pos + 3, peekedNumberLength - 3);
			} else {
				peekedString = new String(buffer, pos + 2, peekedNumberLength - 2);
			}
		} else if (p == PEEKED_SINGLE_QUOTED || p == PEEKED_DOUBLE_QUOTED || p == PEEKED_UNQUOTED) {
			checkLenient(); // using this to catch where this is used, TODO remove
			if (p == PEEKED_UNQUOTED) {
				peekedString = nextUnquotedValue();
			} else {
				peekedString = nextQuotedValue(p == PEEKED_SINGLE_QUOTED ? '\'' : '"');
			}
			try {
				Number result = new BigInteger(peekedString);
				peeked = PEEKED_NONE;
				pathIndices[stackSize - 1]++;
				pos += peekedNumberLength;
				return result;
			} catch (NumberFormatException ignored) {
				// Fall back to parse as a double below.
			}
		} else if (p != PEEKED_NaN && p != PEEKED_INF && p != PEEKED_NEGATIVE_INF) {
			throw new IllegalStateException("Expected a number but was " + peek() + locationString());
		}

		peeked = PEEKED_BUFFERED;
		Number result;
		if (p == PEEKED_HEXADECIMAL) {
			result = new BigInteger(peekedString.toLowerCase(), 16);
			if (negateHex) {
				result = ((BigInteger) result).negate();
			}
		} else if (p == PEEKED_NaN) {
			result = Double.NaN;
		} else if (p == PEEKED_INF) {
			result = Double.POSITIVE_INFINITY;
		} else if (p == PEEKED_NEGATIVE_INF) {
			result = Double.NEGATIVE_INFINITY;
		} else {
			int exp = 0;
			try {
				exp = Integer.parseInt(peekedString.substring('e'));
			} catch (IndexOutOfBoundsException ignored) {
				try {
					exp = Integer.parseInt(peekedString.substring('E'));
				} catch (IndexOutOfBoundsException ignored2) {
				}
			}
			result = new BigDecimal(peekedString).scaleByPowerOfTen(exp);
		}
		peekedString = null;
		peeked = PEEKED_NONE;
		pathIndices[stackSize - 1]++;
		pos += peekedNumberLength;
		peekedNumberLength = 0;
		return result;
	}

	/**
	 * Returns the {@link JsonToken#NUMBER double} value of the next token,
	 * consuming it. If the next token is a string, this method will attempt to
	 * parse it as a double using {@link Double#parseDouble(String)}.
	 *
	 * @throws IllegalStateException if the next token is not a literal value.
	 * @throws NumberFormatException if the next literal value cannot be parsed
	 *     as a double, or is non-finite.
	 */
	public double nextDouble() throws IOException {
		return nextNumber().doubleValue();
	}

	/**
	 * Returns the {@link JsonToken#NUMBER long} value of the next token,
	 * consuming it. If the next token is a string, this method will attempt to
	 * parse it as a long. If the next token's numeric value cannot be exactly
	 * represented by a Java {@code long}, this method throws.
	 *
	 * @throws IllegalStateException if the next token is not a literal value.
	 * @throws NumberFormatException if the next literal value cannot be parsed
	 *     as a number, or exactly represented as a long.
	 */
	public long nextLong() throws IOException {
		Number num = nextNumber();
		if (num instanceof BigInteger) {
			return ((BigInteger) num).longValueExact();
		} else {
			return ((BigDecimal) num).longValueExact();
		}
	}

	/**
	 * Returns the {@link JsonToken#NUMBER int} value of the next token,
	 * consuming it. If the next token is a string, this method will attempt to
	 * parse it as an int. If the next token's numeric value cannot be exactly
	 * represented by a Java {@code int}, this method throws.
	 *
	 * @throws IllegalStateException if the next token is not a literal value.
	 * @throws NumberFormatException if the next literal value cannot be parsed
	 *     as a number, or exactly represented as an int.
	 */
	public int nextInt() throws IOException {
		Number num = nextNumber();
		if (num instanceof BigInteger) {
			return ((BigInteger) num).intValueExact();
		} else {
			return ((BigDecimal) num).intValueExact();
		}
	}

	/**
	 * Consumes the next token from the JSON stream and asserts that it is a
	 * literal null.
	 *
	 * @throws IllegalStateException if the next token is not null or if this
	 *     reader is closed.
	 */
	public void nextNull() throws IOException {
		int p = peeked;
		if (p == PEEKED_NONE) {
			p = doPeek();
		}
		if (p == PEEKED_NULL) {
			peeked = PEEKED_NONE;
			pathIndices[stackSize - 1]++;
		} else {
			throw new IllegalStateException("Expected null but was " + peek() + locationString());
		}
	}

	/**
	 * Skips the next value recursively. If it is an object or array, all nested
	 * elements are skipped. This method is intended for use when the JSON token
	 * stream contains unrecognized or unhandled values.
	 */
	public void skipValue() throws IOException {
		int count = 0;
		do {
			int p = peeked;
			if (p == PEEKED_NONE) {
				p = doPeek();
			}

			if (p == PEEKED_BEGIN_ARRAY) {
				push(Json5Scope.EMPTY_ARRAY);
				count++;
			} else if (p == PEEKED_BEGIN_OBJECT) {
				push(Json5Scope.EMPTY_OBJECT);
				count++;
			} else if (p == PEEKED_END_ARRAY) {
				stackSize--;
				count--;
			} else if (p == PEEKED_END_OBJECT) {
				stackSize--;
				count--;
			} else if (p == PEEKED_UNQUOTED_NAME || p == PEEKED_UNQUOTED) {
				skipUnquotedValue();
			} else if (p == PEEKED_SINGLE_QUOTED || p == PEEKED_SINGLE_QUOTED_NAME) {
				skipQuotedValue('\'');
			} else if (p == PEEKED_DOUBLE_QUOTED || p == PEEKED_DOUBLE_QUOTED_NAME) {
				skipQuotedValue('"');
			} else if (p == PEEKED_NUMBER || p == PEEKED_HEXADECIMAL || p == PEEKED_NaN || p == PEEKED_INF || p == PEEKED_NEGATIVE_INF) {
				pos += peekedNumberLength;
			}
			peeked = PEEKED_NONE;
		} while (count != 0);

		pathIndices[stackSize - 1]++;
		pathNames[stackSize - 1] = "null";
	}

	/**
	 * Closes this JSON reader and the underlying {@link Reader}.
	 */
	public void close() throws IOException {
		peeked = PEEKED_NONE;
		stack[0] = Json5Scope.CLOSED;
		stackSize = 1;
		in.close();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + locationString();
	}

	// Implementation methods
	// Everything below here should be package-private or private
	int doPeek() throws IOException {
		int peekStack = stack[stackSize - 1];
		if (peekStack == Json5Scope.EMPTY_ARRAY) {
			stack[stackSize - 1] = Json5Scope.NONEMPTY_ARRAY;
		} else if (peekStack == Json5Scope.NONEMPTY_ARRAY) {
			// Look for a comma before the next element.
			int c = nextNonWhitespace(true);
			switch (c) {
				case ']':
					return peeked = PEEKED_END_ARRAY;
				case ',':
					break;
				default:
					throw syntaxError("Unterminated array");
			}
		} else if (peekStack == Json5Scope.EMPTY_OBJECT || peekStack == Json5Scope.NONEMPTY_OBJECT) {
			stack[stackSize - 1] = Json5Scope.DANGLING_NAME;
			// Look for a comma before the next element.
			if (peekStack == Json5Scope.NONEMPTY_OBJECT) {
				int c = nextNonWhitespace(true);
				switch (c) {
					case '}':
						return peeked = PEEKED_END_OBJECT;
					case ';':
						checkLenient(); // fall-through
					case ',':
						break;
					default:
						throw syntaxError("Unterminated object");
				}
			}
			int c = nextNonWhitespace(true);
			switch (c) {
				case '"':
					return peeked = PEEKED_DOUBLE_QUOTED_NAME;
				case '\'':
					return peeked = PEEKED_SINGLE_QUOTED_NAME;
				case '}':
					// Trailing comma
					return peeked = PEEKED_END_OBJECT;
				default:
					// Unquoted names are only allowed in JSON5
					pos--; // Don't consume the first character in an unquoted string.
					if (isLiteral((char) c)) {
						return peeked = PEEKED_UNQUOTED_NAME;
					} else {
						throw syntaxError("Expected name");
					}
			}
		} else if (peekStack == Json5Scope.DANGLING_NAME) {
			stack[stackSize - 1] = Json5Scope.NONEMPTY_OBJECT;
			// Look for a colon before the value.
			int c = nextNonWhitespace(true);
			switch (c) {
				case ':':
					break;
				case '=':
					checkLenient();
					if ((pos < limit || fillBuffer(1)) && buffer[pos] == '>') {
						pos++;
					}
					break;
				default:
					throw syntaxError("Expected ':'");
			}
		} else if (peekStack == Json5Scope.EMPTY_DOCUMENT) {
			stack[stackSize - 1] = Json5Scope.NONEMPTY_DOCUMENT;
		} else if (peekStack == Json5Scope.NONEMPTY_DOCUMENT) {
			int c = nextNonWhitespace(false);
			if (c == -1) {
				return peeked = PEEKED_EOF;
			} else {
				checkLenient();
				pos--;
			}
		} else if (peekStack == Json5Scope.CLOSED) {
			throw new IllegalStateException("JsonReader is closed");
		}

		int c = nextNonWhitespace(true);
		switch (c) {
			case ']':
				// Trailing comma: we expected another element, but found the end of an array
				return peeked = PEEKED_END_ARRAY;
			case ',':
				throw syntaxError("Unexpected value");
			case '\'':
				return peeked = PEEKED_SINGLE_QUOTED;
			case '"':
				return peeked = PEEKED_DOUBLE_QUOTED;
			case '[':
				return peeked = PEEKED_BEGIN_ARRAY;
			case '{':
				return peeked = PEEKED_BEGIN_OBJECT;
			default:
				pos--; // Don't consume the first character in a literal value.
		}

		int result = peekKeyword();
		if (result != PEEKED_NONE) {
			return result;
		}

		result = peekNumber();
		if (result != PEEKED_NONE) {
			return result;
		}

		if (!isLiteral(buffer[pos])) {
			throw syntaxError("Expected value");
		}

		checkLenient();
		return peeked = PEEKED_UNQUOTED;
	}

	private int peekKeyword() throws IOException {
		// Figure out which keyword we're matching against by its first character.
		char c = buffer[pos];
		String keyword;
		String keywordUpper;
		int peeking;
		if (c == 't' || c == 'T') {
			keyword = "true";
			keywordUpper = "TRUE";
			peeking = PEEKED_TRUE;
		} else if (c == 'f' || c == 'F') {
			keyword = "false";
			keywordUpper = "FALSE";
			peeking = PEEKED_FALSE;
		} else if (c == 'n' || c == 'N') {
			keyword = "null";
			keywordUpper = "NULL";
			peeking = PEEKED_NULL;
		} else {
			return PEEKED_NONE;
		}

		// Confirm that chars [1..length) match the keyword.
		int length = keyword.length();
		for (int i = 1; i < length; i++) {
			if (pos + i >= limit && !fillBuffer(i + 1)) {
				return PEEKED_NONE;
			}
			c = buffer[pos + i];
			if (c != keyword.charAt(i) && c != keywordUpper.charAt(i)) {
				return PEEKED_NONE;
			}
		}

		if ((pos + length < limit || fillBuffer(length + 1))
			&& isLiteral(buffer[pos + length])) {
			return PEEKED_NONE; // Don't match trues, falsey or nullsoft!
		}

		// We've found the keyword followed either by EOF or by a non-literal character.
		pos += length;
		return peeked = peeking;
	}

	private int peekNumber() throws IOException {
		// Like nextNonWhitespace, this uses locals 'p' and 'l' to save inner-loop field access.
		char[] buffer = this.buffer;
		int p = pos;
		int l = limit;

		boolean hexadecimal = false;
		int last = NUMBER_CHAR_NONE;

		int i = 0;

		charactersOfNumber:
		for (; true; i++) {
			if (p + i == l) {
				if (i == buffer.length) {
					// Though this looks like a well-formed number, it's too long to continue reading. Give up
					// and let the application handle this as an unquoted literal.
					return PEEKED_NONE;
				}
				if (!fillBuffer(i + 1)) {
					break;
				}
				p = pos;
				l = limit;
			}

			char c = buffer[p + i];
			switch (c) {
				case '-':
					if (last == NUMBER_CHAR_NONE) {
						last = NUMBER_CHAR_SIGN;
						continue;
					} else if (last == NUMBER_CHAR_EXP_E) {
						last = NUMBER_CHAR_EXP_SIGN;
						continue;
					}
					throw syntaxError("unexpected negative sign");
				case 'N':
					// NaN json5 only
					if ((last == NUMBER_CHAR_NONE) && literal(buffer, p + i, "NaN" )) {
						peekedNumberLength = i + 3;
						return peeked = PEEKED_NaN;
					}
					throw syntaxError("unexpected char N");
				case 'I':
					// Infinity json5 only
					if ((last == NUMBER_CHAR_NONE || last == NUMBER_CHAR_SIGN) && literal(buffer, p + i, "Infinity" )) {
						peekedNumberLength = i + 8;
						return peeked = last == NUMBER_CHAR_NONE ? PEEKED_INF : PEEKED_NEGATIVE_INF;
					}
				case '+':
					if (last == NUMBER_CHAR_EXP_E) {
						last = NUMBER_CHAR_EXP_SIGN;
						continue;
					}

		if (last == NUMBER_CHAR_NONE) {
						continue;
					}
					throw syntaxError("unexpected positive sign");

				case 'e':
				case 'E':
					if (last == NUMBER_CHAR_HEXADECIMAL) {
						continue;
					}

		if (last == NUMBER_CHAR_ZERO || last == NUMBER_CHAR_DIGIT || last == NUMBER_CHAR_FRACTION_DIGIT || last == NUMBER_CHAR_DECIMAL) {
						last = NUMBER_CHAR_EXP_E;
						continue;
					}
					throw syntaxError("unexpected exponent " + c);

				case '.':

		if (last == NUMBER_CHAR_DIGIT || last == NUMBER_CHAR_ZERO || last == NUMBER_CHAR_NONE || last == NUMBER_CHAR_SIGN) {
						last = NUMBER_CHAR_DECIMAL;
						continue;
					}
					throw syntaxError("unexpected decimal marker");

				case '0':
					if (last == NUMBER_CHAR_NONE || last == NUMBER_CHAR_SIGN) {
						last = NUMBER_CHAR_ZERO;
						continue;
					}
					if (last == NUMBER_CHAR_HEXADECIMAL || last == NUMBER_CHAR_FRACTION_DIGIT || last == NUMBER_CHAR_EXP_DIGIT) {
						continue;
					}
					if (last == NUMBER_CHAR_EXP_E || last == NUMBER_CHAR_EXP_SIGN) {
						last = NUMBER_CHAR_EXP_DIGIT;
						continue;
					}

					if (last == NUMBER_CHAR_ZERO) {
						throw syntaxError("unexpected leading zero");
					}
					last = NUMBER_CHAR_DIGIT;
					continue;
				case 'X':
				case 'x':
					if (last == NUMBER_CHAR_ZERO) {
						last = NUMBER_CHAR_HEXADECIMAL;
						hexadecimal = true;
						continue;
					}
					throw syntaxError("unexpected character x");
				default:
					if (hexadecimal) {
						if (!(c >= '0' && c <= '9') && !(c >= 'a' && c <= 'f') && !(c >= 'A' && c <= 'F')) {
							if (!isLiteral(c)) {
								break charactersOfNumber;
							}

							throw syntaxError("unexpected character " + c);
						}
					} else {
						if (c < '0' || c > '9') {
							if (!isLiteral(c)) {
								break charactersOfNumber;
							}
							throw syntaxError("unexpected character " + c);
						}
						if (last == NUMBER_CHAR_SIGN || last == NUMBER_CHAR_NONE) {
							last = NUMBER_CHAR_DIGIT;
						} else if (last == NUMBER_CHAR_DECIMAL) {
							last = NUMBER_CHAR_FRACTION_DIGIT;
						} else if (last == NUMBER_CHAR_EXP_E || last == NUMBER_CHAR_EXP_SIGN) {
							last = NUMBER_CHAR_EXP_DIGIT;
						}
					}
			}
		}

		if (last == NUMBER_CHAR_HEXADECIMAL) {
			peekedNumberLength = i;
			return peeked = PEEKED_HEXADECIMAL;
		}
		// We've read a complete number. Decide if it's a PEEKED_LONG or a PEEKED_NUMBER.

		if (last == NUMBER_CHAR_ZERO || last == NUMBER_CHAR_DIGIT || last == NUMBER_CHAR_FRACTION_DIGIT
			|| last == NUMBER_CHAR_EXP_DIGIT || last == NUMBER_CHAR_DECIMAL) { // JSON5 allows trailing decimal
			peekedNumberLength = i;
			return peeked = PEEKED_NUMBER;
		} else {
			throw syntaxError("unable to parse number");
		}
	}

	private boolean literal(char[] chars, int idx, String text) throws IOException {
		char[] expected = text.toCharArray();
		int end = text.length();
		for (int i = 0; i < end; i++) {
			if (expected[i] != chars[i + idx]) {
				return false;
			}
		}

		return !isLiteral(chars[idx + text.length()]);
	}

	private boolean isLiteral(char c) throws IOException {
		switch (c) {
			case '/':
			case '\\':
			case ';':
			case '#':
			case '=':
				checkLenient(); // fall-through
			case '{':
			case '}':
			case '[':
			case ']':
			case ':':
			case ',':
			case ' ':
			case '\t':
			case '\f':
			case '\r':
			case '\n':
				return false;
			default:
				return true;
		}
	}

	/**
	 * Returns the string up to but not including {@code quote}, unescaping any
	 * character escape sequences encountered along the way. The opening quote
	 * should have already been read. This consumes the closing quote, but does
	 * not include it in the returned string.
	 *
	 * @param quote either ' or ".
	 * @throws NumberFormatException if any unicode escape sequences are
	 *     malformed.
	 */
	private String nextQuotedValue(char quote) throws IOException {
		// Like nextNonWhitespace, this uses locals 'p' and 'l' to save inner-loop field access.
		char[] buffer = this.buffer;
		StringBuilder builder = null;
		while (true) {
			int p = pos;
			int l = limit;
			/* the index of the first character not yet appended to the builder. */
			int start = p;
			while (p < l) {
				int c = buffer[p++];

				if (c == quote) {
					pos = p;
					int len = p - start - 1;
					if (builder == null) {
						return new String(buffer, start, len);
					} else {
						builder.append(buffer, start, len);
						return builder.toString();
					}
				} else if (c == '\\') {
					pos = p;
					int len = p - start - 1;
					if (builder == null) {
						int estimatedLength = (len + 1) * 2;
						builder = new StringBuilder(Math.max(estimatedLength, 16));
					}
					builder.append(buffer, start, len);
					builder.append(readEscapeCharacter());
					p = pos;
					l = limit;
					start = p;
				} else if (c == '\n') {
					lineNumber++;
					lineStart = p;
				}
			}

			if (builder == null) {
				int estimatedLength = (p - start) * 2;
				builder = new StringBuilder(Math.max(estimatedLength, 16));
			}
			builder.append(buffer, start, p - start);
			pos = p;
			if (!fillBuffer(1)) {
				throw syntaxError("Unterminated string");
			}
		}
	}

	/**
	 * Returns an unquoted value as a string.
	 */
	@SuppressWarnings("fallthrough")
	private String nextUnquotedValue() throws IOException {
		StringBuilder builder = null;
		int i = 0;

		findNonLiteralCharacter:
		while (true) {
			for (; pos + i < limit; i++) {
				switch (buffer[pos + i]) {
					//					case '/':
					//					case '\\':
					//					case ';':
					//					case '#':
					//					case '=':
					//						checkNotStrict(); // fall-through
					case '{':
					case '}':
					case '[':
					case ']':
					case ':':
					case ',':
					case ' ':
					case '\t':
					case '\f':
					case '\r':
					case '\n':
						break findNonLiteralCharacter;
				}
			}

			// Attempt to load the entire literal into the buffer at once.
			if (i < buffer.length) {
				if (fillBuffer(i + 1)) {
					continue;
				} else {
					break;
				}
			}

			// use a StringBuilder when the value is too long. This is too long to be a number!
			if (builder == null) {
				builder = new StringBuilder(Math.max(i,16));
			}
			builder.append(buffer, pos, i);
			pos += i;
			i = 0;
			if (!fillBuffer(1)) {
				break;
			}
		}

		String result = (null == builder) ? new String(buffer, pos, i) : builder.append(buffer, pos, i).toString();
		pos += i;
		return result;
	}

	private void skipQuotedValue(char quote) throws IOException {
		// Like nextNonWhitespace, this uses locals 'p' and 'l' to save inner-loop field access.
		char[] buffer = this.buffer;
		do {
			int p = pos;
			int l = limit;
			/* the index of the first character not yet appended to the builder. */
			while (p < l) {
				int c = buffer[p++];
				if (c == quote) {
					pos = p;
					return;
				} else if (c == '\\') {
					pos = p;
					readEscapeCharacter();
					p = pos;
					l = limit;
				} else if (c == '\n') {
					lineNumber++;
					lineStart = p;
				}
			}
			pos = p;
		} while (fillBuffer(1));
		throw syntaxError("Unterminated string");
	}

	private void skipUnquotedValue() throws IOException {
		do {
			int i = 0;
			for (; pos + i < limit; i++) {
				switch (buffer[pos + i]) {
					case '/':
					case '\\':
					case ';':
					case '#':
					case '=':
						checkLenient(); // fall-through
					case '{':
					case '}':
					case '[':
					case ']':
					case ':':
					case ',':
					case ' ':
					case '\t':
					case '\f':
					case '\r':
					case '\n':
						pos += i;
						return;
				}
			}
			pos += i;
		} while (fillBuffer(1));
	}

	private void push(int newTop) {
		if (stackSize == stack.length) {
			int newLength = stackSize * 2;
			stack = Arrays.copyOf(stack, newLength);
			pathIndices = Arrays.copyOf(pathIndices, newLength);
			pathNames = Arrays.copyOf(pathNames, newLength);
		}
		stack[stackSize++] = newTop;
	}

	/**
	 * Returns true once {@code limit - pos >= minimum}. If the data is
	 * exhausted before that many characters are available, this returns
	 * false.
	 */
	private boolean fillBuffer(int minimum) throws IOException {
		char[] buffer = this.buffer;
		lineStart -= pos;
		if (limit != pos) {
			limit -= pos;
			System.arraycopy(buffer, pos, buffer, 0, limit);
		} else {
			limit = 0;
		}

		pos = 0;
		int total;
		while ((total = in.read(buffer, limit, buffer.length - limit)) != -1) {
			limit += total;

			// if this is the first read, consume an optional byte order mark (BOM) if it exists
			if (lineNumber == 0 && lineStart == 0 && limit > 0 && buffer[0] == '\ufeff') {
				pos++;
				lineStart++;
				minimum++;
			}

			if (limit >= minimum) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the next character in the stream that is neither whitespace nor a
	 * part of a comment. When this returns, the returned character is always at
	 * {@code buffer[pos-1]}; this means the caller can always push back the
	 * returned character by decrementing {@code pos}.
	 */
	private int nextNonWhitespace(boolean throwOnEof) throws IOException {
		/*
		 * This code uses ugly local variables 'p' and 'l' representing the 'pos'
		 * and 'limit' fields respectively. Using locals rather than fields saves
		 * a few field reads for each whitespace character in a pretty-printed
		 * document, resulting in a 5% speedup. We need to flush 'p' to its field
		 * before any (potentially indirect) call to fillBuffer() and reread both
		 * 'p' and 'l' after any (potentially indirect) call to the same method.
		 */
		char[] buffer = this.buffer;
		int p = pos;
		int l = limit;
		while (true) {
			if (p == l) {
				pos = p;
				if (!fillBuffer(1)) {
					break;
				}
				p = pos;
				l = limit;
			}

			int c = buffer[p++];
			if (c == '\n') {
				lineNumber++;
				lineStart = p;
				continue;
			} else if (c == ' ' || c == '\r' || c == '\t') {
				continue;
			} else if (c == 0x000b || c == 0x000C || c == 0x2028 || c == 0x2029 || c == 0xFEFF || Character.getType(c) == Character.SPACE_SEPARATOR) {
				continue;
			}

			pos = p;
			if (c == '/') {
				if (p == l) {
					pos--; // push back '/' so it's still in the buffer when this method returns
					boolean charsLoaded = fillBuffer(2);
					pos++; // consume the '/' again
					if (!charsLoaded) {
						return c;
					}
				}

				// Comments are JSONC/5 only
				char peek = buffer[pos];
				switch (peek) {
					case '*':
						// skip a /* c-style comment */
						pos++;
						if (!skipTo("*/")) {
							throw syntaxError("Unterminated comment");
						}
						p = pos + 2;
						l = limit;
						continue;

					case '/':
						// skip a // end-of-line comment
						pos++;
						skipToEndOfLine();
						p = pos;
						l = limit;
						continue;

					default:
						return c;
				}
			} else {
				return c;
			}
		}
		if (throwOnEof) {
			throw new EOFException("End of input" + locationString());
		} else {
			return -1;
		}
	}

	private void checkLenient() throws IOException {
		throw syntaxError("This file may be valid in lenient GSON, but it is not valid in any format we support.");
	}

	/**
	 * Advances the position until after the next newline character. If the line
	 * is terminated by "\r\n", the '\n' must be consumed as whitespace by the
	 * caller.
	 */
	private void skipToEndOfLine() throws IOException {
		while (pos < limit || fillBuffer(1)) {
			char c = buffer[pos++];
			if (c == '\n') {
				lineNumber++;
				lineStart = pos;
				break;
			} else if (c == '\r') {
				break;
			}
		}
	}

	/**
	 * @param toFind a string to search for. Must not contain a newline.
	 */
	private boolean skipTo(String toFind) throws IOException {
		int length = toFind.length();
		outer:
		for (; pos + length <= limit || fillBuffer(length); pos++) {
			if (buffer[pos] == '\n') {
				lineNumber++;
				lineStart = pos + 1;
				continue;
			}
			for (int c = 0; c < length; c++) {
				if (buffer[pos + c] != toFind.charAt(c)) {
					continue outer;
				}
			}
			return true;
		}
		return false;
	}

	public String locationString() {
		int line = lineNumber + 1;
		int column = pos - lineStart + 1;
		return " at line " + line + " column " + column + " path " + getPath();
	}

	/**
	 * Unescapes the character identified by the character or characters that
	 * immediately follow a backslash. The backslash '\' should have already
	 * been read. This supports both unicode escapes "u000A" and two-character
	 * escapes "\n".
	 *
	 * @throws NumberFormatException if any unicode escape sequences are
	 *     malformed.
	 */
	private char readEscapeCharacter() throws IOException {
		if (pos == limit && !fillBuffer(1)) {
			throw syntaxError("Unterminated escape sequence");
		}

		char escaped = buffer[pos++];
		switch (escaped) {
			case 'u':
				if (pos + 4 > limit && !fillBuffer(4)) {
					throw syntaxError("Unterminated escape sequence");
				}
				// Equivalent to Integer.parseInt(stringPool.get(buffer, pos, 4), 16);
				char result = 0;
				for (int i = pos, end = i + 4; i < end; i++) {
					char c = buffer[i];
					result <<= 4;
					if (c >= '0' && c <= '9') {
						result += (char) (c - '0');
					} else if (c >= 'a' && c <= 'f') {
						result += (char) (c - 'a' + 10);
					} else if (c >= 'A' && c <= 'F') {
						result += (char) (c - 'A' + 10);
					} else {
						throw new NumberFormatException("\\u" + new String(buffer, pos, 4));
					}
				}
				pos += 4;
				return result;

			case 't':
				return '\t';

			case 'b':
				return '\b';

			case 'n':
				return '\n';

			case 'r':
				return '\r';

			case 'f':
				return '\f';
			case '\r':
			case '\n':
				lineNumber++;
				lineStart = pos;
				// fall-through

			case '\'':
			case '"':
			case '\\':
			case '/':
				return escaped;
			default:
				// throw error when none of the above cases are matched
				throw syntaxError("Invalid escape sequence " + (int) escaped);
		}
	}

	/**
	 * Returns a <a href="https://goessner.net/articles/JsonPath/">JSONPath</a>
	 * in <i>dot-notation</i> to the next (or current) location in the JSON document:
	 * <ul>
	 *   <li>For JSON arrays the path points to the index of the next element (even
	 *   if there are no further elements).</li>
	 *   <li>For JSON objects the path points to the last property, or to the current
	 *   property if its name has already been consumed.</li>
	 * </ul>
	 *
	 * <p>This method can be useful to add additional context to exception messages
	 * <i>before</i> a value is consumed, for example when the {@linkplain #peek() peeked}
	 * token is unexpected.
	 */
	public String getPath() {
		StringBuilder result = new StringBuilder().append('$');
		for (int i = 0; i < stackSize; i++) {
			switch (stack[i]) {
				case Json5Scope.EMPTY_ARRAY:
				case Json5Scope.NONEMPTY_ARRAY:
					int pathIndex = pathIndices[i];
					result.append('[').append(pathIndex).append(']');
					break;

				case Json5Scope.EMPTY_OBJECT:
				case Json5Scope.DANGLING_NAME:
				case Json5Scope.NONEMPTY_OBJECT:
					result.append('.');
					if (pathNames[i] != null) {
						result.append(pathNames[i]);
					}
					break;

				case Json5Scope.NONEMPTY_DOCUMENT:
				case Json5Scope.EMPTY_DOCUMENT:
				case Json5Scope.CLOSED:
					break;
			}
		}
		return result.toString();
	}

	/**
	 * Throws a new IO exception with the given message and a context snippet
	 * with this reader's content.
	 */
	private IOException syntaxError(String message) throws IOException {
		throw new MalformedJsonException(message + locationString());
	}

	void promoteNameToValue() throws IOException {
		int p = peeked;
		if (p == PEEKED_NONE) {
			p = doPeek();
		}
		if (p == PEEKED_DOUBLE_QUOTED_NAME) {
			peeked = PEEKED_DOUBLE_QUOTED;
		} else if (p == PEEKED_SINGLE_QUOTED_NAME) {
			peeked = PEEKED_SINGLE_QUOTED;
		} else if (p == PEEKED_UNQUOTED_NAME) {
			peeked = PEEKED_UNQUOTED;
		} else {
			throw new IllegalStateException("Expected a name but was " + peek() + locationString());
		}
	}
}
