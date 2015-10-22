package framgia.imagemanagement;

/**
 * Created by FRAMGIA\nguyen.huu.quyen on 19/10/2015.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class CustomGridViewAdapter extends BaseAdapter {

    ArrayList imageList;
    Context context;
    ImageProcessing imageProcess;
    private static LayoutInflater inflater = null;

    public CustomGridViewAdapter(Context context, ArrayList<String> imgList) {
        // TODO Auto-generated constructor stub
        imageList = imgList;
        this.context = context;
        imageProcess = new ImageProcessing();
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return imageList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder {
        TextView imageName;
        ImageView imageView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.local_gridview, null);
        holder.imageName = (TextView) rowView.findViewById(R.id.imageName);
        holder.imageView = (ImageView) rowView.findViewById(R.id.imageView);
        holder.imageName.setText(imageList.get(position).toString());
        //Display several data only
        String imagePath = context.getString(R.string.image_filepath_format, Environment.getExternalStorageDirectory().toString(), context.getString(R.string.photo_folder), imageList.get(position).toString());
        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            //Bitmap myBitmap = BitmapFactory.decodeFile(imagePath);
            Bitmap thumnailBitmap = imageProcess.decodeSampledBitmapFromResource(imagePath, 100, 100);
            holder.imageView.setImageBitmap(thumnailBitmap);
        } else {
            holder.imageView.setImageResource(R.drawable.image_icon);
        }
        return rowView;
    }
}