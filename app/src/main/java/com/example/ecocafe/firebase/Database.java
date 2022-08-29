package com.example.ecocafe.firebase;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ecocafe.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collection;

public class Database {
    private static DatabaseReference mDBRoot = null;
    private static DatabaseReference cafeRoot = null;
    private static DatabaseReference postRoot = null;
    private static DatabaseReference userRoot = null;
    private static ValueEventListener userDataListener = null;

    private static StorageReference mStorage = null;
    private static StorageReference imageRoot = null;

    private static FirebaseAuth mAuth = null;

    public static Context context;

    public static boolean getInstance(){
        try{
            if(mAuth == null)
                mAuth = FirebaseAuth.getInstance();

            if(mStorage == null)
                mStorage = FirebaseStorage.getInstance().getReference();
            if(mStorage != null){
                imageRoot = mStorage.child(context.getString(R.string.ST_image));
            }

            if(mDBRoot == null)
                mDBRoot = FirebaseDatabase.getInstance().getReference();
            if(mDBRoot != null){
                if(cafeRoot == null){
                    cafeRoot = mDBRoot.child(context.getString(R.string.DB_cafe));
                }
                if(postRoot == null) {
                    postRoot = mDBRoot.child(context.getString(R.string.DB_post));
                }
                if(userRoot == null){
                    userRoot = mDBRoot.child(context.getString(R.string.DB_user));
                }
            }

            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public Database(){
        getInstance();
    }
    public  Database(Context context){
        this.context = context;
        getInstance();
    }
    public Database(ValueEventListener cafe,
                    ValueEventListener post,
                    ValueEventListener user,
                    Context context){
        this.context = context;
        getInstance();
        cafeRoot.addValueEventListener(cafe);
        postRoot.addValueEventListener(post);
        userRoot.addValueEventListener(user);
    }

    public DatabaseReference getCafeRoot(){
        return cafeRoot;
    }
    public DatabaseReference getPostRoot(){
        return postRoot;
    }
    public DatabaseReference getUserRoot(){
        return userRoot;
    }
    public void removeUserValueEventListener(){
        userRoot.child(mAuth.getCurrentUser().getUid()).
                removeEventListener(userDataListener);
        userDataListener = null;
        Log.d(context.getString(R.string.Dirtfy_test), "removeUserValueEventListener end");
    }
    public void setUserValueEventListener(Reacts reacts){
        if(userDataListener != null){
            removeUserValueEventListener();
        }
        userDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reacts.ifDataChanged(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                reacts.ifCancelled(error);
            }
        };

        userRoot.child(mAuth.getCurrentUser().getUid()).
                addValueEventListener(userDataListener);
    }
    public FirebaseAuth getAuth(){
        return mAuth;
    }

    public boolean writeUser(User.Data data){
        String path = "firebase.Database.writeUser - ";
        try{
            User user = new User(context);
            userRoot.child(mAuth.getCurrentUser().getUid()).
                    setValue(data);
            Log.d(context.getString(R.string.Dirtfy_test), path+"success");
            return true;
        }
        catch (Exception e){
            Log.d(context.getString(R.string.Dirtfy_test), path+"fail");
            return false;
        }
    }
    public boolean readUser(Acts acts){
        String path = "firebase.Database.readUser - ";

        try {
            User user = new User();

            userRoot.child(mAuth.getCurrentUser().getUid()).
                    get().addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            User.Data data = task.getResult().getValue(User.Data.class);
                            acts.ifSuccess(task);
                            Log.d(context.getString(R.string.Dirtfy_test), String.valueOf(data.getPoint()));
                            Log.d(context.getString(R.string.Dirtfy_test), path+"success");
                        }
                        else{
                            acts.ifFail(task);
                            Log.d(context.getString(R.string.Dirtfy_test), path+"fail");
                        }
                    });

