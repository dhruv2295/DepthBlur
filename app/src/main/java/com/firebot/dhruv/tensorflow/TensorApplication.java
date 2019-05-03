package com.firebot.dhruv.tensorflow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.os.Bundle;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public class TensorApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		Timber.plant(new Timber.DebugTree() {
			@NonNull
			@Override
			protected String createStackElementTag(@NotNull StackTraceElement element) {
				return super.createStackElementTag(element) + ":" + element.getLineNumber();
			}
		});
	}
}
