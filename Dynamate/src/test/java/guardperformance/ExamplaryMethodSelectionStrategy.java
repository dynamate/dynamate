package guardperformance;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

import libraray.methodselection.MethodSelectionStrategy;
import libraray.methodselection.ObjectGraphIteratingHistory;
import libraray.methodselection.SimplifiedMethodHandle;
import library.guarding.GuardStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExamplaryMethodSelectionStrategy implements MethodSelectionStrategy<Class<?>, Object, Method> {
	private final static Logger logger = LoggerFactory.getLogger(ExamplaryMethodSelectionStrategy.class);

	private GuardStrategy<Class<?>> guardStrategy = new GuardStrategy<Class<?>>() {

		@Override
		public Class<?> buildPartialKey(MethodType type, int index, Object argument) {
			return argument.getClass();
		}

		@Override
		public boolean test(Class<?> saved, Class<?> argument) {
			return saved.equals(argument);
		}

		@Override
		public library.guarding.GuardStrategy.GuardType getGuardType() {
			return GuardType.RECEIVER_GUARD;
		}
	};

	@Override
	public SimplifiedMethodHandle<Method> selectMethod(ObjectGraphIteratingHistory<Class<?>, Object> resolveHistory, MethodType callType, Class<?> clazz, String method,
			Object[] arguments) {

		SimplifiedMethodHandle<Method> simplifiedMethodHandle = new SimplifiedMethodHandle<Method>(A.class, "m", true, callType);
		simplifiedMethodHandle.setGuardStrategy(this.guardStrategy);
		return simplifiedMethodHandle;
	}

	@Override
	public SimplifiedMethodHandle<Method> adapt(Class<?> receiverType, MethodType callType, SimplifiedMethodHandle<Method> methodHandle) {
		return null;
	}
}