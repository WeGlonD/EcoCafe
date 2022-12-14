package com.example.ecocafe;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.ecocafe.firebase.Acts;
import com.example.ecocafe.firebase.Database;
import com.example.ecocafe.firebase.Post;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoActivity extends Activity implements View.OnClickListener {
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_iMAGE = 2;
    private Uri mImageCaptureUri;
    private ImageView iv_Post;
    private int id_view;
    private String absoultePath;
    private Post userpost;
    private Database mdb;
    private EditText storename;
    private EditText storedetail;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.posting);
        mdb = new Database();
        iv_Post = (ImageView) this.findViewById(R.id.iv_post);
        Button btn_uploadpic = (Button) this.findViewById(R.id.btn_UploadPicture);
        btn_uploadpic.setOnClickListener(this);
    }
    /**
     * 카메라에서 사진 촬영
     */
    public void doTakePhotoAction() // 카메라 촬영 후 이미지 가져오기
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 임시로 사용할 파일의 경로를 생성
//        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
//        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
//        mImageCaptureUri = FileProvider.getUriForFile(this, "com.example.ecocafe.fileprovider", new File(Environment.getExternalStorageDirectory(), url));
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_"; File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File StorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            );
        }catch (IOException exception) {Toast.makeText(this, "파일 실패", Toast.LENGTH_LONG).show();}
        mImageCaptureUri = FileProvider.getUriForFile(this, getPackageName()+".fileprovider",image);
        grantUriPermission("com.example.ecocafe.fileprovider",mImageCaptureUri, FLAG_GRANT_READ_URI_PERMISSION);
        grantUriPermission("com.example.ecocafe.fileprovider",mImageCaptureUri, FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }
    /**
     * 앨범에서 이미지 가져오기
     */
    public void doTakeAlbumAction() // 앨범에서 이미지 가져오기
    {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode != RESULT_OK) {
            Toast.makeText(this, "RESULT NOT OK", Toast.LENGTH_LONG).show();
            return;
        }
        switch(requestCode)
        {
            case PICK_FROM_ALBUM:
            {
                // 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다.
                // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.
                mImageCaptureUri = data.getData();
                Log.d("SmartWheel",mImageCaptureUri.getPath().toString());
                //Toast.makeText(this,"wqewqeqe",Toast.LENGTH_LONG).show();
            }
            case PICK_FROM_CAMERA:
            {
                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.
//                Intent intent = new Intent("com.android.camera.action.CROP");
//                intent.setDataAndType(mImageCaptureUri, "image/*");
//                // CROP할 이미지를 200*200 크기로 저장
//                intent.putExtra("outputX", 200); // CROP한 이미지의 x축 크기
//                intent.putExtra("outputY", 200); // CROP한 이미지의 y축 크기
//                intent.putExtra("aspectX", 1); // CROP 박스의 X축 비율
//                intent.putExtra("aspectY", 1); // CROP 박스의 Y축 비율
//                intent.putExtra("scale", true);
//                intent.putExtra("return-data", true);
//                startActivityForResult(intent, CROP_FROM_iMAGE); // CROP_FROM_CAMERA case문 이동

                iv_Post.setImageURI(mImageCaptureUri);
                break;
            }
//            case CROP_FROM_iMAGE:
//            {
//                // 크롭이 된 이후의 이미지를 넘겨 받습니다.
//                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
//                // 임시 파일을 삭제합니다.
//                if(resultCode != RESULT_OK) {
//                    return;
//                }
//                final Bundle extras = data.getExtras();
//                // CROP된 이미지를 저장하기 위한 FILE 경로
//                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+
//                        "/SmartWheel/"+System.currentTimeMillis()+".jpg";
//                if(extras != null) {
//                    Bitmap photo = extras.getParcelable("data"); // CROP된 BITMAP
//                    iv_Post.setImageBitmap(photo); // 레이아웃의 이미지칸에 CROP된 BITMAP을 보여줌
//                    storeCropImage(photo, filePath); // CROP된 이미지를 외부저장소, 앨범에 저장한다.
//                    absoultePath = filePath;
//                    break;
//                }
//                // 임시 파일 삭제
//                File f = new File(mImageCaptureUri.getPath());
//                if(f.exists())
//                {
//                    f.delete();
//                }
//            }
        }
    }
    @Override
    public void onClick(View v) {
        id_view = v.getId();
        if(v.getId() == R.id.btn_upload) {
            storename = (EditText)findViewById(R.id.storename);
            storedetail = (EditText)findViewById(R.id.posttext);
            mdb.writeImage(mImageCaptureUri, "사진", new Acts() {
                @Override
                public void ifSuccess(Object task) {
                    userpost.setPic(String.valueOf(mdb.readImage("사진")));
                    userpost.setStore_name(storename.getText().toString());
                    userpost.setText(storedetail.getText().toString());
                    mdb.writePost(userpost);
                }
                @Override
                public void ifFail(Object task) {

                }
            });
            mdb.writePost(userpost);
            Toast.makeText(this, "포스팅 성공!", Toast.LENGTH_SHORT).show();
        } else if(v.getId() == R.id.btn_UploadPicture) {
            DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doTakePhotoAction();
                }
            };
            DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doTakeAlbumAction();
                }
            };
            DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };
            new AlertDialog.Builder(this)
                    .setTitle("업로드할 이미지 선택")
                    .setPositiveButton("사진촬영", cameraListener)
                    .setNeutralButton("앨범선택", albumListener)
                    .setNegativeButton("취소", cancelListener)
                    .show();
        }
    }

    /*
     * Bitmap을 저장하는 부분
     */
//    private void storeCropImage(Bitmap bitmap, String filePath) {
//        // SmartWheel 폴더를 생성하여 이미지를 저장하는 방식이다.
//        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/SmartWheel";
//        File directory_SmartWheel = new File(dirPath);
//        if(!directory_SmartWheel.exists()) // SmartWheel 디렉터리에 폴더가 없다면 (새로 이미지를 저장할 경우에 속한다.)
//            directory_SmartWheel.mkdir();
//        File copyFile = new File(filePath);
//        BufferedOutputStream out = null;
//        try {
//            copyFile.createNewFile();
//            out = new BufferedOutputStream(new FileOutputStream(copyFile));
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//            // sendBroadcast를 통해 Crop된 사진을 앨범에 보이도록 갱신한다.
//            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//                    Uri.fromFile(copyFile)));
//                    //FileProvider.getUriForFile(this, "com.example.ecocafe.fileprovider", copyFile)));
//
//            out.flush();
//            out.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
