package framgia.imagemanagement;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
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
    public OnlineImageAdapter imageAdapter;
    // string to search
    private String strSearch;
    // string from edited text
    private EditText editText;
    // search button
    private Button searchBttn;
    //load more button
    private Button loadMoreButtn;
    //local search button
    private Button localSearchButton;
    // list of images
    private ArrayList<Object> listImageObj;
    // start index for load the next page, startIndex = pageNum*8, n is number of pages
    private byte startIndex;
    private int mVisibleThreshold = 5;
    private int mCurrentPage = 0;
    private int mPreviousTotal = 0;
    private boolean mLoading = true;
    private boolean mLastPage = false;
    private final static int ITEMS_PPAGE = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set view as activity_main.xml
        setContentView(R.layout.online_image_finder);
        // set view to controller
        setFindViewById();
        initValue();
    }

    public void setFindViewById() {
        // can edit the text in text box
        editText = (EditText) findViewById(R.id.editTextForSearch);
        //grid view
        gridView = (GridView) findViewById(R.id.gridViewOnline);
        //gridView.setOnScrollListener(this);
        // setup event to see image's detail
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showImagedetail(position);
            }
        });
        // can click the search button
        searchBttn = (Button) findViewById(R.id.online_search_button);
        searchBttn.setOnClickListener(clickListener);
        loadMoreButtn = (Button) findViewById(R.id.buttonLoadMore);
        loadMoreButtn.setOnClickListener(clickListener);
        loadMoreButtn.setVisibility(View.INVISIBLE);
        localSearchButton = (Button)findViewById(R.id.buttonBackLocal);
        localSearchButton.setOnClickListener(clickListener);
    }
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonBackLocal:
                    Intent intent = new Intent(OnlineActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.buttonLoadMore:
                    if (startIndex != 0) {
                        listImageObj.clear();
                        (new OnlineActivity.getImagesTask()).execute(new Void[0]);
                    }
                    break;
                case R.id.online_search_button:
                    // get string from edit text
                    strSearch = editText.getText().toString();

                    if (!strSearch.equals("")) {
                        listImageObj.clear();
                        startIndex = 0;
                        loadMoreButtn.setVisibility(View.VISIBLE);
                        // encode that key work to add to the link
                        strSearch = Uri.encode(strSearch);
                        (new OnlineActivity.getImagesTask()).execute(new Void[0]);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private void initValue() {
        // init image of index (ex: in page 1, the first image's index is 0)
        startIndex = 0;
        // init list of image's object
        listImageObj = new ArrayList<>();
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

    public ArrayList<Object> getImageListDetail(JSONArray resultArray) {
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
            ArrayList<Object> tempList = getImageList();
            for (int i = 0; i < tempList.size(); i++) {
                listImageObj.add(tempList.get(i));
            }
            // show image to the view
            //setup data source for Adapter
            imageAdapter = new OnlineImageAdapter(OnlineActivity.this, listImageObj);
            // set image's adapter to gridView
            gridView.setAdapter(imageAdapter);
        }
        protected ArrayList<Object> getImageList() {
            list = new ArrayList<>();
            try {
                // get a json object
                JSONObject e = this.json.getJSONObject("responseData");
                // get result of array from json object
                JSONArray resultArray = e.getJSONArray("results");
                // get a list of image from result array
                list = getImageListDetail(resultArray);
            } catch (JSONException var4) {
                var4.printStackTrace();
            }
            return list;
        }
    }

}