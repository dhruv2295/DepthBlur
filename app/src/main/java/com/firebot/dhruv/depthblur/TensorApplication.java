package com.firebot.dhruv.depthblur;

import androidx.annotation.NonNull;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.firebot.dhruv.depthblur.ml.DeeplabModel;
import com.google.android.gms.ads.MobileAds;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.common.logging.MoPubLog;

import io.fabric.sdk.android.Fabric;
import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public class TensorApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		DeeplabModel.getInstance().initialize(this);

		MobileAds.initialize(this, getString(R.string.ad_app_id));


		SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder("b195f8dd8ded45fe847ad89ed1d016da")
				.withLogLevel(MoPubLog.LogLevel.DEBUG)
				.withLegitimateInterestAllowed(false)
				.build();

		MoPub.initializeSdk(this, sdkConfiguration, new SdkInitializationListener() {
			@Override
			public void onInitializationFinished() {
				Timber.d("Mopub Initialized");
			}
		});


		Timber.plant(new Timber.DebugTree() {
			@NonNull
			@Override
			protected String createStackElementTag(@NotNull StackTraceElement element) {
				return super.createStackElementTag(element) + ":" + element.getLineNumber();
			}
		});

		if(!BuildConfig.DEBUG) Fabric.with(this, new Crashlytics());
	}
}
