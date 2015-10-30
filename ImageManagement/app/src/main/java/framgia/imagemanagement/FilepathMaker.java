package framgia.imagemanagement;
import android.os.Environment;
import java.io.File;

/**
 * Created by FRAMGIA\luu.vinh.loc on 30/10/2015.
 */
public class FilepathMaker {
    String filepath;
    String foldername;
    File root;
    public FilepathMaker(String foldername) {
        this.foldername = foldername;
    }
    public void make() {
        root = new File(Environment.getExternalStorageDirectory()
            + File.separator + foldername + File.separator);
        if (!root.exists()) {
            root.mkdirs();
        }
    }
    public String getFilepath() {
        filepath = root.getPath();
        return filepath;
    }
}
