package library.impl.jruby;

import java.lang.invoke.MethodType;

import library.TypeResolver;

import org.jruby.RubyClass;
import org.jruby.runtime.builtin.IRubyObject;

public class JRubyTypeResolver implements TypeResolver<RubyClass, IRubyObject> {

	@Override
	public RubyClass resolve(MethodType type, int index, IRubyObject object) {
		return object.getMetaClass();
	}
}