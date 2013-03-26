package library;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import libraray.adaptation.Adaptor;
import libraray.adaptation.ArgumentInjector;
import libraray.adaptation.ArgumentSelector;
import libraray.adaptation.BasicArgumenSelector;
import libraray.adaptation.BasicArgumentTypeConverter;
import libraray.methodselection.MethodSelectionStrategy;
import libraray.methodselection.ObjectGraphIteratingHistory;
import libraray.methodselection.ObjectGraphIterator;
import libraray.methodselection.ResolvedMethodHandle;
import libraray.methodselection.SimplifiedMethodHandle;
import library.guarding.ArgumentGuardBuilder;
import library.guarding.CachingStrategy;
import library.guarding.DefaultCachingStrategy;
import library.guarding.DefaultFallbackBuilder;
import library.guarding.FallbackBuilder;
import library.guarding.GuardBuilder;
import library.guarding.GuardStrategy.GuardType;
import library.guarding.ReceiverGuardBuilder;
import library.invalidator.Invalidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.SubstringMap;

public class DefaultMethodDispatcher<T, O, M> implements MethodDispatcher<T, O> {

	private final Logger logger = LoggerFactory.getLogger(DefaultMethodDispatcher.class);

	private MethodSelectionStrategy<T, O, M> methodSelectionStrategy;

	private TypeResolver<T, O> receiverTypeResolver = (TypeResolver<T, O>) new JavaRuntimeTypeResolver();

	public ObjectGraphIterator<T, O> objectGraphIterator = (ObjectGraphIterator<T, O>) new JavaClassHierrachyIterator();

	private ArgumentSelector argumentSelector = new BasicArgumenSelector();

	private ArgumentInjector argumentInjector;

	private BasicArgumentTypeConverter argumentTypeConverter;

	private CachingStrategy cachingStrategy = new DefaultCachingStrategy<>();

	private SubstringMap<ExceptionHandler<O>> exceptionHandlers = new SubstringMap<>();

	private Map<Pair<String, MethodType>, BoxedCallSite<?, ?>> globalHandleCache = new ConcurrentHashMap<>();

	private static final Lookup LOOKUP = MethodHandles.lookup();

	private boolean cachingEnabled = true;

	private int cascadingGuards = 2;

	protected boolean callSiteCachingEnabled;

	private static MethodHandle INITIAL_TARGET;

