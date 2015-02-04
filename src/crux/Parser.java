package crux;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class Parser {
    public static String studentName = "Matthew Hartz";
    public static String studentID = "87401675";
    public static String uciNetID = "hartzm";
    
 // SymbolTable Management ==========================
    private SymbolTable symbolTable;
    
    private void initSymbolTable()
    {
    	symbolTable = new SymbolTable();
    	symbolTable.insert("readInt");
    	symbolTable.insert("readFloat");
    	symbolTable.insert("printBool");
    	symbolTable.insert("printInt");
    	symbolTable.insert("printFloat");
    	symbolTable.insert("println");
    }
    
    private void enterScope()
    {
    	SymbolTable s = new SymbolTable();
    	s.parent = symbolTable;
    	s.depth = symbolTable.depth + 1;
    	symbolTable = s;
    }
    
    private void exitScope()
    {
    	symbolTable = symbolTable.parent;
    }

    private Symbol tryResolveSymbol(Token ident)
    {
        assert(ident.is(Token.Kind.IDENTIFIER));
        String name = ident.lexeme();
        try {
            return symbolTable.lookup(name);
        } catch (SymbolNotFoundError e) {
            String message = reportResolveSymbolError(name, ident.lineNumber(), ident.charPosition());
            return new ErrorSymbol(message);
        }
    }

    private String reportResolveSymbolError(String name, int lineNum, int charPos)
    {
        String message = "ResolveSymbolError(" + lineNum + "," + charPos + ")[Could not find " + name + ".]";
        errorBuffer.append(message + "\n");
        errorBuffer.append(symbolTable.toString() + "\n");
        return message;
    }

    private Symbol tryDeclareSymbol(Token ident)
    {
        assert(ident.is(Token.Kind.IDENTIFIER));
        String name = ident.lexeme();
        try {
            return symbolTable.insert(name);
        } catch (RedeclarationError re) {
            String message = reportDeclareSymbolError(name, ident.lineNumber(), ident.charPosition());
            return new ErrorSymbol(message);
        }
    }

    private String reportDeclareSymbolError(String name, int lineNum, int charPos)
    {
        String message = "DeclareSymbolError(" + lineNum + "," + charPos + ")[" + name + " already exists.]";
        errorBuffer.append(message + "\n");
        errorBuffer.append(symbolTable.toString() + "\n");
        return message;
    }    
    
// Grammar Rule Reporting ==========================================
    private int parseTreeRecursionDepth = 0;
    private StringBuffer parseTreeBuffer = new StringBuffer();
    
    public String parseTreeReport()
    {
        return parseTreeBuffer.toString();
    }

// Error Reporting ==========================================
    private StringBuffer errorBuffer = new StringBuffer();
    
    private String reportSyntaxError(NonTerminal nt)
    {
        String message = "SyntaxError(" + lineNumber() + "," + charPosition() + ")[Expected a token from " + nt.name() + " but got " + currentToken.kind() + ".]";
        errorBuffer.append(message + "\n");
        return message;
    }
     
    private String reportSyntaxError(Token.Kind kind)
    {
        String message = "SyntaxError(" + lineNumber() + "," + charPosition() + ")[Expected " + kind + " but got " + currentToken.kind() + ".]";
        errorBuffer.append(message + "\n");
        return message;
    }
    
    public String errorReport()
    {
        return errorBuffer.toString();
    }
    
    public boolean hasError()
    {
        return errorBuffer.length() != 0;
    }
    
    private class QuitParseException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;
        public QuitParseException(String errorMessage) {
            super(errorMessage);
        }
    }
    
    private int lineNumber()
    {
        return currentToken.lineNumber();
    }
    
    private int charPosition()
    {
        return currentToken.charPosition();
    }
          
// Parser ==========================================
    private Scanner scanner;
    private Token currentToken;
    
    public Parser(Scanner s)
    {
    	scanner = s;
    	currentToken = s.next();
    }
    
    public void parse()
    {
    	initSymbolTable();
        try {
            program();
        } catch (QuitParseException q) {
            errorBuffer.append("SyntaxError(" + lineNumber() + "," + charPosition() + ")");
            errorBuffer.append("[Could not complete parsing.]");
        }
    }
    
