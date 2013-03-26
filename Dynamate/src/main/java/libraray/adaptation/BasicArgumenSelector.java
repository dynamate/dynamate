package libraray.adaptation;

public class BasicArgumenSelector implements ArgumentSelector {

	@Override
	public int absoluteReceiverIndex() {
		return 0;
	}

	@Override
	public int absoluteArgumentsStartIndex() {
		return 0;
	}

	@Override
	public int[] absoluteDroppableArgumentsIndices() {
		return null;
	}
}
