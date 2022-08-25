package com.example.ecocafe.firebase;

import android.util.Log;

public class ActsTest implements Acts{
    @Override
    public void ifSuccess(Object task) {
        Log.d("Dirtfy_test", "success");
    }

    @Override
    public void ifFail(Object task) {
        Log.d("Dirtfy_test", "fail");
    }
}
