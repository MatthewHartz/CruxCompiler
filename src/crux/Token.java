package crux;

import java.util.Formatter;
import java.util.Locale;

public class Token {
	public enum Kind {
		AND("and"),
		OR("or"),
		NOT("not"),
		LET("let"),
		VAR("var"),
		ARRAY("array"),
		FUNC("func"),
		IF("if"),
		ELSE("else"),
		WHILE("while"),
		TRUE("true"),
		FALSE("false"),
		RETURN("return"),
		ADD("+"),
		SUB("-"),
		MUL("*"),
		DIV("/"),
		GREATER_EQUAL(">="),
		LESSER_EQUAL("<="),
		NOT_EQUAL("!="),
		EQUAL("=="),
		GREATER_THAN(">"),
		LESS_THAN("<"),
		ASSIGN("="),
		COMMA(","),
		SEMICOLON(";"),
		COLON(":"),
		CALL("::"),
		OPEN_PAREN("("),
		CLOSE_PAREN(")"),
		OPEN_BRACE("{"),
		CLOSE_BRACE("}"),
		OPEN_BRACKET("["),
		CLOSE_BRACKET("]"),
		IDENTIFIER(),
		INTEGER(),
		FLOAT(),
		ERROR(),
		EOF();
		
		private String default_lexeme;
		
		Kind()
		{
			default_lexeme = "";
		}
		
		Kind(String lexeme)
		{
			default_lexeme = lexeme;
		}
		
		public boolean hasStaticLexeme()
		{
			return default_lexeme != "";
		}
		
		public static Boolean matches(String lex) {
			Kind[] kinds = Kind.values();
			for (Kind k : kinds ) {
				if (k.default_lexeme.equals(lex))
					return true;
			}
			
			return false;
		}
		
		public static Kind getKind(String lex) throws Exception{
			Kind[] kinds = Kind.values();
			for (Kind k : kinds ) {
				if (k.default_lexeme.equals(lex))
					return k;
			}
			
			throw new Exception("Lexeme does not match any known kind");
		}
	}
	
	
	
	private int lineNum;
	private int charPos;
	Kind kind;
	private String lexeme = "";
	
	private Token(int lineNum, int charPos)
	{
		this.lineNum = lineNum;
		this.charPos = charPos;
		
		// if we don't match anything, signal error
		this.kind = Kind.ERROR;
		this.lexeme = "No Lexeme Given";
	}
	
	public Token(String lexeme, int lineNum, int charPos)
	{
		this.lineNum = lineNum;
		this.charPos = charPos;
		
		try {
			this.kind = Kind.getKind(lexeme);
		} catch (Exception e) {
			this.kind = Kind.ERROR;
			this.lexeme = "Unexpected character: " + lexeme;
		}
	}
	
	public int lineNumber()
	{
		return lineNum;
	}
	
	public int charPosition()
	{
		return charPos;
	}
	
	public String lexeme()
	{
		return lexeme;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb, Locale.US);
		
		if (this.lexeme == "") {
			formatter.format("%s(lineNum:%d, charPos:%d)", kind.toString(),this.lineNum, this.charPos);
		} else {
			formatter.format("%s(%s)(lineNum:%d, charPos:%d)", kind.toString(), this.lexeme.toString(), this.lineNum, this.charPos);
		}
		
		formatter.close();
		return sb.toString();
	}
	
	public boolean is(Kind k) {
		return k.name() == kind.name();
	}
	
	public static Token GetToken(String type, String lexeme, int lineNum, int charPos) {
		if (type == null) {
			return new Token(lineNum, charPos);
		}
		
		switch (type.toUpperCase()) {
			case "EOF":
				return EOF(lineNum, charPos);
			case "INTEGER":
				return INTEGER(lexeme, lineNum, charPos);
			case "FLOAT":
				return FLOAT(lexeme, lineNum, charPos);
			case "IDENTIFIER":
				return IDENTIFIER(lexeme, lineNum, charPos);
			default:
				return new Token(lexeme, lineNum, charPos);
		}
	}
	
	public static Token EOF(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.EOF;
		tok.lexeme = "";
		return tok;
	}
	
	public static Token INTEGER(String lexeme, int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.INTEGER;
		tok.lexeme = lexeme;
		return tok;
	}
	
	public static Token FLOAT(String lexeme, int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.FLOAT;
		tok.lexeme = lexeme;
		return tok;
	}
	
	public static Token IDENTIFIER(String lexeme, int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.IDENTIFIER;
		tok.lexeme = lexeme;
		return tok;
	}

	public Kind kind() {
		// TODO Auto-generated method stub
		return kind;
	}
}
