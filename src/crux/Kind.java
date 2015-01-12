package crux;

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