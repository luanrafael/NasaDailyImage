package com.nasa.dailyimage;


import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static android.provider.MediaStore.Images.Media.getBitmap;


public class NasaDailyImage extends Activity {

    public ProgressDialog dialog;
    public Handler handler;
    public IotdHandler iotdHandler = null;
    public Bitmap image;

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
        image = BitmapFactory.decodeStream(new URL(imageUrl).openStream());
        descriptionView.setText(description);
        imageView.setImageBitmap(image);

        System.out.println(" \n==== INFORMAÇÕES =====\n ");
        System.out.println(" \n====   TITULO    =====\n " + title);
        System.out.println(" \n====    DATA     =====\n " + date);

    }

    public void onSetWallPaper(){

        Thread th = new Thread(){
            public void run(){
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(NasaDailyImage.this);
                try {
                    wallpaperManager.setBitmap(image);
                    showToast("Papel de Parede Configurado!");
                }catch (Exception e){
                    e.printStackTrace();
                    showToast("Erro!");
                }
            }
        };
        th.start();
    }

    public void showToast(final String strMsg){
        handler.post(
                new Runnable() {
                    public void run() {
                        Toast.makeText(NasaDailyImage.this, strMsg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nasa_daily_image, menu);
        return true;
    }
    
}
