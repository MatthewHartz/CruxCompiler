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
    
// Grammar Rule Reporting ==========================================
    private int parseTreeRecursionDepth = 0;
    private StringBuffer parseTreeBuffer = new StringBuffer();

    public void enterRule(NonTerminal nonTerminal) {
        String lineData = new String();
        for(int i = 0; i < parseTreeRecursionDepth; i++)
        {
            lineData += "  ";
        }
        lineData += nonTerminal.name();
        //System.out.println("descending " + lineData);
        parseTreeBuffer.append(lineData + "\n");
        parseTreeRecursionDepth++;
    }
    
    private void exitRule(NonTerminal nonTerminal)
    {
        parseTreeRecursionDepth--;
    }
    
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
   
// Grammar Rules =====================================================
    
    // should be done
    public void literal()
    {
    	enterRule(NonTerminal.LITERAL);
    	if (accept(Token.Kind.INTEGER)) {
    	} else if (accept(Token.Kind.FLOAT)) {
    	} else if (accept(Token.Kind.TRUE)) {    		
    	} else if (accept(Token.Kind.FALSE)) {	
    	} else {
    		reportSyntaxError(NonTerminal.LITERAL);
    	}
    	exitRule(NonTerminal.LITERAL);
    }
    
    // should be done
    public void designator()
    {
        enterRule(NonTerminal.DESIGNATOR);

        expect(Token.Kind.IDENTIFIER);
        while (accept(Token.Kind.OPEN_BRACKET)) {
            expression0();
            expect(Token.Kind.CLOSE_BRACKET);
        }
        
        exitRule(NonTerminal.DESIGNATOR);
    }
    
    // should be done
    public void type() {
    	enterRule(NonTerminal.TYPE);
    	
    	expect(Token.Kind.IDENTIFIER);
    	
    	exitRule(NonTerminal.TYPE);
    }
    
    // should be done
    public void op0() {
    	enterRule(NonTerminal.OP0);
    	
    	if (accept(Token.Kind.GREATER_EQUAL)) {
    	} else if (accept(Token.Kind.LESSER_EQUAL)) {
    	} else if (accept(Token.Kind.NOT_EQUAL)) {
    	} else if (accept(Token.Kind.EQUAL)) {
    	} else if (accept(Token.Kind.GREATER_THAN)) {
    	} else if (accept(Token.Kind.LESS_THAN)) {
    	} else {
    		reportSyntaxError(NonTerminal.OP0);
    	}
    	
    	exitRule(NonTerminal.OP0);
    }
    
    // should be done
    public void op1() {
    	enterRule(NonTerminal.OP1);
    	if (accept(Token.Kind.ADD)) {
    	} else if (accept(Token.Kind.SUB)) {
    	} else if (accept(Token.Kind.OR)) {    		
    	} else {
    		reportSyntaxError(NonTerminal.OP1);
    	}
    	exitRule(NonTerminal.OP1);
    }
    
    // should be done
    public void op2() {
    	enterRule(NonTerminal.OP2);
    	if (accept(Token.Kind.MUL)) {
    	} else if (accept(Token.Kind.DIV)) {
    	} else if (accept(Token.Kind.AND)) {    		
    	} else {
    		reportSyntaxError(NonTerminal.OP2);
    	}
    	exitRule(NonTerminal.OP2);
    }

    // should be done
    public void program()
    {
    	enterRule(NonTerminal.PROGRAM);
    	declarationList();
    	expect(Token.Kind.EOF);
    	exitRule(NonTerminal.PROGRAM);
    }
    
    // should be done
    public void statementBlock() {
    	enterRule(NonTerminal.STATEMENT_BLOCK);
    	
    	expect(Token.Kind.OPEN_BRACE);
    	statementList();
    	expect(Token.Kind.CLOSE_BRACE);
    	
    	exitRule(NonTerminal.STATEMENT_BLOCK);
    }
    
    // should be done
    public void statementList() {
    	enterRule(NonTerminal.STATEMENT_LIST);
    	
    	while (have(NonTerminal.STATEMENT)) { 
    		statement();
    	} // loop for all statements
    	
    	exitRule(NonTerminal.STATEMENT_LIST);
    }
    
    // should be done
    public void statement()
    {
    	enterRule(NonTerminal.STATEMENT);
    	
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
    	
    	exitRule(NonTerminal.STATEMENT);
    }
    
    // should be done
    public void returnStatement() {
    	enterRule(NonTerminal.RETURN_STATEMENT);
    	
    	expect(Token.Kind.RETURN);
    	expression0();
    	expect(Token.Kind.SEMICOLON);
    	
    	exitRule(NonTerminal.RETURN_STATEMENT);
    }
    
    // should be done
    public void whileStatement() {
    	enterRule(NonTerminal.WHILE_STATEMENT);
    	
    	expect(Token.Kind.WHILE);
    	expression0();
    	statementBlock();
    	
    	exitRule(NonTerminal.WHILE_STATEMENT);
    }
    
    // should be done
    public void ifStatement()
    {
    	enterRule(NonTerminal.IF_STATEMENT);
    	
    	expect(Token.Kind.IF);
    	expression0();
    	statementBlock();
    	
    	if (accept(Token.Kind.ELSE)) {
    		statementBlock();
    	}
    	
    	exitRule(NonTerminal.IF_STATEMENT);
    }
    
    // should be done
    public void callStatement() {
    	enterRule(NonTerminal.CALL_STATEMENT);
    	
    	callExpression();
    	expect(Token.Kind.SEMICOLON);
    	
    	exitRule(NonTerminal.CALL_STATEMENT);
    }
    
    // should be done
    public void assignmentStatement() {
    	enterRule(NonTerminal.ASSIGNMENT_STATEMENT);
    	
    	expect(Token.Kind.LET);
    	designator();
    	expect(Token.Kind.ASSIGN);
    	expression0();
    	expect(Token.Kind.SEMICOLON);
    	
    	exitRule(NonTerminal.ASSIGNMENT_STATEMENT);
    }
    
    // should be done
    public void declarationList()
    {
    	enterRule(NonTerminal.DECLARATION_LIST);
    	while (have(NonTerminal.DECLARATION)) { 
    		declaration();
    	} // loop for all declarations
    	exitRule(NonTerminal.DECLARATION_LIST);
    }
    
    // should be done
    public void declaration() {
    	enterRule(NonTerminal.DECLARATION);
    	if (have(NonTerminal.VARIABLE_DECLARATION)) {
    		variableDeclaration();
    	} else if (have(NonTerminal.ARRAY_DECLARATION)) {
    		arrayDeclaration();
    	} else if (have(NonTerminal.FUNCTION_DEFINITION)) {
    		functionDefinition();
    	} else {
    		reportSyntaxError(NonTerminal.DECLARATION);
    	}
    	exitRule(NonTerminal.DECLARATION);
    }
    
    public void functionDefinition() {
    	enterRule(NonTerminal.FUNCTION_DEFINITION);
    	
    	expect(Token.Kind.FUNC);
    	expect(Token.Kind.IDENTIFIER);
    	expect(Token.Kind.OPEN_PAREN);    	

    	parameterList();
    	
    	expect(Token.Kind.CLOSE_PAREN);
    	expect(Token.Kind.COLON);

    	type();
    	statementBlock();
    	
    	exitRule(NonTerminal.FUNCTION_DEFINITION);
    }
    
    // should be done
    public void arrayDeclaration()
    {
    	enterRule(NonTerminal.ARRAY_DECLARATION);
    	
    	expect(Token.Kind.ARRAY);
    	expect(Token.Kind.IDENTIFIER);
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
    	
    	exitRule(NonTerminal.ARRAY_DECLARATION);
    }
    
    // should be done
    public void variableDeclaration() {
    	enterRule(NonTerminal.VARIABLE_DECLARATION);
    	
    	expect(Token.Kind.VAR);
    	expect(Token.Kind.IDENTIFIER);
    	expect(Token.Kind.COLON);
    	type();
    	expect(Token.Kind.SEMICOLON);
    	
    	exitRule(NonTerminal.VARIABLE_DECLARATION);
    }
    
    // should be done
    public void parameterList() {
    	enterRule(NonTerminal.PARAMETER_LIST);
    	
    	if (have(NonTerminal.PARAMETER)) {
    		parameter();
    		while(accept(Token.Kind.COMMA)) {
    			parameter();
    		}
    	}
    	
    	exitRule(NonTerminal.PARAMETER_LIST);
    }
    
    // should be done
    public void parameter()
    {
    	enterRule(NonTerminal.PARAMETER);
    	
    	expect(Token.Kind.IDENTIFIER);
    	expect(Token.Kind.COLON);
    	type();
    	
    	exitRule(NonTerminal.PARAMETER);
    }
    
    // should be done
    public void expressionList() {
    	enterRule(NonTerminal.EXPRESSION_LIST);
    	
    	if(have(NonTerminal.EXPRESSION0)) {
    		expression0();
    		while(accept(Token.Kind.COMMA)) {
    			expression0();
    		}
    	}
    	
    	exitRule(NonTerminal.EXPRESSION_LIST);
    }
    
    // should be done
    public void callExpression() {
    	enterRule(NonTerminal.CALL_EXPRESSION);
    	
    	expect(Token.Kind.CALL);
    	expect(Token.Kind.IDENTIFIER);
    	expect(Token.Kind.OPEN_PAREN);
    	expressionList();
    	expect(Token.Kind.CLOSE_PAREN);    	
    	
    	exitRule(NonTerminal.CALL_EXPRESSION);
    }
    
    // should be done
    public void expression3()
    {
    	enterRule(NonTerminal.EXPRESSION3);
    	
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
    	
    	exitRule(NonTerminal.EXPRESSION3);
    }
    
    // should be done
    public void expression2() {
    	enterRule(NonTerminal.EXPRESSION2);
    	
    	expression3();
    	while(have(NonTerminal.OP2)) {
    		op2();
    		expression3();
    	}
    	
    	exitRule(NonTerminal.EXPRESSION2);
    }
    
    // should be done
    public void expression1() {
    	enterRule(NonTerminal.EXPRESSION1);
    	
    	expression2();
    	while(have(NonTerminal.OP1)) {
    		op1();
    		expression2();
    	}
    	
    	exitRule(NonTerminal.EXPRESSION1);
    }
    
    // should be done
    public void expression0()
    {
    	enterRule(NonTerminal.EXPRESSION0);
    	
    	expression1();
    	if(have(NonTerminal.OP0)) {
    		op0();
    		expression1();
    	}
    	
    	exitRule(NonTerminal.EXPRESSION0);
    } 
}
