package woodward.joshua.ribbit.UI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import woodward.joshua.ribbit.Model.MessageAdapter;
import woodward.joshua.ribbit.Model.ParseConstants;
import woodward.joshua.ribbit.R;

/**
 * Created by Joshua on 12/16/2014.
 */
public class InboxFragment extends android.support.v4.app.ListFragment {

    protected List<ParseObject> mMessages;
    protected SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.inbox_fragment, container, false);

        //because this is not an activity, we mush use findViewById on the rootView
        mSwipeRefreshLayout=(SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        //set the color scheme of the "loader"
        mSwipeRefreshLayout.setColorScheme(R.color.swipeRefresh1,R.color.swipeRefresh2,R.color.swipeRefresh3,R.color.swipeRefresh4);
        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();

        getActivity().setProgressBarIndeterminateVisibility(true);
        retrieveMessages();

    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        //determine if image or video
        ParseObject message=mMessages.get(position);
        String messageType=message.getString(ParseConstants.KEY_FILE_TYPE);
        ParseFile file=message.getParseFile(ParseConstants.KEY_FILE);
        Uri fileUri= Uri.parse(file.getUrl());

        if(messageType.equals(ParseConstants.TYPE_IMAGE)){
            //view the image
            Intent viewImageIntent=new Intent(getActivity(),ViewImageActivity.class);
            viewImageIntent.setData(fileUri);
            startActivity(viewImageIntent);
        }else{
            //view the video
            Intent intent=new Intent(Intent.ACTION_VIEW,fileUri);
            intent.setDataAndType(fileUri,"video/*");
            startActivity(intent);
        }

        //delete the message
        List<String> ids=message.getList(ParseConstants.KEY_RECIPIENT_IDS);

        //check the count of recipients
        if(ids.size()==1){
            //last recipient, delete the file from the backend
            //crossing our fingers and hoping that it will delete in the background
            message.deleteInBackground();

        }else{
            //remove the recipient and save
            ids.remove(ParseUser.getCurrentUser().getObjectId());

            ArrayList<String> idsToRemove=new ArrayList<String>();
            idsToRemove.add(ParseUser.getCurrentUser().getObjectId());

            message.removeAll(ParseConstants.KEY_RECIPIENT_IDS,idsToRemove);
            message.saveInBackground();
        }
    }

    protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener=new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            retrieveMessages();
        }
    };

    // method to retrieve and set messages in the list view
    private void retrieveMessages() {
        //query the Parse message class
        //only pull messages where we are in the list of recipients.
        ParseQuery<ParseObject> inboxQuery= new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
        //Parse will do the work of looking at ALL the recipient ids and determine if current user is in set
        inboxQuery.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        //orders results by created date, descending
        inboxQuery.orderByDescending(ParseConstants.KEY_CREATED_AT);
        inboxQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                getActivity().setProgressBarIndeterminateVisibility(false);

                //if refresh animation is occuring, kill it
                if(mSwipeRefreshLayout.isRefreshing()){
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                //if message is valid, build list here
                if(e==null){
                    //successful, we found messages
                    mMessages=messages;
                    String[] usernames=new String[mMessages.size()];
                    //initialize counter, and add ParseUser.username to list
                    int i=0;
                    for(ParseObject message: mMessages){
                        usernames[i]=message.getString(ParseConstants.KEY_SENDER_NAME);
                        i++;
                    }

                    //check to see if list view has an adapter
                    if(getListView().getAdapter()==null){
                        MessageAdapter inboxAdapter=new MessageAdapter(getListView().getContext(),mMessages);
                        setListAdapter(inboxAdapter);
                    }else{
                        ((MessageAdapter)getListView().getAdapter()).refill(mMessages);
                    }
                }else{
                    //didnt find any messages
                }
            }
        });
    }
}
