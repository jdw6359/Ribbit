package woodward.joshua.ribbit.UI;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import woodward.joshua.ribbit.R;

/**
 * Created by Joshua on 12/16/2014.
 */
public class InboxFragment extends android.support.v4.app.ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.inbox_fragment, container, false);
        return rootView;
    }
}
