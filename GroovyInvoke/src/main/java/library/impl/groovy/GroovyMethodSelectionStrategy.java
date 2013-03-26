package library.impl.groovy;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MetaClassImpl;
import groovy.lang.MetaMethod;
import groovy.lang.MetaObjectProtocol;

import java.lang.invoke.MethodType;
import java.util.Arrays;

import libraray.methodselection.MethodSelectionStrategy;
import libraray.methodselection.ObjectGraphIteratingHistory;
import libraray.methodselection.SimplifiedMethodHandle;
import library.guarding.GuardStrategy;

import org.codehaus.groovy.reflection.CachedMethod;

public class GroovyMethodSelectionStrategy implements MethodSelectionStrategy<MetaClass, Object, MetaMethod> {

	private GuardStrategy<Object> guardStrategy = new GuardStrategy<Object>() {

		@Override
		public Object buildPartialKey(MethodType type, int index, Object argument) {
			if (index == 0) {
				if (argument instanceof GroovyObject) {
					return ((GroovyObject) argument).getMetaClass();
				}
			}

			return argument.getClass();
		}

		@Override
		public boolean test(Object savedClass, Object argumentClass) {
			return savedClass == argumentClass;
		}

		@Override
		public library.guarding.GuardStrategy.GuardType getGuardType() {
			return GuardType.ARGUMENTS_GUARD;
		}
	};

	@Override
	public SimplifiedMethodHandle<MetaMethod> selectMethod(ObjectGraphIteratingHistory<MetaClass, Object> resolveHistory, MethodType callType, MetaClass metaClass,
			String methodName, Object[] arguments) {

		boolean isStatic = arguments[0] instanceof Class;

		MetaMethod metaMethod;

		if (metaClass instanceof MetaClassImpl) {
			MetaClassImpl mci = ((MetaClassImpl) metaClass);
			if (isStatic) {
				metaMethod = mci.retrieveStaticMethod(methodName, Arrays.copyOfRange(arguments, 1, arguments.length));
			} else {
				metaMethod = mci.getMethodWithCaching(metaClass.getTheClass(), methodName, Arrays.copyOfRange(arguments, 1, arguments.length), false);
			}
		} else {
			metaMethod = metaClass.getMetaMethod(methodName, Arrays.copyOfRange(arguments, 1, arguments.length));
		}

		SimplifiedMethodHandle<MetaMethod> directMethodHandle;

		if (metaMethod == null) {
			if (isStatic) {
				directMethodHandle = new SimplifiedMethodHandle<MetaMethod>(metaClass.getClass(), "invokeStaticMethod", false, MethodType.methodType(Object.class, Object.class,
						String.class, Object[].class));
			} else {
				directMethodHandle = new SimplifiedMethodHandle<MetaMethod>(MetaObjectProtocol.class, "invokeMethod", false, MethodType.methodType(Object.class, Object.class,
						String.class, Object[].class));
			}

			directMethodHandle.bind(0, metaClass);
			directMethodHandle.bind(2, methodName);
		} else if (metaMethod instanceof CachedMethod) {
			directMethodHandle = new SimplifiedMethodHandle<MetaMethod>(metaMethod.getDeclaringClass().getTheClass(), metaMethod.getName(), isStatic, metaMethod.getReturnType(),
					metaMethod.getNativeParameterTypes());
		} else {
			directMethodHandle = new SimplifiedMethodHandle<MetaMethod>(MetaMethod.class, "invoke", isStatic, MethodType.methodType(Object.class, Object.class, Object[].class));
			directMethodHandle.bind(0, metaMethod);
		}

		directMethodHandle.setGuardStrategy(this.guardStrategy);
		return directMethodHandle;
	}

	@Override
	public SimplifiedMethodHandle<MetaMethod> adapt(MetaClass receiverType, MethodType callType, SimplifiedMethodHandle<MetaMethod> methodHandle) {
		// TODO Auto-generated method stub
		return null;
	}
}