package woodward.joshua.ribbit.UI;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

import woodward.joshua.ribbit.Model.ParseConstants;
import woodward.joshua.ribbit.R;

public class RecipientsActivity extends ListActivity {

    public static final String TAG= RecipientsActivity.class.getSimpleName();

    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected List<ParseUser> mFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_recipients);

        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    public void onResume(){
        super.onResume();

        mCurrentUser= ParseUser.getCurrentUser();
        mFriendsRelation=mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        setProgressBarIndeterminateVisibility(true);

        ParseQuery<ParseUser> friendsQuery=mFriendsRelation.getQuery();
        friendsQuery.addAscendingOrder(ParseConstants.KEY_USERNAME);
        friendsQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                setProgressBarIndeterminateVisibility(false);
                if(e==null){
                    //create the list
                    //set member variable to list of ParseUsers returned by query
                    mFriends=parseUsers;
                    //initialize array of strings the size of returned ParseUsers
                    String[] usernames=new String[mFriends.size()];
                    //initialize counter, and add ParseUser.username to list
                    int i=0;
                    for(ParseUser user: mFriends){
                        usernames[i]=user.getUsername();
                        i++;
                    }
                    //Create an array adapter of type String, give params (Context, ListType, Source)
                    ArrayAdapter<String> friendsAdapter=new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_checked, usernames);
                    //setListAdapter() is method of ListActivity (the class this activity inherits from)
                    setListAdapter(friendsAdapter);
                }else{
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder friendsListAlertBuilder=new AlertDialog.Builder(RecipientsActivity.this);
                    friendsListAlertBuilder.setTitle(getString(R.string.friends_list_error_title));
                    friendsListAlertBuilder.setMessage((getString(R.string.friends_list_error_message)));
                    friendsListAlertBuilder.setPositiveButton(android.R.string.ok,null);
                    AlertDialog friendsListAlert=friendsListAlertBuilder.create();
                    friendsListAlert.show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.recipients, menu);
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
