package com.cse110.flashbackmusicplayer;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.api.services.people.v1.PeopleServiceScopes;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, View.OnClickListener {


    private String CLIENT_ID;
    private final int RESULT_ID = 1;
    final int RESULT_ERROR_ID = 0;

    SignInButton signInButton;
    GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("RUN", "WHAT");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CLIENT_ID = getString(R.string.client_id);

        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);

        GoogleSignInOptions signInInfo = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(CLIENT_ID)
                .requestEmail()
                .requestProfile()
                .requestScopes(new Scope(PeopleServiceScopes.PLUS_LOGIN), new Scope(PeopleServiceScopes.CONTACTS_READONLY),
                        new Scope(PeopleServiceScopes.USERINFO_PROFILE))
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInInfo)
                .build();

    }
    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_in_button) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            // goes to onActivityResult when done
            startActivityForResult(signInIntent, RESULT_ID);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("SignInResult", "Activity sent back.");
        super.onActivityResult(requestCode,resultCode, data);
        if (requestCode == RESULT_ID) {
            Log.d("SignInResult", "Activity sent back with a result ID.");
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d("GoogleResult", googleSignInResult.getStatus().toString());
            if (googleSignInResult.isSuccess()) {
                Log.d("SignInResult", "Activity sent back successfully.");
                finish();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = googleApiAvailability.getErrorDialog(this, connectionResult.getErrorCode(), RESULT_ERROR_ID);
        dialog.show();
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

}
