package com.infurza.infurzaapplication.introduction;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
//import com.google.api.services.people.v1.People;
//import com.google.api.services.people.v1.PeopleService.People;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.PeopleServiceScopes;
import com.google.api.services.people.v1.model.Date;
import com.google.api.services.people.v1.model.Person;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.infurza.infurzaapplication.activities.HomeActivity;
import com.infurza.infurzaapplication.R;
//import com.google.api.services.people.v1.PeopleScopes;
import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    SignInButton googleSignInButton;
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private final static int RC_SIGN_IN = 2;
    Dialog dialog;

    GoogleSignInClient mGoogleSignInClient;
    GoogleApiClient mGoogleApiClient;
    GoogleSignInAccount acct;

    String GOOGLE_CLIENT_ID = "261358947950-6qoq7m9mnk50f85ptqsejs752819vslb.apps.googleusercontent.com";
    String GOOGLE_CLIENT_SECRET = "8mfOMFMFmFlLBrAAPqYrP1OX";
    String profileGender, profileBirthday, profileAbout,profileCover, profileStreetAddress, profileCity, profileCountry, profileName, profileEmail, profilePhoneNumber;
    String profileAgeRange;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        googleSignInButton = (SignInButton) findViewById(R.id.googleSignInBtn);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null) {
                    //startActivity(new Intent(LoginActivity.this, BaseActivity.class));\
                    startHomeActivity();
                }
            }
        };

        setupGoogleAdditionalDetailsLogin();

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //signIn();
                startGoogleAdditionalRequest();
                dialog = new Dialog(LoginActivity.this);
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

    public void startHomeActivity(){
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
    }


    ///////////////////////////////////////////////////////////////////////////////////
    // Configure Google Sign In

        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(getString(R.string.clientID))
                .requestIdToken(getString(R.string.clientID))
                .requestEmail()
                .requestProfile()
                .requestScopes(
                        //new Scope(PeopleServiceScopes.PLUS_LOGIN),
                        //new Scope(PeopleServiceScopes.USER_ADDRESSES_READ),
                        //new Scope(PeopleServiceScopes.USER_BIRTHDAY_READ),
                        new Scope(Scopes.EMAIL),
                        new Scope(Scopes.PLUS_ME),
                        new Scope(Scopes.PROFILE),
                        new Scope(Scopes.APP_STATE),
                        new Scope("profile"))
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);  */

    //////////////////////////////////////////////////////////////////////////////

    public void setupGoogleAdditionalDetailsLogin() {
        // Configure sign-in to request the user's ID, email address, and basic profile. ID and
        // basic profile are included in DEFAULT_SIGN_IN.

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(GOOGLE_CLIENT_ID)
                .requestServerAuthCode(GOOGLE_CLIENT_ID)
                .requestScopes(new Scope("profile"),
                        new Scope(PeopleServiceScopes.USER_BIRTHDAY_READ),
                        new Scope(PeopleServiceScopes.USER_PHONENUMBERS_READ),
                        new Scope(PeopleServiceScopes.USERINFO_PROFILE),
                        new Scope(PeopleServiceScopes.USER_ADDRESSES_READ),
                        new Scope(PeopleServiceScopes.PLUS_LOGIN),
                        new Scope(PeopleServiceScopes.USER_EMAILS_READ),
                        new Scope(Scopes.PROFILE),
                        new Scope(Scopes.PLUS_ME))
                .build();

        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d("TAG", "onConnectionFailed: ");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    //////////////////////////////////////////////////////////////////////////////////////////

   /* private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    } */

    ////////////////////////////////////////////////////////////////////////////////


    private void startGoogleAdditionalRequest() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {    // result for addition details request
            googleAdditionalDetailsResult(data);
            return;
        } else if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {  //logged in with firebase
            if (FirebaseAuth.getInstance().getCurrentUser().getProviders().get(0).equals("google.com")) {
                // user logged in with google account using firebase ui
                startGoogleAdditionalRequest();
            } else {
                // user logged in with google
                startHomeActivity();
            }
        } else {
            // handle error
        }
    }

    public void googleAdditionalDetailsResult(Intent data) {
        Log.d("TAG", "googleAdditionalDetailsResult: ");
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (result.isSuccess()) {
            // Signed in successfully
            acct = result.getSignInAccount();
            // execute AsyncTask to get data from Google People API
            new GoogleAdditionalDetailsTask().execute(acct);
        } else {
            Log.d("TAG", "googleAdditionalDetailsResult: fail");
            startHomeActivity();
        }
    }


    /////////////////////////////////////////////////////////////////////////////////

    /*@Override
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

                    Toast.makeText(LoginActivity.this, personName + ", " + personEmail, Toast.LENGTH_LONG).show();

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
    }  */



    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            Toast.makeText(LoginActivity.this, "Google SignIn Success",
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
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
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


    ///////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public class GoogleAdditionalDetailsTask extends AsyncTask<GoogleSignInAccount, Void, Person> {
        @Override
        protected Person doInBackground(GoogleSignInAccount... googleSignInAccounts) {
            Person profile = null;
            try {
                HttpTransport httpTransport = new NetHttpTransport();
                JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

                //Redirect URL for web based applications.
                // Can be empty too.
                String redirectUrl = "urn:ietf:wg:oauth:2.0:oob";

                // Exchange auth code for access token
                GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                        httpTransport,
                        jsonFactory,
                        GOOGLE_CLIENT_ID,
                        GOOGLE_CLIENT_SECRET,
                        googleSignInAccounts[0].getServerAuthCode(),
                        redirectUrl
                ).execute();

                GoogleCredential credential = new GoogleCredential.Builder()
                        .setClientSecrets(GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET)
                        .setTransport(httpTransport)
                        .setJsonFactory(jsonFactory)
                        .build();

                credential.setFromTokenResponse(tokenResponse);

                /*PeopleService.People peopleService = new PeopleService.People().Builder(httpTransport, jsonFactory, credential)
                        .setApplicationName(getString(R.string.app_name))
                        .build(); */
                PeopleService peopleService =
                        new PeopleService.Builder(httpTransport, jsonFactory, credential)
                                .setApplicationName(getString(R.string.app_name))
                                .build();

                // Get the user's profile
                profile = peopleService.people().get("people/me").setPersonFields("names,emailAddresses,birthdays,genders,ageRanges,phoneNumbers,locales,addresses,residences").execute();
                /*profile = peopleService.people().get("people/me")
                          .setRequestMaskIncludeField("person.names,person.emailAddress‌​es,person.genders,pe‌​rson.birthdays").execute(); */

            } catch (IOException e) {
                Log.d("TAG", "doInBackground: " + e.getMessage());
                e.printStackTrace();
            }
            return profile;
        }

        @Override
        protected void onPostExecute(Person person) {
            if (person != null) {
                //profilePhoneNumber = person.getPhoneNumbers().get(0).getValue();
                //profileGender = person.getGenders().get(0).getValue();
                //profileStreetAddress = person.getAddresses().get(0).getStreetAddress();
                //profileCity = person.getResidences().get(0).getValue();
                //profileCountry = person.getAddresses().get(0).getCountry();

                if (person.getNames() != null){
                    profileName = person.getNames().get(0).getDisplayName();
                }
                if (person.getAgeRanges() != null){
                    profileAgeRange = person.getAgeRanges().get(0).getAgeRange();
                }
                if (person.getEmailAddresses() != null){
                    profileEmail = person.getEmailAddresses().get(0).getValue();
                }
                if (person.getPhoneNumbers() != null){
                    profilePhoneNumber = person.getPhoneNumbers().get(0).getValue();
                }
                if (person.getGenders() != null) {
                    profileGender = person.getGenders().get(0).getValue();
                }
                if (person.getBirthdays() != null) {
//                    yyyy-MM-dd
                    Date dobDate = person.getBirthdays().get(0).getDate();

                    profileBirthday = dobDate.getYear() +  "-" + dobDate.getMonth() + "-" + dobDate.getDay();
                    //profileYearOfBirth = DateHelper.getYearFromGoogleDate(profileBirthday);

                }
              /*  if(person.getAddresses() != null && person.getAddresses().size() > 0){
                    profileStreetAddress = person.getAddresses().get(0).getStreetAddress();
                    profileCity = person.getAddresses().get(0).getCity();
                    profileCountry = person.getAddresses().get(0).getCountry();

                }*/
                Toast.makeText(LoginActivity.this, profileName+", " +profileEmail+", "+profileAgeRange+ ", " +profileBirthday+ ", "
                        +profileGender+", "+profilePhoneNumber+", "+profileStreetAddress+", "+profileCity+", "+profileCountry, Toast.LENGTH_LONG).show();

                Log.d("TAG", String.format("googleOnComplete: gender: %s, birthday: %s, about: %s, cover: %s", profileGender, profileBirthday, profileAbout, profileCover));
            }
            firebaseAuthWithGoogle(acct);
            //startHomeActivity();
        }
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
