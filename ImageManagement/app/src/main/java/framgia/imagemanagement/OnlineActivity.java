package framgia.imagemanagement;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class OnlineActivity extends Activity {
    // number of image per page we want to get
    private final static byte imageResultNum = 8;
    private GridView gridView;
    // adapter for gridView
    public OnlineImageAdapter imageAdapter = null;
    // save bundle backup for MainActivity
    public Bundle mBackupBundle;
    // string to search
    private String strSearch;
    // string from preview Searched process, save to compare to present string search
    private String previewStrSearch;
    // string from edited text
    private EditText editText;
    // search button
    private Button searchBttn;
    // list of images
    private ArrayList<Object> listImageObj;
    // start index for load the next page, startIndex = pageNum*8, n is number of pages
    private byte startIndex;
    // number of page
    private byte numPage = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //save savedInstanceState into myBackupBundle
        mBackupBundle = savedInstanceState;
        // set view as activity_main.xml
        setContentView(R.layout.online_image_finder);
        // set view to controller
        setFindViewById();
        initValue();
    }

    public void setFindViewById() {
        // can edit the text in text box
        editText = (EditText) findViewById(R.id.editTextForSearch);
        // can click the search button
        searchBttn = (Button) findViewById(R.id.search_button);
        searchBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get string from edit text
                strSearch = editText.getText().toString();
                listImageObj.clear();
                startIndex = 0;
                // save value and clear search result if needed
                if (!previewStrSearch.equals(strSearch)) {
                    previewStrSearch = strSearch;
                }
                // encode that key work to add to the link
                strSearch = Uri.encode(strSearch);
                (new OnlineActivity.getImagesTask()).execute(new Void[0]);
            }
        });
    }

    private void initValue() {
        // init image of index (ex: in page 1, the first image's index is 0)
        startIndex = 0;
        // init list of image's object
        listImageObj = new ArrayList<>();
        previewStrSearch = "";
    }

    public void showImagedetail(int position) {
        // get information of a image by GoogleImageBean from image list
        GoogleImageBean imageBean = (GoogleImageBean) this.listImageObj.get(position);
        Intent intent = new Intent(OnlineActivity.this, OnlineImageDisplayActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.imageUrl), imageBean.getUrl());
        bundle.putString(getString(R.string.imageTitle), imageBean.getTitle());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void setGridViewContent(ArrayList<Object> listImageObj) {
        gridView = (GridView) findViewById(R.id.gridViewOnline);
        //setup data source for Adapter
        imageAdapter = new OnlineImageAdapter(this, listImageObj);
        // set image's adapter to gridView
        gridView.setAdapter(imageAdapter);
        // setup event to see image's detail
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showImagedetail(position);
            }
        });
    }

    public ArrayList<Object> getImageList(JSONArray resultArray) {
        // get a list of image
        ArrayList listImages = new ArrayList();
        try {
            for (int e = 0; e < resultArray.length(); ++e) {
                // get a json object from result of array
                JSONObject obj = resultArray.getJSONObject(e);
                GoogleImageBean bean = new GoogleImageBean();
                // get title information
                bean.setTitle(obj.getString("titleNoFormatting"));
                // get image thumb URL information
                bean.setThumbUrl(obj.getString("tbUrl"));
                // get image
                bean.setUrl(obj.getString("unescapedUrl"));
                // add a bean (couple of information) to list images
                listImages.add(bean);
            }
            // return a list of image of URL and title
            return listImages;
        } catch (JSONException var6) {
            var6.printStackTrace();
            return null;
        }
    }

    public class getImagesTask extends AsyncTask<Void, Void, Void> {
        JSONObject json;
        ProgressDialog dialog;
        ArrayList<Object> list;

        public getImagesTask() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            // show a dialog tell to user wait for feedback's result
            this.dialog = ProgressDialog.show(OnlineActivity.this, "", getString(R.string.wait_msg));
        }

        protected Void doInBackground(Void... params) {
            createJsonObjFromUrl();
            return null;
        }

        protected void createJsonObjFromUrl() {
            try {
                StringBuilder builder = new StringBuilder();
                String line;
                // config the URL
                URL url = new URL(getString(R.string.base_google_image_api_request_link)
                        + ".0&q="
                        + OnlineActivity.this.strSearch
                        + "&rsz="
                        + imageResultNum
                        + "&start="
                        + startIndex);
                // the value of start index has been used, so renew that value to get another image
                startIndex += 8;
                URLConnection e = url.openConnection();
                // add request property to the connection
                e.addRequestProperty("Referer", getString(R.string.fram_web));
                // read from connection
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(e.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    Log.e("String", line);
                    builder.append(line);
                }
                // create a json object
                this.json = new JSONObject(builder.toString());
            } catch (MalformedURLException var7) {
                var7.printStackTrace();
            } catch (IOException var8) {
                var8.printStackTrace();
            } catch (JSONException var9) {
                var9.printStackTrace();
            }
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // if a dialog is showing
            if (this.dialog.isShowing()) {
                // dismiss that dialog
                this.dialog.dismiss();
            }
            getImageList();
            for (int i = 0; i < list.size(); i++) {
                listImageObj.add(getImageList().get(i));
            }
            // show image to the view
            setGridViewContent(listImageObj);
        }

        protected ArrayList<Object> getImageList() {
            list = new ArrayList<>();
            try {
                // get a json object
                JSONObject e = this.json.getJSONObject("responseData");
                // get result of array from json object
                JSONArray resultArray = e.getJSONArray("results");
                // get a list of image from result array
                list = OnlineActivity.this.getImageList(resultArray);
            } catch (JSONException var4) {
                var4.printStackTrace();
            }
            return list;
        }
    }
}