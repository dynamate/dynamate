package library.impl.jython;

import libraray.methodselection.ObjectGraphIterator;
import library.Pair;

import org.python.core.PyClass;
import org.python.core.PyObject;
import org.python.core.PyType;

public class JythonHierarchyResolver implements ObjectGraphIterator<PyType, PyObject> {

	@Override
	public Pair<PyType, PyObject> next(PyType type, PyObject object) {
		return new Pair<PyType, PyObject>(type.getBase().getType(), object);
	}

	@Override
	public boolean hasNext(PyType type, PyObject object) {
		return false;
	}

}
