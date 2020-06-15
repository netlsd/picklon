package id.co.picklon;

import android.app.Application;

import com.facebook.FacebookSdk;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import id.co.picklon.injector.components.AppComponent;
import id.co.picklon.injector.components.DaggerAppComponent;
import id.co.picklon.injector.modules.AppModule;
import id.co.picklon.push.Pusher;
import id.co.picklon.utils.L;
import id.co.picklon.utils.Picklon;

public class PicklonApplication extends Application {
    private AppComponent mAppComponent;
    private static final int APP_ID = 101;

    @Override
    public void onCreate() {
        super.onCreate();

        CustomActivityOnCrash.install(this);
        L.init();
        FacebookSdk.sdkInitialize(getApplicationContext());
        initializeInjector();

        Picklon.DEVICE_TOKEN = Pusher.registerPush(APP_ID, this, "test");
    }

    private void initializeInjector() {
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
