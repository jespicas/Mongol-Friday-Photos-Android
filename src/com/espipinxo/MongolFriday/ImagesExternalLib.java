package com.espipinxo.MongolFriday;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Gallery;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

//import android.support.v4.view.MenuItemCompat;
//import android.support.v7.widget.ShareActionProvider;

/**
 * Created with IntelliJ IDEA.
 * User: espi
 * Date: 8/11/13
 * Time: 23:17
 * To change this template use File | Settings | File Templates.
 */
public class ImagesExternalLib extends Activity {
    ArrayList<PhotoMongol> ListUrlImage = new ArrayList<PhotoMongol>();
    private String PathImages;
    private ShareActionProvider mShareActionProvider;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.menu, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.action_share);

        // Fetch and store ShareActionProvider
      //  mShareActionProvider = (android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(item); // item.getActionProvider();

        // Return true to display menu
        return true;
    }

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private void SaveListtoFile(String NameandPathFiile)
    {
        Writer writer = null;
        String content = "";
        try {

            writer = new FileWriter(NameandPathFiile);
            for(PhotoMongol photo : ListUrlImage) {
                content = content +  photo.Mname + "\r\n";
            }
            writer.write(content);

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

    private void ReadListImagesfromFile(String NameandPathFiile)
    {
        //Find the directory for the SD Card using the API
        //Get the text file
        File file = new File(NameandPathFiile);

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                // If exists image from line ... add filename not URL
                String NameImage = line.split("/")[line.split("/").length-1];
                PhotoMongol photo = new PhotoMongol();
                photo.MPath = this.PathImages;

                File checkImage = new File(PathImages + "/" + NameImage);
                if (checkImage.length() == 0)
                {
                    checkImage.delete();
                }
                if(checkImage.exists() && checkImage.isFile()) {
                    photo.Mname = PathImages + "/" + NameImage;
                } else {
                    photo.Mname = line;
                }
                ListUrlImage.add(photo);
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.images);
        getIntent().getExtras().getString("SelectedUrl");
        String UrlWithImages =  getIntent().getExtras().getString("SelectedUrl");
        PathImages = Environment.getExternalStorageDirectory() + "/MongolFridayPhotos/"+ UrlWithImages.replace("-","_");
        String PathImagesListImages = PathImages+ "/listImages.txt";
        File dir = new File(PathImagesListImages);

         if(dir.exists() && dir.isFile()) {
            Toast.makeText(this, "Exists", Toast.LENGTH_SHORT).show();
             ReadListImagesfromFile(PathImagesListImages);
/*            for (PhotoMongol temp : ListUrlImage) {
                String imgname = "";
                if (temp.Mname.contains("https")) {
                    String imgname = temp.Mname.split("/")[temp.Mname.split("/").length-1];
                }

           //     File img = new File()

            }
            */
         } else {

             dir = new File(PathImages);
             dir.mkdirs();
             GetAllImagesFromUrl(UrlWithImages);
             SaveListtoFile(PathImages+"/listImages.txt");
        }

            TextView textItems = (TextView) findViewById(R.id.TextItmes);
            textItems.setTag(PathImages);

            // textItems.setText("dsfadfs");
            Gallery g = (Gallery) findViewById(R.id.listImagesMongol);
            getIntent().getExtras().putString("PathImages",PathImages);
            g.setAdapter(new MyAdapter(getApplicationContext(), R.layout.itemimages, ListUrlImage));
       // g.setOnItemSelectedListener(this);
        /*
        ListView a = (ListView) findViewById(R.id.listImagesMongol);
        MyAdapter adapter = new MyAdapter(getApplicationContext(),R.layout.itemimages,ListUrlImage);
        adapter.notifyDataSetChanged();
        a.setAdapter(adapter);
        */
        /*a.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text

               // Toast.makeText(getApplicationContext(),
               //        (String) ((ImageView) view).getTag(), Toast.LENGTH_SHORT).show();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                shareIntent.setType("image*//*");

                // For a file in shared storage.  For data in private storage, use a ContentProvider.
                Uri uri = Uri.parse(ListUrlImage.get(position));
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(shareIntent);

                *//*
                Intent i = new Intent(getApplicationContext(), LlistaImages.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("SelectedUrl",ruls.get(position));
                startActivity(i);
                //  Main.this.finish();
                *//*
            }
        });*/
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        return;
    }

    private void GetAllImagesFromDisk(String Path)
    {
        File dir = new File(PathImages);
        for ( int i = 0; i < dir.listFiles().length; i++) {
            PhotoMongol photo = new PhotoMongol();
            photo.MPath = this.PathImages;
            photo.Mname = dir.listFiles()[i].getName();
            ListUrlImage.add(photo);
        }

    }
    private void GetAllImagesFromUrl(String Url)
    {
        try{
            Document Doc;
            Doc = Jsoup.connect("http://www.machacas.org/" + Url).get();
            //Doc.select("img.lazy.data-lazy-ready");
            Elements pargrf = Doc.getElementsByClass("entry-content");

            if (pargrf.size() > 0)
            {
               Elements imgs =  pargrf.get(0).getElementsByTag("img");
                for ( int i = 0; i < imgs.size(); i++)
                {
                    Log.i("Info",imgs.get(i).className().toString());
                    if (imgs.get(i).attr("class").toString().equals("lazy lazy-hidden")) {

                    } else {
                        PhotoMongol photo = new PhotoMongol();
                        photo.MPath = this.PathImages;
                        photo.Mname = imgs.get(i).attr("src").toString();
                        ListUrlImage.add(photo);
                    }
                }
            }
        } catch (Exception Ex)
        {
        }
    }
}
