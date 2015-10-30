package framgia.imagemanagement;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by FRAMGIA\luu.vinh.loc on 20/10/2015.
 */
public class OnlineImageAdapter extends BaseAdapter {
    private Context mContext;
    public ArrayList<Object> listImageObj;

    public OnlineImageAdapter(Context mContext, ArrayList<Object> listImageObj) {
        this.mContext = mContext;
        this.listImageObj = listImageObj;
    }

    public void addImage(ArrayList<Object> addlist) {
        for (int i = 0; i < addlist.size(); i++) {
            listImageObj.add(addlist.get(i));
        }
    }

    @Override
    public int getCount() {
        return listImageObj.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imgView;
        if (convertView == null) {
            imgView = new ImageView(mContext);
            // scale image
            imgView.setLayoutParams(new GridView.LayoutParams(400, 400));
            imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imgView.setPadding(10, 10, 10, 10);
            GoogleImageBean imageBean = (GoogleImageBean) this.listImageObj.get(position);
            Picasso.with(mContext)
                    .load(imageBean.getUrl())
                    .into(imgView);
        } else {
            imgView = (ImageView) convertView;
        }
        return imgView;
    }
}