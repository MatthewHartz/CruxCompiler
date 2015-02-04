package crux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable {
    public Map<String, Symbol> table;
    public SymbolTable parent;
    public int depth;
    
    public SymbolTable()
    {
        table = new LinkedHashMap<String, Symbol>();
        parent = null;
        depth = 0;
    }
    
    public Symbol lookup(String name) throws SymbolNotFoundError
    {    
    	SymbolTable s = this;
    	
        do {
        	Symbol value = s.table.get(name);
        	
        	if (value != null) {
        		return value;
        	} else {
        		s = s.parent;
        	}
        } while (s != null);
        
    	   	
        throw new SymbolNotFoundError(name);
    }
       
    public Symbol insert(String name) throws RedeclarationError
    {
        Symbol value = table.get(name);
        if (value != null) {
        	throw new RedeclarationError(value);
        } else {
        	return table.put(name, new Symbol(name));
        }
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        if (parent != null)
            sb.append(parent.toString());
        
        String indent = new String();
        for (int i = 0; i < depth; i++) {
            indent += "  ";
        }
        
        for (Symbol s : table.values())
        {
            sb.append(indent + s.toString() + "\n");
        }
        return sb.toString();
    }
}

class SymbolNotFoundError extends Error
{
    private static final long serialVersionUID = 1L;
    private String name;
    
    SymbolNotFoundError(String name)
    {
        this.name = name;
    }
    
    public String name()
    {
        return name;
    }
}

class RedeclarationError extends Error
{
    private static final long serialVersionUID = 1L;

    public RedeclarationError(Symbol sym)
    {
        super("Symbol " + sym + " being redeclared.");
    }
}
