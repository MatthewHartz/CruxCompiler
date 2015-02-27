package ast;

import java.util.List;

import crux.Symbol;

public class ArrayDeclaration extends Command implements Declaration, Statement {
	
	private Symbol symbol;
	private List<Integer> extents;
	
	public ArrayDeclaration(int lineNum, int charPos, Symbol symbol, List<Integer> extents)
	{
		super(lineNum, charPos);
		this.symbol = symbol;
		this.extents = extents;
	}

	public List<Integer> extents() {
		return extents;
	}
	
	public Integer GetExtent(int index) {
		return extents.get(index);
	}
	
	@Override
	public Symbol symbol() {
		return symbol;
	}
	
	@Override
	public String toString() {
		return super.toString() + "[" + symbol.toString() + "]";
	}

	@Override
	public void accept(CommandVisitor visitor) {
		visitor.visit(this);
	}

}
