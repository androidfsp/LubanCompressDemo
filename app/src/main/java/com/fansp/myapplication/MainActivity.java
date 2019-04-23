package com.fansp.myapplication;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fansp.myapplication.utils.FileUtils;
import com.fansp.myapplication.utils.IvCompressCallBack;
import com.fansp.myapplication.utils.IvCompressUtil;
import com.fansp.myapplication.utils.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.iv_one)
    ImageView ivOne;
    @Bind(R.id.tv_one)
    TextView tvOne;
    @Bind(R.id.iv_two)
    ImageView ivTwo;
    @Bind(R.id.tv_two)
    TextView tvTwo;
    @Bind(R.id.btn_photo)
    Button btnPhoto;
    private File mTmpFile;
    String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private Bitmap bitmap;
    private Bitmap bitmapLuB;
    List<String> mPermissionList = new ArrayList<>();
    private List<String> piclist = new ArrayList<>();
    private List<String> piclist2 = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ScreenUtils.setCustomDensity(this, getApplication());
        MyApplication.setContext(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCameraAction();
            }
        });
        ivOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PhotosViewActivity.class);
                intent.putExtra("pic", piclist.toString().substring(1, piclist.toString().length() - 1));
                intent.putExtra("pos", 0);
                startActivity(intent);
            }
        });

        ivTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (piclist2.size() > 0) {
                    Intent intent = new Intent(MainActivity.this, PhotosViewActivity.class);
                    intent.putExtra("pic", piclist2.toString().substring(1, piclist2.toString().length() - 1));
                    intent.putExtra("pos", 0);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissions[i]);
                    if (showRequestPermission) {
                        LogUtils.i("fsp", showRequestPermission + "");
                    }
                } else {
                    mPermissionList.remove(permissions[i]);
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showCameraAction() {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permission);
            }
        }
        if (mPermissionList.isEmpty()) {
            takePhoto();
        } else {
            String[] permissions = mPermissionList.toArray(new String[0]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 100);
        }
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(this.getPackageManager()) != null) {
//            mTmpFile = FileUtils.createFile(this);
            String timeStamp = String.valueOf(new Date().getTime());
            mTmpFile = new File(IvCompressUtil.getJpgFileAbsolutePath(timeStamp));
            int currentapiVersion = Build.VERSION.SDK_INT;

            if (currentapiVersion < 24) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
                startActivityForResult(intent, 1);
            } else {
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, mTmpFile.getAbsolutePath());
                Uri uri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, 1);
            }
        } else {
            Toast.makeText(this, "没有相机", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            //原图
            String absolutePath = mTmpFile.getAbsolutePath();
            LogUtils.i("fsp1", absolutePath);
            bitmap = BitmapFactory.decodeFile(absolutePath);
            ivOne.setImageBitmap(bitmap);

            piclist.add(absolutePath);
            try {
                long fileSize = getFileSize(mTmpFile);
                String size = toFileSize(fileSize);
                tvOne.setText(size);
            } catch (Exception e) {
                e.printStackTrace();
            }

            IvCompressUtil.luBanCompress(this, mTmpFile.getAbsolutePath(), new IvCompressCallBack() {
                @Override
                public void onSucceed(File file) {
                    String absolutePath = file.getAbsolutePath();
                    LogUtils.i("fsp2", absolutePath);
                    bitmapLuB = BitmapFactory.decodeFile(absolutePath);
                    ivTwo.setImageBitmap(bitmapLuB);

                    piclist2.add(absolutePath);
                    try {
                        long fileSize = getFileSize(file);
                        String size = toFileSize(fileSize);
                        tvTwo.setText(size);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(String msg) {

                }
            });
        }
    }

    /**
     * 获取指定文件大小
     */
    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {

            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }

    /**
     * 转换文件大小
     */
    public static String toFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null) {
            bitmap.recycle();
        }
    }
}
