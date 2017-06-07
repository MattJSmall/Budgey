package mjsma5.budgey;

import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.WidgetContainer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.*;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Demonstrate Firebase Authentication using a Google ID Token.
 */
public class GoogleSignInActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;


    // [START declare_auth]
    public FirebaseAuth mAuth;
    // [END declare_auth]


    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;

    public static String uID;
    public static DatabaseReference userRef;
    public static FirebaseDatabase database;
    public static DatabaseReference transRef;

    private Intent firebaseServiceIntent;

    private SignInButton btnSignIn;
    private ImageView logo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_in);


        btnSignIn = (SignInButton) findViewById(R.id.sign_in_button);
        btnSignIn.setVisibility(View.GONE);


        logo = (ImageView) findViewById(R.id.imgLogo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        firebaseServiceIntent = new Intent(this, FirebaseServices.class);

        // Views
        mStatusTextView = (TextView) findViewById(R.id.status);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);

    }

    // [START onStart check user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        // [START config_signIn]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signIn]

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        Log.d("Google", "Setup complete");

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Intent intent = getIntent();
        if (intent.getBooleanExtra("stopFirebase", false)) {
            stopFirebaseService();
            signOut();
        } else {
            if (currentUser == null) {
                signIn();
            } else {
                pass();
            }
        }
    }
    // [END onStart check user]

    @Override
    protected void onResume() {
        super.onResume();
        /*
        logo.animate().rotation(20f).setDuration(400).start();
        logo.animate().rotation(-40f).setDuration(400).start();
        logo.animate().rotation(40f).setDuration(400).start();
        logo.animate().rotation(-40f).setDuration(400).start();
        logo.animate().rotation(40f).setDuration(400).start();
        logo.animate().rotation(-40f).setDuration(400).start();
        logo.animate().rotation(0f).setDuration(400).start();
        */
    }




    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                // [END_EXCLUDE]
            }
        }
    }
    // [END onActivityResult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        // showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            pass();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(GoogleSignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        // hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void signOut() {
        // Firebase sign out
        // [START initialize_auth]

        stopFirebaseService();
        FirebaseAuth.getInstance().signOut();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    public void pass() {
        uID = mAuth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        DatabaseReference categoryRef = database.getReference("users/" + uID);
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            // Create default categories for new user
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    newUser();
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        setupReferences();
        Intent intent = new Intent(this, Landing.class);
        // Wait until firebase data is loaded.
        startActivity(intent);
    }

    private void setupReferences() {
        userRef = database.getReference("users/").child(uID);
        Log.d("Firebase Login", "User reference success" + uID);
        transRef = database.getReference("users/" + uID ).child("transactions");
        this.startService(firebaseServiceIntent);
    }

    private void newUser() {
        DatabaseReference catListRef = database.getReference("users/" + uID + "/categories");
        categoryInit("Home", catListRef);
        categoryInit("Food", catListRef);
        categoryInit("Entertainment", catListRef);
        categoryInit("Gifts", catListRef);
        categoryInit("Car", catListRef);
        categoryInit("Clothes", catListRef);
        categoryInit("Health", catListRef);
        categoryInit("Transport", catListRef);
        categoryInit("Salary", catListRef);
        Log.d("SETUP", "NEW USER");
        setupReferences();
        Intent intent = new Intent(this, CreateTransaction.class);
        startActivity(intent);
    }

    private void categoryInit(String value, DatabaseReference ref) {
        String key = ref.push().getKey();
        ref.child(key).setValue(value);
    };

    private void delete() {
        userRef.removeValue();
        newUser();
    }

    private void stopFirebaseService() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();

        Iterator<ActivityManager.RunningAppProcessInfo> iter = runningAppProcesses.iterator();

        while(iter.hasNext()){
            ActivityManager.RunningAppProcessInfo next = iter.next();

            String pricessName = getPackageName() + ":FirebaseServices";

            if(next.processName.equals(pricessName)){
                Process.killProcess(next.pid);
                break;
            }
        }
    }
}