package libraray.methodselection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.util.LinkedList;
import java.util.List;

import library.Pair;
import library.guarding.GuardStrategy;
import library.invalidator.Invalidator;
import utils.ListUtils;

public class SimplifiedMethodHandle<M> {
	private MethodType methodType;
	private Class<?> implementationClass;
	private String methodName;
	private boolean staticMethod;

	private M concreteMethod;

	private List<Pair<Integer, Object>> bindings = new LinkedList<>();
	private List<Integer> drops = new LinkedList<>();

	private GuardStrategy<?> guardStrategy;
	private Pair<Boolean, Invalidator> invalidator;
	private ArgumentFilter argumentFilter;

	public SimplifiedMethodHandle(Class<?> clazz, String name, boolean staticMethod, MethodType methodType) {
		this.implementationClass = clazz;
		this.methodName = name;
		this.staticMethod = staticMethod;
		this.methodType = methodType;
	}

	public SimplifiedMethodHandle(Class<?> clazz, String name, boolean staticMethod, Class<?> returnType, Class<?>[] parameterTypes) {
		this(clazz, name, staticMethod, MethodType.methodType(returnType, parameterTypes));
	}

	public SimplifiedMethodHandle(Class<?> clazz, String name, boolean staticMethod, Class<?> returnType, List<Class<?>> parameterTypes) {
		this(clazz, name, staticMethod, MethodType.methodType(returnType, parameterTypes.toArray(new Class<?>[parameterTypes.size()])));
	}

	public MethodType getMethodType() {
		return this.methodType;
	}

	public void setMethodType(MethodType methodType) {
		this.methodType = methodType;
	}

	public Class<?> getImplementationClass() {
		return this.implementationClass;
	}

	public void setImplementationClass(Class<?> clazz) {
		this.implementationClass = clazz;
	}

	public String getMethodName() {
		return this.methodName;
	}

	public void setMethodName(String name) {
		this.methodName = name;
	}

	@Override
	public String toString() {
		return "IndirectMethodHandle [methodType=" + this.methodType + ", clazz=" + this.implementationClass + ", name=" + this.methodName + "]";
	}

	public boolean isStaticMethod() {
		return this.staticMethod;
	}

	public void setStaticMethod(boolean staticMethod) {
		this.staticMethod = staticMethod;
	}

	public ResolvedMethodHandle resolveMethod(Lookup lookup) throws NoSuchMethodException, IllegalAccessException {
		boolean permutationNeeded = !this.isStaticMethod();

		MethodHandle targetHandle = null;

		if (this.isStaticMethod()) {
			targetHandle = lookup.findStatic(this.getImplementationClass(), this.getMethodName(), this.getMethodType());
		} else {
			targetHandle = lookup.findVirtual(this.getImplementationClass(), this.getMethodName(), this.getMethodType());
		}

		if (this.argumentFilter != null) {
			MethodHandle filter = lookup.findVirtual(ArgumentFilter.class, "filter", MethodType.methodType(Object[].class, Object[].class));
			filter = filter.bindTo(this.argumentFilter);
			filter = filter.asCollector(Object[].class, targetHandle.type().parameterCount()).asType(
					targetHandle.type().changeReturnType(Object[].class).changeParameterType(0, Object.class));

			MethodHandle modifiedTargetHandle = targetHandle.asSpreader(Object[].class, targetHandle.type().parameterCount());

			modifiedTargetHandle = MethodHandles.dropArguments(modifiedTargetHandle, 1, targetHandle.type().changeParameterType(0, Object.class).parameterList());

			targetHandle = MethodHandles.foldArguments(modifiedTargetHandle, filter);
		}

		int i = 0;
		for (Pair<Integer, Object> binding : this.bindings) {
			if (binding.getY() instanceof SingleArgumentFilter) {
				MethodType type = targetHandle.type();
				MethodType newType = targetHandle.type().dropParameterTypes(binding.getX() - i, (binding.getX() - i) + 1);

				targetHandle = this.moveParameter(targetHandle, 0, binding.getX() - i);

				MethodHandle combiner = lookup.findVirtual(SingleArgumentFilter.class, "perform", MethodType.methodType(Object.class, Object[].class)).bindTo(binding.getY());
				combiner = combiner.asCollector(Object[].class, newType.parameterCount());
				combiner = combiner.asType(newType.changeReturnType(type.parameterType(binding.getX() - i)));

				targetHandle = MethodHandles.foldArguments(targetHandle, combiner);
			} else {
				targetHandle = MethodHandles.insertArguments(targetHandle, binding.getX() - i, binding.getY());
			}

			permutationNeeded = false;
			i++;
		}

		for (Integer j : this.drops) {
			permutationNeeded = false;
			targetHandle = MethodHandles.dropArguments(targetHandle, j, Object.class);
		}

		return new ResolvedMethodHandle(permutationNeeded, targetHandle, this.guardStrategy, this.invalidator);
	}

	private MethodHandle moveParameter(MethodHandle handle, int to, int from) {
		MethodType type = handle.type();
		List<Integer> reorder = ListUtils.range(0, handle.type().parameterCount() - 1);

		reorder.remove(to);
		reorder.add(from, to);

		MethodType permutedType = type.dropParameterTypes(from, from + 1).insertParameterTypes(to, type.parameterType(from));
		return MethodHandles.permuteArguments(handle, permutedType, ListUtils.toIntArray(reorder));
	}

	public void bind(int index, Object object) {
		this.bindings.add(new Pair<Integer, Object>(index, object));
	}

	public void setConcreteMethod(M concreteMethod) {
		this.concreteMethod = concreteMethod;
	}

	public M getConcreteMethod() {
		return this.concreteMethod;
	}

	public void setGuardStrategy(GuardStrategy<?> guardStrategy) {
		this.guardStrategy = guardStrategy;
	}

	public GuardStrategy<?> getGuardStrategy() {
		return this.guardStrategy;
	}

	public void drop(int i) {
		this.drops.add(i);
	}

	public Pair<Boolean, Invalidator> getInvalidator() {
		return this.invalidator;
	}

	public void setInvalidator(boolean skipGuardOnDefaultPath, Invalidator invalidator) {
		this.invalidator = new Pair<>(skipGuardOnDefaultPath, invalidator);
	}

	public void setArgumentFilter(ArgumentFilter argumentFilter) {
		this.argumentFilter = argumentFilter;
	}
}
