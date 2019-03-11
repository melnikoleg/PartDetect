package com.objectdetect.tflite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<Parts> {
    private Context mContext;
    private List<Parts> part_list;
    private LayoutInflater LayoutInflater;

    public CustomAdapter(@NonNull Context context, ArrayList<Parts> list) {
        super(context, 0, list);
        mContext = context;
        part_list = list;
        LayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public Parts getItem(int position){
        return null;
    }
    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = android.view.LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);

        Parts currentPart = part_list.get(position);

        ImageView image = (ImageView) listItem.findViewById(R.id.imageView_drawing);


        image.setImageBitmap(currentPart.getImageDrawPreview());

        TextView name = (TextView) listItem.findViewById(R.id.textView_part_name);
        name.setText(currentPart.getPartName());

        TextView Designation = (TextView) listItem.findViewById(R.id.textView_Designation);
        Designation.setText(currentPart.getDesignation());






        return listItem;
    }


}