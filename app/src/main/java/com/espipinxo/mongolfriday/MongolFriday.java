package com.espipinxo.mongolfriday;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class MongolFriday extends ActionBarActivity {

    private static final String TAG = "MEDIA";
    private TextView tv;

    private ArrayList<String> ruls = null;
    private ArrayList<Volumen> ListVols = null;
    ListView listView;
    LinearLayout layout;
    ArrayAdapter adapter;

    Button btnMore;
    String CurrentUrl = Constants.Url_MongolFriday;
    int CurrentPagination = 1;
    int MaxPages = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mongol_friday);

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
        ListVols = new ArrayList<Volumen>();

        listView = (ListView) findViewById(R.id.listUrlsMongol);
        layout = (LinearLayout) findViewById(R.id.progressbar_view);

        ruls = new ArrayList<String>();

        new GetMaxPagesfromWebsite().execute();

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
    public void MorePosts(View v) {
        CurrentPagination = CurrentPagination+1;
        new UpdateListVol().execute();
    }
    private void Initfolders() {
        File dir = new File(com.espipinxo.mongolfriday.Constants.Path_App_Saving_Images);
        if (dir.exists() && dir.isDirectory()) {
          //  Toast.makeText(this, "Exists", Toast.LENGTH_SHORT).show(); // do something here
        } else {
          //  Toast.makeText(this, "Created", Toast.LENGTH_SHORT).show(); // do something here
            dir.mkdirs();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mongol_friday, menu);
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

    private Volumen GetFormattedVolumen(String NameVolHref)
    {
        Volumen vol = new Volumen();
        vol.Name = NameVolHref.replace("mongol-friday-photos-vol-","");
        String[] SplittedName = vol.Name.split("-");

        vol.Name = SplittedName[0] + ". ";
        for(int i=1; i < SplittedName.length; i++)
        {
            StringBuilder rackingSystemSb = new StringBuilder(SplittedName[i].toLowerCase());
            rackingSystemSb.setCharAt(0, Character.toUpperCase(rackingSystemSb.charAt(0)));
            vol.Name = vol.Name + " " + rackingSystemSb.toString();
        }
        vol.UrlVolumen = NameVolHref;

        return vol;
    }
    private class UpdateListVol extends AsyncTask<Void,Void, Document>
    {
        public UpdateListVol(){ super();}
        @Override
        protected Document doInBackground(Void... params) {
            Document doc = null;
            try {
                doc = Jsoup.connect(Constants.Url_MongolFriday+"/page/" + String.valueOf(CurrentPagination) + "/").get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document result){
            try {
                Elements options = result.select("article div.blog-item-wrap div.post-inner-content header h2 a");
                for (Element temp : options) {
                    String UrlFoto = temp.attributes().get("href").split("/")[3];
                    String PathImages = Environment.getExternalStorageDirectory() + "/MongolFridayPhotos/"+ UrlFoto.replace("-","_");
                    File dir = new File(PathImages);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    ListVols.add(GetFormattedVolumen(UrlFoto));
                    ruls.add(UrlFoto);
                }
                options.clear();

                listView.setSelection(adapter.getCount() - 1);
            }  catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private class GetMaxPagesfromWebsite extends AsyncTask<Void, Void, Document> {
        //int MaxPages = 0;
        private void GetMaxPagesfromJsoup(Document doc)
        {
           int toReturn = 0;
            try {

                Elements options = doc.select("div.pagination a");
                int lastHref = options.last().attributes().get("href").split("/").length;
                String LastPagination = options.last().attributes().get("href").split("/")[lastHref - 1].toString();
                try {
                    MaxPages = Integer.parseInt(LastPagination);
                } catch(NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }

        }
        public GetMaxPagesfromWebsite()
        {
            super();
         }
        @Override
        protected Document doInBackground(Void... params) {
            Document doc = null;
            try {
                doc = Jsoup.connect(Constants.Url_MongolFriday).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document result) {

            GetMaxPagesfromJsoup(result);
            try {
                Elements options = result.select("article div.blog-item-wrap div.post-inner-content header h2 a");
                for (Element temp : options) {
                    String UrlFoto = temp.attributes().get("href").split("/")[3];
                    String PathImages = Environment.getExternalStorageDirectory() + "/MongolFridayPhotos/"+ UrlFoto.replace("-","_");
                    File dir = new File(PathImages);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    ListVols.add(GetFormattedVolumen(UrlFoto));
                    ruls.add(UrlFoto);
                }
                options.clear();

            }  catch (Exception e) {
                e.printStackTrace();
            }

            UpdateLastPosition();

            layout.setVisibility(View.GONE);
            adapter = new ArrayAdapter<Volumen>(getApplicationContext(),
                    android.R.layout.simple_expandable_list_item_1,ListVols);

            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    Intent i = new Intent(getApplicationContext(), PagerImages.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("SelectedUrl", ListVols.get(position).UrlVolumen);
                    startActivity(i);
                }
            });
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
                            Arrays.sort(files, Collections.reverseOrder());

                            for (File inFile : files) {
                                if (inFile.isDirectory()) {
                                    String Image = inFile.toString().replace("_","-").replace(Constants.Path_App_Saving_Images+"/","");
                                    if (!Exists(Image))
                                    {
                                        ListVols.add(GetFormattedVolumen(Image));
                                        ruls.add(Image);
                                    }
                                }
                            }
                        } else {
                            CurrentPagination = PaginationFile + DiffNewPages;
                            /*
                            for (int i = 2; i < DiffNewPages; i++) {
                                GetPostsFromPagination(i);
                            }
                            */
                            new UpdateListVol().execute();
                            //GetPostsFromPagination(CurrentPagination+1);
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
    }

}
