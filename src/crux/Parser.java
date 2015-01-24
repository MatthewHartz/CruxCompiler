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
    
    // literal := INTEGER | FLOAT | TRUE | FALSE .
    public void literal()
    {
    	enterRule(NonTerminal.LITERAL);
    	exitRule(NonTerminal.LITERAL);
    }
    
    // designator := IDENTIFIER { "[" expression0 "]" } .
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
    
    public void type() {
    	enterRule(NonTerminal.TYPE);
    	expect(Token.Kind.IDENTIFIER);
    	exitRule(NonTerminal.TYPE);
    }
    
    public void op0() {
    	enterRule(NonTerminal.OP0);
    	exitRule(NonTerminal.OP0);
    }
    
    public void op1() {
    	enterRule(NonTerminal.OP1);
    	exitRule(NonTerminal.OP1);
    }
    
    public void op2() {
    	enterRule(NonTerminal.OP2);
    	exitRule(NonTerminal.OP2);
    }

    // program := declaration-list EOF .
    public void program()
    {
    	enterRule(NonTerminal.PROGRAM);
    	declarationList();
    	expect(Token.Kind.EOF);
    	exitRule(NonTerminal.PROGRAM);
    }
    
    public void statementblock() {
    	enterRule(NonTerminal.STATEMENT_BLOCK);
    	expect(Token.Kind.OPEN_BRACE);
    	statementList();
    	expect(Token.Kind.CLOSE_BRACE);
    	exitRule(NonTerminal.STATEMENT_BLOCK);
    }
    
    public void statementList() {
    	enterRule(NonTerminal.STATEMENT_LIST);
    	while (have(NonTerminal.STATEMENT)) { } // loop for all statements
    	exitRule(NonTerminal.STATEMENT_LIST);
    }
    
    public void statement()
    {
    	enterRule(NonTerminal.STATEMENT);
    	exitRule(NonTerminal.STATEMENT);
    }
    
    public void returnStatement() {
    	enterRule(NonTerminal.RETURN_STATEMENT);
    	exitRule(NonTerminal.RETURN_STATEMENT);
    }
    
    public void whileStatement() {
    	enterRule(NonTerminal.WHILE_STATEMENT);
    	exitRule(NonTerminal.WHILE_STATEMENT);
    }
    
    public void ifStatement()
    {
    	enterRule(NonTerminal.IF_STATEMENT);
    	exitRule(NonTerminal.IF_STATEMENT);
    }
    
    public void callStatement() {
    	enterRule(NonTerminal.CALL_STATEMENT);
    	exitRule(NonTerminal.CALL_STATEMENT);
    }
    
    public void assignmentStatement() {
    	enterRule(NonTerminal.ASSIGNMENT_STATEMENT);
    	exitRule(NonTerminal.ASSIGNMENT_STATEMENT);
    }
    
    public void declarationList()
    {
    	enterRule(NonTerminal.DECLARATION_LIST);
    	while (have(NonTerminal.DECLARATION)) { 
    		declaration();
    	} // loop for all declarations
    	exitRule(NonTerminal.DECLARATION_LIST);
    }
    
    public void declaration() {
    	enterRule(NonTerminal.DECLARATION);
    	if (have(NonTerminal.VARIABLE_DECLARATION)) {
    		variableDeclaration();
    	} else if (have(NonTerminal.ARRAY_DECLARATION)) {
    		arrayDeclaration();
    	} else if (have(NonTerminal.FUNCTION_DEFINITION)) {
    		functionDefinition();
    	}
    	exitRule(NonTerminal.DECLARATION);
    }
    
    public void functionDefinition() {
    	enterRule(NonTerminal.FUNCTION_DEFINITION);
    	expect(Token.Kind.VAR);
    	expect(Token.Kind.IDENTIFIER);
    	exitRule(NonTerminal.FUNCTION_DEFINITION);
    }
    
    public void arrayDeclaration()
    {
    	enterRule(NonTerminal.ARRAY_DECLARATION);
    	exitRule(NonTerminal.ARRAY_DECLARATION);
    }
    
    public void variableDeclaration() {
    	enterRule(NonTerminal.VARIABLE_DECLARATION);
    	exitRule(NonTerminal.VARIABLE_DECLARATION);
    }
    
    public void parameterList() {
    	enterRule(NonTerminal.PARAMETER_LIST);
    	exitRule(NonTerminal.PARAMETER_LIST);
    }
    
    public void parameter()
    {
    	enterRule(NonTerminal.PARAMETER);
    	exitRule(NonTerminal.PARAMETER);
    }
    
    public void expressionList() {
    	enterRule(NonTerminal.EXPRESSION_LIST);
    	exitRule(NonTerminal.EXPRESSION_LIST);
    }
    
    public void callExpression() {
    	enterRule(NonTerminal.CALL_EXPRESSION);
    	exitRule(NonTerminal.CALL_EXPRESSION);
    }
    
    public void expression3()
    {
    	enterRule(NonTerminal.EXPRESSION3);
    	exitRule(NonTerminal.EXPRESSION3);
    }
    
    public void expression2() {
    	enterRule(NonTerminal.EXPRESSION2);
    	exitRule(NonTerminal.EXPRESSION2);
    }
    
    public void expression1() {
    	enterRule(NonTerminal.EXPRESSION1);
    	exitRule(NonTerminal.EXPRESSION1);
    }
    
    public void expression0()
    {
    	enterRule(NonTerminal.EXPRESSION0);
    	exitRule(NonTerminal.EXPRESSION0);
    } 
}
