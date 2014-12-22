package woodward.joshua.ribbit.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.*;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import woodward.joshua.ribbit.R;

public class SignUpActivity extends Activity {

    //Declares member variables
    protected EditText mUsername;
    protected EditText mPassword;
    protected EditText mEmail;
    protected Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //enables window's indeterminate progress spinner
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_sign_up);

        //sets member variables
        mUsername=(EditText)findViewById(R.id.usernameField);
        mPassword=(EditText)findViewById(R.id.passwordField);
        mEmail=(EditText)findViewById(R.id.emailField);
        mSignUpButton=(Button)findViewById(R.id.signUpButton);

        //listener for sign up button
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //grabs input from UI fields
                String username=mUsername.getText().toString();
                String password=mPassword.getText().toString();
                String email=mEmail.getText().toString();
                //trims (removes whitespace) from UI fields
                username=username.trim();
                password=password.trim();
                email=email.trim();

                if(username.isEmpty() || password.isEmpty() || email.isEmpty()){
                    //creates an alert dialog via Dialog Factory pattern
                    AlertDialog.Builder builder=new AlertDialog.Builder(SignUpActivity.this);
                    builder.setTitle(R.string.signup_error_title);
                    builder.setMessage(R.string.signup_error_message);
                    //param1: android system string resource
                    //param2: listener for positive button (dont want to do anything
                    builder.setPositiveButton(android.R.string.ok,null);
                    AlertDialog invalidAlert=builder.create();
                    invalidAlert.show();
                }else{
                    //TODO: Create ParseUser
                    createParseUser(username, password, email);
                }
            }
        });
    }

    //given fields, will create parseUser
    private void createParseUser(String username, String password, String email) {
        //shows the window spinner that we enabled earlier
        setProgressBarIndeterminateVisibility(true);
        ParseUser newUser=new ParseUser();
        //sets fields of new parse user
        //additional fields may be added using: newUser.put("field","value");
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setEmail(email);
        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                //hides the window spinner that we enabled earlier
                setProgressBarIndeterminateVisibility(false);
                //exception will be null if signup has succeeded
                if(e==null){
                    //success
                    Intent intent=new Intent(SignUpActivity.this,MainActivity.class);
                    //start a new task for the inbox, clear navigation history
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else{
                    //fail, explain to user what went wrong
                    AlertDialog.Builder builder=new AlertDialog.Builder(SignUpActivity.this);
                    builder.setTitle(R.string.signup_error_title);
                    builder.setMessage(e.getMessage());
                    builder.setPositiveButton(android.R.string.ok,null);
                    AlertDialog createUserAlert=builder.create();
                    createUserAlert.show();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
