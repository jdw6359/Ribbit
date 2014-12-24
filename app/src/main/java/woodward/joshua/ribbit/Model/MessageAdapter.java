package woodward.joshua.ribbit.Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

import woodward.joshua.ribbit.R;

/**
 * Created by Joshua on 12/24/2014.
 */
public class MessageAdapter extends ArrayAdapter<ParseObject> {

    protected Context mContext;
    protected List<ParseObject> mMessages;

    public MessageAdapter(Context context, List<ParseObject> messages) {
        super(context, R.layout.message_item, messages);

        mContext=context;
        mMessages=messages;

    }

    //we need to override this so that we are utilizing and inflating message_item.xml
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if(convertView==null){
            //create it for the first time
            convertView= LayoutInflater.from(mContext).inflate(R.layout.message_item,null);
            holder=new ViewHolder();
            holder.iconImageView=(ImageView)convertView.findViewById(R.id.messageIcon);
            holder.nameLabel=(TextView)convertView.findViewById(R.id.senderLabel);
        }else{
            //it already exists, we just need to change the data
            holder=(ViewHolder)convertView.getTag();
        }

        ParseObject message=mMessages.get(position);

        if(message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE)){
            //the file is an image
            holder.iconImageView.setImageResource(R.drawable.ic_action_picture);
        }else{
            holder.iconImageView.setImageResource(R.drawable.ic_action_play_over_video);
        }

        holder.nameLabel.setText(message.getString(ParseConstants.KEY_SENDER_NAME));

        return convertView;
    }

    //follows view holder pattern such that view holder object will hold the state
    private static class ViewHolder{
        ImageView iconImageView;
        TextView nameLabel;
    }

}
