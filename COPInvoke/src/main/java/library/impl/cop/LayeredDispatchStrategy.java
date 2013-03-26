package library.impl.cop;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.List;

import libraray.methodselection.MethodSelectionStrategy;
import libraray.methodselection.ObjectGraphIteratingHistory;
import libraray.methodselection.SimplifiedMethodHandle;
import library.guarding.GuardStrategy;
import library.invalidator.GlobalInvalidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import core.Composition;
import core.Layer;

public class LayeredDispatchStrategy implements MethodSelectionStrategy<Class<?>, Object, Method> {
	private final static Logger logger = LoggerFactory.getLogger(LayeredDispatchStrategy.class);

	private GuardStrategy<List<Layer>> guardStrategy = new GuardStrategy<List<Layer>>() {

		@Override
		public List<Layer> buildPartialKey(MethodType type, int index, Object argument) {
			return Composition.getCurrentComposition();
		}

		@Override
		public boolean test(List<Layer> object, List<Layer> argument) {
			return object.equals(argument);
		}

		@Override
		public library.guarding.GuardStrategy.GuardType getGuardType() {
			return GuardType.RECEIVER_GUARD;
		}
	};

	@Override
	public SimplifiedMethodHandle<Method> selectMethod(ObjectGraphIteratingHistory<Class<?>, Object> resolveHistory, MethodType callType, Class<?> clazz, String identifier,
			Object[] arguments) {

		String methodName = this.getMethodName(identifier);
		String layerName = this.getLayerName(identifier);

		Layer nextLayer = this.determineNextLayer(layerName);

		String targetMethodSuffix = (nextLayer == null) ? "base" : nextLayer.getClass().getSimpleName();

		MethodType receiverLessType = callType.dropParameterTypes(0, 1);

		SimplifiedMethodHandle<Method> handle = new SimplifiedMethodHandle<Method>(clazz, methodName + "$$" + targetMethodSuffix, false, receiverLessType);
		handle.setGuardStrategy(this.guardStrategy);
		handle.setInvalidator(true, GlobalInvalidator.getInvalidator("cop$" + Thread.currentThread().getId()));

		return handle;
	}

	private String getMethodName(String identifier) {
		String[] split = identifier.split(":");
		String methodIdentifier;

		if ((split.length == 3) && split[0].equals("proceed")) {
			methodIdentifier = split[1];
		} else {
			methodIdentifier = split[0];
		}

		return methodIdentifier.split("\\$\\$")[1];
	}

	private String getLayerName(String identifier) {
		String[] split = identifier.split(":");
		String layerName;

		if ((split.length == 3) && split[0].equals("proceed")) {
			layerName = split[2];
		} else {
			layerName = null;
		}

		return layerName;
	}

	private Layer determineNextLayer(String layerName) {
		Layer nextLayer = null;

		List<Layer> currentComposition = Composition.getCurrentComposition();
		if (layerName != null) {
			for (int i = 0; i < currentComposition.size(); i++) {
				if (currentComposition.get(i).getClass().getSimpleName().equals(layerName)) {
					nextLayer = (i + 1) >= currentComposition.size() ? null : currentComposition.get(i + 1);
					break;
				}
			}
		} else {
			nextLayer = currentComposition.isEmpty() ? null : currentComposition.get(0);
		}

		return nextLayer;
	}

	@Override
	public SimplifiedMethodHandle<Method> adapt(Class<?> receiverType, MethodType callType, SimplifiedMethodHandle<Method> methodHandle) {
		// TODO Auto-generated method stub
		return null;
	}
}