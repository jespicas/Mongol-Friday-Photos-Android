package com.espipinxo.mongolfriday;

/**
 * Created by espi on 26/07/2015.
 */
public class Volumen {
    public String Name;
    public String UrlVolumen;

    @Override
    public String toString() {
        return Name;
    }
    public void setValues(String name, String url)
    {
        Name = name;
        UrlVolumen = url;
    }
}
