package com.example.ecocafe.firebase;
//끝나면 할일을 넘겨줌
public interface Acts {
    void ifSuccess(Object task);
    void ifFail(Object task);
}
