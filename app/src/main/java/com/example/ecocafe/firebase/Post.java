package com.example.ecocafe.firebase;

public class Post {
    private String uploader_uid;
    private String uploader_name;
    private String store_name;
    private String pic;
    private String text;

    public Post(){
    }

    public Post(String uploader_uid,
                String uploader_name,
                String store_name,
                String pic,
                String text){
        this.uploader_uid = uploader_uid;
        this.uploader_name = uploader_name;
        this.store_name = store_name;
        this.pic = pic;
        this.text = text;
    }

    public void setUploader_uid(String uploader_uid){
        this.uploader_uid = uploader_uid;
    }
    public String getUploader_uid(){
        return uploader_uid;
    }
    public void setUploader_name(String uploader_name){
        this.uploader_name = uploader_name;
    }
    public String getUploader_name(){
        return uploader_name;
    }
    public void setStore_name(String store_name){
        this.store_name = store_name;
    }
    public String getStore_name(){
        return store_name;
    }
    public void setPic(String pic){
        this.pic = pic;
    }
    public String getPic(){
        return pic;
    }
    public void setText(String text){
        this.text = text;
    }
    public String getText(){
        return text;
    }
}
