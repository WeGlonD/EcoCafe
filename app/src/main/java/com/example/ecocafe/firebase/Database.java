package com.example.ecocafe.firebase;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ecocafe.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Database {
    private static DatabaseReference mDBRoot = null;
    private static DatabaseReference cafeRoot = null;
    private static DatabaseReference postRoot = null;
    private static DatabaseReference userRoot = null;
    private static FirebaseAuth mAuth = null;
    public static Context context;

    public static boolean getInstance(){
        try{
            if(mAuth == null)
                mAuth = FirebaseAuth.getInstance();

            if(mDBRoot == null)
                mDBRoot = FirebaseDatabase.getInstance().getReference();
            else {
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
    public void addUserValueEventListener(Reacts reacts){
        userRoot.child(mAuth.getCurrentUser().getUid()).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        reacts.ifDataChanged(snapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        reacts.ifCancelled(error);
                    }
                });
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
                            User.data = task.getResult().getValue(User.Data.class);
                            acts.ifSuccess(task);
                            Log.d(context.getString(R.string.Dirtfy_test), String.valueOf(user.data.getPoint()));
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
}
