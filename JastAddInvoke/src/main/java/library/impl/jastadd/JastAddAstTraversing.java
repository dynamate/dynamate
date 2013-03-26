package library.impl.jastadd;

import libraray.methodselection.ObjectGraphIterator;
import library.Pair;
import AST.ASTNode;

public class JastAddAstTraversing implements ObjectGraphIterator<Class<?>, ASTNode> {

	@Override
	public Pair<Class<?>, ASTNode> next(Class<?> type, ASTNode node) {
		return new Pair<Class<?>, ASTNode>(node.getParent().getClass(), node.getParent());
	}

	@Override
	public boolean hasNext(Class<?> type, ASTNode object) {
		return object.getParent() != null;
	}
}
