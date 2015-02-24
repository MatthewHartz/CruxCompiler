package types;

public class BoolType extends Type {
    
    public BoolType()
    {
    }
    
    @Override
    public String toString()
    {
        return "bool";
    }
    
    @Override
    public Type and(Type that)
    {
    	if (!(that instanceof BoolType))
            return super.add(that);
        return new BoolType();
    }
    
    @Override
    public Type or(Type that)
    {
    	if (!(that instanceof BoolType))
            return super.add(that);
        return new BoolType();
    }
    
    @Override
    public Type not()
    {
        return new BoolType();
    }
    
    @Override
    public Type compare(Type that)
    {
    	if (!(that instanceof BoolType))
            return super.add(that);
        return new BoolType();
    }

    @Override
    public boolean equivalent(Type that)
    {
        if (that == null)
            return false;
        if (!(that instanceof BoolType))
            return false;
        
        return true;
    }
}    
