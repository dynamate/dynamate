package library.impl.jruby;

import java.lang.invoke.MethodType;

import libraray.methodselection.MethodSelectionStrategy;
import libraray.methodselection.ObjectGraphIteratingHistory;
import libraray.methodselection.SimplifiedMethodHandle;
import library.guarding.GuardStrategy;
import library.invalidator.GlobalInvalidator;

import org.jruby.RubyClass;
import org.jruby.RubyModule;
import org.jruby.ast.executable.AbstractScript;
import org.jruby.internal.runtime.methods.AttrReaderMethod;
import org.jruby.internal.runtime.methods.AttrWriterMethod;
import org.jruby.internal.runtime.methods.CallConfiguration;
import org.jruby.internal.runtime.methods.DynamicMethod;
import org.jruby.internal.runtime.methods.DynamicMethod.NativeCall;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.util.JavaNameMangler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JRubyMethodSelectionStrategy implements MethodSelectionStrategy<RubyClass, IRubyObject, DynamicMethod> {
	private final Logger logger = LoggerFactory.getLogger(JRubyMethodSelectionStrategy.class);

	private GuardStrategy<RubyClass> guardStrategy = new GuardStrategy<RubyClass>() {

		@Override
		public boolean test(RubyClass savedClass, RubyClass receiverClass) {
			return savedClass == receiverClass;
		}

		@Override
		public RubyClass buildPartialKey(MethodType type, int index, Object argument) {
			return ((IRubyObject) argument).getMetaClass();
		}

		@Override
		public library.guarding.GuardStrategy.GuardType getGuardType() {
			return GuardType.RECEIVER_GUARD;
		}
	};

	@Override
	public SimplifiedMethodHandle<DynamicMethod> selectMethod(ObjectGraphIteratingHistory<RubyClass, IRubyObject> resolveHistory, MethodType callType, RubyClass receiverType,
			String method, Object[] arguments) {
		String[] identifierParts = method.split(":");

		if (identifierParts.length > 1) {
			method = JavaNameMangler.demangleMethodName(identifierParts[1]);
		}

		DynamicMethod dynamicMethod = receiverType.getMethods().get(method);

		// check of current receiver class contains the method
		if (dynamicMethod != null) {
			// use null guard for function invocations
			GuardStrategy<RubyClass> guardStrategy = !identifierParts[0].startsWith("call") ? null : this.guardStrategy;

			// try to find the native direct path
			NativeCall nativeCall = dynamicMethod.getNativeCall();

			if (nativeCall == null) {
				// method is a setter/getter
				if (dynamicMethod instanceof AttrReaderMethod) {
					return this.getGetterHandle(receiverType, dynamicMethod);
				} else if (dynamicMethod instanceof AttrWriterMethod) {
					return this.getSetterHandle(receiverType, dynamicMethod);
				}

				// no direct path available, must use indiraction
				return this.buildIndirectionHandle(dynamicMethod, receiverType, callType, method, guardStrategy);
			} else if (dynamicMethod.getCallConfig() != CallConfiguration.FrameNoneScopeNone) {
				// cannot use direct path, because frame manipulation required
				return this.buildIndirectionHandle(dynamicMethod, receiverType, callType, method, guardStrategy);
			} else {
				// construct handle to native target method
				SimplifiedMethodHandle<DynamicMethod> handle = new SimplifiedMethodHandle<DynamicMethod>(nativeCall.getNativeTarget(), nativeCall.getNativeName(),
						nativeCall.isStatic(), nativeCall.getNativeReturn(), nativeCall.getNativeSignature());

				handle.setConcreteMethod(dynamicMethod);
				handle.setGuardStrategy(guardStrategy);

				// drop ThreadContext iff it is not part of the method's type
				if (this.mustDropThreadContext(nativeCall.getNativeSignature())) {
					handle.drop(0);
				} else if (nativeCall.isStatic() && AbstractScript.class.isAssignableFrom(nativeCall.getNativeTarget())) {
					// retrieve the script instance which contains the native target method
					try {
						handle.bind(0, dynamicMethod.getClass().getMethod("getScriptObject").invoke(dynamicMethod));
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}

				return handle;
			}
		}

		// method not contained in current receiver's class, traverse object graph
		return null;
	}

	private SimplifiedMethodHandle<DynamicMethod> getGetterHandle(RubyClass receiverType, DynamicMethod dynamicMethod) {
		AttrReaderMethod attrReader = (AttrReaderMethod) dynamicMethod;
		return this.getXetterHandle(receiverType, dynamicMethod, attrReader.getVariableName(), "getVariable", MethodType.methodType(Object.class, int.class));
	}

	private SimplifiedMethodHandle<DynamicMethod> getSetterHandle(RubyClass receiverType, DynamicMethod dynamicMethod) {
		AttrWriterMethod attrWriter = (AttrWriterMethod) dynamicMethod;
		return this.getXetterHandle(receiverType, dynamicMethod, attrWriter.getVariableName(), "setVariable", MethodType.methodType(void.class, int.class, Object.class));
	}

	private SimplifiedMethodHandle<DynamicMethod> getXetterHandle(RubyClass receiverType, DynamicMethod dynamicMethod, String varName, String xetterType, MethodType type) {
		RubyClass.VariableAccessor accessor = receiverType.getRealClass().getVariableAccessorForWrite(varName);
		SimplifiedMethodHandle<DynamicMethod> handle = new SimplifiedMethodHandle<DynamicMethod>(IRubyObject.class, xetterType, false, type);
		handle.drop(0);
		handle.bind(1, accessor.getIndex());
		handle.setGuardStrategy(this.guardStrategy);
		handle.setInvalidator(false, GlobalInvalidator.getInvalidator(receiverType));
		return handle;
	}

	private boolean mustDropThreadContext(Class<?>[] args) {
		for (Class<?> arg : args) {
			if (arg.equals(ThreadContext.class)) {
				return false;
			}
		}

		return true;
	}

	private SimplifiedMethodHandle<DynamicMethod> buildIndirectionHandle(DynamicMethod dynamicMethod, RubyClass receiverType, MethodType methodType, String methodName,
			GuardStrategy<?> guardStrategy) {
		MethodType modifiedType = methodType.dropParameterTypes(1, 2).insertParameterTypes(2, RubyModule.class, String.class);

		// invoke the functional class instance capable of adapting the method call
		SimplifiedMethodHandle<DynamicMethod> handle = new SimplifiedMethodHandle<DynamicMethod>(dynamicMethod.getClass(), "call", false, modifiedType);
		handle.bind(0, dynamicMethod);
		handle.bind(3, dynamicMethod.getImplementationClass());
		handle.bind(4, methodName);
		handle.setGuardStrategy(guardStrategy);
		handle.setInvalidator(false, GlobalInvalidator.getInvalidator(receiverType));
		return handle;
	}

	@Override
	public SimplifiedMethodHandle<DynamicMethod> adapt(RubyClass receiverType, MethodType callType, SimplifiedMethodHandle<DynamicMethod> handle) {
		// Dynamate adaptatatio failed, use indiraction
		return this.buildIndirectionHandle(handle.getConcreteMethod(), receiverType, callType, handle.getMethodName(), handle.getGuardStrategy());
	}
}