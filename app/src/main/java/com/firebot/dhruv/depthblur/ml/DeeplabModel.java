package com.firebot.dhruv.depthblur.ml;

public class DeeplabModel {


	private static DeeplabInterface sInterface = null;

	public synchronized static DeeplabInterface getInstance() {
		if (sInterface != null) {
			return sInterface;
		}


		sInterface = new DeeplabMobile();


		return sInterface;
	}

}
