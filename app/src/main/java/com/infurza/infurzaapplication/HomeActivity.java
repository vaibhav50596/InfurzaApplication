package com.infurza.infurzaapplication;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Contacts;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.api.services.people.v1.PeopleServiceScopes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Person;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleBrowserClientRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    SignInButton googleSignInButton;
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private final static int RC_SIGN_IN = 2;
    Dialog dialog;

    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        googleSignInButton = (SignInButton) findViewById(R.id.googleSignInBtn);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() != null){
                    startActivity(new Intent(HomeActivity.this, BaseActivity.class));
                }
            }
        };
        // Configure Google Sign In

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(getString(R.string.clientID))
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .requestScopes(new Scope(PeopleServiceScopes.PLUS_LOGIN),
                        new Scope(PeopleServiceScopes.USER_ADDRESSES_READ),
                        new Scope(PeopleServiceScopes.USER_BIRTHDAY_READ),
                        new Scope(Scopes.EMAIL),
                        new Scope(Scopes.PLUS_ME),
                        new Scope(Scopes.PROFILE),
                        new Scope(Scopes.APP_STATE))
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
                dialog = new Dialog(HomeActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.progressbar);
                ProgressBar progress = (ProgressBar) dialog.findViewById(R.id.progressBarServerData);
                progress.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String personName = account.getDisplayName();
                String personGivenName = account.getGivenName();
                String personFamilyName = account.getFamilyName();
                String personEmail = account.getEmail();
                String personId = account.getId();
                Uri personPhoto = account.getPhotoUrl();
                String idToken = account.getIdToken();

                // execute AsyncTask to get gender from Google People API
                //new GetGendersTask().execute(account);

                firebaseAuthWithGoogle(account);
                Toast.makeText(HomeActivity.this, personName + ", " + personEmail, Toast.LENGTH_LONG).show();
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            Toast.makeText(HomeActivity.this, "Google SignIn Success",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            user.getDisplayName();
                            user.getEmail();
                            user.getPhotoUrl();
                            user.getPhoneNumber();
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(HomeActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

  /*  public void setUp() throws IOException {
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();

        // Go to the Google API Console, open your application's
        // credentials page, and copy the client ID and client secret.
        // Then paste them into the following code.
        String clientId = "261358947950-6qoq7m9mnk50f85ptqsejs752819vslb.apps.googleusercontent.com";
        String clientSecret = "8mfOMFMFmFlLBrAAPqYrP1OX";

        // Or your redirect URL for web based applications.
        String redirectUrl = "https://infurza-infurza.firebaseapp.com/__/auth/handler";
        String scope = "https://www.googleapis.com/auth/contacts.readonly";

        // Step 1: Authorize -->
        String authorizationUrl =
                new GoogleBrowserClientRequestUrl(clientId, redirectUrl, Arrays.asList(scope)).build();

        // Point or redirect your user to the authorizationUrl.
        System.out.println("Go to the following link in your browser:");
        System.out.println(authorizationUrl);

        // Read the authorization code from the standard input stream.
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("What is the authorization code?");
        String code = in.readLine();
        // End of Step 1 <--

        // Step 2: Exchange -->
        GoogleTokenResponse tokenResponse =
                new GoogleAuthorizationCodeTokenRequest(
                        httpTransport, jsonFactory, clientId, clientSecret, code, redirectUrl)
                        .execute();
        // End of Step 2 <--

        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setClientSecrets(clientId, clientSecret)
                .build()
                .setFromTokenResponse(tokenResponse);

        PeopleService peopleService =
                new PeopleService.Builder(httpTransport, jsonFactory, credential).build();


        ListConnectionsResponse response = peopleService.people().connections().list("people/me")
                .setPersonFields("names,emailAddresses")
                .execute();
        List<Person> connections = response.getConnections();

        Person profile = peopleService.people().get("people/me")
                .setPersonFields("names,emailAddresses")
                .execute();

    } */

}
