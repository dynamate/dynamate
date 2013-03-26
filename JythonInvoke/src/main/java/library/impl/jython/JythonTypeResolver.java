package library.impl.jython;

import java.lang.invoke.MethodType;

import library.TypeResolver;

import org.python.core.PyClass;
import org.python.core.PyObject;
import org.python.core.PyType;

public class JythonTypeResolver implements TypeResolver<PyType, PyObject> {

	@Override
	public PyType resolve(MethodType type, int index, PyObject object) {
		return object.getType();
	}

}
