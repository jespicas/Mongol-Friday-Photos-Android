package com.espipinxo.MongolFriday;

import android.os.Environment;

/**
 * Created by espi on 10/05/2015.
 */
public final class Constants {

    public static String Path_App_Saving_Images = Environment.getExternalStorageDirectory() + "/MongolFridayPhotos";
    public static String Url_MongolFriday = "http://www.machacas.com/category/mongol-friday-photos";
    private Constants() {
    }

    public static class Config {
        public static final boolean DEVELOPER_MODE = false;
    }

    public static class Extra {
        public static final String FRAGMENT_INDEX = "com.nostra13.example.universalimageloader.FRAGMENT_INDEX";
        public static final String IMAGE_POSITION = "com.nostra13.example.universalimageloader.IMAGE_POSITION";
    }
}
