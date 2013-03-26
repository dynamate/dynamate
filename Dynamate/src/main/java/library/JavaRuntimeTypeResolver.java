package library;

import java.lang.invoke.MethodType;

import library.TypeResolver;

public class JavaRuntimeTypeResolver implements TypeResolver<Class<?>, Object> {

	@Override
	public Class<?> resolve(MethodType type, int index, Object object) {
		return object.getClass();
	}

}