package types;

import java.util.HashMap;
import java.util.List;

import crux.Symbol;
import ast.*;

public class TypeChecker implements CommandVisitor {
    
    private HashMap<Command, Type> typeMap;
    private StringBuffer errorBuffer;
    private HashMap<String, Command> functionMap;
    private Type returnType;

    /* Useful error strings:
     *
     * "Function " + func.name() + " has a void argument in position " + pos + "."
     * "Function " + func.name() + " has an error in argument in position " + pos + ": " + error.getMessage()
     *
     * "Function main has invalid signature."
     *
     * "Not all paths in function " + currentFunctionName + " have a return."
     *
     * "IfElseBranch requires bool condition not " + condType + "."
     * "WhileLoop requires bool condition not " + condType + "."
     *
     * "Function " + currentFunctionName + " returns " + currentReturnType + " not " + retType + "."
     *
     * "Variable " + varName + " has invalid type " + varType + "."
     * "Array " + arrayName + " has invalid base type " + baseType + "."
     */

    public TypeChecker()
    {
        typeMap = new HashMap<Command, Type>();
        functionMap = new HashMap<String, Command>();
        errorBuffer = new StringBuffer();
    }

    private void reportError(int lineNum, int charPos, String message)
    {
        errorBuffer.append("TypeError(" + lineNum + "," + charPos + ")");
        errorBuffer.append("[" + message + "]" + "\n");
    }

    private void put(Command node, Type type)
    {
        if (type instanceof ErrorType) {
            reportError(node.lineNumber(), node.charPosition(), ((ErrorType)type).getMessage());
        }
        typeMap.put(node, type);
    }
    
    private void putFunction(String func, Command node)
    {
        functionMap.put(func, node);
    }
    
    private void getFunction(String func)
    {
        functionMap.get(func);
    }
    
    public Type getType(Command node)
    {
        return typeMap.get(node);
    }
    
    public boolean check(Command ast)
    {
        ast.accept(this);
        return !hasError();
    }
    
    public boolean hasError()
    {
        return errorBuffer.length() != 0;
    }
    
    public String errorReport()
    {
        return errorBuffer.toString();
    }

    @Override
    public void visit(ExpressionList node) {
    	TypeList types = new TypeList();
    	
    	for (Expression e : node) {
    		check((Command) e);
    		Type type = getType((Command) e);
    		types.append(type);
    	}
    	
    	put(node, types);
    }

    @Override
    public void visit(DeclarationList node) {
    	for (Declaration d : node) {
    		check((Command) d);
	    	Type type = getType((Command) d);
			put(node, type);
    	}
    }

    @Override
    public void visit(StatementList node) {
    	for (Statement s : node) {
    		check((Command) s);
    		
    		if (s instanceof ast.Return) {
    			Type type = getType((Command) s);
    			
    			if (type != null) {
    				put(node, type);
    			}
    		}
    	}
    	
    	if (getType(node) == null && returnType == null) {
    		put(node, new VoidType());
    	}
    }

    @Override
    public void visit(AddressOf node) {
    	//put(node, new AddressType(node.symbol().type()));
    	put(node, node.symbol().type());
    }

    @Override
    public void visit(LiteralBool node) {
    	put(node, new BoolType());
    }

    @Override
    public void visit(LiteralFloat node) {
    	put(node, new FloatType());
    }

    @Override
    public void visit(LiteralInt node) {
    	put(node, new IntType());
    }

    @Override
    public void visit(VariableDeclaration node) {
    	Symbol sym = node.symbol();
    	Type varType = sym.type();
    	
    	if (varType instanceof VoidType) {
    		put(node, new ErrorType("Variable " + sym.name() + " has invalid type " + varType + "."));
    	} else if (varType instanceof ErrorType) {
    		put(node, new ErrorType("Variable " + sym.name() + " has invalid type " + varType + "."));
    	} else {
    		put(node, node.symbol().type());
    	}
    }

    @Override
    public void visit(ArrayDeclaration node) {
        throw new RuntimeException("Implement this");
    }

    @Override
    public void visit(FunctionDefinition node) {
    	Symbol sym = node.symbol();
    	Type symType = sym.type();
    	returnType = null;
    	TypeList types = new TypeList();
    		
    	List<Symbol> parameters = node.arguments();
    	for (int i = 0; i < parameters.size(); i++) {
    		if (parameters.get(i).type() instanceof VoidType) {
    			put(node, new ErrorType("Function " + sym.name() + " has a void argument in position " + i));
    		} else if (parameters.get(i).type() instanceof ErrorType) {
    			put(node, new ErrorType("Function " + sym.name() + " has an error argument in position " + i + ": " + ((ErrorType)parameters.get(i).type()).getMessage()));
    		}
			types.append(parameters.get(i).type());
    	}
    	
    	// if main does not return void, error.
    	if (sym.name().equals("main") &&
    			!(sym.type() instanceof VoidType)) {
    		put(node, new ErrorType("Function main has invalid signature."));
    	} else {
    		check(node.body());
    		
    		Command returnNode = (Command)node.body();
    		Type tempType = getType(returnNode);
    		
    		if (tempType == null) {
    			tempType = returnType;
    		}
        	
        	// check what was returned by function
        	if (!symType.equivalent(tempType)) {
        		put(returnNode, new ErrorType("Function " + sym.name() + " returns " + symType + " not " + tempType + "."));
        	} else {
        		put(node, new FuncType(types, symType));
        		putFunction(sym.name(), node);
        	}
    	}
    }

    @Override
    public void visit(Comparison node) {
    	check((Command) node.leftSide());
    	check((Command) node.rightSide());
    	
    	Type left = getType((Command) node.leftSide());
    	Type right = getType((Command) node.rightSide());
    	
    	put(node, left.compare(right));
    }
    
