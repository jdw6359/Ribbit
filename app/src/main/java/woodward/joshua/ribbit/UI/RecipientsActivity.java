package woodward.joshua.ribbit.UI;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import woodward.joshua.ribbit.Model.FileHelper;
import woodward.joshua.ribbit.Model.ParseConstants;
import woodward.joshua.ribbit.R;

public class RecipientsActivity extends ListActivity {

    protected Uri mMediaUri;
    protected String mFileType;

    public static final String TAG= RecipientsActivity.class.getSimpleName();

    protected MenuItem mSendMenuItem;

    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected List<ParseUser> mFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.user_grid);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        mMediaUri=getIntent().getData();
        mFileType=getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);
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
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        int recipientCount=l.getCheckedItemCount();
        if(recipientCount>0){
            //show send action button
            mSendMenuItem.setVisible(true);
        }else{
            //do not show send action button
            mSendMenuItem.setVisible(false);
        }
    }

    protected ParseObject createMessage(){

        ParseObject message=new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID,ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME,ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECIPIENT_IDS,getRecipientIds());
        message.put(ParseConstants.KEY_FILE_TYPE,mFileType);

        //get a byte array from FileHelper class
        byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);
        if(fileBytes==null){
            //send back null value
            return null;
        }else{
            //file bytes has data
            //ready to create parse file
            if(mFileType.equals(ParseConstants.TYPE_IMAGE)){
                //we have an image
                //reduces the size of the file
                fileBytes=FileHelper.reduceImageForUpload(fileBytes);
            }

            String fileName=FileHelper.getFileName(this,mMediaUri, mFileType);

            //create ParseFile object
            ParseFile mediaFile=new ParseFile(fileName, fileBytes);
            //add the file to the message
            message.put(ParseConstants.KEY_FILE,mediaFile);

            //returned is a ParseObject of class ParseConstants.CLASS_MESSAGES
            return message;
        }
    }

    protected ArrayList<String> getRecipientIds(){
        ArrayList<String> recipientIds=new ArrayList<String>();
        //loop through checked items in list and append to recipientIds
        for(int i=0;i<getListView().getCount();i++){
            if(getListView().isItemChecked(i)){
                recipientIds.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientIds;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.recipients, menu);
        mSendMenuItem=menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_send) {
            //create a parse object for our "message" (file)
            ParseObject message=createMessage();
            if(message==null){
                AlertDialog.Builder messageAlertBuilder=new AlertDialog.Builder(RecipientsActivity.this);
                messageAlertBuilder.setTitle(getString(R.string.message_error_title));
                messageAlertBuilder.setMessage(getString(R.string.message_error_message));
                messageAlertBuilder.setPositiveButton(android.R.string.ok,null);
                AlertDialog messageAlert=messageAlertBuilder.create();
                messageAlert.show();
            }else{
                send(message);
                //finishes the activity and loads up the last Activity in the stack
                finish();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //given a ParseObject, method will save message to backend
    protected void send(ParseObject message){
        //uploads ParseObject "message" to backend
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                //if there was an exception,
                if(e==null){
                    //successful upload
                    Toast.makeText(RecipientsActivity.this, "Message Sent",Toast.LENGTH_LONG).show();
                }else{
                    //unsuccessful upload
                    AlertDialog.Builder uploadAlertBuilder=new AlertDialog.Builder(RecipientsActivity.this);
                    uploadAlertBuilder.setTitle(getString(R.string.upload_error_title));
                    uploadAlertBuilder.setMessage(getString(R.string.upload_error_message));
                    uploadAlertBuilder.setPositiveButton(android.R.string.ok,null);
                    AlertDialog uploadAlert=uploadAlertBuilder.create();
                    uploadAlert.show();
                }
            }
        });
    }


}
