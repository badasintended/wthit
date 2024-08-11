// This file was a part of Quilt Parsers, modified as follows:
// - Removed unused members
// - Directly extends GSON's JsonWriter
// - Added generic commenter function
// - Fixed issue with character replacement table (https://github.com/QuiltMC/quilt-parsers/pull/5)
// - Disabled backspace escape on comments
// https://github.com/QuiltMC/quilt-parsers/blob/00803c4e70fb0cf93765593eaae5c781b1505bee/json/src/main/java/org/quiltmc/parsers/json/JsonWriter.java
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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.Nullable;

/*
 *
 * The following changes have been applied to the original GSON code:
 * - Lenient mode has been removed
 * - Support for JSONC and JSON5 added
 *
 * You may view the original, including its license header, here:
 * https://github.com/google/gson/blob/530cb7447089ccc12dc2009c17f468ddf2cd61ca/gson/src/main/java/com/google/gson/stream/JsonReader.java
 */

/**
 * Writes a <a href="https://json5.org/"> JSON5</a> or strict JSON (<a href="http://www.ietf.org/rfc/rfc7159.txt">RFC 7159</a>)
 * encoded value to a stream, one token at a time. The stream includes both
 * literal values (strings, numbers, booleans and nulls) as well as the begin
 * and end delimiters of objects and arrays.
 *
 * <h3>Encoding JSON</h3>
 * To encode your data as JSON, create a new {@code JsonWriter}. Each JSON
 * document must contain one top-level array or object. Call methods on the
 * writer as you walk the structure's contents, nesting arrays and objects as
 * necessary:
 * <ul>
 *   <li>To write <strong>arrays</strong>, first call {@link #beginArray()}.
 *       Write each of the array's elements with the appropriate {@link #value}
 *       methods or by nesting other arrays and objects. Finally close the array
 *       using {@link #endArray()}.
 *   <li>To write <strong>objects</strong>, first call {@link #beginObject()}.
 *       Write each of the object's properties by alternating calls to
 *       {@link #name} with the property's value. Write property values with the
 *       appropriate {@link #value} method or by nesting other objects or arrays.
 *       Finally close the object using {@link #endObject()}.
 * </ul>
 *
 * <h3>Example</h3>
 * Suppose we'd like to encode a stream of messages such as the following: <pre> {@code
 * [
 *   {
 *     id: 912345678901,
 *     text: "How do I stream JSON in Java?",
 *     geo: null,
 *     user: {
 *       name: "json_newb",
 *       "followers_count": 41
 *      }
 *   },
 *   {
 *     id: 912345678902,
 *     text: "@json_newb just use JsonWriter!",
 *     geo: [50.454722, -104.606667],
 *     user: {
 *       name: "jesse",
 *       followers_count: 2
 *     }
 *   }
 * ]}</pre>
 * This code encodes the above structure: <pre>   {@code
 *   public void writeJsonStream(OutputStream out, List<Message> messages) throws IOException {
 *     JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
 *     writer.setIndent("    ");
 *     writeMessagesArray(writer, messages);
 *     writer.close();
 *   }
 *
 *   public void writeMessagesArray(JsonWriter writer, List<Message> messages) throws IOException {
 *     writer.beginArray();
 *     for (Message message : messages) {
 *       writeMessage(writer, message);
 *     }
 *     writer.endArray();
 *   }
 *
 *   public void writeMessage(JsonWriter writer, Message message) throws IOException {
 *     writer.beginObject();
 *     writer.name("id").value(message.getId());
 *     writer.name("text").value(message.getText());
 *     if (message.getGeo() != null) {
 *       writer.name("geo");
 *       writeDoublesArray(writer, message.getGeo());
 *     } else {
 *       writer.name("geo").nullValue();
 *     }
 *     writer.name("user");
 *     writeUser(writer, message.getUser());
 *     writer.endObject();
 *   }
 *
 *   public void writeUser(JsonWriter writer, User user) throws IOException {
 *     writer.beginObject();
 *     writer.name("name").value(user.getName());
 *     writer.name("followers_count").value(user.getFollowersCount());
 *     writer.endObject();
 *   }
 *
 *   public void writeDoublesArray(JsonWriter writer, List<Double> doubles) throws IOException {
 *     writer.beginArray();
 *     for (Double value : doubles) {
 *       writer.value(value);
 *     }
 *     writer.endArray();
 *   }}</pre>
 *
 * <p>Each {@code JsonWriter} may be used to write a single JSON stream.
 * Instances of this class are not thread safe. Calls that would result in a
 * malformed JSON string will fail with an {@link IllegalStateException}.
 */
