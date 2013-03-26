package org.python.compiler;

import static org.python.util.CodegenUtils.ci;
import static org.python.util.CodegenUtils.p;
import static org.python.util.CodegenUtils.sig;

import java.io.PrintStream;
import java.lang.invoke.MethodHandle;
import java.util.ArrayList;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.python.antlr.ast.Call;
import org.python.antlr.ast.Name;
import org.python.antlr.ast.OptimizedFor;
import org.python.antlr.ast.expr_contextType;
import org.python.antlr.base.expr;
import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.core.ThreadState;
import org.python.core.opt.IndyOptimizerException;
import org.python.core.opt.MethodHandleHelper;
import org.python.core.opt.RangeOptimizer;

/**
 * @author shashank
 * 
 */
public class OptimizedCodeCompiler extends CodeCompiler {

	/** Offset accounting for: <code>this</code> and the common params */
	private static final int VAR_OFFSET = MethodHandleHelper.COMMON_PARAMS_SIZE + 1;
	static String TYPE = "OptimizedCC";

	public OptimizedCodeCompiler(Module module, boolean print_results) {
		super(module, print_results);
	}

	public static int produceMethodHandle(Code code, PyCodeConstant pyCode, String funcName, String className) {
		int mHandle = code.getLocal(p(MethodHandle.class));
		funcName = "_" + funcName;
		// MethodHandles.lookup().findVirtual(this, "_funcName", mType)
		// code.invokestatic(p(MethodHandles.class), "lookup", sig(MethodHandles.Lookup.class));
		// code.aload(0);
		// code.invokevirtual(p(Object.class), "getClass", sig(Class.class));
		// code.ldc(funcName);
		// code.iconst(pyCode.argcount);
		// code.invokestatic(p(MethodHandleHelper.class), "getMethodType", sig(MethodType.class,
		// int.class));
		String mSig = MethodHandleHelper.getMethodType(pyCode.argcount).toMethodDescriptorString();
		Handle handle = new Handle(Opcodes.H_INVOKEVIRTUAL, className, funcName, mSig);
		// code.ldc(Type.getMethodType(mSig));
		code.ldc(handle);
		// code.invokevirtual(p(MethodHandles.Lookup.class), "findVirtual",
		// sig(MethodHandle.class, Class.class, String.class, MethodType.class));
		code.astore(mHandle);
		code.aload(mHandle);
		code.aload(0); // this
		code.invokevirtual(p(MethodHandle.class), "bindTo", sig(MethodHandle.class, Object.class));

		// code.invokestatic(p(RuntimeCallHelper.class), "setupMHForCall", sig(MethodHandle.class,
		// MethodHandle.class));
		code.astore(mHandle);
		return mHandle;
	}

	public static void printInJava(Code code, String whatToSay) {
		code.getstatic(p(System.class), "err", ci(PrintStream.class));
		code.ldc(whatToSay);
		code.invokevirtual(p(PrintStream.class), "println", sig(Void.TYPE, String.class));
	}

	@Override
	public Object visitCall(Call node) throws Exception {

		java.util.List<String> keys = new ArrayList<String>();
		java.util.List<expr> values = new ArrayList<expr>();
		for (int i = 0; i < node.getInternalArgs().size(); i++) {
			values.add(node.getInternalArgs().get(i));
		}
		for (int i = 0; i < node.getInternalKeywords().size(); i++) {
			keys.add(node.getInternalKeywords().get(i).getInternalArg());
			values.add(node.getInternalKeywords().get(i).getInternalValue());
		}

		int argLen = values.size();
		if ((node.getInternalStarargs() == null) && (node.getInternalKwargs() == null) && (argLen <= MethodHandleHelper.MAX_ARGUMENT_ARITY)) {

			this.loadThreadState();
			this.stackProduce(p(ThreadState.class));

			this.visit(node.getInternalFunc());
			this.stackProduce();

			for (int i = 0; i < argLen; i++) {
				this.visit(values.get(i));
				this.stackProduce();
			}
			this.stackConsume(2 + argLen); // ts + pyfunction + args
			// System.err.println("Generating indy call");
			// code.invokedynamic("_", MethodHandleHelper.getMethodType(argLen).dropParameterTypes(0, 1).toMethodDescriptorString(),
			// p(SpecializeCallSite.class), "bootstrap", Opcodes.H_INVOKESTATIC, keys.toArray());

			if (node.getFunc() instanceof org.python.antlr.ast.Name) {
				org.python.antlr.ast.Name name = (org.python.antlr.ast.Name) node.getFunc();

				this.code.invokedynamic("_", MethodHandleHelper.getMethodType(argLen).dropParameterTypes(0, 1).toMethodDescriptorString(), "library/impl/jython/Bootstrap",
						"bootstrap", Opcodes.H_INVOKESTATIC, keys.toArray());
			} else {
				this.code.invokedynamic("_", MethodHandleHelper.getMethodType(argLen).dropParameterTypes(0, 1).toMethodDescriptorString(), "library/impl/jython/Bootstrap",
						"bootstrap", Opcodes.H_INVOKESTATIC, keys.toArray());
			}

			return null;
		}
		// System.err.println("calling super: " + node.getInternalFunc() + node.getInternalStarargs() == null ? "has starargs " : ""
		// + (node.getInternalKwargs() == null ? "has kwargs " : "")
		// /*+ argLen == 0 ? " exceeds max arg count" : ""*/);
		return super.visitCall(node);
	}

