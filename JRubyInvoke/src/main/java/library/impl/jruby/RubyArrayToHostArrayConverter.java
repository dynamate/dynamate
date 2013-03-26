package library.impl.jruby;

import libraray.adaptation.BasicArgumentTypeConverter.TypeConverter;

import org.jruby.RubyArray;
import org.jruby.runtime.builtin.IRubyObject;

public class RubyArrayToHostArrayConverter implements TypeConverter<RubyArray, IRubyObject[]> {

	@Override
	public IRubyObject[] convert(RubyArray object) {
		return (IRubyObject[]) object.getList().toArray();
	}
}
