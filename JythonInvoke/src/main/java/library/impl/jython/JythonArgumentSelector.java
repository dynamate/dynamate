package library.impl.jython;

import libraray.adaptation.ArgumentSelector;

public class JythonArgumentSelector implements ArgumentSelector {

	@Override
	public int absoluteReceiverIndex() {
		return 1;
	}

	@Override
	public int[] absoluteDroppableArgumentsIndices() {
		return null;
	}

	@Override
	public int absoluteArgumentsStartIndex() {
		return 1;
	}
}
