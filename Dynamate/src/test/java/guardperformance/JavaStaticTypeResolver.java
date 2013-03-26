package guardperformance;

import java.lang.invoke.MethodType;

import library.TypeResolver;

public class JavaStaticTypeResolver implements TypeResolver<Class<?>, Object> {

	@Override
	public Class<?> resolve(MethodType type, int index, Object object) {
		return type.parameterType(index);
	}
}