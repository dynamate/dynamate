package library.guarding;

import java.lang.invoke.MethodType;

public abstract class DefaultGuardStrategy<T> implements GuardStrategy<T> {

	@Override
	public T buildPartialKey(MethodType type, int index, Object argument) {
		return (T) argument;
	}

	@Override
	public boolean test(T saved, T argument) {
		return saved == argument;
	}

	@Override
	public library.guarding.GuardStrategy.GuardType getGuardType() {
		return GuardType.ARGUMENTS_GUARD;
	}
}
