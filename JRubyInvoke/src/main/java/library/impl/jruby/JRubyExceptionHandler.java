package library.impl.jruby;

import library.ExceptionHandler;

import org.jruby.RubyLocalJumpError;
import org.jruby.exceptions.JumpException;
import org.jruby.exceptions.JumpException.BreakJump;
import org.jruby.exceptions.JumpException.RetryJump;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

public class JRubyExceptionHandler implements ExceptionHandler<IRubyObject> {

	@Override
	public IRubyObject handle(Throwable e, Object[] args) throws Throwable {
		if (e instanceof JumpException.BreakJump) {
			JumpException.BreakJump bj = (BreakJump) e;
			ThreadContext context = (ThreadContext) args[0];

			if (context.getFrameJumpTarget() == bj.getTarget()) {
				return (IRubyObject) bj.getValue();
			}
		}

		if (e instanceof JumpException.RetryJump) {
			ThreadContext context = (ThreadContext) args[0];
			JumpException.RetryJump rj = (RetryJump) e;
			throw context.getRuntime().newLocalJumpError(RubyLocalJumpError.Reason.RETRY, context.getRuntime().getNil(), "retry outside of rescue not supported");
		}

		throw e;
	}
}
