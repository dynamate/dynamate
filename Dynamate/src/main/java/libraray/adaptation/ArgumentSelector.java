package libraray.adaptation;

public interface ArgumentSelector {
	public int absoluteReceiverIndex();

	public int absoluteArgumentsStartIndex();

	// public int argumentsEndIndex();

	public int[] absoluteDroppableArgumentsIndices();
}
