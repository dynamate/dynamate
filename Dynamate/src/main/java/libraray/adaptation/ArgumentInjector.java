package libraray.adaptation;

public interface ArgumentInjector {

	public boolean isInjectable(Class<?> clazz);

	public Object injectValue(Class<?> clazz);

}