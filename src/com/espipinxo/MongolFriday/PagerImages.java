package com.espipinxo.MongolFriday;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
//import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ShareActionProvider;

import android.widget.TextView;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

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
import android.support.v4.view.PagerAdapter;
//import android.s/upport.v7.widget.ShareActionProvider;

/**
 * Created with IntelliJ IDEA.
 * User: espi
 * Date: 8/11/13
 * Time: 23:17
 * To change this template use File | Settings | File Templates.
 */
public class PagerImages extends Activity {
    ArrayList<PhotoMongol> ListUrlImage = new ArrayList<PhotoMongol>();
    private String PathImages;
    private ShareActionProvider mShareActionProvider;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
     /*
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) item.getActionProvider(); //.getActionProvider();

       mShareActionProvider.setShareIntent(getDefaultIntent());

        return true;
        */
        return true;
    }

    private void setShareIntent(Intent shareIntent) {

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private Intent getDefaultIntent() {
/*
        Intent mShareIntent = new Intent();
        mShareIntent.setAction(Intent.ACTION_SEND);
        mShareIntent.setType("text/plain");
        mShareIntent.putExtra(Intent.EXTRA_TEXT, "From me to you, this text is new.");
  */
        Bitmap cachedImage = ImageLoader.getInstance().getMemoryCache().get(ListUrlImage.get(0).Mname);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, cachedImage);

        return intent;
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
           //     if(checkImage.exists() && checkImage.isFile()) {
           //         photo.Mname = PathImages + "/" + NameImage;
           //     } else {
                    photo.Mname = line;
           //     }
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

        getIntent().getExtras().getString("SelectedUrl");
        String UrlWithImages =  getIntent().getExtras().getString("SelectedUrl");
        PathImages = Environment.getExternalStorageDirectory() + "/MongolFridayPhotos/"+ UrlWithImages.replace("-","_");
        String PathImagesListImages = PathImages+ "/listImages.txt";
        File dir = new File(PathImagesListImages);

         if(dir.exists() && dir.isFile()) {
            Toast.makeText(this, "Exists", Toast.LENGTH_SHORT).show();
             ReadListImagesfromFile(PathImagesListImages);
         } else {
             dir = new File(PathImages);
             dir.mkdirs();
             GetAllImagesFromUrl(UrlWithImages);
             SaveListtoFile(PathImages+"/listImages.txt");
        }
        setContentView(R.layout.fr_image_pager);
        ViewPager pager = (ViewPager) this.findViewById(R.id.pager);
        pager.setAdapter(new ImageAdapter(this.getApplicationContext(),ListUrlImage));
        pager.setCurrentItem(0);
     }

    public void SelectedImage(View v) {
        Toast.makeText(this, "Exists", Toast.LENGTH_SHORT).show(); // do something here
        //imageId = image.getId();
       // Intent fullScreenIntent = new Intent(v.getContext(),FullImageActivity.class);
       // fullScreenIntent.putExtra(ProfilePageNormalUser.class.getName(),imageId);
       // ProfilePageNormalUser.this.startActivity(fullScreenIntent);
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        return;
    }


    private static class ImageAdapter extends PagerAdapter {

        private LayoutInflater inflater;
        private DisplayImageOptions options;
        private ArrayList<PhotoMongol> ListUrlimages;

        ImageAdapter(Context context, ArrayList<PhotoMongol> Listimages) {
            this.ListUrlimages = Listimages;

            inflater = LayoutInflater.from(context);

            options = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.ic_empty)
                    .showImageOnFail(R.drawable.ic_error)
                    .resetViewBeforeLoading(true)
                    .cacheOnDisk(true)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .considerExifParams(true)
                    .displayer(new FadeInBitmapDisplayer(300))
                    .build();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return ListUrlimages.size(); //   IMAGE_URLS.length;
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
            assert imageLayout != null;
            ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
            final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

            String ImagePath= "";
           // if ( ListUrlimages.get(position).Mname.contains("http:") )
           // {
                ImagePath = ListUrlimages.get(position).Mname;
           // } else {
           //     ImagePath = "file:///"+ListUrlimages.get(position).Mname;
           // }
            ImageLoader.getInstance().displayImage( ImagePath, imageView, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    spinner.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    String message = null;
                    switch (failReason.getType()) {
                        case IO_ERROR:
                            message = "Input/Output error";
                            break;
                        case DECODING_ERROR:
                            message = "Image can't be decoded";
                            break;
                        case NETWORK_DENIED:
                            message = "Downloads are denied";
                            break;
                        case OUT_OF_MEMORY:
                            message = "Out Of Memory error";
                            break;
                        case UNKNOWN:
                            message = "Unknown error";
                            break;
                    }
                    Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();

                    spinner.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    spinner.setVisibility(View.GONE);
                }
            });

            view.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
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
