package stanislavsikorsyi.oauthtest.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;

import java.util.Arrays;
import java.util.List;

import stanislavsikorsyi.oauthtest.R;

/**
 * Created by stanislavsikorsyi on 30.06.14.
 */
public class MainActivity extends BaseActivity  implements View.OnClickListener  {

    private static final String TAG = "MainActivity1";
    private String facebookName  = "";
    private TextView googleWellcomeMessageTextView;
    private TextView facebookWellcomeMessageTextView;
    private String get_id, get_name, get_gender, get_email, get_birthday, get_locale, get_location;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity_mainlayout);
        googleWellcomeMessageTextView = (TextView)findViewById(R.id.google_wellcome);
        facebookWellcomeMessageTextView = (TextView)findViewById(R.id.facebook_welcome);
        findViewById(R.id.sign_in_button_google).setOnClickListener(this);
        facebookLoginButton = (LoginButton)findViewById(R.id.sign_in_button_facebook);
        facebookLoginButton.setReadPermissions(Arrays.asList("user_friends"));

    }

    @Override
    public void onClick(View v) {
        onClickGoogleSignIn(v);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.d(TAG, "back button pressed");
        }
        return false;
    }

    public void onClickGoogleSignOut(View view) {
        Log.d(TAG,"onClickGoogleSignOut");
        super.onClickGoogleSignOut(view);
    }



    @Override
    public void onConnected(Bundle bundle) {
        super.onConnected(bundle);
        Log.d(TAG,"onConnected");
        refreshWelcomeText();
        printResultsOfGooglePersonAuth();
        getGoogleFriendsList();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        super.onConnectionFailed(result);
        Log.d(TAG, "onConnectionFailed");
        refreshWelcomeText();
    }

    public void  refreshWelcomeText() {
        Log.d(TAG,"refreshWelcomeText");
        Log.d(TAG,"refreshWelcomeText googleApiClient.isConnected() = " + googleApiClient.isConnected());
        if(googleApiClient.isConnected()) {
            Person person = Plus.PeopleApi.getCurrentPerson(googleApiClient);
            String wellcomeMessage = getString(R.string.google_wellcome_message) + ": Logged in as " + person.getDisplayName();
            googleWellcomeMessageTextView.setText(wellcomeMessage);
        } else {
            String wellcomeMessage = getString(R.string.google_wellcome_message);
            googleWellcomeMessageTextView.setText(wellcomeMessage);
        }
    }



    public boolean isFacebookLoggedIn() {
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            return true;
        } else {
            return false;
        }
    }

    private void printResultsOfGooglePersonAuth() {
        String accountName = Plus.AccountApi.getAccountName(googleApiClient);
        Log.d(TAG,"account name: " + accountName);
        Person person = Plus.PeopleApi.getCurrentPerson(googleApiClient);
        Log.d(TAG," name: " + person.getName());
        Log.d(TAG," gender: " + person.getGender());
        Log.d(TAG," about me: " + person.getAboutMe());
        Log.d(TAG," language: " + person.getLanguage());
        Log.d(TAG," display name: " + person.getDisplayName());
        Log.d(TAG," relations: " + person.getRelationshipStatus());
        Log.d(TAG," location: " + person.getCurrentLocation());
    }

    private void getGoogleFriendsList() {
        Plus.PeopleApi.loadVisible(googleApiClient, null).setResultCallback(new ResultCallback<People.LoadPeopleResult>() {

            @Override
            public void onResult(People.LoadPeopleResult loadPeopleResult) {
                if (!loadPeopleResult.getStatus().isSuccess()) {
                    Log.e(TAG, loadPeopleResult.getStatus().toString());
                    return;
                }
                PersonBuffer people = loadPeopleResult.getPersonBuffer();
                Log.d(TAG, "" + people.getCount());
                for (Person p : people) {
                    Log.d(TAG, p.getDisplayName());
                }
                people.close();
            }

        });
    }

    //Facebook login listener
    protected void onSessionStateChange(Session session, SessionState state, Exception exception) {
        super.onSessionStateChange(session, state, exception);
        refreshFacebookWellcomeMessage();
        if (state.isOpened()) {
            Log.i(TAG, "Facebook Logged in...");

        } else if (state.isClosed()) {
            Log.i(TAG, "Facebook Logged out...");
        }
    }


    private void requestFacebookFriends(Session session) {
        Request.executeMyFriendsRequestAsync(session,
                new Request.GraphUserListCallback() {
                    @Override
                    public void onCompleted(List<GraphUser> users,
                                            Response response) {
                        for (GraphUser user : users) {
                            Log.d(TAG, "friendname: " + user.getName());
                        }
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshFacebookWellcomeMessage();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void refreshFacebookWellcomeMessage() {
        if(Session.getActiveSession().isOpened()) {
            if(!facebookName.equals("") && facebookName != null) {
                String wellcomeMessage = getString(R.string.facebook_wellcome_message) + ": Logged in as " + facebookName;
                facebookWellcomeMessageTextView.setText(wellcomeMessage);
            } else {
                Request.executeMeRequestAsync(Session.getActiveSession(), new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        if (user != null) {
                            // Display the parsed user info
                            Log.i(TAG, "executeMeRequestAsync onComplete");
                            String userName = user.getName();
                            facebookName = userName;
                            Log.d(TAG, "Facebook name: " + userName);
                            if(isFacebookLoggedIn()) {
                                String wellcomeMessage = getString(R.string.facebook_wellcome_message) + ": Logged in as " + facebookName;
                                facebookWellcomeMessageTextView.setText(wellcomeMessage);
                            } else {
                                String wellcomeMessage = getString(R.string.facebook_wellcome_message);
                                facebookWellcomeMessageTextView.setText(wellcomeMessage);
                            }
                        }
                    }
                });
            }
        } else if(Session.getActiveSession().isClosed()) {
            String wellcomeMessage = getString(R.string.facebook_wellcome_message);
            facebookWellcomeMessageTextView.setText(wellcomeMessage);
        }
    }



}

