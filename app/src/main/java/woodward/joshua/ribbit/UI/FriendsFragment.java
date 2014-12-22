package woodward.joshua.ribbit.UI;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

import woodward.joshua.ribbit.Model.ParseConstants;
import woodward.joshua.ribbit.R;

/**
 * Created by Joshua on 12/16/2014.
 */
public class FriendsFragment extends ListFragment {

    public static final String TAG=FriendsFragment.class.getSimpleName();

    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected List<ParseUser> mFriends;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.friends_fragment, container, false);
        return rootView;
    }

    public void onResume(){
        super.onResume();

        mCurrentUser=ParseUser.getCurrentUser();
        mFriendsRelation=mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        getActivity().setProgressBarIndeterminateVisibility(true);

        ParseQuery<ParseUser> friendsQuery=mFriendsRelation.getQuery();
        friendsQuery.addAscendingOrder(ParseConstants.KEY_USERNAME);

        friendsQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                getActivity().setProgressBarIndeterminateVisibility(false);

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
                    ArrayAdapter<String> friendsAdapter=new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_1, usernames);
                    //setListAdapter() is method of ListActivity (the class this activity inherits from)
                    setListAdapter(friendsAdapter);
                }else{
                    Log.e(TAG,e.getMessage());
                    AlertDialog.Builder friendsListAlertBuilder=new AlertDialog.Builder(getListView().getContext());
                    friendsListAlertBuilder.setTitle(getString(R.string.friends_list_error_title));
                    friendsListAlertBuilder.setMessage((getString(R.string.friends_list_error_message)));
                    friendsListAlertBuilder.setPositiveButton(android.R.string.ok,null);
                    AlertDialog friendsListAlert=friendsListAlertBuilder.create();
                    friendsListAlert.show();
                }
            }
        });
    }
}
