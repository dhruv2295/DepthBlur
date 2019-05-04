package com.firebot.dhruv.tensorflow;

import androidx.annotation.NonNull;

import android.app.Application;

import com.firebot.dhruv.tensorflow.ml.DeeplabModel;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public class TensorApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		DeeplabModel.getInstance().initialize(this);

		Timber.plant(new Timber.DebugTree() {
			@NonNull
			@Override
			protected String createStackElementTag(@NotNull StackTraceElement element) {
				return super.createStackElementTag(element) + ":" + element.getLineNumber();
			}
		});
	}
}