// Helper Methods ==========================================
    private boolean have(Token.Kind kind)
    {
        return currentToken.is(kind);
    }
    
    private boolean have(NonTerminal nt)
    {
        return nt.firstSet().contains(currentToken.kind);
    }

    private boolean accept(Token.Kind kind)
    {
        if (have(kind)) {
            currentToken = scanner.next();
            return true;
        }
        return false;
    }    
    
    private boolean accept(NonTerminal nt)
    {
        if (have(nt)) {
            currentToken = scanner.next();
            return true;
        }
        return false;
    }
   
    private boolean expect(Token.Kind kind)
    {
        if (accept(kind))
            return true;
        String errorMessage = reportSyntaxError(kind);
        throw new QuitParseException(errorMessage);
        //return false;
    }
        
    private boolean expect(NonTerminal nt)
    {
        if (accept(nt))
            return true;
        String errorMessage = reportSyntaxError(nt);
        throw new QuitParseException(errorMessage);
        //return false;
    }
    
    private Token expectRetrieve(Token.Kind kind)
    {
        Token tok = currentToken;
        if (accept(kind))
            return tok;
        String errorMessage = reportSyntaxError(kind);
        throw new QuitParseException(errorMessage);
        //return ErrorToken(errorMessage);
    }
        
    private Token expectRetrieve(NonTerminal nt)
    {
        Token tok = currentToken;
        if (accept(nt))
            return tok;
        String errorMessage = reportSyntaxError(nt);
        throw new QuitParseException(errorMessage);
        //return ErrorToken(errorMessage);
    }
   
