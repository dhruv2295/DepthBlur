package com.firebot.dhruv.depthblur;

import androidx.annotation.NonNull;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.firebot.dhruv.depthblur.ml.DeeplabModel;
import com.google.android.gms.ads.MobileAds;

import io.fabric.sdk.android.Fabric;
import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public class TensorApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		DeeplabModel.getInstance().initialize(this);

		MobileAds.initialize(this, getString(R.string.ad_app_id));

		Timber.plant(new Timber.DebugTree() {
			@NonNull
			@Override
			protected String createStackElementTag(@NotNull StackTraceElement element) {
				return super.createStackElementTag(element) + ":" + element.getLineNumber();
			}
		});
		Fabric.with(this, new Crashlytics());
	}
}
