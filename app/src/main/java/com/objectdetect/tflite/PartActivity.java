package com.objectdetect.tflite;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.os.Trace;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;




public class PartActivity extends AppCompatActivity {

    private Matrix mCurrentDisplayMatrix = null;

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.part_activity);
        TextView textView =findViewById(R.id.count);


        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize=2;


        byte[] decodedBytes = getIntent().getByteArrayExtra("image");
        Bitmap drawImgPreview = BitmapFactory.decodeByteArray(decodedBytes , 0, decodedBytes.length,opts);


        //Uri myUri = Uri.parse(extras.getString("imageUri"));
        PhotoView mPhotoView = findViewById(R.id.photo_view);

        mPhotoView.setImageBitmap(drawImgPreview);
        //mPhotoView.isZoomable();
        //mPhotoView.setScale(2);
        //mPhotoView.setImageResource(R.drawable.draw_half);




    }
}
