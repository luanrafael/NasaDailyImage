package com.nasa.dailyimage;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;


public class NasaDailyImage extends Activity {

    public ProgressDialog dialog;
    public Handler handler;
    public IotdHandler iotdHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();
        refreshFromFeed();
    }



    private void refreshFromFeed(){
        dialog = ProgressDialog.show(this,"Atualizando","Carregando Imagem do Dia!");

        Thread th = new Thread(){

            public void run(){

                if(iotdHandler == null){
                    iotdHandler = new IotdHandler();
                }

                try {
                    URL rssFeed = new URL("http://www.nasa.gov/rss/dyn/image_of_the_day.rss");
                    iotdHandler.processFeed(rssFeed);
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                resetDisplay(iotdHandler.getTitle(), iotdHandler.getDate(), iotdHandler.getUrl(), iotdHandler.getDescription());
                                dialog.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                }};
            th.start();
    }



    public void onRefresh(View view){
        refreshFromFeed();
    }



    public void resetDisplay(String title, String date, String imageUrl, String description) throws NoSuchFieldException, IllegalAccessException, IOException, URISyntaxException {
        TextView titleView = (TextView) findViewById(R.id.imageTitle);
        TextView descriptionView = (TextView) findViewById(R.id.imageDescription);
        TextView dateView = (TextView) findViewById(R.id.imageDate);
        ImageView imageView = (ImageView) findViewById(R.id.imageDisplay);
        titleView.setText(title);
        dateView.setText(date);
        Bitmap bm = BitmapFactory.decodeStream(new URL(imageUrl).openStream());
        descriptionView.setText(description);
        imageView.setImageBitmap(bm);

        System.out.println(" \n==== INFORMAÇÕES =====\n ");
        System.out.println(" \n====   TITULO    =====\n " + title);
        System.out.println(" \n====    DATA     =====\n " + date);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nasa_daily_image, menu);
        return true;
    }
    
}