// Grammar Rules =====================================================
    
 // should be done
    public void program()
    {
    	declarationList();
    	expect(Token.Kind.EOF);
    }
       
    // should be done
    public void literal()
    {
    	if (accept(Token.Kind.INTEGER)) {
    	} else if (accept(Token.Kind.FLOAT)) {
    	} else if (accept(Token.Kind.TRUE)) {    		
    	} else if (accept(Token.Kind.FALSE)) {	
    	} else {
    		reportSyntaxError(NonTerminal.LITERAL);
    	}
    }
    
    // should be done
    public void designator()
    {
    	Token id = expectRetrieve(Token.Kind.IDENTIFIER);
    	tryResolveSymbol(id);
        while (accept(Token.Kind.OPEN_BRACKET)) {
            expression0();
            expect(Token.Kind.CLOSE_BRACKET);
        }
    }
    
    // should be done
    public void type() {
    	expect(Token.Kind.IDENTIFIER);
    }
    
    // should be done
    public void op0() {
    	if (accept(Token.Kind.GREATER_EQUAL)) {
    	} else if (accept(Token.Kind.LESSER_EQUAL)) {
    	} else if (accept(Token.Kind.NOT_EQUAL)) {
    	} else if (accept(Token.Kind.EQUAL)) {
    	} else if (accept(Token.Kind.GREATER_THAN)) {
    	} else if (accept(Token.Kind.LESS_THAN)) {
    	} else {
    		reportSyntaxError(NonTerminal.OP0);
    	}
    }
    
    // should be done
    public void op1() {
    	if (accept(Token.Kind.ADD)) {
    	} else if (accept(Token.Kind.SUB)) {
    	} else if (accept(Token.Kind.OR)) {    		
    	} else {
    		reportSyntaxError(NonTerminal.OP1);
    	}
    }
    
    // should be done
    public void op2() {
    	if (accept(Token.Kind.MUL)) {
    	} else if (accept(Token.Kind.DIV)) {
    	} else if (accept(Token.Kind.AND)) {    		
    	} else {
    		reportSyntaxError(NonTerminal.OP2);
    	}
    }
    
    // should be done
    public void statementBlock() {
    	expect(Token.Kind.OPEN_BRACE);
    	statementList();
    	expect(Token.Kind.CLOSE_BRACE);
    	exitScope();
    }
    
    // should be done
    public void statementList() {
    	while (have(NonTerminal.STATEMENT)) { 
    		statement();
    	} // loop for all statements
    }
    
    // should be done
    public void statement()
    {
    	if (have(NonTerminal.VARIABLE_DECLARATION)) {
    		variableDeclaration();
    	} else if (have(NonTerminal.CALL_STATEMENT)) {
    		callStatement();
    	} else if (have(NonTerminal.ASSIGNMENT_STATEMENT)) {
    		assignmentStatement();
    	} else if (have(NonTerminal.IF_STATEMENT)) {
    		ifStatement();
    	} else if (have(NonTerminal.WHILE_STATEMENT)) {
    		whileStatement();
    	} else if (have(NonTerminal.RETURN_STATEMENT)) {
    		returnStatement();
    	} else {
    		reportSyntaxError(NonTerminal.STATEMENT);
    	}
    }
    
    // should be done
    public void returnStatement() {
    	expect(Token.Kind.RETURN);
    	expression0();
    	expect(Token.Kind.SEMICOLON);
    }
    
    // should be done
    public void whileStatement() {
    	expect(Token.Kind.WHILE);
    	expression0();
    	
    	enterScope();
    	
    	statementBlock();
    }
    
    // should be done
    public void ifStatement()
    {
    	expect(Token.Kind.IF);
    	expression0();
    	
    	enterScope();
    	
    	statementBlock();
    	
    	if (accept(Token.Kind.ELSE)) {
    		enterScope();
    		
    		statementBlock();
    	}
    }
    
    // should be done
    public void callStatement() {
    	callExpression();
    	expect(Token.Kind.SEMICOLON);
    }
    
    // should be done
    public void assignmentStatement() {
    	expect(Token.Kind.LET);
    	designator();
    	expect(Token.Kind.ASSIGN);
    	expression0();
    	expect(Token.Kind.SEMICOLON);
    }
    
    // should be done
    public void declarationList()
    {
    	while (have(NonTerminal.DECLARATION)) { 
    		declaration();
    	}
    }
    
    // should be done
    public void declaration() {
    	if (have(NonTerminal.VARIABLE_DECLARATION)) {
    		variableDeclaration();
    	} else if (have(NonTerminal.ARRAY_DECLARATION)) {
    		arrayDeclaration();
    	} else if (have(NonTerminal.FUNCTION_DEFINITION)) {
    		functionDefinition();
    	} else {
    		reportSyntaxError(NonTerminal.DECLARATION);
    	}
    }
    
    public void functionDefinition() {
    	expect(Token.Kind.FUNC);
    	
    	Token id = expectRetrieve(Token.Kind.IDENTIFIER);
    	tryDeclareSymbol(id);

    	expect(Token.Kind.OPEN_PAREN);    	   	
    	enterScope();
    	parameterList();
    	
    	expect(Token.Kind.CLOSE_PAREN);
    	expect(Token.Kind.COLON);

    	type();
    	statementBlock();
    }
    
    // should be done
    public void arrayDeclaration()
    {
    	expect(Token.Kind.ARRAY);
    	Token id = expectRetrieve(Token.Kind.IDENTIFIER);
    	tryDeclareSymbol(id);
    	expect(Token.Kind.COLON);
    	type();
    	expect(Token.Kind.OPEN_BRACKET);
    	expect(Token.Kind.INTEGER);
    	expect(Token.Kind.CLOSE_BRACKET);
    	
    	while(accept(Token.Kind.OPEN_BRACKET)) {
    		expect(Token.Kind.INTEGER);
    		expect(Token.Kind.CLOSE_BRACKET);
    	}
    	
    	expect(Token.Kind.SEMICOLON);
    }
    
    // should be done
    public void variableDeclaration() {
    	expect(Token.Kind.VAR);
    	Token id = expectRetrieve(Token.Kind.IDENTIFIER);
    	tryDeclareSymbol(id);
    	expect(Token.Kind.COLON);
    	type();
    	expect(Token.Kind.SEMICOLON);
    }
    
    // should be done
    public void parameterList() {
    	if (have(NonTerminal.PARAMETER)) {
    		parameter();
    		while(accept(Token.Kind.COMMA)) {
    			parameter();
    		}
    	}
    }
    
    // should be done
    public void parameter()
    {
    	Token id = expectRetrieve(Token.Kind.IDENTIFIER);
    	tryDeclareSymbol(id);
    	expect(Token.Kind.COLON);
    	type();
    }
    
    // should be done
    public void expressionList() {
    	if(have(NonTerminal.EXPRESSION0)) {
    		expression0();
    		while(accept(Token.Kind.COMMA)) {
    			expression0();
    		}
    	}
    }
    
    // should be done
    public void callExpression() {
    	expect(Token.Kind.CALL);
    	Token id = expectRetrieve(Token.Kind.IDENTIFIER);
    	tryResolveSymbol(id);
    	expect(Token.Kind.OPEN_PAREN);
    	expressionList();
    	expect(Token.Kind.CLOSE_PAREN);
    }
    
    // should be done
    public void expression3()
    {
    	if (accept(Token.Kind.NOT)) {
    		expression3();
    	} else if (accept(Token.Kind.OPEN_PAREN)) {
    		expression0();
    		expect(Token.Kind.CLOSE_PAREN);
    	} else if (have(NonTerminal.DESIGNATOR)) {    
    		designator();
    	} else if (have(NonTerminal.CALL_EXPRESSION)) {	
    		callExpression();
    	} else if (have(NonTerminal.LITERAL)) {
    		literal();
    	} else {
    		reportSyntaxError(NonTerminal.EXPRESSION3);
    	}
    }
    
    // should be done
    public void expression2() {
    	expression3();
    	while(have(NonTerminal.OP2)) {
    		op2();
    		expression3();
    	}
    }
    
    // should be done
    public void expression1() {
    	expression2();
    	while(have(NonTerminal.OP1)) {
    		op1();
    		expression2();
    	}
    }
    
    // should be done
    public void expression0()
    {
    	expression1();
    	if(have(NonTerminal.OP0)) {
    		op0();
    		expression1();
    	}
    } 
}
