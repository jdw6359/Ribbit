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

import java.util.Date;
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

        //assign member variables
        mContext=context;
        mMessages=messages;

    }

    //we need to override this so that we are utilizing and inflating message_item.xml
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //follow 'ViewHolder' design pattern to adapt custom list layout
        ViewHolder holder;
        if(convertView==null){
            //create it for the first time
            convertView= LayoutInflater.from(mContext).inflate(R.layout.message_item,null);
            holder=new ViewHolder();
            holder.iconImageView=(ImageView)convertView.findViewById(R.id.messageIcon);
            holder.nameLabel=(TextView)convertView.findViewById(R.id.senderLabel);
            holder.timeLabel=(TextView)convertView.findViewById(R.id.timeLabel);
            convertView.setTag(holder);
        }else{
            //it already exists, we just need to change the data
            holder=(ViewHolder)convertView.getTag();
        }

        //get the message object in question
        ParseObject message=mMessages.get(position);

        //create date object
        // .getCreatedAt() is a special method of ParseObject since CreatedAt field is uniform
        Date createdAt=message.getCreatedAt();
        //create a time equivalent to now
        long now=new Date().getTime();
        //static helper method to get string representation of relative span
        String convertedDate= DateUtils.getRelativeTimeSpanString(createdAt.getTime(),now,DateUtils.SECOND_IN_MILLIS).toString();

        holder.timeLabel.setText(convertedDate);

        //decide which image to show (photo vs. video)
        if(message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE)){
            //the file is an image
            holder.iconImageView.setImageResource(R.drawable.ic_picture);
        }else{
            holder.iconImageView.setImageResource(R.drawable.ic_video);
        }

        holder.nameLabel.setText(message.getString(ParseConstants.KEY_SENDER_NAME));

        return convertView;
    }

    //follows view holder pattern such that view holder object will hold the state
    private static class ViewHolder{
        ImageView iconImageView;
        TextView nameLabel;
        TextView timeLabel;
    }

    public void refill(List<ParseObject> messages){
        //clear current data
        mMessages.clear();
        mMessages.addAll(messages);
        notifyDataSetChanged();
    }

}
