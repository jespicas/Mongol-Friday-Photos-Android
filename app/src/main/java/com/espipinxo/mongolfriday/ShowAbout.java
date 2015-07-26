package com.espipinxo.mongolfriday;

import android.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

/**
 * Created by espi on 26/07/2015.
 */
public class ShowAbout {

    private View messageView;
    public ShowAbout(View ViewToshow)
    {
            messageView = ViewToshow;
    }
    protected void showAbout() {
        // Inflate the about message contents
        //messageView = getLayoutInflater().inflate(R.layout.activity_about, null, false);

        // When linking text, force to always use default color. This works
        // around a pressed color state bug.
        /*
        TextView textView = (TextView) messageView.findViewById(R.id.);
        int defaultColor = textView.getTextColors().getDefaultColor();
        textView.setTextColor(defaultColor);
        */

        AlertDialog.Builder builder = new AlertDialog.Builder(messageView.getContext());

        builder.setIcon(R.drawable.logo_machacas);
        builder.setTitle(R.string.app_name);
        builder.setView(messageView);
        builder.create();
        builder.show();
    }
}
