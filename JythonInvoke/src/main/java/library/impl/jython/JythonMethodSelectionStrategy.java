package library.impl.jython;

import java.lang.invoke.MethodType;

import libraray.methodselection.MethodSelectionStrategy;
import libraray.methodselection.ObjectGraphIteratingHistory;
import libraray.methodselection.SimplifiedMethodHandle;
import libraray.methodselection.SingleArgumentFilter;
import library.guarding.GuardStrategy;

import org.python.core.PyFrame;
import org.python.core.PyFunction;
import org.python.core.PyFunctionTable;
import org.python.core.PyMethod;
import org.python.core.PyObject;
import org.python.core.PyTableCode;
import org.python.core.PyType;
import org.python.core.ThreadState;
import org.python.core.opt.RuntimeCallHelper;

public class JythonMethodSelectionStrategy implements MethodSelectionStrategy<PyType, PyObject, PyMethod> {

	private GuardStrategy<PyType> guardStrategy = new GuardStrategy<PyType>() {

		@Override
		public PyType buildPartialKey(MethodType callType, int argumentIndex, Object argument) {
			if (argumentIndex != 1) {
				return null;
			}

			PyMethod py = (PyMethod) argument;
			return py.im_self.getType();
		}

		@Override
		public boolean test(PyType savedType, PyType argumentType) {
			return savedType == argumentType;
		}

		@Override
		public GuardType getGuardType() {
			return GuardType.ARGUMENTS_GUARD;
		}
	};

	@Override
	public SimplifiedMethodHandle<PyMethod> selectMethod(ObjectGraphIteratingHistory<PyType, PyObject> resolveHistory, MethodType callType, PyType type, String method,
			Object[] arguments) {

		if (type.isSubType(PyFunction.TYPE)) {
			final PyFunction pyFunc = (PyFunction) arguments[1];

			int noOfArgs = arguments.length - 2;
			PyTableCode funcCode = (PyTableCode) pyFunc.func_code;

			if (noOfArgs == funcCode.co_argcount) {
				PyFunctionTable funcTable = funcCode.funcs;
				int funcId = funcCode.func_id;
				String funcName = pyFunc.getFuncName().getString();

				MethodType newType = callType.insertParameterTypes(0, PyFrame.class);
				SimplifiedMethodHandle<PyMethod> directMethodHandle = new SimplifiedMethodHandle<PyMethod>(funcTable.getClass(), "_" + funcName + "$" + funcId, false, newType);
				directMethodHandle.bind(0, funcTable);

				PyObject[] args = new PyObject[arguments.length - 2];
				System.arraycopy(arguments, 2, args, 0, arguments.length - 2);
				directMethodHandle.bind(1, RuntimeCallHelper.callBefore((ThreadState) arguments[0], pyFunc, args));

				return directMethodHandle;
			}
		} else if (type.isSubType(PyMethod.TYPE)) {
			PyMethod pyMethod = (PyMethod) arguments[1];
			PyFunction pyFunc = (PyFunction) pyMethod.im_func;
			PyTableCode funcCode = (PyTableCode) pyFunc.func_code;
			PyFunctionTable funcTable = funcCode.funcs;

			int funcId = funcCode.func_id;
			String funcName = pyFunc.getFuncName().getString();

			MethodType newType = callType.insertParameterTypes(0, PyFrame.class).insertParameterTypes(2, PyObject.class);
			SimplifiedMethodHandle<PyMethod> directMethodHandle = new SimplifiedMethodHandle<PyMethod>(funcTable.getClass(), "_" + funcName + "$" + funcId, false, newType);

			directMethodHandle.setGuardStrategy(this.guardStrategy);
			directMethodHandle.bind(0, funcTable);
			directMethodHandle.bind(1, pyFunc.createFrame());

			directMethodHandle.bind(4, new SingleArgumentFilter() {

				@Override
				public Object perform(Object[] arguments) {
					PyMethod pyMethod = (PyMethod) arguments[1];
					return pyMethod.im_self;
				}
			});
			return directMethodHandle;
		}

		MethodType fallbackType = callType.dropParameterTypes(1, 2);
		SimplifiedMethodHandle<PyMethod> directMethodHandle = new SimplifiedMethodHandle<PyMethod>(PyObject.class, "__call__", false, fallbackType);
		return directMethodHandle;
	}

	@Override
	public SimplifiedMethodHandle<PyMethod> adapt(PyType receiverType, MethodType callType, SimplifiedMethodHandle<PyMethod> methodHandle) {
		return null;
	}
}