@SuppressWarnings({"RedundantExplicitVariableType", "DataFlowIssue"})
public final class Json5Writer extends JsonWriter {
	/*
	 * From RFC 7159, "All Unicode characters may be placed within the
	 * quotation marks except for the characters that must be escaped:
	 * quotation mark, reverse solidus, and the control characters
	 * (U+0000 through U+001F)."
	 *
	 * We also escape '\u2028' and '\u2029', which JavaScript interprets as
	 * newline characters. This prevents eval() from failing with a syntax
	 * error. http://code.google.com/p/google-gson/issues/detail?id=341
	 */
	private static final String[] REPLACEMENT_CHARS;
	static {
		REPLACEMENT_CHARS = new String[128];
		for (int i = 0; i <= 0x1f; i++) {
			REPLACEMENT_CHARS[i] = String.format("\\u%04x", i);
		}
		REPLACEMENT_CHARS['"'] = "\\\"";
		REPLACEMENT_CHARS['\\'] = "\\\\";
		REPLACEMENT_CHARS['\t'] = "\\t";
		REPLACEMENT_CHARS['\b'] = "\\b";
		REPLACEMENT_CHARS['\n'] = "\\n";
		REPLACEMENT_CHARS['\r'] = "\\r";
		REPLACEMENT_CHARS['\f'] = "\\f";
	}

	/** The output data, containing at most one top-level array or object. */
	private final Writer out;

	private int[] stack = new int[32];
	private int stackSize = 0;
	{
		push(Json5Scope.EMPTY_DOCUMENT);
	}

	/**
	 * A string containing a full set of spaces for a single level of
	 * indentation, or null for no pretty printing.
	 */
	private final String indent = "  ";

	private String deferredName;
	private String deferredComment;

	private final Function<List<String>, @Nullable String> commenter;
	private final ArrayList<String> pathNames = new ArrayList<>(32);
	private final List<String> pathNamesView = Collections.unmodifiableList(pathNames);

	// API methods

	public Json5Writer(Writer out, Function<List<String>, @Nullable String> commenter) {
		super(Writer.nullWriter());
		Objects.requireNonNull(out, "Writer cannot be null");
		this.out = out;
		this.commenter = commenter;
	}

	/**
	 * Encodes the property name.
	 *
	 * @param name the name of the forthcoming value. May not be null.
	 * @return this writer.
	 */
	public Json5Writer name(String name) throws IOException {
		if (name == null) {
			throw new NullPointerException("name == null");
		}
		if (deferredName != null) {
			throw new IllegalStateException();
		}
		if (stackSize == 0) {
			throw new IllegalStateException("JsonWriter is closed.");
		}
		deferredName = name;
		pathNames.set(pathNames.size() - 1, name);
		comment(commenter.apply(pathNamesView));
		return this;
	}

	/**
	 * Begins encoding a new object. Each call to this method must be paired
	 * with a call to {@link #endObject}.
	 *
	 * @return this writer.
	 */
	public Json5Writer beginObject() throws IOException {
		writeDeferredName();
		return open(Json5Scope.EMPTY_OBJECT, '{');
	}

	/**
	 * Ends encoding the current object.
	 *
	 * @return this writer.
	 */
	public Json5Writer endObject() throws IOException {
		return close(Json5Scope.EMPTY_OBJECT, Json5Scope.NONEMPTY_OBJECT, '}');
	}

	/**
	 * Begins encoding a new array. Each call to this method must be paired with
	 * a call to {@link #endArray}.
	 *
	 * @return this writer.
	 */
	public Json5Writer beginArray() throws IOException {
		writeDeferredName();
		return open(Json5Scope.EMPTY_ARRAY, '[');
	}

	/**
	 * Ends encoding the current array.
	 *
	 * @return this writer.
	 */
	public Json5Writer endArray() throws IOException {
		return close(Json5Scope.EMPTY_ARRAY, Json5Scope.NONEMPTY_ARRAY, ']');
	}

	/**
	 * Encodes {@code value}.
	 *
	 * @param value the literal string value, or null to encode a null literal.
	 * @return this writer.
	 */
	public Json5Writer value(String value) throws IOException {
		if (value == null) {
			return nullValue();
		}
		writeDeferredName();
		beforeValue();
		string(value, true, true);
		return this;
	}

	/**
	 * Encodes {@code value}.
	 *
	 * @return this writer.
	 */
	public Json5Writer value(boolean value) throws IOException {
		writeDeferredName();
		beforeValue();
		out.write(value ? "true" : "false");
		return this;
	}

