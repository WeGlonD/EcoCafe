package com.example.ecocafe.firebase;

import android.content.Context;
import android.util.Log;

import com.example.ecocafe.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.IgnoreExtraProperties;

public class User {
    @IgnoreExtraProperties
    public static class  Data{
        private String name;
        private Long point;

        public Data() {
        }

        public Data(String name, Long point){
            this.name = name;
            this.point = point;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getPoint() {
            return point;
        }

        public void setPoint(Long point) {
            this.point = point;
        }
    }

    private static FirebaseUser currentUser = null;
    public static Context context = null;
    public static Data data = null;


    public static void getInstance(){
        Database db = new Database();
        if(db.getAuth() != null){
            if(currentUser == null){
                currentUser = db.getAuth().getCurrentUser();
            }
        }
    }

    public User(){
        getInstance();
    }
    public User(Context context){
        getInstance();
        this.context = context;
    }

    public void create(String email, String password, Acts acts){
        Database db = new Database();
        FirebaseAuth mAuth = db.getAuth();
        String path = "firebase.User.create - ";

        mAuth.createUserWithEmailAndPassword(email, password).
            addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    acts.ifSuccess(task);
                    Log.d(context.getString(R.string.Dirtfy_test), path+"success");
                } else {
                    acts.ifFail(task);
                    Log.d(context.getString(R.string.Dirtfy_test), path+"fail");
                }
            });
    }

    public void delete(Acts acts){
        Database db = new Database();
        FirebaseAuth mAuth = db.getAuth();
        String path = "firebase.User.delete - ";

        mAuth.getCurrentUser().delete().
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        acts.ifSuccess(task);
                        Log.d(context.getString(R.string.Dirtfy_test), path+"success");
                    } else {
                        acts.ifFail(task);
                        Log.d(context.getString(R.string.Dirtfy_test), path+"fail");
                    }
                });
        db.getUserRoot().child(currentUser.getUid()).removeValue();
    }

    public void login(String email, String password, Acts acts){
        Database db = new Database();
        FirebaseAuth mAuth = db.getAuth();
        String path = "firebase.User.login - ";

        mAuth.signInWithEmailAndPassword(email, password).
                addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                acts.ifSuccess(task);
                Log.d(context.getString(R.string.Dirtfy_test), path+"success");
            }
            else{
                acts.ifFail(task);
                Log.d(context.getString(R.string.Dirtfy_test), path+"fail");
            }
        });
    }

    public void logout(){
        Database db = new Database();
        FirebaseAuth mAuth = db.getAuth();
        String path = "firebase.User.logout - ";

        try {
            mAuth.signOut();
            Log.d(context.getString(R.string.Dirtfy_test), path+"success");
        }
        catch (Exception e){
            Log.d(context.getString(R.string.Dirtfy_test), path+"fail");
        }
    }
}
