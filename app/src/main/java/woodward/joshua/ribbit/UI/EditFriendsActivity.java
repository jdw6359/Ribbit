package woodward.joshua.ribbit.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import woodward.joshua.ribbit.Model.ParseConstants;
import woodward.joshua.ribbit.R;

public class EditFriendsActivity extends ListActivity {

    public static final String TAG=EditFriendsActivity.class.getSimpleName();

    protected List<ParseUser> mUsers;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_edit_friends);

        //gets us the default list view associated with this activity
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setProgressBarIndeterminateVisibility(true);

        mCurrentUser=ParseUser.getCurrentUser();
        mFriendsRelation=mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        //create Parse query to get users
        ParseQuery<ParseUser> query= ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.setLimit(ParseConstants.LIMIT_FRIENDS);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                setProgressBarIndeterminateVisibility(false);
                //check if exception is null, then success
                if(e==null){
                    //we have users to display
                    //set member variable to list of ParseUsers returned
                    mUsers=parseUsers;
                    //initialize array of strings the size of returned ParseUsers
                    String[] usernames=new String[mUsers.size()];
                    //initialize counter, and add ParseUser.username to list
                    int i=0;
                    for(ParseUser user: mUsers){
                        usernames[i]=user.getUsername();
                        i++;
                    }
                    //Create an array adapter of type String, give params (Context, ListType, Source)
                    ArrayAdapter<String> friendsAdapter=new ArrayAdapter<String>(EditFriendsActivity.this, android.R.layout.simple_list_item_checked, usernames);
                    //setListAdapter() is method of ListActivity (the class this activity inherits from)
                    setListAdapter(friendsAdapter);
                    //sets check marks for friend relationships
                    setFriendCheckmarks();

                }else{
                    //log the exception
                    Log.e(TAG, e.getMessage());
                    //create an alert dialog for the user
                    AlertDialog.Builder parseUserAlertBuilder=new AlertDialog.Builder(EditFriendsActivity.this);
                    parseUserAlertBuilder.setTitle(R.string.edit_friends_error_title);
                    parseUserAlertBuilder.setMessage(R.string.edit_friends_error_message);
                    parseUserAlertBuilder.setPositiveButton(android.R.string.ok,null);
                    AlertDialog parseUserAlert=parseUserAlertBuilder.create();
                    parseUserAlert.show();
                }
            }
        });
    }

    private void setFriendCheckmarks() {
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if(e==null){
                    //success, find a match
                    for(int i=0;i<mUsers.size();i++){
                        ParseUser user=mUsers.get(i);
                        for(ParseUser friend:parseUsers){
                            if(friend.getObjectId().equals(user.getObjectId())){
                                //need to set the checkmark
                                getListView().setItemChecked(i,true);
                            }
                        }
                    }
                }else{
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        //check to see if item is checked
        //note: in super.onListItemClick(), item is toggled.
        if(getListView().isItemChecked(position)){
            //adds friend
            mFriendsRelation.add(mUsers.get(position));
        }else{
            //removes friend
            mFriendsRelation.remove(mUsers.get(position));
        }
        //regardless of whether we are adding or removing a relationship,
        //save the state in the background here
        mCurrentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    //there was an exception, lets log
                    Log.e(TAG,e.getMessage());
                }
            }
        });
    }

}