	/**
	 * Encodes {@code value}.
	 *
	 * @return this writer.
	 */
	public Json5Writer value(@Nullable Boolean value) throws IOException {
		if (value == null) {
			return nullValue();
		}
		writeDeferredName();
		beforeValue();
		out.write(value ? "true" : "false");
		return this;
	}

	/**
	 * Encodes {@code value}.
	 *
	 * @param value a finite value. May not be {@link Double#isNaN() NaNs} or
	 *     {@link Double#isInfinite() infinities}.
	 * @return this writer.
	 */
	public Json5Writer value(Number value) throws IOException {
		if (value == null) {
			return nullValue();
		}

		writeDeferredName();
		String string = value.toString();
		beforeValue();
		out.append(string);
		return this;
	}

	/**
	 * Encodes {@code value}.
	 *
	 * @param value a finite value. May not be {@link Double#isNaN() NaNs} or
	 *     {@link Double#isInfinite() infinities}.
	 * @return this writer.
	 */
	public Json5Writer value(double value) throws IOException {
		writeDeferredName();
		beforeValue();
		out.append(Double.toString(value));
		return this;
	}

	/**
	 * Encodes {@code value}.
	 *
	 * @return this writer.
	 */
	public Json5Writer value(long value) throws IOException {
		writeDeferredName();
		beforeValue();
		out.write(Long.toString(value));
		return this;
	}

	/**
	 * Encodes {@code null}.
	 *
	 * @return this writer.
	 */
	public Json5Writer nullValue() throws IOException {
		if (deferredName != null) {
			writeDeferredName();
		}
		beforeValue();
		out.write("null");
		return this;
	}

	/**
	 * Writes {@code value} directly to the writer without quoting or
	 * escaping.
	 *
	 * @param value the literal string value, or null to encode a null literal.
	 * @return this writer.
	 */
	public Json5Writer jsonValue(String value) throws IOException {
		if (value == null) {
			return nullValue();
		}
		writeDeferredName();
		beforeValue();
		out.append(value);
		return this;
	}

	/**
	 * Encodes a comment, handling newlines and HTML safety gracefully.
	 * Silently does nothing when strict JSON mode is enabled.
	 * @param comment the comment to write, or null to encode nothing.
	 */
	public Json5Writer comment(String comment) throws IOException {
		if (comment == null) {
			return this;
		}

		if (deferredComment == null) {
			deferredComment = comment;
		} else {
			deferredComment += "\n" + comment;
		}

		// Be aggressive about writing comments if we are at the end of the document
		if (stackSize == 1 && peek() == Json5Scope.NONEMPTY_DOCUMENT) {
			out.append('\n');
			writeDeferredComment();
		}

		return this;
	}

	/**
	 * Ensures all buffered data is written to the underlying {@link Writer}
	 * and flushes that writer.
	 */
	public void flush() throws IOException {
		if (stackSize == 0) {
			throw new IllegalStateException("JsonWriter is closed.");
		}
		out.flush();
	}

	/**
	 * Flushes and closes this writer and the underlying {@link Writer}.
	 *
	 * @throws IOException if the JSON document is incomplete.
	 */
	public void close() throws IOException {
		out.close();

		int size = stackSize;
		if (size > 1 || size == 1 && stack[size - 1] != Json5Scope.NONEMPTY_DOCUMENT) {
			throw new IOException("Incomplete document");
		}
		stackSize = 0;
	}

	// Implementation methods
	// Everything below here should be package-private or private

	/**
	 * Enters a new scope by appending any necessary whitespace and the given
	 * bracket.
	 */
	private Json5Writer open(int empty, char openBracket) throws IOException {
		beforeValue();
        pathNames.addLast("NULL");
		push(empty);
		out.write(openBracket);
		return this;
	}

	/**
	 * Closes the current scope by appending any necessary whitespace and the
	 * given bracket.
	 */
	private Json5Writer close(int empty, int nonempty, char closeBracket)
			throws IOException {
		int context = peek();
		if (context != nonempty && context != empty) {
			throw new IllegalStateException("Nesting problem.");
		}
		if (deferredName != null) {
			throw new IllegalStateException("Dangling name: " + deferredName);
		}

		stackSize--;
		if (!pathNames.isEmpty()) pathNames.removeLast();
		if (context == nonempty) {
			commentAndNewline();
		}
		out.write(closeBracket);
		return this;
	}

	private void push(int newTop) {
		if (stackSize == stack.length) {
			int newLength = stackSize * 2;
			stack = Arrays.copyOf(stack, newLength);
			pathNames.ensureCapacity(newLength);
		}
		stack[stackSize++] = newTop;
	}

