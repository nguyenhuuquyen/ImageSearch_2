package framgia.imagemanagement;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by FRAMGIA\nguyen.huu.quyen on 19/10/2015.
 */
public class ImageAdapter extends PagerAdapter {
    Context context;
    ImageProcessing imageProcess;
    ArrayList<String> imageList;

    ImageAdapter(Context context, ArrayList<String> imgList) {
        this.context = context;
        this.imageProcess = new ImageProcessing();
        imageList = imgList;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        TouchImageView imageView = new TouchImageView(context);
        String imagePath = context.getString(R.string.image_filepath_format, Environment.getExternalStorageDirectory().toString(), context.getString(R.string.photo_folder), imageList.get(position).toString());
        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            //Bitmap myBitmap = BitmapFactory.decodeFile(imagePath);
            Bitmap imageBitmap = imageProcess.decodeSampledBitmapFromResource(imagePath, 500, 500);
            //imageBitmap = imageProcess.rotateImage(imageBitmap,imagePath);
            imageView.setImageBitmap(imageBitmap);
        } else {
            imageView.setImageResource(R.drawable.image_icon);
        }
        ((ViewPager) container).addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }

}
