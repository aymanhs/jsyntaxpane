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


import jsyntaxpane.Token;
import jsyntaxpane.TokenType;

%%

%public
%class LuaLexer
%extends DefaultJFlexLexer
%final
%unicode
%char
%type Token


%{
    /**
     * Create an empty lexer, yyrset will be called later to reset and assign
     * the reader
     */
    public LuaLexer() {
        super();
    }

    @Override
    public int yychar() {
        return yychar;
    }
%}

/* main character classes */
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]

WhiteSpace = {LineTerminator} | [ \t\f]+

/* comments */
Comment = {TraditionalComment} | {EndOfLineComment} 

TraditionalComment = "--[[" [^\]] ~"]]" | "-[[" "]"+ "]"
EndOfLineComment = "--" {InputCharacter}* {LineTerminator}?

/* identifiers */
Identifier = [:jletter:][:jletterdigit:]*

/* integer literals */
DecIntegerLiteral = 0 | [1-9][0-9]*

HexIntegerLiteral = 0 [x] 0* {HexDigit} {1,8}
HexDigit          = [0-9a-fA-F]

/* floating point literals */        
FloatLiteral  = ({FLit1}|{FLit2}|{FLit3}) {Exponent}? [fF]
DoubleLiteral = ({FLit1}|{FLit2}|{FLit3}) {Exponent}?

FLit1    = [0-9]+ \. [0-9]* 
FLit2    = \. [0-9]+ 
FLit3    = [0-9]+ 
Exponent = [eE] [+-]? [0-9]+

/* string and character literals */
StringCharacter = [^\r\n\"\\]

%state STRING

%%

<YYINITIAL> {

  /* keywords */
  "and"                        	 |
  "break"                        |
  "do"      	                 |
  "else"	                     |
  "do"                           |
  "else"                         |
  "elseif"                       |
  "end"                          |
  "for"                       	 |
  "function"                     |
  "if"                         	 |
  "in"                           |
  "local"                        |
  "not"                        	 |
  "or"                         	 |
  "repeat"                       |
  "return"                       |
  "then"                     	 |
  "until"                        |
  "while"                        |
  
  /* boolean literals */
  "true"                         |
  "false"                        |
  
  /* nil literal */
  "nil"                          { return token(TokenType.KEYWORD); }

  /* operators */

  "+"                            |
  "-"                            |
  "*"                            | 
  "/"                            | 
  "%"                            | 
  "^"                            | 
  "#"                            | 
  "=="                           | 
  "~="                           | 
  "<="                           | 
  ">="                           | 
  "<"                            |
  ">"                            | 
  "="                            | 
  "("                            | 
  ")"                            | 
  "{"                            | 
  "}"                            | 
  "["                            | 
  "]"                            | 
  ";"                            | 
  ":"                            | 
  ","                            | 
  "."                            | 
  ".."                           | 
  "..."                          { return token(TokenType.OPERATOR); } 
  
  /* string literal */
  \"                             {  
                                    yybegin(STRING); 
                                    tokenStart = yychar; 
                                    tokenLength = 1; 
                                 }

  /* numeric literals */

  {DecIntegerLiteral}            |
  
  {HexIntegerLiteral}            |
 
  {FloatLiteral}                 |
  {DoubleLiteral}		         { return token(TokenType.NUMBER); }
  
  /* comments */
  {Comment}                      { return token(TokenType.COMMENT); }

  /* whitespace */
  {WhiteSpace}                   { }

  /* identifiers */ 
  {Identifier}                   { return token(TokenType.IDENTIFIER); }
}

<STRING> {
  \"                             { 
                                     yybegin(YYINITIAL); 
                                     // length also includes the trailing quote
                                     return token(TokenType.STRING, tokenStart, tokenLength + 1);
                                 }
  
  {StringCharacter}+             { tokenLength += yylength(); }

  /* escape sequences */

  \\.                            { tokenLength += 2; }
  {LineTerminator}               { yybegin(YYINITIAL);  }
}

/* error fallback */
.|\n                             {  }
<<EOF>>                          { return null; }

