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
import java.util.Arrays;
import java.util.Collections;

//http://javatechig.com/android/universal-image-loader-library-in-android
//https://github.com/nostra13/Android-Universal-Image-Loader
//http://stackoverflow.com/questions/19670899/share-image-with-shareactionprovider-from-universal-image-loader
//http://stackoverflow.com/questions/14607763/save-and-share-images-universal-image-loader
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
    Button btnMore;
    String CurrentUrl = Constants.Url_MongolFriday;
    int CurrentPagination = 1;
    int MaxPages = 0;

    private void SaveLog(String ValuetoSave)
    {
        Writer writer = null;
        File dir = new File(Environment.getExternalStorageDirectory() + File.separator + "myapp.log");
        try {
            if(dir.exists()) {
                writer = new FileWriter(Environment.getExternalStorageDirectory() + File.separator + "myapp.log",true);
            } else {
                writer = new FileWriter(Environment.getExternalStorageDirectory() + File.separator + "myapp.log");
            }

            //writer.append(ValuetoSave);
            writer.write(ValuetoSave + System.getProperty( "line.separator" ));
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

    private int GetMaxPagesfromWebsite()
    {
        int toReturn = 0;
        try {
            Elements options = Jsoup.connect(Constants.Url_MongolFriday).get().select("div.pagination a");
            int lastHref = options.last().attributes().get("href").split("/").length;
            String LastPagination = options.last().attributes().get("href").split("/")[lastHref - 1].toString();
            try {
                toReturn = Integer.parseInt(LastPagination);
            } catch(NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    private int GetLastPosition()
    {

        return 0;
    }
    private boolean Exists(String ValuetoExists)
    {
        boolean toReturn = false;
        for(String item : ruls) {
            if ( item.equals(ValuetoExists))
            {
                toReturn = true;
            }
        }
        return toReturn;
    }
    private void UpdateLastPosition()
    {
        /*
        Pagina 10 i hi havia 85 pagines max
al cap 3 setmana
Max pagines 87
Son 2 pagines de Mes... i per tant el 10 sera el 12 pero he de fer 2 ,3 i 13 i llavors anar summant.

         */
        File dir = new File(Constants.Path_App_Saving_Images +"/Pagination.txt");
        int PaginationFile = 0;
        int MaxPagesFile = 0;
        if(dir.exists() && dir.isFile()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(Constants.Path_App_Saving_Images +"/Pagination.txt"));
                String line;
                line = br.readLine();
                br.close();
                try {
                    PaginationFile = Integer.parseInt(line.split(",")[0]);
                    MaxPagesFile = Integer.parseInt(line.split(",")[1]);

                    int DiffNewPages = MaxPages - MaxPagesFile;
                    if ( DiffNewPages == 0)
                    {
                     CurrentPagination = PaginationFile;
                     File folderdMongol = new File(Constants.Path_App_Saving_Images);
                        File[] files = folderdMongol.listFiles();
                        Arrays.sort(files, Collections.reverseOrder() );

                        for (File inFile : files) {
                            if (inFile.isDirectory()) {
                                String Image = inFile.toString().replace("_","-").replace(Constants.Path_App_Saving_Images+"/","");
                                if (!Exists(Image))
                                {
                                    ruls.add(Image);
                                }
                            }
                        }
                    } else {
                        CurrentPagination = PaginationFile + DiffNewPages;
                        for (int i = 2; i < DiffNewPages; i++) {
                            GetPostsFromPagination(i);
                        }
                        GetPostsFromPagination(CurrentPagination+1);
                        UpdatePagination();
                    }
                } catch (NumberFormatException nfe){
                    System.out.println("Could not parse " + nfe);
                }
            }
            catch (IOException e) {
                //You'll need to add proper error handling here
            }
        } else {

        }

    }
    private void UpdatePagination()
    {
        Writer writer = null;
        try {
            writer = new FileWriter(Constants.Path_App_Saving_Images +"/Pagination.txt");
            writer.write(String.valueOf(CurrentPagination)+","+String.valueOf(MaxPages));
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

        SaveLog("Init");
        File dir = new File(Constants.Path_App_Saving_Images);
        if(dir.exists() && dir.isDirectory()) {
            Toast.makeText(this, "Exists", Toast.LENGTH_SHORT).show(); // do something here
        } else {
            Toast.makeText(this, "Created", Toast.LENGTH_SHORT).show(); // do something here
            dir.mkdirs();
        }
        SaveLog("EndInit");
    }

    public void GetPostsFromPagination(int NumPage)
    {
        try {
            Elements options = Jsoup.connect("http://www.machacas.com/category/mongol-friday-photos/page/"+String.valueOf(NumPage)+"/").get().select("article div.blog-item-wrap div.post-inner-content header h2 a");
            for (Element temp : options) {
                String UrlFoto = temp.attributes().get("href").split("/")[3];
                String PathImages = Environment.getExternalStorageDirectory() + "/MongolFridayPhotos/"+ UrlFoto.replace("-","_");
                File dir = new File(PathImages);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                ruls.add(UrlFoto);
            }
            options.clear();

        }  catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void MorePosts(View v) {

        CurrentPagination = CurrentPagination+1;
        GetPostsFromPagination(CurrentPagination);
        listView.setSelection(adapter.getCount() - 1);
    }
    public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Initfolders();

        btnMore = (Button) findViewById(R.id.BtnMore);
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MorePosts(v);
                UpdatePagination();
            }
        });

        listView = (ListView) findViewById(R.id.listUrlsMongol);
        layout = (LinearLayout) findViewById(R.id.progressbar_view);

        ruls = new ArrayList<String>();
        MaxPages = GetMaxPagesfromWebsite();

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
            options.clear();

          }  catch (Exception e) {
             e.printStackTrace();
        }

        UpdateLastPosition();

        layout.setVisibility(View.GONE);
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_expandable_list_item_1, ruls);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                    Intent i = new Intent(getApplicationContext(), PagerImages.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("SelectedUrl", ruls.get(position));
                    startActivity(i);
            }
        });
    }
}