	@Override
	public Object visitName(Name node) throws Exception {
		String name;
		if (this.fast_locals) {
			name = node.getInternalId();
		} else {
			name = this.getName(node.getInternalId());
		}
		SymInfo syminf = this.tbl.get(name);

		expr_contextType ctx = node.getInternalCtx();

		if (ctx == expr_contextType.AugStore) {
			ctx = this.augmode;
		}

		switch (ctx) {
		case Load: {
			if ((this.my_scope != null) && (this.my_scope.ac != null) && !this.my_scope.ac.arglist && !this.my_scope.ac.keywordlist) {
				int argcount = this.my_scope.ac.names.size();
				// if (((syminf.flags & ScopeInfo.BOUND) != 0) && ((syminf.flags & ScopeInfo.FROM_PARAM) != 0) &&
				if ((syminf.flags == 13) && (syminf.locals_index < argcount) && (argcount <= MethodHandleHelper.MAX_ARGUMENT_ARITY)) {
					int i = 0;
					for (i = 0; i < argcount; i++) {
						if (this.my_scope.ac.names.get(i).equals(name)) {
							break;
						}
					}
					if (i < argcount) {
						// Py.writeError(TYPE, "loading " + name + " from stack: " + syminf.locals_index);
						this.code.aload(syminf.locals_index + VAR_OFFSET);
						return null;
					}
				}
			}
			break;
		}
		case Store: {
			if ((this.my_scope != null) && (this.my_scope.ac != null) && !this.my_scope.ac.arglist && !this.my_scope.ac.keywordlist) {
				int argcount = this.my_scope.ac.names.size();
				// if (((syminf.flags & ScopeInfo.BOUND) != 0) && ((syminf.flags & ScopeInfo.FROM_PARAM) != 0) &&
				if ((syminf.flags == 13) && (syminf.locals_index < argcount) && (argcount <= MethodHandleHelper.MAX_ARGUMENT_ARITY)) {
					int i = 0;
					for (i = 0; i < argcount; i++) {
						if (this.my_scope.ac.names.get(i).equals(name)) {
							break;
						}
					}
					if (i < argcount) {
						// Py.writeError(TYPE, "storing " + name + " to stack: " + syminf.locals_index);
						this.code.aload(this.temporary);
						this.code.astore(syminf.locals_index + VAR_OFFSET);
						return null;
					}
				}
			}
			break;
		}
		}
		return super.visitName(node);
	}

