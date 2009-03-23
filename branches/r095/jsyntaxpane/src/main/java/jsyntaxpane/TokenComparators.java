/*
 * Copyright 2008 Ayman Al-Sairafi ayman.alsairafi@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License
 *       at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jsyntaxpane;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Two comparators to compare Tokens.
 * @author Ayman Al-Sairafi
 */
public class TokenComparators {

	public static Comparator<Token> longestFirst = new LongestFirst();
	public static Comparator<Token> ShortestFirst = new ShortestFirst();

	private static class LongestFirst implements Comparator<Token> {

		@Override
		public int compare(Token t1, Token t2) {
			if (t1.start != t2.start) {
				return (t1.start - t2.start);
			} else if (t1.length != t2.length) {
				return (t2.length - t1.length);
			} else {
				return t1.type.compareTo(t2.type);
			}
		}
	}

	private static class ShortestFirst implements Comparator<Token> {

		@Override
		public int compare(Token t1, Token t2) {
			if (t1.start != t2.start) {
				return (t1.start - t2.start);
			} else if (t1.length != t2.length) {
				return (t1.length - t2.length);
			} else {
				return t1.type.compareTo(t2.type);
			}
		}
	}

	public static void main(String[] args) {
		Token[] toks = new Token[] {
			new Token(TokenType.COMMENT, 0, 10),
			new Token(TokenType.KEYWORD, 0, 5),
			new Token(TokenType.DELIMITER, 0, 5),
			new Token(TokenType.DELIMITER, 1, 15),
			new Token(TokenType.IDENTIFIER, 1, 3),
			new Token(TokenType.TYPE, 1, 3),
		};

		Arrays.sort(toks, longestFirst);
		System.out.println("Longest:  " + Arrays.toString(toks));
		Arrays.sort(toks, ShortestFirst);
		System.out.println("Shortest: " + Arrays.toString(toks));
	}
}
