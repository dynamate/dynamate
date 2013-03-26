package library.guarding;

import java.lang.invoke.MethodType;

public interface GuardStrategy<PK> {
	/**
	 * Derives a partial key from the given argument and external runtime context
	 * 
	 * @param type
	 *            the type of the current call
	 * @param index
	 *            the index of the passed argument
	 * @param argument
	 *            the argument to derive a partial key from
	 * @return
	 */
	public PK buildPartialKey(MethodType type, int index, Object argument);

	/**
	 * The boolean test to compare two passed partial keys.
	 * 
	 * @param savedPartialKey
	 *            a specifi saved partial key, used in a guard
	 * @param argumentPartialKey
	 *            an argument of the current call
	 * @return
	 */
	public boolean test(PK savedPartialKey, PK argumentPartialKey);

	/**
	 * Returns the guard type specifying whether this guard protects only the receiver argument or all arguments.
	 * 
	 * @return guard type
	 */
	public GuardType getGuardType();

	public enum GuardType {
		RECEIVER_GUARD, ARGUMENTS_GUARD;
	}
}
