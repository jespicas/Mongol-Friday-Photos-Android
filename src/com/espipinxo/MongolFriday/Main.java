package com.espipinxo.MongolFriday;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.*;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

//http://javatechig.com/android/universal-image-loader-library-in-android
//https://github.com/nostra13/Android-Universal-Image-Loader
/**
 * Created with IntelliJ IDEA.
 * User: espi
 * Date: 3/11/13
 * Time: 9:23
 * To change this template use File | Settings | File Templates.
 */
public class Main extends Activity {
    private static final String TAG = "MEDIA";
    private TextView tv;
    private ArrayList<String> ruls = null;
    ListView listView;
    LinearLayout layout;
    ArrayAdapter adapter;
    String CurrentUrl = Constants.Url_MongolFriday;

    private void UpdateCurrentUrl()
    {
        Writer writer = null;
        try {

            writer = new FileWriter(Constants.Path_App_Saving_Images +"/currentUrl.txt");
            writer.write(CurrentUrl);

        } catch (IOException e) {

            System.err.println("Error writing the file : ");
            e.printStackTrace();

        } finally {

            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {

                    System.err.println("Error closing the file : ");
                    e.printStackTrace();
                }
            }

        }
    }
    private void Initfolders()
    {
        File dir = new File(Constants.Path_App_Saving_Images);
        if(dir.exists() && dir.isDirectory()) {
            Toast.makeText(this, "Exists", Toast.LENGTH_SHORT).show(); // do something here
        } else {
            dir.mkdirs();
        }
    }
    public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Initfolders();
        tv = (TextView) findViewById(R.id.TextView01);

        listView = (ListView) findViewById(R.id.listUrlsMongol);
        layout = (LinearLayout) findViewById(R.id.progressbar_view);

        ruls = new ArrayList<String>();
        try {
            Elements options = Jsoup.connect(CurrentUrl).get().select("article div.blog-item-wrap div.post-inner-content header h2 a");
            for (Element temp : options) {
                String UrlFoto = temp.attributes().get("href").split("/")[3];
                String PathImages = Environment.getExternalStorageDirectory() + "/MongolFridayPhotos/"+ UrlFoto.replace("-","_");
                File dir = new File(PathImages);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                ruls.add(UrlFoto);
            }
            ruls.add("More");
            options.clear();

          }  catch (Exception e) {
             e.printStackTrace();
        }
        layout.setVisibility(View.GONE);
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_expandable_list_item_1, ruls);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                if (ruls.get(position).equals("More")) {
                   // ruls.remove(ruls.size() -1);
                    try {
                        Elements Pagination = Jsoup.connect(CurrentUrl).get().select("div.pagination a");
                        CurrentUrl = Pagination.get(Pagination.size()-2).attributes().get("href");
                        UpdateCurrentUrl();
                        Elements options = Jsoup.connect(CurrentUrl).get().select("article div.blog-item-wrap div.post-inner-content header h2 a");
                        for (Element temp : options) {
                            String UrlFoto = temp.attributes().get("href").split("/")[3];
                            String PathImages = Environment.getExternalStorageDirectory() + "/MongolFridayPhotos/"+ UrlFoto.replace("-","_");
                            File dir = new File(PathImages);
                            if (!dir.exists()) {
                                dir.mkdirs();
                            }
                            ruls.add(UrlFoto);
                        }
                        ruls.add("More");
                        options.clear();

                    }  catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Intent i = new Intent(getApplicationContext(), PagerImages.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("SelectedUrl", ruls.get(position));
                    startActivity(i);
                }
            }
        });
    }
}