	static {
		try {
			INITIAL_TARGET = LOOKUP.findVirtual(DefaultMethodDispatcher.class, "resolveMethod",
					MethodType.methodType(Object.class, Lookup.class, Box.class, BoxedCallSite.class, String.class, MethodType.class, Object[].class));
		} catch (NoSuchMethodException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public BoxedCallSite dispatch(String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
		return this.dispatch(MethodHandles.lookup(), name, type);
	}

	@Override
	public BoxedCallSite<?, ?> dispatch(Lookup lookup, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {

		BoxedCallSite<?, ?> cachedCallSite = !this.callSiteCachingEnabled ? null : this.getCachedCallSite(name, type);
		BoxedCallSite<?, ?> callSite;

		if (cachedCallSite == null) {
			callSite = new BoxedCallSite<>(type, this.cascadingGuards);

			Box<MethodHandle> initialResolverMethod = new Box<MethodHandle>();

			MethodHandle initialTarget = MethodHandles.insertArguments(INITIAL_TARGET, 0, this, lookup, initialResolverMethod, callSite, name, type);
			initialTarget = initialTarget.asCollector(Object[].class, type.parameterCount());
			initialTarget = initialTarget.asType(type);

			initialResolverMethod.setValue(initialTarget);
			callSite.setTarget(initialTarget);
		} else {
			callSite = cachedCallSite.clone();
		}

		return callSite;
	}

	private Object resolveMethod(Lookup lookup, Box<MethodHandle> initialResolveMethod, BoxedCallSite callSite, String name, MethodType callType, O[] arguments) throws Throwable {
		this.logger.debug("Resolving method name: {}, type: {}, args: {}", new Object[] { name, callType, arguments });
		int receiverIndex = this.argumentSelector.absoluteReceiverIndex();

		T receiverType = this.determineReceiverType(callType, receiverIndex, arguments);
		Object result = null;

		// iterate object graph and select method
		try {
			O currentObject = arguments[receiverIndex];
			T currentType = receiverType;

			ObjectGraphIteratingHistory<T, O> resolveHistory = new ObjectGraphIteratingHistory<>();
			SimplifiedMethodHandle<M> simplifiedMethodHandle;

			do {
				simplifiedMethodHandle = this.methodSelectionStrategy.selectMethod(resolveHistory, callType, currentType, name, arguments);
				resolveHistory.addHistoryItem(new Pair<>(currentType, currentObject));

				if (simplifiedMethodHandle == null) {
					if (!this.objectGraphIterator.hasNext(currentType, currentObject)) {
						throw new RuntimeException("Method " + name + " not found in " + currentObject + ", " + currentType + " or any parent.");
					}

					Pair<T, O> pair = this.objectGraphIterator.next(currentType, currentObject);

					if ((pair.getX() == currentType) && (pair.getY() == currentObject)) {
						throw new RuntimeException("circle in object graph detected");
					}
					currentType = pair.getX();
					currentObject = pair.getY();
				}

			} while (simplifiedMethodHandle == null);

			// method finally selected, resolve it
			ResolvedMethodHandle resolvedMethodHandle = this.resolveMethodHandle(lookup, simplifiedMethodHandle);
			MethodHandle methodHandle = resolvedMethodHandle.getMethodHandle();

			// adapt method, so that droppable arguments are ommited for invocation
			Adaptor adaptor = new Adaptor(this.argumentSelector, this.argumentTypeConverter, this.argumentInjector);
			methodHandle = adaptor.adaptOnDroppableArguments(methodHandle);

			if ((methodHandle.type().parameterCount() > 1) && resolvedMethodHandle.isPermutationNeeded()) {
				methodHandle = this.permuteHandleByReceiver(methodHandle, callType, receiverIndex);
			}

			// check types and try automatic method adaptation
			MethodHandle targetHandle = adaptor.adaptMethodHandleToCallSite(methodHandle, callType);

			// if adaptation failed, request manual adaptatation by method selection strategy
			if (targetHandle == null) {
				simplifiedMethodHandle = this.methodSelectionStrategy.adapt(receiverType, callType, simplifiedMethodHandle);
				resolvedMethodHandle = this.resolveMethodHandle(lookup, simplifiedMethodHandle);
				targetHandle = resolvedMethodHandle.getMethodHandle();
				targetHandle = adaptor.adaptOnDroppableArguments(targetHandle);
			}

			targetHandle = this.wrapWithExceptionHandlers(name, callType, targetHandle);

			MethodHandle callTypedHandle = targetHandle.asType(callType);
			MethodHandle callSiteTarget = null;

			if (resolvedMethodHandle.getGuardStrategy() == null) {
				// no guard strategy attached
				callSiteTarget = callTypedHandle;
			} else {
				GuardBuilder guardBuilder = resolvedMethodHandle.getGuardStrategy().getGuardType() == GuardType.RECEIVER_GUARD ? new ReceiverGuardBuilder()
						: new ArgumentGuardBuilder();
				FallbackBuilder fallbackBuilder = new DefaultFallbackBuilder();

				// build composite key
				Object key = guardBuilder.buildCompositeKey(resolvedMethodHandle.getGuardStrategy(), callType, receiverIndex, arguments);

				// build guards and fallback based on composite key
				MethodHandle guardHandle = guardBuilder.buildGuard(resolvedMethodHandle.getGuardStrategy(), callType, receiverIndex, key);
				MethodHandle fallbackHandle;

				if (this.cachingEnabled) {
					fallbackHandle = fallbackBuilder.buildFallback(callSite, initialResolveMethod.getValue(), this.cachingStrategy, resolvedMethodHandle.getGuardStrategy(),
							guardBuilder, callType, receiverIndex);
					this.cachingStrategy.cache(callSite, key, callTypedHandle, arguments);
				} else {
					fallbackHandle = initialResolveMethod.getValue();
				}

				if (this.cascadingGuards > 0) {
					callSiteTarget = callSite.guard(guardHandle, callTypedHandle, fallbackHandle);
				} else {
					callSiteTarget = fallbackHandle;
				}
			}

			// install attached switchpoint
			callSiteTarget = this.installSwitchPoint(callSite, initialResolveMethod.getValue(), resolvedMethodHandle.getInvalidator(), callTypedHandle, callSiteTarget);

			if (this.callSiteCachingEnabled) {
				this.updateCachedCallsite(name, callType, callSite);
			}

			callSite.setTarget(callSiteTarget);

			// invoke method
			result = targetHandle.invokeWithArguments(arguments);
		} catch (IllegalAccessException | SecurityException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
			throw e;
		}

		return result;
	}

	private MethodHandle permuteHandleByReceiver(MethodHandle targetHandle, MethodType effectiveCallType, int absoluteReceiverIndex) {
		Class<?>[] permutationTypes = targetHandle.type().parameterArray();
		int[] permutationIndices = new int[targetHandle.type().parameterCount()];

		for (int i = 0; i < permutationIndices.length; i++) {
			permutationIndices[i] = i;
		}

		permutationIndices[0] = absoluteReceiverIndex;
		permutationIndices[absoluteReceiverIndex] = 0;

		Class<?> tmp = permutationTypes[0];
		permutationTypes[0] = permutationTypes[absoluteReceiverIndex];
		permutationTypes[absoluteReceiverIndex] = tmp;

		targetHandle = MethodHandles.permuteArguments(targetHandle, MethodType.methodType(targetHandle.type().returnType(), permutationTypes), permutationIndices);

		return targetHandle;
	}

	private MethodHandle installSwitchPoint(BoxedCallSite callSite, MethodHandle initialMethodResolver, Pair<Boolean, Invalidator> invalidator, MethodHandle targetMethod,
			MethodHandle target) {

		if (invalidator != null) {
			MethodHandle switchPointdefaultPath = invalidator.getX() ? targetMethod : target;
			MethodHandle switchPointFallbackPath = invalidator.getX() ? target : initialMethodResolver;
			target = callSite.switchpoint(invalidator.getY(), switchPointdefaultPath, switchPointFallbackPath);
		}

		return target;
	}

	private MethodHandle wrapWithExceptionHandlers(String name, MethodType type, MethodHandle targetHandle) throws NoSuchMethodException, IllegalAccessException {
		ExceptionHandler<O> handler = this.exceptionHandlers.get(name);

		if (handler != null) {
			MethodHandle target = MethodHandles.lookup().findVirtual(ExceptionHandler.class, "handle", MethodType.methodType(Object.class, Throwable.class, Object[].class))
					.bindTo(handler);

			targetHandle = MethodHandles.catchException(targetHandle, Throwable.class,
					target.asCollector(Object[].class, targetHandle.type().parameterCount()).asType(targetHandle.type().insertParameterTypes(0, Throwable.class)));
		}

		return targetHandle;
	}

	private void updateCachedCallsite(String name, MethodType type, BoxedCallSite callSite) {
		Pair<String, MethodType> pair = new Pair<>(name, type);
		this.globalHandleCache.put(pair, callSite);
	}

	@Override
	public BoxedCallSite<?, ?> getCachedCallSite(String name, MethodType type) {
		Pair<String, MethodType> pair = new Pair<>(name, type);
		return this.globalHandleCache.get(pair);
	}

	public static boolean isSame(Object a, Object b) {
		return a == b;
	}

	private T determineReceiverType(MethodType type, int receiverIndex, O[] arguments) {
		O receiver = arguments[receiverIndex];
		return this.receiverTypeResolver.resolve(type, receiverIndex, receiver);
	}

	public ResolvedMethodHandle resolveMethodHandle(Lookup lookup, SimplifiedMethodHandle<M> handle) throws NoSuchMethodException, IllegalAccessException {
		return handle.resolveMethod(lookup);
	}

	@Override
	public Map<String, ExceptionHandler<O>> getExceptionHandlers() {
		return this.exceptionHandlers;
	}

	@Override
	public void setCachingEnabled(boolean cachingEnabled) {
		this.cachingEnabled = cachingEnabled;
	}

	@Override
	public void setCascadingGuards(int cascadingGuards) {
		this.cascadingGuards = cascadingGuards;
	}

	@Override
	public void setMethodSelectionStrategy(MethodSelectionStrategy<T, O, ?> methodSelectionStrategy) {
		this.methodSelectionStrategy = (MethodSelectionStrategy<T, O, M>) methodSelectionStrategy;
	}

	@Override
	public void setObjectGraphIterator(ObjectGraphIterator<T, O> objectGraphIterator) {
		this.objectGraphIterator = objectGraphIterator;
	}

	@Override
	public void setTypeResolver(TypeResolver<T, O> typeResolver) {
		this.receiverTypeResolver = typeResolver;
	}

	@Override
	public void setArgumentTypeConverter(BasicArgumentTypeConverter argumentTypeConverter) {
		this.argumentTypeConverter = argumentTypeConverter;
	}

	@Override
	public void setArgumentInjector(ArgumentInjector argumentInjector) {
		this.argumentInjector = argumentInjector;
	}

	@Override
	public void setArgumentSelector(ArgumentSelector argumentSelector) {
		this.argumentSelector = argumentSelector;
	}
}
