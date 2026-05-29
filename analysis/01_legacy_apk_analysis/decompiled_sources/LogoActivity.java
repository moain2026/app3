package com.p001yd.electricecollector;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/* loaded from: classes6.dex */
public class LogoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PICK_IMAGE_REQUEST = 0;
    private Button choosePhoto;
    private ImageView mImage;
    private Uri mImageUri;
    private Button reset;

    private String getRealPathFromURI(Uri uri) {
        Cursor managedQuery = managedQuery(uri, new String[]{"_data"}, null, null, null);
        if (managedQuery == null) {
            return uri.getPath();
        }
        int columnIndexOrThrow = managedQuery.getColumnIndexOrThrow("_data");
        managedQuery.moveToFirst();
        return managedQuery.getString(columnIndexOrThrow);
    }

    private String storeImage(Bitmap bitmap) {
        String str = Environment.getExternalStorageDirectory() + "//Accountant_Book/";
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        File file = new File(str, System.currentTimeMillis() + ".jpg");
        try {
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        String encodeToString = Base64.encodeToString(byteArrayOutputStream.toByteArray(), 0);
        edit.putString("CompanyLogo", encodeToString);
        edit.commit();
        return encodeToString;
    }

    public void clearData() {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
        edit.clear();
        edit.commit();
        finish();
        startActivity(getIntent());
    }

    public void imageSelect() {
        permissionsCheck();
        Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT");
        intent.addCategory("android.intent.category.OPENABLE");
        intent.addFlags(64);
        intent.addFlags(1);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "حدد صورة"), 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 0 && i2 == -1 && intent != null) {
            this.mImageUri = intent.getData();
            getContentResolver().takePersistableUriPermission(this.mImageUri, 3);
            String path = this.mImageUri.getPath();
            SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
            edit.putString("CompanyLogo", String.valueOf(this.mImageUri));
            edit.putString("pathLogo", path);
            edit.commit();
            this.mImage.setImageURI(this.mImageUri);
            this.mImage.invalidate();
            getIntent().putExtra("LOGO", this.mImageUri);
            try {
                MediaStore.Images.Media.getBitmap(getContentResolver(), this.mImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        switch (view.getId()) {
            case C1018R.id.lbl_btn_photo /* 2131362129 */:
                imageSelect();
                return;
            case C1018R.id.lbl_btn_reset /* 2131362130 */:
                clearData();
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1018R.layout.activity_logo);
        this.mImage = (ImageView) findViewById(C1018R.id.lbl_user_photo);
        this.choosePhoto = (Button) findViewById(C1018R.id.lbl_btn_photo);
        this.choosePhoto.setOnClickListener(this);
        this.reset = (Button) findViewById(C1018R.id.lbl_btn_reset);
        this.reset.setOnClickListener(this);
        String string = PreferenceManager.getDefaultSharedPreferences(this).getString("CompanyLogo", null);
        if (string != null) {
            this.mImage.setImageURI(Uri.parse(string));
        } else {
            this.mImage.setImageResource(C1018R.drawable.ic_launcher_foreground);
        }
    }

    public void permissionsCheck() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") != 0) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 1);
        }
    }
}