	@Override
	public Object visitOptimizedFor(OptimizedFor node) throws Exception {
		java.util.List<expr> values = null;
		expr internalFunc = null;
		values = ((Call) node.getInternalIter()).getInternalArgs();
		internalFunc = ((Call) node.getInternalIter()).getInternalFunc();
		int start_value = this.code.getLocal(p(int.class));
		int stop_value = this.code.getLocal(p(int.class));
		int step_value = this.code.getLocal(p(int.class));
		int down_count_flag = this.code.getLocal(p(int.class));
		switch (values.size()) {
		case 1:
			this.visit(values.get(0));
			this.code.iconst_0();
			this.code.istore(start_value);
			// stackProduce();
			// stackConsume();
			this.code.invokevirtual(p(PyObject.class), "asInt", sig(int.class));
			this.code.istore(stop_value);
			this.code.iconst_1();
			this.code.istore(step_value);
			this.code.iconst_0();
			this.code.istore(down_count_flag);
			break;
		case 2:
			this.visit(values.get(0));
			// stackProduce();
			// stackConsume();
			this.code.invokevirtual(p(PyObject.class), "asInt", sig(int.class));
			this.code.istore(start_value);
			this.visit(values.get(1));
			// stackProduce();
			// stackConsume();
			this.code.invokevirtual(p(PyObject.class), "asInt", sig(int.class));
			this.code.istore(stop_value);
			this.code.iconst_1();
			this.code.istore(step_value);
			this.code.iconst_0();
			this.code.istore(down_count_flag);
			break;
		case 3:
			this.visit(values.get(0));
			// stackProduce();
			// stackConsume();
			this.code.invokevirtual(p(PyObject.class), "asInt", sig(int.class));
			this.code.istore(start_value);
			this.visit(values.get(1));
			// stackProduce();
			// stackConsume();
			this.code.invokevirtual(p(PyObject.class), "asInt", sig(int.class));
			this.code.istore(stop_value);
			this.visit(values.get(2));
			// stackProduce();
			// stackConsume();
			this.code.invokevirtual(p(PyObject.class), "asInt", sig(int.class));
			this.code.istore(step_value);
			// Since a step is provided, we have to check for down count
			// Also we need to throw an error in case step size is 0
			Label stepNotZero = new Label();
			Label greaterThanZero = new Label();
			Label step_end = new Label();
			this.code.iconst_0();
			this.code.iload(step_value);
			this.code.if_icmpne(stepNotZero);
			// step == 0: throw error
			this.code.ldc("[x]range() step argument must not be zero");
			this.code.invokestatic(p(Py.class), "ValueError", sig(PyException.class, String.class));
			this.code.athrow();
			// step != 0
			this.code.label(stepNotZero);
			this.code.iload(step_value);
			this.code.iconst_0();
			this.code.if_icmpgt(greaterThanZero);
			// step < 0, start down count
			this.code.iconst_1();
			this.code.istore(down_count_flag);
			this.code.goto_(step_end);
			// step > 0, normal up count
			this.code.label(greaterThanZero);
			this.code.iconst_0();
			this.code.istore(down_count_flag);
			this.code.label(step_end);
			break;
		default:
			break;
		}
		// Try-catch block for guarding
		Label start = new Label();
		Label end = new Label();
		Label handler_start = new Label();
		Label handler_end = new Label();
		this.code.trycatch(start, end, handler_start, p(IndyOptimizerException.class));
		this.code.label(start);
		// Do the try-stuff
		this.visit(internalFunc);
		// stackProduce();
		// stackConsume();
		this.code.invokedynamic("xrangeTarget", sig(Void.TYPE, PyObject.class), RangeOptimizer.className, "xrangeBsm", Opcodes.H_INVOKESTATIC);

		this.optimizedForLoop(node, start_value, stop_value, step_value, down_count_flag);
		this.code.label(end);
		this.code.goto_(handler_end);
		this.code.label(handler_start);
		// Catch block
		// TODO: call the pbcvm here
		this.code.new_(p(IndyOptimizerException.class));
		this.code.ldc("Range function Exception!");
		this.code.invokespecial(p(IndyOptimizerException.class), "<init>", sig(Void.TYPE, String.class));
		this.code.athrow();
		this.code.label(handler_end);
		this.code.freeLocal(start_value);
		this.code.freeLocal(stop_value);
		this.code.freeLocal(step_value);
		return null;
	}

	private void optimizedForLoop(OptimizedFor forNode, int start_value, int stop_value, int step_value, int down_count_flag) throws Exception {
		// Now the optimized code path for the for loop begins
		int savebcf = this.beginLoop();
		Label continue_loop = this.continueLabels.peek();
		Label break_loop = this.breakLabels.peek();
		Label start_loop = new Label();
		Label next_loop = new Label();
		Label down_count_label = new Label();
		Label loop_end = new Label();
		this.setline(forNode);
		// reuse the start_value variable as the iter_tmp variable
		int iter_tmp = start_value;
		int expr_tmp = this.code.getLocal(p(PyObject.class));
		// set up the loop iterator
		this.code.iload(start_value);
		this.code.istore(iter_tmp);
		// do check at end of loop. Saves one opcode ;-)
		this.code.goto_(next_loop);
		this.code.label(start_loop);
		// set iter variable to current entry in list
		this.set(forNode.getInternalTarget(), expr_tmp);
		// evaluate for body
		this.suite(forNode.getInternalBody());
		this.code.label(continue_loop);
		this.setline(forNode);
		this.code.iload(iter_tmp);
		this.code.iload(step_value);
		this.code.iadd();
		this.code.istore(iter_tmp);
		this.code.label(next_loop);
		// make the element available in python
		this.code.iload(iter_tmp);
		this.code.invokestatic(p(Py.class), "newInteger", sig(PyInteger.class, int.class));
		this.code.astore(expr_tmp);
		// down counting?
		this.code.iload(down_count_flag);
		this.code.iconst_1();
		this.code.if_icmpeq(down_count_label);
		// now check if we should go back into the loop
		this.code.iload(iter_tmp);
		this.code.iload(stop_value);
		this.code.if_icmplt(start_loop);
		this.code.goto_(loop_end);
		// We are down counting
		this.code.label(down_count_label);
		this.code.iload(iter_tmp);
		this.code.iload(stop_value);
		this.code.if_icmpgt(start_loop);
		this.code.label(loop_end);
		this.finishLoop(savebcf);
		if (forNode.getInternalOrelse() != null) {
			// Do else clause if provided
			this.suite(forNode.getInternalOrelse());
		}
		this.code.label(break_loop);
		this.code.freeLocal(expr_tmp);
	}
}
