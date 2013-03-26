package library;

public interface ExceptionHandler<O> {
	public O handle(Throwable e, Object[] args) throws Throwable;
}
