package woodward.joshua.ribbit.Model;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import woodward.joshua.ribbit.R;

/**
 * Created by Joshua on 12/24/2014.
 */
public class UserAdapter extends ArrayAdapter<ParseUser> {

    protected Context mContext;
    protected List<ParseUser> mUsers;

    public UserAdapter(Context context, List<ParseUser> users) {
        super(context, R.layout.message_item,users);

        //assign member variables
        mContext=context;
        mUsers =users;
    }

    //we need to override this so that we are utilizing and inflating message_item.xml
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //follow 'ViewHolder' design pattern to adapt custom list layout
        ViewHolder holder;
        if(convertView==null){
            //create it for the first time
            convertView= LayoutInflater.from(mContext).inflate(R.layout.user_item,null);
            holder=new ViewHolder();
            holder.userImageView=(ImageView)convertView.findViewById(R.id.userImageView);
            holder.nameLabel=(TextView)convertView.findViewById(R.id.nameLabel);
            convertView.setTag(holder);
        }else{
            //it already exists, we just need to change the data
            holder=(ViewHolder)convertView.getTag();
        }

        //get the message object in question
        ParseUser user= mUsers.get(position);

        String email=user.getEmail().toLowerCase();
        if(email.equals("")){
            //use the default image
            holder.userImageView.setImageResource(R.drawable.avatar_empty);
        }else{
            String hash=MD5Util.md5Hex(email);
            String imageSize="204";
            String returnParam="404";
            String gravatarUrl="http://www.gravatar.com/avatar/"+hash+"?s=" + imageSize + "&d=" + returnParam;

            Picasso.with(mContext).load(gravatarUrl).placeholder(R.drawable.avatar_empty).into(holder.userImageView);
        }




        holder.nameLabel.setText(user.getUsername());

        return convertView;
    }

    //follows view holder pattern such that view holder object will hold the state
    private static class ViewHolder{
        ImageView userImageView;
        TextView nameLabel;

    }

    public void refill(List<ParseUser> users){
        //clear current data
        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();
    }

}
