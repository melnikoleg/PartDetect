package com.objectdetect.tflite;


import android.graphics.Bitmap;

import org.json.JSONObject;

public class Parts {

    private String partName;

    private String Designation;

    private Bitmap imageDrawPreview;
    private JSONObject ImageDrawId;

    //constructor
    public Parts(String partName, String Designation, Bitmap imageDrawPreview, JSONObject ImageDrawId){
        this.partName = partName;
        this.Designation = Designation;
        this.imageDrawPreview = imageDrawPreview;
        this.ImageDrawId = ImageDrawId;

    }



    public String getPartName() {
        return partName;
    }

    public String getDesignation() {
        return Designation;
    }

    public Bitmap getImageDrawPreview() {
        return imageDrawPreview;
    }

    public JSONObject getImageDrawId() {
        return ImageDrawId;
    }



    public void setImageDrawPreview(Bitmap imageResourseId) {
        this.imageDrawPreview = imageResourseId;
    }

    public void  setImageDraw(JSONObject imageResourseFullId) {
        this.ImageDrawId = imageResourseFullId;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public void setDesignation(String designation) {
        Designation = designation;
    }




}
