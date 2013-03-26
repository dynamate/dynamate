package library;

import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.util.Map;

import libraray.adaptation.ArgumentInjector;
import libraray.adaptation.ArgumentSelector;
import libraray.adaptation.BasicArgumentTypeConverter;
import libraray.methodselection.MethodSelectionStrategy;
import libraray.methodselection.ObjectGraphIterator;

public interface MethodDispatcher<T, O> {

	BoxedCallSite dispatch(Lookup lookup, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException;

	BoxedCallSite dispatch(String name, MethodType type) throws NoSuchMethodException, IllegalAccessException;

	Map<String, ExceptionHandler<O>> getExceptionHandlers();

	BoxedCallSite<?, ?> getCachedCallSite(String name, MethodType type);

	void setCachingEnabled(boolean caching);

	void setCascadingGuards(int cascadingGuards);

	void setMethodSelectionStrategy(MethodSelectionStrategy<T, O, ?> methodSelectionStrategy);

	void setObjectGraphIterator(ObjectGraphIterator<T, O> objectGraphIterator);

	void setTypeResolver(TypeResolver<T, O> typeResolver);

	void setArgumentInjector(ArgumentInjector argumentInjector);

	void setArgumentTypeConverter(BasicArgumentTypeConverter argumentTypeConverter);

	void setArgumentSelector(ArgumentSelector argumentSelector);
}
