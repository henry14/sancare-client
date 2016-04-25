package za.co.sanlam.account;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by henry14 on 11/8/14.
 */
public class PanelAdapter extends BaseAdapter {

    private List<String> data;
    private Context context;

    public PanelAdapter(Context context, int resourceId, List<String> data){
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String rowItem = getItem(position);
        ViewHolder mViewHolder = null;
        LayoutInflater mInfalter = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            mViewHolder = new ViewHolder();
            convertView = mInfalter.inflate(R.layout.sub_list_item, null);
            mViewHolder.mItemTextView = (TextView) convertView.findViewById(R.id.textViewA);
            //mViewHolder.mCountTextView = (TextView) convertView.findViewById(R.id.item_count);
           // mViewHolder.mItemImageView = (ImageView) convertView.findViewById(R.id.item_image);

            convertView.setTag(mViewHolder);

        }
        else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.mItemTextView.setText(rowItem);
        //mViewHolder.mCountTextView.setText("4");

        return null;
    }

    private class ViewHolder{
        private TextView mItemTextView;//, mCountTextView;
       // private ImageView mItemImageView;

    }
}
