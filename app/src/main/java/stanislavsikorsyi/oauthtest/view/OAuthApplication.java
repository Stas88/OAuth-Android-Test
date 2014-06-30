package stanislavsikorsyi.oauthtest.view;

import android.app.Application;
import android.util.Log;

import com.facebook.Session;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by stanislavsikorsyi on 30.06.14.
 */
public class OAuthApplication extends Application {


    public Session facebookSession;

    private static final String TAG = "OAuthApplication";

    private static Bus eventBus;

    public static Bus getEventBus() {
        return eventBus;
    }
    public Session getSession() {
        return facebookSession;
    }

    public void setSession(Session facebookSession) {
        this.facebookSession = facebookSession;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate Application");
        eventBus = new Bus(ThreadEnforcer.ANY);
    }

}
