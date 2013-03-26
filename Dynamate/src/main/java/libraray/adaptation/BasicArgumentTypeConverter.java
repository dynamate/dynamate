package libraray.adaptation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BasicArgumentTypeConverter implements ArgumentTypeConverter {

	public interface TypeConverter<U, V> {
		public V convert(U object);
	}

	private Map<Class<?>, Map<Class<?>, TypeConverter<?, ?>>> typeConverters = new HashMap<>();

	public <U, V> void registerTypeConversion(Class<V> toClass, Class<U> fromClass, TypeConverter<U, V> typeConverter) {
		Map<Class<?>, TypeConverter<?, ?>> toMap = this.typeConverters.get(toClass);
		if (toMap == null) {
			toMap = new HashMap<>();
			this.typeConverters.put(toClass, toMap);
		}

		toMap.put(fromClass, typeConverter);
	}

	@Override
	public <U, V> V convertType(Class<V> toClass, Class<U> fromClass, Object value) {
		Map<Class<?>, TypeConverter<?, ?>> toMap = this.typeConverters.get(toClass);
		if (toMap != null) {
			TypeConverter<U, V> typeConverter = (TypeConverter<U, V>) toMap.get(fromClass);

			if (typeConverter != null) {
				return typeConverter.convert((U) value);
			}
		}

		return null;
	}

	@Override
	public <U, V> Set<Class<?>> getPossibleTypeConversions(Class<V> toClass) {
		Map<Class<?>, TypeConverter<?, ?>> toMap = this.typeConverters.get(toClass);
		if (toMap == null) {
			return null;
		}

		return toMap.keySet();
	}

	@Override
	public <U, V> boolean isConvertable(Class<V> toClass, Class<U> fromClass) {
		Map<Class<?>, TypeConverter<?, ?>> toMap = this.typeConverters.get(toClass);
		if (toMap != null) {
			TypeConverter<?, ?> typeConverter = toMap.get(fromClass);

			return typeConverter != null;
		}

		return false;
	}
}