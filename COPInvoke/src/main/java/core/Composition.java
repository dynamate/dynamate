package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import library.invalidator.GlobalInvalidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Composition {
	private static final Logger logger = LoggerFactory.getLogger(Composition.class);

	private static ThreadLocal<List<Layer>> currentComposition = new ThreadLocal<List<Layer>>() {
		@Override
		protected List<Layer> initialValue() {
			return new ArrayList<>();
		}
	};

	static ArrayList<Layer> composition = new ArrayList<Layer>();

	public static void with(Layer... layers) {
		logger.trace("Activating layers: {}", Arrays.toString(layers));

		List<Layer> currentComposition = getCurrentComposition();
		for (Layer layer : layers) {
			currentComposition.add(layer);
		}

		GlobalInvalidator.getInvalidator("cop$" + Thread.currentThread().getId());
	}

	public static void without(Layer... layers) {
		logger.trace("Deactivating layers: {}", Arrays.toString(layers));

		List<Layer> currentComposition = getCurrentComposition();
		if ((layers == null) || (layers.length == 0)) {
			currentComposition.clear();
		} else {
			for (Layer layer : layers) {
				currentComposition.remove(layer);
			}
		}

		GlobalInvalidator.getInvalidator("cop$" + Thread.currentThread().getId());
	}

	public static List<Layer> getCurrentComposition() {
		return currentComposition.get();
	}
}
