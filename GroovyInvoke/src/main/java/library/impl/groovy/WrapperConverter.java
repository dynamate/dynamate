package library.impl.groovy;

import libraray.adaptation.BasicArgumentTypeConverter.TypeConverter;

import org.codehaus.groovy.runtime.wrappers.Wrapper;

public class WrapperConverter implements TypeConverter<Wrapper, Object> {

	@Override
	public Object convert(Wrapper object) {
		return object.unwrap();
	}
}
