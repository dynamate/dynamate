package libraray.adaptation;

import java.util.Set;

public interface ArgumentTypeConverter {

	public <U, V> V convertType(Class<V> toClass, Class<U> fromClass, Object value);

	public <U, V> Set<Class<?>> getPossibleTypeConversions(Class<V> toClass);

	public <U, V> boolean isConvertable(Class<V> toClass, Class<U> fromClass);

}