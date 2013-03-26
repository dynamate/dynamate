package libraray.methodselection;

import java.lang.invoke.MethodHandle;

import library.Pair;
import library.guarding.GuardStrategy;
import library.invalidator.Invalidator;

public class ResolvedMethodHandle {
	private boolean permutationNeeded;

	private MethodHandle methodHandle;

	private GuardStrategy<?> guardStrategy;

	private Pair<Boolean, Invalidator> invalidator;

	public ResolvedMethodHandle(boolean permutationNeeded, MethodHandle methodHandle, GuardStrategy<?> guardStrategy, Pair<Boolean, Invalidator> invalidator) {
		this.permutationNeeded = permutationNeeded;
		this.methodHandle = methodHandle;
		this.guardStrategy = guardStrategy;
		this.invalidator = invalidator;
	}

	public boolean isPermutationNeeded() {
		return this.permutationNeeded;
	}

	public MethodHandle getMethodHandle() {
		return this.methodHandle;
	}

	public GuardStrategy<?> getGuardStrategy() {
		return this.guardStrategy;
	}

	public Pair<Boolean, Invalidator> getInvalidator() {
		return this.invalidator;
	}

	public void setInvalidator(Pair<Boolean, Invalidator> invalidator) {
		this.invalidator = invalidator;
	}
}
