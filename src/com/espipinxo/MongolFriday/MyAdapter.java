package com.espipinxo.MongolFriday;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: espi
 * Date: 3/11/13
 * Time: 23:46
 * To change this template use File | Settings | File Templates.
 */
public class MyAdapter extends ArrayAdapter<PhotoMongol> {
    private final static String TAG = "MediaItemAdapter";
    Context mContext;
    ArrayList<PhotoMongol> Urls;
    int rowResourceId;
    private ImageThreadLoader imageLoader = new ImageThreadLoader();

    public MyAdapter(Context c, int itemId, ArrayList<PhotoMongol> lstUrls){
        super(c,itemId,lstUrls);
        mContext = c;
        Urls = lstUrls;
        rowResourceId = itemId;
    }
    @Override
    public int getCount() {
        return Urls.size()  ;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PhotoMongol getItem(int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long getItemId(int i) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private static class ViewHolder {
        ImageView imageView;
    }

    private boolean SaveImagetofile(String PathtoSave, Bitmap imagetoSave)
    {
         try{
            File file = new File(PathtoSave);
            FileOutputStream fOut = new FileOutputStream(file);
            imagetoSave.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
             return true;
         }
        catch (Exception e) {
            e.printStackTrace();
            Log.i(null, "Save file error!");
            return false;
        }
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(rowResourceId, viewGroup, false);
        final ImageView imgView = (ImageView) rowView.findViewById(R.id.ImageMongol);

       // imgView.setImageDrawable(LoadImageFromWeb(Urls.get(i)));
        //http://img811.imageshack.us/img811/7710/k27w.jpg

       // imgView.setImageURI(Uri.parse("http://img811.imageshack.us/img811/7710/k27w.jpg"));
       // imgView.setImageBitmap(LoadImageFromWebOperations(Urls.get(i)));

        Bitmap cachedImage = null;
        try {
            if (Urls.get(i).Mname.contains("https")) {
                cachedImage = imageLoader.loadImage(Urls.get(i).Mname, new ImageThreadLoader.ImageLoadedListener() {
                    public void imageLoaded(Bitmap imageBitmap) {
                        imgView.setImageBitmap(imageBitmap);
                        notifyDataSetChanged();
                    }
                });
                String NamImg = Urls.get(i).Mname.split("/")[Urls.get(i).Mname.split("/").length-1];
                SaveImagetofile(Urls.get(i).MPath + "/" + NamImg,cachedImage);
            } else {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                cachedImage = BitmapFactory.decodeFile(Urls.get(i).Mname , options);
                imgView.setImageBitmap(cachedImage);
                imgView.setTag(Urls.get(i));
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "Bad remote image URL: " + Urls.get(i), e);
        }
       return rowView;
    }
 }
