package library.impl.jruby;

import libraray.methodselection.ObjectGraphIterator;
import library.Pair;

import org.jruby.RubyClass;
import org.jruby.runtime.builtin.IRubyObject;

public class JRubyHierarchyResolver implements ObjectGraphIterator<RubyClass, IRubyObject> {

	@Override
	public Pair<RubyClass, IRubyObject> next(RubyClass type, IRubyObject object) {
		return new Pair<RubyClass, IRubyObject>(type.getSuperClass(), object);
	}

	@Override
	public boolean hasNext(RubyClass type, IRubyObject object) {
		return type.getSuperClass() != null;
	}
}
