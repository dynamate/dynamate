package library.impl.jastadd;

import java.util.LinkedList;

import libraray.methodselection.ArgumentFilter;
import libraray.methodselection.ObjectGraphIteratingHistory;
import library.Pair;
import AST.ASTNode;

public class JastAddArgumentFilter<T, O> implements ArgumentFilter {

	private JastAddAstTraversing astTraversing;
	private ObjectGraphIteratingHistory<T, O> history;

	public JastAddArgumentFilter(JastAddAstTraversing astTraversing, ObjectGraphIteratingHistory<T, O> history) {
		this.astTraversing = astTraversing;
		this.history = history;
	}

	@Override
	public Object[] filter(Object[] arguments) {
		Object[] modifiedArguments = new Object[arguments.length];
		System.arraycopy(arguments, 0, modifiedArguments, 0, arguments.length);

		Class<?> currentType = arguments[0].getClass();
		ASTNode currentObject = (ASTNode) arguments[0];

		LinkedList<Object> list = new LinkedList<>();
		list.addFirst(currentObject);

		// iterate the object graph according to the object graph history of the resolved call
		for (int i = 0; i < (this.history.size() - 1); i++) {
			Pair<Class<?>, ASTNode> next = this.astTraversing.next(currentType, currentObject);
			currentType = next.getX();
			currentObject = next.getY();
			list.addFirst(currentObject);
		}

		// change original node to target node containing the attribute method
		modifiedArguments[0] = list.get(0);

		// change 'caller' parameter if more than one intermediate node between original node and target node
		if (list.size() > 1) {
			modifiedArguments[1] = list.get(1);
		}

		// change 'child' parameter if more than two intermediate node between original node and target node
		if (list.size() > 2) {
			modifiedArguments[2] = list.get(2);
		}

		return modifiedArguments;
	}
}
