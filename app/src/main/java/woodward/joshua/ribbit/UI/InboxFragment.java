package woodward.joshua.ribbit.UI;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import woodward.joshua.ribbit.Model.ParseConstants;
import woodward.joshua.ribbit.R;

/**
 * Created by Joshua on 12/16/2014.
 */
public class InboxFragment extends android.support.v4.app.ListFragment {

    protected List<ParseObject> mMessages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.inbox_fragment, container, false);
        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();

        getActivity().setProgressBarIndeterminateVisibility(true);

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
                    //Create an array adapter of type String, give params (Context, ListType, Source)
                    ArrayAdapter<String> friendsAdapter=new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_1, usernames);
                    //setListAdapter() is method of ListActivity (the class this activity inherits from)
                    setListAdapter(friendsAdapter);
                }else{
                    //fuuuuuuck
                }
            }
        });
    }
}
