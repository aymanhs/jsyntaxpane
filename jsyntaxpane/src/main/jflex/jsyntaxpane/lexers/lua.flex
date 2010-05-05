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

	TokenType longType;
    int longLen;
%}

/* main character classes */
LineTerminator = \r|\n|\r\n

WhiteSpace = {LineTerminator} | [ \t\f]+

LongStart = \[=*\[
LongEnd = \]=*\]

/* identifiers */
Identifier = [:jletter:][:jletterdigit:]*

/* integer literals */
DecIntegerLiteral = [0-9]+
HexDigit          = [0-9a-fA-F]

HexIntegerLiteral = 0x{HexDigit}+

/* floating point literals */        
DoubleLiteral = ({FLit1}|{FLit2}) {Exponent}?

FLit1    = [0-9]+(\.[0-9]*)?
FLit2    = \.[0-9]+ 
Exponent = [eE] [+-]? [0-9]+

/* string and character literals */
StringCharacter1 = [^\r\n\"\\]
StringCharacter2 = [^\r\n\'\\]

%state STRING1
%state STRING2
%state LONGSTRING

%state COMMENT
%state LINECOMMENT

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

  {LongStart}				     {
                                   longType = TokenType.STRING;
                                   yybegin(LONGSTRING);
                                   tokenStart = yychar;
                                   tokenLength = yylength();
                                   longLen = tokenLength;
                                 }

  "--"							 {
                                   yybegin(COMMENT);
                                   tokenStart = yychar;
                                   tokenLength = yylength();
                                 }


  /* string literal */
  \"                             {  
                                    yybegin(STRING1);
                                    tokenStart = yychar; 
                                    tokenLength = 1; 
                                 }
  \'                             {
                                    yybegin(STRING2);
                                    tokenStart = yychar;
                                    tokenLength = 1;
                                 }

  /* numeric literals */

  {DecIntegerLiteral}            |
  
  {HexIntegerLiteral}            |
 
  {DoubleLiteral}		         { return token(TokenType.NUMBER); }
  
  /* whitespace */
  {WhiteSpace}                   { }

  /* identifiers */ 
  {Identifier}                   { return token(TokenType.IDENTIFIER); }
}

<LONGSTRING> {
	{LongEnd}                    {
                                     if (longLen == yylength()) {
										tokenLength += yylength();
	                                    yybegin(YYINITIAL);
                                        return token(longType, tokenStart, tokenLength);
									 } else {
                                        tokenLength++;
									    yypushback(yylength() - 1);
                                     }

	                             }
    {LineTerminator}			 { tokenLength += yylength(); }	                             
    .                            { tokenLength++; }
}

<COMMENT> {
	{LongStart}			         {
	                               longType = TokenType.COMMENT;
                                   yybegin(LONGSTRING);
                                   tokenLength += yylength();
                                   longLen = yylength();
								}

	{LineTerminator}			{
									yybegin(YYINITIAL);
                                    return token(TokenType.COMMENT, tokenStart, tokenLength);
								}

	.							{
								   yybegin(LINECOMMENT);
								   tokenLength += yylength();
								}
	<<EOF>>	             		{
									yybegin(YYINITIAL);
                                    return token(TokenType.COMMENT, tokenStart, tokenLength);
								}

}

<LINECOMMENT> {
	{LineTerminator}			{
									yybegin(YYINITIAL);
									tokenLength += yylength();
                                    return token(TokenType.COMMENT, tokenStart, tokenLength);
								}
    {LineTerminator}			 { tokenLength += yylength(); }
    .                            { tokenLength++; }
}

<STRING1> {
  \"                             { 
                                     yybegin(YYINITIAL); 
                                     // length also includes the trailing quote
                                     return token(TokenType.STRING, tokenStart, tokenLength + 1);
                                 }
  
  {StringCharacter1}+             { tokenLength += yylength(); }

  /* escape sequences */

  \\.                            { tokenLength += 2; }
  {LineTerminator}               { yybegin(YYINITIAL);  }
}

<STRING2> {
  \'                             {
                                     yybegin(YYINITIAL);
                                     // length also includes the trailing quote
                                     return token(TokenType.STRING, tokenStart, tokenLength + 1);
                                 }

  {StringCharacter2}+             { tokenLength += yylength(); }

  /* escape sequences */

  \\.                            { tokenLength += 2; }
  {LineTerminator}               { yybegin(YYINITIAL);  }
}

/* error fallback */
.|\n                             {  }
<<EOF>>                          { return null; }