	/**
	 * Returns the value on the top of the stack.
	 */
	private int peek() {
		if (stackSize == 0) {
			throw new IllegalStateException("JsonWriter is closed.");
		}
		return stack[stackSize - 1];
	}

	/**
	 * Replace the value on the top of the stack with the given value.
	 */
	private void replaceTop(int topOfStack) {
		stack[stackSize - 1] = topOfStack;
	}

	private void writeDeferredName() throws IOException {
		if (deferredName != null) {
			beforeName();
			boolean quotes = true;
			// JSON5 allows bare names... only for keys that are valid EMCA5 identifiers
			// luckily, Java identifiers follow the same standard,
			//  so we can just use the built-in Character.isJavaIdentifierStart/Part methods
			if (deferredName.length() > 0) {
				if (Character.isJavaIdentifierStart(deferredName.charAt(0))) {
					quotes = false;
					for (int i = 1; i < deferredName.length(); i++) {
						if (!Character.isJavaIdentifierPart(deferredName.charAt(i))) {
							quotes = true;
							break;
						}
					}
				}
			}

			string(deferredName, quotes, true);
			deferredName = null;
		}
	}

	private void writeDeferredComment() throws IOException {
		if (deferredComment == null) {
			return;
		}

		for (String s : deferredComment.split("\n")) {
			for (int i = 1, size = stackSize; i < size; i++) {
				out.write(indent);
			}
			out.write("// ");
			string(s, false, false);
			out.write("\n");
		}

		deferredComment = null;
	}

	private void string(String value, boolean quotes, boolean escapeQuotesAndBackspace) throws IOException {
		if (quotes) {
			out.write('\"');
		}

		int last = 0;
		int length = value.length();

		for (int i = 0; i < length; i++) {
			char c = value.charAt(i);
			String replacement;
			if (c < 128) {
                if (!escapeQuotesAndBackspace) {
                    if (c == '"' || c == '\\') continue;
                }
				replacement = REPLACEMENT_CHARS[c];
				if (replacement == null) {
					continue;
				}
			} else if (c == '\u2028') {
				replacement = "\\u2028";
			} else if (c == '\u2029') {
				replacement = "\\u2029";
			} else {
				continue;
			}
			if (last < i) {
				out.write(value, last, i - last);
			}
			out.write(replacement);
			last = i + 1;
		}
		if (last < length) {
			out.write(value, last, length - last);
		}

		if (quotes) {
			out.write('\"');
		}
	}

	private void commentAndNewline() throws IOException {
		out.write('\n');
		writeDeferredComment();

		for (int i = 1, size = stackSize; i < size; i++) {
			out.write(indent);
		}
	}

	/**
	 * Inserts any necessary separators and whitespace before a name. Also
	 * adjusts the stack to expect the name's value.
	 */
	private void beforeName() throws IOException {
		int context = peek();
		if (context == Json5Scope.NONEMPTY_OBJECT) { // first in object
			out.write(',');
		} else if (context != Json5Scope.EMPTY_OBJECT) { // not in an object!
			throw new IllegalStateException("Nesting problem.");
		}
		commentAndNewline();
		replaceTop(Json5Scope.DANGLING_NAME);
	}

	/**
	 * Inserts any necessary comments, separators, and whitespace before a literal value,
	 * inline array, or inline object. Also adjusts the stack to expect either a
	 * closing bracket or another element.
	 */
	@SuppressWarnings("fallthrough")
	private void beforeValue() throws IOException {
		switch (peek()) {
			case Json5Scope.NONEMPTY_DOCUMENT:
				// TODO: This isn't a JSON5 feature, right?
				throw new IllegalStateException(
						"JSON must have only one top-level value.");
				// fall-through
			case Json5Scope.EMPTY_DOCUMENT: // first in document
				comment(commenter.apply(List.of()));
				writeDeferredComment();
				replaceTop(Json5Scope.NONEMPTY_DOCUMENT);
				break;

			case Json5Scope.EMPTY_ARRAY: // first in array
				replaceTop(Json5Scope.NONEMPTY_ARRAY);
				commentAndNewline();
				break;

			case Json5Scope.NONEMPTY_ARRAY: // another in array
				out.append(',');
				commentAndNewline();
				break;

			case Json5Scope.DANGLING_NAME: // value for name
				out.append(": ");
				replaceTop(Json5Scope.NONEMPTY_OBJECT);
				break;

			default:
				throw new IllegalStateException("Nesting problem.");
		}
	}
}
