package library.impl.groovy;

import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import libraray.methodselection.ObjectGraphIterator;
import library.Pair;

public class JavaClassHierrachyIterator implements ObjectGraphIterator<MetaClass, Object> {

	@Override
	public Pair<MetaClass, Object> next(MetaClass type, Object object) {
		return new Pair<MetaClass, Object>(GroovySystem.getMetaClassRegistry().getMetaClass(type.getTheClass().getSuperclass()), object);
	}

	@Override
	public boolean hasNext(MetaClass type, Object object) {
		return type.getTheClass().getSuperclass() != null;
	}
}