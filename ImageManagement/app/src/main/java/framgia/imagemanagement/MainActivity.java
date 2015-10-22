package framgia.imagemanagement;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    GridView localGridView;
    EditText localEditTextSearch;
    Button localButtonSearch;
    Button onlineModeButton;
    ArrayList<String> localImageList;
    ArrayList<String> localResultImageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createLocalFolder();
        setfindViewById();
        listAllFileInDownloadFolder();
        displayListImage(localImageList);
    }

    public void setfindViewById() {
        localImageList = new ArrayList<>();
        localResultImageList = new ArrayList<>();
        //GridView
        onlineModeButton = (Button) findViewById(R.id.buttonOnline);
        onlineModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OnlineActivity.class);
                startActivity(intent);
            }
        });
        localGridView = (GridView) findViewById(R.id.loCal_gridview);
        localGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DisplayImage.class);
                Bundle bundle = new Bundle();
                bundle.putInt(getString(R.string.intent_current_image), position);
                bundle.putStringArrayList(getString(R.string.intent_image_list), localResultImageList);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        //edit text
        localEditTextSearch = (EditText) findViewById(R.id.local_editText_Search);
        //Button
        localButtonSearch = (Button) findViewById(R.id.local_button_search);
        localButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (localEditTextSearch.getText().toString().equals("")) {
                    localResultImageList = localImageList;
                } else {
                    searchLocalImage(localEditTextSearch.getText().toString());
                }
                displayListImage(localResultImageList);
            }
        });
    }

    public void displayListImage(ArrayList<String> imageList) {
        localGridView.setAdapter(new CustomGridViewAdapter(this, imageList));

    }

    public void listAllFileInDownloadFolder() {
        String path = getString(R.string.folder_filepath_format, Environment.getExternalStorageDirectory().toString(), getString(R.string.photo_folder));
        File f = new File(path);
        File file[] = f.listFiles();
        for (int i = 0; i < file.length; i++) {
            localImageList.add(i, file[i].getName());
            localResultImageList.add(i, file[i].getName());
        }
    }

    public void searchLocalImage(String keyWords) {
        localResultImageList.clear();
        for (int i = 0; i < localImageList.size(); i++) {
            if (localImageList.get(i).contains(keyWords)) {
                localResultImageList.add(localImageList.get(i));
            }
        }
    }

    public void createLocalFolder() {
        DataCopyer dataCopyer = new DataCopyer(this);
        dataCopyer.copyFileOrDir(getString(R.string.photo_folder));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
