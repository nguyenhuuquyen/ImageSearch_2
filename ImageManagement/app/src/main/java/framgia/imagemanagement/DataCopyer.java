package framgia.imagemanagement;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by FRAMGIA\nguyen.huu.quyen on 26/10/2015.
 */
public class DataCopyer {
    private Context c;
    private static int ARRAYSIZE = 1024;

    public DataCopyer(Context context) {
        c = context;
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[ARRAYSIZE];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    final static String TARGET_BASE_PATH =
            Environment.getExternalStorageDirectory().getAbsolutePath();

    public void copyFileOrDir(String path) {
        AssetManager assetManager = c.getAssets();
        String assets[] = null;
        try {
            assets = assetManager.list(path);
            String fullPath = TARGET_BASE_PATH + File.separator + path;
            File dir = new File(fullPath);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.i("tag", "could not create dir " + fullPath);
                }
            }
            for (int i = 0; i < assets.length; i++) {
                String assetPath = path + File.separator + assets[i];
                String destPath = fullPath + File.separator + assets[i];
                copyFile(assetPath, destPath);
            }
        } catch (IOException ex) {
            Log.e("tag", "I/O Exception", ex);
        }
    }

    private void copyFile(String filename, String dest) {
        AssetManager assetManager = c.getAssets();
        InputStream in;
        OutputStream out;
        try {
            in = assetManager.open(filename);
            out = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}