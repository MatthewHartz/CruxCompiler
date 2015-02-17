package crux;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import ast.Command;
import ast.Expression;

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
    
    public ast.Command parse()
    {
    	initSymbolTable();
        try {
            return program();
        } catch (QuitParseException q) {
        	return new ast.Error(lineNumber(), charPosition(), "Could not complete parsing.");
        }
    }
    
// Helper Methods ==========================================
    private boolean have(Token.Kind kind)
    {
        return currentToken.is(kind);
    }
    
    private boolean have(NonTerminal nt)
    {
        return nt.firstSet().contains(currentToken.kind());
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
    public ast.DeclarationList program()
    {
    	ast.DeclarationList expr;
    	
    	expr = declarationList();    	
    	expect(Token.Kind.EOF);
    	
    	return expr;
    }
       
    // should be done
    public ast.Expression literal()
    {
    	ast.Expression expr;
        //enterRule(NonTerminal.LITERAL);
        
        Token tok = expectRetrieve(NonTerminal.LITERAL);
        expr = Command.newLiteral(tok);
        
        //exitRule(NonTerminal.LITERAL);
        return expr;
    }
    
    // should be done
    public ast.Index designator()
    {
    	
    	
    	Token id = expectRetrieve(Token.Kind.IDENTIFIER);
    	Symbol sym = tryResolveSymbol(id);
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
    public ast.Expression op0() {
    	Token t = currentToken;
    	
    	if (accept(Token.Kind.GREATER_EQUAL)) {
    	} else if (accept(Token.Kind.LESSER_EQUAL)) {
    	} else if (accept(Token.Kind.NOT_EQUAL)) {
    	} else if (accept(Token.Kind.EQUAL)) {
    	} else if (accept(Token.Kind.GREATER_THAN)) {
    	} else if (accept(Token.Kind.LESS_THAN)) {
    	} else {
    		return new ast.Error(lineNumber(), charPosition(), reportSyntaxError(NonTerminal.OP0));
    	}
    	
    	return t;
    }
    
    // should be done
    public ast.Expression op1() {
    	
    	if (accept(Token.Kind.ADD)) {
    	} else if (accept(Token.Kind.SUB)) {
    	} else if (accept(Token.Kind.OR)) {    		
    	} else {
    		return new ast.Error(lineNumber(), charPosition(), reportSyntaxError(NonTerminal.OP1));
    		
    	}
    }
    
    // should be done
    public void op2() {
    	if (accept(Token.Kind.MUL)) {
    	} else if (accept(Token.Kind.DIV)) {
    	} else if (accept(Token.Kind.AND)) {    		
    	} else {
    		//return new ast.Error(lineNumber(), charPosition(), reportSyntaxError(NonTerminal.OP2));
    	}
    }
    
    // should be done
    public ast.StatementList statementBlock() {
    	expect(Token.Kind.OPEN_BRACE);
    	ast.StatementList sl = statementList();
    	expect(Token.Kind.CLOSE_BRACE);
    	//exitScope();
    	return sl;
    }
    
    // should be done
    public ast.StatementList statementList() {
    	ast.StatementList sl = new ast.StatementList(lineNumber(), charPosition());
    	
    	while (have(NonTerminal.STATEMENT)) { 
    		sl.add(statement());
    	} // loop for all statements
    	
    	return sl;
    }
    
    // should be done
    public ast.Statement statement()
    {	
    	if (have(NonTerminal.VARIABLE_DECLARATION)) {
    		return variableDeclaration();
    	} else if (have(NonTerminal.CALL_STATEMENT)) {
    		return callStatement();
    	} else if (have(NonTerminal.ASSIGNMENT_STATEMENT)) {
    		return assignmentStatement();
    	} else if (have(NonTerminal.IF_STATEMENT)) {
    		return ifStatement();
    	} else if (have(NonTerminal.WHILE_STATEMENT)) {
    		return whileStatement();
    	} else if (have(NonTerminal.RETURN_STATEMENT)) {
    		return returnStatement();
    	} else {
    		return new ast.Error(lineNumber(), charPosition(), reportSyntaxError(NonTerminal.STATEMENT));
    	}
    }
    
    // should be done
    public ast.Return returnStatement() {
    	expect(Token.Kind.RETURN);
    	ast.Expression expr = expression0();
    	expect(Token.Kind.SEMICOLON);
    	
    	return new ast.Return(lineNumber(), charPosition(), expr);
    }
    
    // should be done
    public ast.WhileLoop whileStatement() {
    	expect(Token.Kind.WHILE);
    	ast.Expression expr = expression0();
    	
    	enterScope();
    	
    	ast.StatementList body = statementBlock();
    	
    	return new ast.WhileLoop(lineNumber(), charPosition(), expr, body);
    }
    
    // should be done
    public ast.IfElseBranch ifStatement()
    {
    	expect(Token.Kind.IF);
    	ast.Expression cond = expression0();
    	
    	enterScope();
    	
    	ast.StatementList ifBlock = statementBlock();
    	
    	ast.StatementList elseBlock = null;
    	
    	if (accept(Token.Kind.ELSE)) {
    		enterScope();
    		
    		elseBlock = statementBlock();
    	}
    	
    	return new ast.IfElseBranch(lineNumber(), charPosition(), cond, ifBlock, elseBlock);
    }
    
    // should be done
    public ast.Statement callStatement() {
    	ast.Call call = callExpression();
    	expect(Token.Kind.SEMICOLON);
    	
    	return call;
    }
    
    // should be done
    public ast.Assignment assignmentStatement() {
    	expect(Token.Kind.LET);
    	ast.Expression dest = designator();
    	expect(Token.Kind.ASSIGN);
    	ast.Expression source = expression0();
    	expect(Token.Kind.SEMICOLON);
    	
    	return new ast.Assignment(lineNumber(), charPosition(), dest, source);
    }
    
    // should be done
    public ast.DeclarationList declarationList()
    {
    	ast.DeclarationList dl = new ast.DeclarationList(lineNumber(), charPosition());
    	
    	while (have(NonTerminal.DECLARATION)) { 
    		dl.add(declaration());
    	}
    	
    	return dl;
    }
    
    // should be done
    public ast.Declaration declaration() {
    	if (have(NonTerminal.VARIABLE_DECLARATION)) {
    		return variableDeclaration();
    	} else if (have(NonTerminal.ARRAY_DECLARATION)) {
    		return arrayDeclaration();
    	} else if (have(NonTerminal.FUNCTION_DEFINITION)) {
    		return functionDefinition();
    	} else {
    		return new ast.Error(lineNumber(), charPosition(), reportSyntaxError(NonTerminal.DECLARATION));
    	}
    }
    
    public ast.FunctionDefinition functionDefinition() {
    	expect(Token.Kind.FUNC);
    	
    	Token id = expectRetrieve(Token.Kind.IDENTIFIER);
    	Symbol sym = tryDeclareSymbol(id);

    	expect(Token.Kind.OPEN_PAREN);    	   	
    	enterScope();
    	List<Symbol> args = parameterList();
    	
    	expect(Token.Kind.CLOSE_PAREN);
    	expect(Token.Kind.COLON);

    	type();
    	ast.StatementList body = statementBlock();
    	
    	return new ast.FunctionDefinition(lineNumber(), charPosition(), sym, args, body);
    }
    
    // should be done
    public ast.ArrayDeclaration arrayDeclaration()
    {
    	expect(Token.Kind.ARRAY);
    	Token id = expectRetrieve(Token.Kind.IDENTIFIER);
    	Symbol sym = tryDeclareSymbol(id);
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
    	
    	return new ast.ArrayDeclaration(lineNumber(), charPosition(), sym);
    }
    
    // should be done
    public ast.VariableDeclaration variableDeclaration() {
    	expect(Token.Kind.VAR);
    	Token id = expectRetrieve(Token.Kind.IDENTIFIER);
    	Symbol sym = tryDeclareSymbol(id);
    	expect(Token.Kind.COLON);
    	type();
    	expect(Token.Kind.SEMICOLON);
    	
    	return new ast.VariableDeclaration(lineNumber(), charPosition(), sym);
    }
    
    // should be done
    public List<Symbol> parameterList() {
    	List<Symbol> parameters = new ArrayList<Symbol>();
    	if (have(NonTerminal.PARAMETER)) {
    		parameters.add(parameter());
    		while(accept(Token.Kind.COMMA)) {
    			parameters.add(parameter());
    		}
    	}
    	
    	return parameters;
    }
    
    // should be done
    public Symbol parameter()
    {
    	Token id = expectRetrieve(Token.Kind.IDENTIFIER);
    	Symbol sym = tryDeclareSymbol(id);
    	expect(Token.Kind.COLON);
    	type();
    	
    	return sym;
    }
    
    // should be done
    public ast.ExpressionList expressionList() {
    	ast.ExpressionList el = new ast.ExpressionList(lineNumber(), charPosition());
    	
    	if(have(NonTerminal.EXPRESSION0)) {
    		el.add(expression0());
    		while(accept(Token.Kind.COMMA)) {
    			el.add(expression0());
    		}
    	}
    	
    	return el;
    }
    
    // should be done
    public ast.Call callExpression() {
    	expect(Token.Kind.CALL);
    	Token id = expectRetrieve(Token.Kind.IDENTIFIER);
    	Symbol sym = tryResolveSymbol(id);
    	expect(Token.Kind.OPEN_PAREN);
    	ast.ExpressionList args = expressionList();
    	expect(Token.Kind.CLOSE_PAREN);
    	
    	return new ast.Call(lineNumber(), charPosition(), sym, args);
    }
    
    // should be done
    public ast.Expression expression3()
    {
    	if (accept(Token.Kind.NOT)) {
    		return expression3();
    	} else if (accept(Token.Kind.OPEN_PAREN)) {
    		ast.Expression expr = expression0();
    		expect(Token.Kind.CLOSE_PAREN);
    		return expr;
    	} else if (have(NonTerminal.DESIGNATOR)) {    
    		return designator();
    	} else if (have(NonTerminal.CALL_EXPRESSION)) {	
    		return callExpression();
    	} else if (have(NonTerminal.LITERAL)) {
    		return literal();
    	} else {
    		return new ast.Error(lineNumber(), charPosition(), reportSyntaxError(NonTerminal.EXPRESSION3));
    	}
    }
    
    // should be done
    public ast.Expression expression2() {
    	expression3();
    	while(have(NonTerminal.OP2)) {
    		op2();
    		expression3();
    	}
    }
    
    // should be done
    public ast.Expression expression1() {
    	expression2();
    	while(have(NonTerminal.OP1)) {
    		op1();
    		expression2();
    	}
    }
    
    // should be done
    public ast.Expression expression0()
    {
    	ast.Expression leftSide = expression1();
    	ast.Expression rightSide;
    	ast.Token op;
    	if(have(NonTerminal.OP0)) {
    		ast.Comparison.Operation op = op0();
    		rightSide = expression1();
    	}
    	
    	return Command.newExpression(leftSide, op, rightSide);
    	
    	//return new ast.Comparison(lineNumber(), charPosition(), leftSide, operator, rightSide);
    } 
}
