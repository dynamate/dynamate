package library.impl.jruby;

import libraray.adaptation.BasicArgumentTypeConverter.TypeConverter;

import org.jruby.runtime.builtin.IRubyObject;

public class SingletonArrayConverter implements TypeConverter<IRubyObject, IRubyObject[]> {

	@Override
	public IRubyObject[] convert(IRubyObject object) {
		return new IRubyObject[] { object };
	}
}