            return true;
        }
        catch (Exception e){
            Log.d(context.getString(R.string.Dirtfy_test), path+"fail");
            return false;
        }
    }

    public void writeImage(Uri file, Acts acts){
        String path = "firebase.Database.writeImage - ";

        StorageReference newFile = imageRoot.child(file.getLastPathSegment());
        newFile.putFile(file).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                readUser(new Acts() {
                    @Override
                    public void ifSuccess(Object task) {
                        User.Data data = ((Task<DataSnapshot>) task).getResult().getValue(User.Data.class);
                        StorageMetadata metadata = new StorageMetadata.Builder().
                                setCustomMetadata("Uploader_Uid", mAuth.getUid()).
                                setCustomMetadata("Uploader_Name", data.getName()).
                                build();

                        newFile.updateMetadata(metadata).
                                addOnCompleteListener(tsk -> {
                                    if (tsk.isSuccessful()){
                                        acts.ifSuccess(task);
                                        Log.d(context.getString(R.string.Dirtfy_test), path+"updateMetadata - "+"success");
                                    }
                                    else{
                                        acts.ifFail(task);
                                        Log.d(context.getString(R.string.Dirtfy_test), path+"updateMetadata - "+"fail");
                                    }
                                });
                    }

                    @Override
                    public void ifFail(Object task) {
                        acts.ifFail(task);
                        Log.d(context.getString(R.string.Dirtfy_test), path+"readUser - "+"success");
                    }
                });

                Log.d(context.getString(R.string.Dirtfy_test), path+"success");
            }
            else{
                acts.ifFail(task);
                Log.d(context.getString(R.string.Dirtfy_test), path+"fail");
            }
        });
    }
    public StorageReference readImage(String name){
        return imageRoot.child(name);
    }

    public boolean writeCafe(Cafe cafe){
        String path = "firebase.Database.writeCafe - ";
        try{
            String latBlock = String.valueOf((long) (cafe.getLatLng().latitude / 0.005));
            String lngBlock = String.valueOf((long) (cafe.getLatLng().longitude / 0.005));

            cafeRoot.child(latBlock).child(lngBlock).child(cafe.getName()).setValue(cafe);

            Log.d(context.getString(R.string.Dirtfy_test), path+"success");
            return true;
        }
        catch (Exception e){
            Log.d(context.getString(R.string.Dirtfy_test), path+"fail");
            return false;
        }

    }
    public void readCafe(LatLng pos, ArrayList<Cafe> returnList, CafeQuery con, Acts acts){
        String latBlock = String.valueOf((long) (pos.latitude / 0.005));
        String lngBlock = String.valueOf((long) (pos.longitude / 0.005));
        String path = "firebase.Database.readCafe - ";

        cafeRoot.child(latBlock).child(lngBlock).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                ArrayList<Cafe> all = new ArrayList<>();
                all.addAll((Collection<? extends Cafe>) task.getResult().getValue());
                for (Cafe cafe : all) {
                    if(con.Q(cafe))
                        returnList.add(cafe);
                }
                acts.ifSuccess(task);
                Log.d(context.getString(R.string.Dirtfy_test), path+"success");
            }
            else{
                acts.ifFail(task);
                Log.d(context.getString(R.string.Dirtfy_test), path+"fail");
            }
        });
    }

    public boolean writePost(Post post){
        String path = "firebase.Database.writePost - ";

        try{
            postRoot.child(post.getStore_name()).setValue(post);

            Log.d(context.getString(R.string.Dirtfy_test), path+"success");
            return true;
        }
        catch (Exception e){
            Log.d(context.getString(R.string.Dirtfy_test), path+"fail");
            return false;
        }
    }
    public void readPost(String name, ArrayList<Post> returnList, PostQuery con, Acts acts){
        String path = "firebase.Database.readPost - ";

        postRoot.child(name).get().addOnCompleteListener(task -> {
           if (task.isSuccessful()){
               ArrayList<Post> all = new ArrayList<>();
               all.addAll((Collection<? extends Post>) task.getResult().getValue());
               for (Post post : all) {
                   if(con.Q(post))
                       returnList.add(post);
               }
               acts.ifSuccess(task);
               Log.d(context.getString(R.string.Dirtfy_test), path+"success");
           }
           else{
               acts.ifFail(task);
               Log.d(context.getString(R.string.Dirtfy_test), path+"fail");
           }
        });
    }
}
