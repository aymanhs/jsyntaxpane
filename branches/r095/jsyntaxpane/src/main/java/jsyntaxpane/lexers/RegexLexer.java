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
package jsyntaxpane.lexers;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jsyntaxpane.DefaultLexer;
import jsyntaxpane.Token;
import jsyntaxpane.TokenType;

/**
 * This is a "dynamic" Lexer that will use Regex patterns to parse any document,
 * It is NOT as fast as other JFLex generated lexers.
 *
 * The current implementation is <b>VERY SLOW</b>
 * 
 * @author Ayman Al-Sairafi
 */
public class RegexLexer extends DefaultLexer {

    static class RegexType {

        public RegexType(String pattern, TokenType type) {
            this.pattern = Pattern.compile(pattern);
            this.type = type;
        }
        final Pattern pattern;
        final TokenType type;
    }
    List<RegexType> patterns = new ArrayList<RegexType>();
    Reader reader;
    String currTokenString;
    int pos;
    char[] chars = new char[1028];

    public RegexLexer() {
        patterns.add(new RegexType("(import|class|public|private|protected|static|final)", TokenType.KEYWORD));
        patterns.add(new RegexType("//.*$*", TokenType.COMMENT));
        patterns.add(new RegexType("[a-z][a-zA-Z0-9_]*", TokenType.IDENTIFIER));
        patterns.add(new RegexType("\"[^\"]*\"", TokenType.STRING));
    }

    public Token matchPattern(Pattern pat, CharSequence seg, TokenType type) throws IOException {
        Matcher m = pat.matcher(seg);
        if (m.find()) {
            Token t = new Token(type, pos + m.start(), m.end() - m.start());
            return t;
        }
        return null;
    }

    @Override
    public void yyreset(Reader reader) {
        this.reader = reader;
        pos = 0;
    }

    public Token yylex() throws IOException {
        reader.mark(1);
        int len = reader.read(chars);
        if (len < 0) {
            return null;
        }
        String seg = new String(chars, 0, len);
        Token matched = null;
        for (RegexType rt : patterns) {
            Token current = null;
            if ((current = matchPattern(rt.pattern, seg, rt.type)) != null) {
                if (matched == null) {
                    matched = current;
                } else {
                    RegexTokensComparator cmp = new RegexTokensComparator();
                    if (cmp.compare(current, matched) < 0) {
                        matched = current;
                    }
                }
            }
        }
        if (matched != null) {
            reader.reset();
            long s = reader.skip(matched.end() - pos);
            // currTokenString = ;
            pos = matched.end();
            return matched;
        }

        return null;
    }

    public char yycharat(int pos) {
        return currTokenString.charAt(pos);
    }

    public int yylength() {
        return currTokenString.length();
    }

    public String yytext() {
        return currTokenString;
    }

    /**
     * This comparator is used to have longer tokens becoming MORE than
     * shorter tokens.  This is needed so the first in a Token tree is
     * always the longest match
     */
    static class RegexTokensComparator implements Comparator<Token> {

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
}
