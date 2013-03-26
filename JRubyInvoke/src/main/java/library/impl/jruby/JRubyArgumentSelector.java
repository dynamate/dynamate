package library.impl.jruby;

import libraray.adaptation.ArgumentSelector;

public class JRubyArgumentSelector implements ArgumentSelector {
	private final static int[] droppableArgumentsIndices = new int[] { 1 };

	@Override
	public int absoluteReceiverIndex() {
		return 2;
	}

	@Override
	public int[] absoluteDroppableArgumentsIndices() {
		return droppableArgumentsIndices;
	}

	@Override
	public int absoluteArgumentsStartIndex() {
		return 1;
	}
}
