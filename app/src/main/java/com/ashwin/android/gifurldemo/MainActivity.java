package com.ashwin.android.gifurldemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.ImageDecoder;
import android.graphics.drawable.AnimatedImageDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {
    private static final String GIF_URL = "https://github.com/ashwin-mavila/resources/raw/master/gifs/error_99.gif";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayGif(GIF_URL);
    }

    private void displayGif(String url) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final AnimatedImageDrawable gif = (AnimatedImageDrawable) ImageDecoder.decodeDrawable(ImageDecoder.createSource(downloadGIF()));

                        // Since we use wrap_content, the height and width are in dp, so we need to convert them to px.
                        final int height = dpToPx(gif.getIntrinsicHeight());
                        final int width = dpToPx(gif.getIntrinsicWidth());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ImageView imgView = (ImageView) findViewById(R.id.gif_imageview);

                                // Here we scale our view according to GIF width and height
                                imgView.getLayoutParams().width = width;
                                imgView.getLayoutParams().height = height;
                                imgView.requestLayout();

                                imgView.setVisibility(View.VISIBLE);
                                imgView.setImageDrawable(gif);
                                gif.start();
                            }
                        });
                    } catch (IOException e) {
                        Log.e("gif-url-demo", "Exception while decoding GIF drawable", e);
                    }
                }
            }).start();
        } else {
            String gif = "<img src=\"" + url + "\" >";

            String html = "<html style=\"margin: auto; width: fit-content;\">\n" +
                    "<body style=\"margin: 0;\">\n" +
                    gif + "\n" +
                    "</body>\n" +
                    "</html>";

            WebView webView = (WebView) findViewById(R.id.gif_webview);
            webView.setVisibility(View.VISIBLE);

            webView.loadData(html, "text/html", "utf-8");
        }
    }

    private ByteBuffer downloadGIF() {
        try {
            URL url = new URL(GIF_URL);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            URLConnection conn = url.openConnection();

            try (InputStream inputStream = conn.getInputStream()) {
                int n = 0;
                byte[] buffer = new byte[1024];
                while (-1 != (n = inputStream.read(buffer))) {
                    output.write(buffer, 0, n);
                }
            }

            byte[] img = output.toByteArray();
            return ByteBuffer.wrap(img);
        } catch (Exception e) {
            Log.e("gif-url-demo", "Exception while downloading GIF", e);
        }

        return null;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
