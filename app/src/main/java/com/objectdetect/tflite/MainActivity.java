package com.objectdetect.tflite;

import java.io.IOException;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageButton;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.StrictMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ListView;

import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String MODEL_PATH = "quantized_model_MIRO_M2_89.tflite";
    //private static final String MODEL_PATH = "quantized_model.tflite";
    private static final int INPUT_SIZE = 224;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client;
    private Classifier classifier;
    private Executor executor = Executors.newSingleThreadExecutor();
    private ImageButton btnDetectObject;
    private CameraView cameraView;
    private ListView listView;
    private CustomAdapter pAdapter;
    private ArrayList<Parts> partsList;
    private float[][] summVec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        cameraView = findViewById(R.id.cameraView);


        btnDetectObject = (ImageButton) findViewById(R.id.btnDetectObject);

        cameraView.setCropOutput(true);
        listView = (ListView) findViewById(R.id.part_list);


        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {
            }

            @Override
            public void onError(CameraKitError cameraKitError) {
            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {

                // создаём битмап
                Bitmap bitmap = cameraKitImage.getBitmap();

                bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);

                final float[][] result = classifier.recognizeImage(bitmap);

                process(result);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });

        btnDetectObject.setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {

                Intent intent = new Intent(MainActivity.this, PartActivity.class);
                String url = "http://192.168.1.4:5000/get_image";
                Parts item = partsList.get(position);

                String respondDraw;
                try {
                    respondDraw = post(url, item.getImageDrawId().toString());
                    JSONObject JobjectDraw = new JSONObject(respondDraw);

                    byte[] decodedBytes = Base64.decode(JobjectDraw.get("$binary").toString(), Base64.DEFAULT);
                    //Bitmap drawImg = BitmapFactory.decodeByteArray(decodedBytes , 0, decodedBytes.length);

                    intent.putExtra("image", decodedBytes);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,
                            e.toString(), Toast.LENGTH_LONG).show();
                }
                startActivity(intent);
            }
        });


        initTensorFlowAndLoadModel();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDetectObject:
                cameraView.captureImage();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                classifier.close();
            }
        });
    }

    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            getAssets(),
                            MODEL_PATH,
                            INPUT_SIZE);

                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }

    String post(String url, String json) throws IOException {


        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();


        return response.body().string();
    }

    private void process(float[][] result) {

        partsList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(Arrays.asList(result));

        client = new OkHttpClient();

        String url = "http://192.168.1.4:5000/recognise_image";

        listView = (ListView) findViewById(R.id.part_list);
        // создаем пустой адаптер
        pAdapter = new CustomAdapter(MainActivity.this, partsList);
        listView.setAdapter(pAdapter);
        pAdapter.notifyDataSetChanged();
        String respond;
        try {
            respond = post(url, jsonArray.toString());
            String jsonData = respond;
            JSONObject Jobject = new JSONObject(jsonData);
            JSONArray Jarray = Jobject.getJSONArray("predict_result");

            for (int i = 0; i < Jarray.length(); i++) {

                JSONObject data = new JSONObject(Jarray.getJSONObject(i).toString());
                JSONObject JoImg = new JSONObject(data.get("draw_img_preview").toString());
                byte[] decodedBytes = Base64.decode(JoImg.get("$binary").toString(), Base64.DEFAULT);
                Bitmap drawImgPreview = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                JSONObject drawId = new JSONObject();
                drawId.put("id", data.get("draw_img_id"));
                partsList.add(new Parts(
                        data.get("Name").toString(),
                        data.get("Designation").toString(),
                        drawImgPreview,
                        drawId)
                );
            }


        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this,
                    e.toString(), Toast.LENGTH_LONG).show();
        }

        listView = (ListView) findViewById(R.id.part_list);
        // создаем адаптер
        pAdapter = new CustomAdapter(MainActivity.this, partsList);
        listView.setAdapter(pAdapter);
        pAdapter.notifyDataSetChanged();
        Toast.makeText(MainActivity.this,
                "Image recognized", Toast.LENGTH_LONG).show();

    }

    private void makeButtonVisible() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnDetectObject.setVisibility(View.VISIBLE);
            }
        });
    }


}
