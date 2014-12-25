package woodward.joshua.ribbit.UI;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.*;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import woodward.joshua.ribbit.R;

public class LoginActivity extends Activity {

    EditText mUsername;
    EditText mPassword;
    Button mLoginButton;
    TextView mSignUpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //indicates that we will want to utilize this 'Window' feature at some point in the future
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_login);

        //get and hide the action bar
        getActionBar().hide();

        mSignUpTextView=(TextView)findViewById(R.id.signUpText);

        mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUpIntent=new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });

        //sets member variables
        mUsername=(EditText)findViewById(R.id.usernameField);
        mPassword=(EditText)findViewById(R.id.passwordField);
        mLoginButton=(Button)findViewById(R.id.loginButton);

        //listener for sign up button
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //grabs input from UI fields
                String username=mUsername.getText().toString();
                String password=mPassword.getText().toString();
                //trims (removes whitespace) from UI fields
                username=username.trim();
                password=password.trim();

                if(username.isEmpty() || password.isEmpty()){
                    //creates an alert dialog via Dialog Factory pattern
                    AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle(R.string.login_error_title);
                    builder.setMessage(R.string.login_error_message);
                    //param1: android system string resource
                    //param2: listener for positive button (dont want to do anything
                    builder.setPositiveButton(android.R.string.ok,null);
                    AlertDialog invalidAlert=builder.create();
                    invalidAlert.show();
                }else{
                    //TODO: Login Parse User
                    loginParseUser(username, password);
                }
            }
        });

    }

    //given username and password params, attempts to login user
    private void loginParseUser(String username, String password) {
        //shows the progress indicator that we enabled at the start of onCreate()
        setProgressBarIndeterminateVisibility(true);
        ParseUser.logInInBackground(username, password, new LogInCallback() {

            @Override
            public void done(ParseUser parseUser, ParseException e) {
                //done talking to Parse, hide the progress indicator
                setProgressBarIndeterminateVisibility(false);
                if(e==null){
                    //success
                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else{
                    //fail
                    AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle(R.string.login_error_title);
                    builder.setMessage(e.getMessage());
                    builder.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {
                        //On click listener will reset the text of the username and password fields
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //resets username and password fields
                            mUsername.setText("");
                            mPassword.setText("");
                        }
                    });
                    AlertDialog createUserAlert=builder.create();
                    createUserAlert.show();
                }
            }
        });
    }


}
