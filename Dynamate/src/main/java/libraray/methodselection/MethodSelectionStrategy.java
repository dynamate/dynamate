package libraray.methodselection;

import java.lang.invoke.MethodType;

public interface MethodSelectionStrategy<T, O, M> {

	public SimplifiedMethodHandle<M> selectMethod(ObjectGraphIteratingHistory<T, O> resolveHistory, MethodType callType, T type, String method, Object[] arguments);

	public SimplifiedMethodHandle<M> adapt(T receiverType, MethodType callType, SimplifiedMethodHandle<M> methodHandle);
}
