package com.firebot.dhruv.tensorflow.ml;

public class DeeplabModel {


	private static DeeplabInterface sInterface = null;

	public synchronized static DeeplabInterface getInstance() {
		if (sInterface != null) {
			return sInterface;
		}

		sInterface = new DeepLabLite();

		return sInterface;
	}

}
