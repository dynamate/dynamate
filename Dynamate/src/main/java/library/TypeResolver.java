package library;

import java.lang.invoke.MethodType;

public interface TypeResolver<T, O> {

	T resolve(MethodType type, int index, O object);
}
