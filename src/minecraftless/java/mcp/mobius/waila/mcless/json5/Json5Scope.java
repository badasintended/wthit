// This file was a part of Quilt Parsers, directly copied with no changes.
// https://github.com/QuiltMC/quilt-parsers/blob/00803c4e70fb0cf93765593eaae5c781b1505bee/json/src/main/java/org/quiltmc/parsers/json/JsonScope.java
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

/*
 * This is a carbon-copy of Gson's JsonScope.
 * You may view the original, including its license header, here:
 * https://github.com/google/gson/blob/530cb7447089ccc12dc2009c17f468ddf2cd61ca/gson/src/main/java/com/google/gson/stream/JsonScope.java
 */

/**
 * Lexical scoping elements within a JSON reader or writer.
 *
 * @author Jesse Wilson
 * @since 1.6
 */
final class Json5Scope {

	/**
	 * An array with no elements requires no separators or newlines before
	 * it is closed.
	 */
	static final int EMPTY_ARRAY = 1;

	/**
	 * A array with at least one value requires a comma and newline before
	 * the next element.
	 */
	static final int NONEMPTY_ARRAY = 2;

	/**
	 * An object with no name/value pairs requires no separators or newlines
	 * before it is closed.
	 */
	static final int EMPTY_OBJECT = 3;

	/**
	 * An object whose most recent element is a key. The next element must
	 * be a value.
	 */
	static final int DANGLING_NAME = 4;

	/**
	 * An object with at least one name/value pair requires a comma and
	 * newline before the next element.
	 */
	static final int NONEMPTY_OBJECT = 5;

	/**
	 * No object or array has been started.
	 */
	static final int EMPTY_DOCUMENT = 6;

	/**
	 * A document with at an array or object.
	 */
	static final int NONEMPTY_DOCUMENT = 7;

	/**
	 * A document that's been closed and cannot be accessed.
	 */
	static final int CLOSED = 8;
}
