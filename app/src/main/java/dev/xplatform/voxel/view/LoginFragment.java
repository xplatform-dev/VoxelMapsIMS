package dev.xplatform.voxel.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.common.api.ApiException;

import static dev.xplatform.voxel.controller.airtable.AirtableController.AIRTABLE;

import dev.xplatform.voxel.R;
import dev.xplatform.voxel.controller.airtable.AirtableController;

/**
 * Fragment responsible for handling the login functionality.
 */
public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;

    private ActivityResultLauncher<Intent> signInLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure Google Sign-In options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .setHostedDomain(getString(R.string.host_domain))
                .build();


        // Build a GoogleSignInClient with the specified options
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso);

        // Get the FirebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize the ActivityResultLauncher
        signInLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RC_SIGN_IN) {
                            Intent data = result.getData();
                            handleSignInResult(data);
                        }
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Handle sign-in button click
        view.findViewById(R.id.btn_sign_in).setOnClickListener(v -> signIn());
        view.findViewById(R.id.bypass_button).setOnClickListener(v -> bypassLogin());
    }

    /**
     * Perform the bypass login process.
     * This method is triggered when the bypass button is clicked.
     * It checks the entered email address and generates dummy data if it matches the expected format.
     */
    private void bypassLogin()
    {
        // Perform actions to bypass the login process
        // This can include populating the application with dummy data
        // or any other functionality you desire when bypassing the login.
        String email = ((EditText) getView().findViewById(R.id.bypassSigninEmailAddress)).getText().toString().trim();
        if (email.matches(getString(R.string.formatted_host_domain_regex, getString(R.string.host_domain)))) {

        }
    }

    /**
     * Initiates the Google Sign-In flow by launching the Google Sign-In intent.
     * This method is triggered when the sign-in button is clicked.
     */
    private void signIn() {
        // Launch the Google Sign-In intent
        Intent signInIntent = googleSignInClient.getSignInIntent();
        signInLauncher.launch(signInIntent);
    }

    /**
     * Handles the result of the Google Sign-In intent.
     * This method is called after the user signs in with their Google account.
     *
     * @param data The intent data containing the sign-in result.
     */
    private void handleSignInResult(Intent data) {
        try {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                firebaseAuthWithGoogle(account);
            }
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    /**
     * Authenticates the user with Firebase using the Google Sign-In credentials.
     * This method is called after the user successfully signs in with their Google account.
     *
     * @param account The GoogleSignInAccount containing the user's sign-in information.
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider
                .getCredential(account.getIdToken(), getContext().getString(R.string.firebase_apikey));
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign-in success, update UI with the signed-in user's information
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            // Perform email domain verification using Firebase Authentication
                            verifyEmailDomain(user.getEmail());
                        }
                    } else {
                        // Sign-in failed, log a message to the console
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                    }
                });9
    }

    /**
     * Verifies the email domain of the signed-in user.
     * This method performs email domain verification using the Google Identity Platform or Firebase Authentication.
     * You should implement the necessary verification logic based on your chosen authentication method.
     * Compare the email domain with the work domain or use an API to validate it.
     * Upon successful verification, proceed with the desired app functionality.
     *
     * @param email The email address obtained from the signed-in user.
     */
    private void verifyEmailDomain(String email) {
        // Perform email domain verification using the Google Identity Platform or Firebase Authentication
        // Use the email address obtained from the signed-in user to validate the domain.
        // Implement the necessary verification logic based on your chosen authentication method.
        // Compare the email domain with the work domain or use an API to validate it.
        // Upon successful verification, proceed with the desired app functionality.
    }
}