    @Override
    public void visit(Addition node) {
    	check((Command) node.leftSide());
    	check((Command) node.rightSide());
    	
    	Type left = getType((Command) node.leftSide());
    	Type right = getType((Command) node.rightSide());

    	put(node, left.add(right));
    }
    
    @Override
    public void visit(Subtraction node) {
    	check((Command) node.leftSide());
    	check((Command) node.rightSide());
    	
    	Type left = getType((Command) node.leftSide());
    	Type right = getType((Command) node.rightSide());

    	put(node, left.sub(right));
    }
    
    @Override
    public void visit(Multiplication node) {
    	check((Command) node.leftSide());
    	check((Command) node.rightSide());
    	
    	Type left = getType((Command) node.leftSide());
    	Type right = getType((Command) node.rightSide());

    	put(node, left.mul(right));
    }
    
    @Override
    public void visit(Division node) {
    	check((Command) node.leftSide());
    	check((Command) node.rightSide());
    	
    	Type left = getType((Command) node.leftSide());
    	Type right = getType((Command) node.rightSide());

    	put(node, left.div(right));
    }
    
    @Override
    public void visit(LogicalAnd node) {
    	check((Command) node.leftSide());
    	check((Command) node.rightSide());
    	
    	Type left = getType((Command) node.leftSide());
    	Type right = getType((Command) node.rightSide());

    	put(node, left.and(right));
    }

    @Override
    public void visit(LogicalOr node) {
    	check((Command) node.leftSide());
    	check((Command) node.rightSide());
    	
    	Type left = getType((Command) node.leftSide());
    	Type right = getType((Command) node.rightSide());

    	put(node, left.or(right));
    }

    @Override
    public void visit(LogicalNot node) {
    	check((Command) node.expression());	
    	Type type = getType((Command) node.expression());

    	put(node, type.not());
    }
    
    @Override
    public void visit(Dereference node) {
    	//node.expression().accept(this);
    	check((Command) node.expression());
    	put(node, getType((Command)node.expression()));
    }

    @Override
    public void visit(Index node) {
        throw new RuntimeException("Implement this");
    }

    @Override
    public void visit(Assignment node) {
    	check((Command) node.destination());
    	check((Command) node.source());
    	
    	Type destination = new AddressType(getType((Command) node.destination()));
    	Type source = getType((Command) node.source());
    	
    	put(node, destination.assign(source));
    }

    @Override
    public void visit(Call node) {
    	Symbol sym = node.function();
    	String funcName = sym.name();
    	Type retType = sym.type();
    	
    	FunctionDefinition matchingFunction = null;
    	TypeList functionArguments = new TypeList();
    	Type matchingFunctionType = null;
    	
    	switch (funcName) {
    		case "readInt":
    			matchingFunctionType = new IntType();
    			break;
    		case "readFloat":
    			matchingFunctionType = new FloatType();
    			break;
    		case "printBool":
    			functionArguments.append(new BoolType());
    			matchingFunctionType = new VoidType();
    			break;
    		case "printInt":
    			functionArguments.append(new IntType());
    			matchingFunctionType = new VoidType();
    			break;
    		case "printFloat":
    			functionArguments.append(new FloatType());
    			matchingFunctionType = new VoidType();
    			break;
    		case "println":
    			matchingFunctionType = new VoidType();
    			break;
    		default:
    			matchingFunction = (FunctionDefinition) functionMap.get(funcName);	
    			
    			if (matchingFunction == null) {
    	    		return;
    	    	}    			
    			
    			List<Symbol> parameters = matchingFunction.arguments();
    	    	for (int i = 0; i < parameters.size(); i++) {
    	    		if (parameters.get(i).type() instanceof VoidType) {
    	    			put(node, new ErrorType("Function " + sym.name() + " has a void argument in position " + i));
    	    		} else if (parameters.get(i).type() instanceof ErrorType) {
    	    			put(node, new ErrorType("Function " + sym.name() + " has an error argument in position " + i + ": " + ((ErrorType)parameters.get(i).type()).getMessage()));
    	    		};
    	    		functionArguments.append(parameters.get(i).type());
    	    	}
    	    	
    	    	matchingFunctionType = matchingFunction.function().type();
    	}
    
    	check(node.arguments());
    	TypeList callArguments = (TypeList)getType(node.arguments());
		    	
    	put(node, new FuncType(functionArguments, matchingFunctionType).call(callArguments));
    }

    @Override
    public void visit(IfElseBranch node) {
    	check((Command) node.condition());
    	Type conditionType = getType((Command) node.condition());
    	
    	if (!conditionType.equivalent(new BoolType())) {
    		put(node, new ErrorType("IfElseBranch requires bool condition not " + conditionType + "."));
    	}
    	
    	check((Command) node.thenBlock());
    	Type ifType = getType((Command) node.thenBlock());
    	
    	check((Command) node.elseBlock());
    	Type elseType = getType((Command) node.elseBlock());
    	
    	put(node, conditionType);
    }

    @Override
    public void visit(WhileLoop node) {
    	check((Command) node.condition());
    	Type type = getType((Command) node.condition());
    	
    	if (type.equivalent(new BoolType())) {
    		put(node, type);
    	} else {
    		put(node, new ErrorType("WhileLoop requires bool condition not " + type + "."));
    	}
    }

    @Override
    public void visit(Return node) {
    	check((Command) node.argument());
    	Type type = getType((Command) node.argument());
    	
    	if (type instanceof ErrorType) {
    		returnType = type;
    	} else {
    		put(node, type);
    	}
    }

    @Override
    public void visit(ast.Error node) {
        put(node, new ErrorType(node.message()));
    }
}
