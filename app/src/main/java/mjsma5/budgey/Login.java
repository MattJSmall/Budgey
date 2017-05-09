package mjsma5.budgey;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;

    private TextView txtPersonName;
    private TextView txtEmail;
    private ImageView imgPhoto;
    
    
    private SignInButton btnSignIn;
    private Button btnSignOut;
    private Button btn_go;      //// edit out

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Button init
        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
        findViewById(R.id.btn_sign_in).setOnClickListener(this);

        btnSignOut = (Button) findViewById(R.id.btn_sign_out);
        findViewById(R.id.btn_sign_out).setOnClickListener(this);

        btn_go = (Button) findViewById(R.id.btn_go);
        findViewById(R.id.btn_go).setOnClickListener(this);


        // Login items
        txtPersonName = (TextView) findViewById(R.id.txtDisplayName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        imgPhoto = (ImageView) findViewById(R.id.imgProfile);

        // Initial Visibility
        setVisibility(false); // boolean represents signed in.

        // Google Sign-In
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signIn();
        setVisibility(true);

        // Firebase currently signed in account
        mAuth = FirebaseAuth.getInstance();


    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_in:
                signIn();
                break;
            case R.id.btn_sign_out:
                signOut();
                break;
            case R.id.btn_go:
                Intent intent = new Intent(this, CreateTransaction.class);
                startActivity(intent);
                Log.d("REACHED", "intent reached");
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        setVisibility(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful => Authenticate
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                Log.d(TAG, "FirebaseAuthentication:Success");
                updateUI(account);
                setVisibility(true);

            } else {
                // Google Sign in failed update accordingly
                Log.d(TAG, "FirebaseAuthentication:Failed");
                signOut();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d(TAG, "signInWithCredential:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUI(GoogleSignInAccount account) {
        txtPersonName.setText(account.getDisplayName());
        txtEmail.setText(account.getEmail());
        new ImageLoadTask(account.getPhotoUrl().toString(), imgPhoto).execute();
        
    }

    // [required] error reporting
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        setVisibility(false);
    }

    public void setVisibility(boolean signed) {

        if (signed) {
            txtPersonName.setVisibility(View.VISIBLE);
            txtEmail.setVisibility(View.VISIBLE);
            imgPhoto.setVisibility(View.VISIBLE);
            btn_go.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.VISIBLE);
            btnSignIn.setVisibility(View.GONE);
        } else {
            txtPersonName.setVisibility(View.GONE);
            txtEmail.setVisibility(View.GONE);
            imgPhoto.setVisibility(View.GONE);
            btn_go.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.VISIBLE);
            btnSignIn.setVisibility(View.VISIBLE);
        }
    }



}
