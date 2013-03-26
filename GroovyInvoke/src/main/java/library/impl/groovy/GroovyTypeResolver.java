package library.impl.groovy;

import groovy.lang.GroovyObject;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;

import java.lang.invoke.MethodType;

import library.TypeResolver;

public class GroovyTypeResolver implements TypeResolver<MetaClass, Object> {

	@Override
	public MetaClass resolve(MethodType type, int index, Object object) {
		if (object instanceof GroovyObject) {
			return ((GroovyObject) object).getMetaClass();
		}
		// else if (object instanceof Class) {
		// return GroovySystem.getMetaClassRegistry().getMetaClass((Class<?>) object);
		// }

		return GroovySystem.getMetaClassRegistry().getMetaClass(object.getClass());
	}
}