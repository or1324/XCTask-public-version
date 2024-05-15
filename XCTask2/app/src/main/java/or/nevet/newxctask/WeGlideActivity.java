package or.nevet.newxctask;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

public class WeGlideActivity extends OrActivity {

    EditText userID;
    AppCompatButton download;
    static boolean isWorking = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_we_glide);
        download = findViewById(R.id.download);
        userID = findViewById(R.id.user_id);
        userID.setText(getIdFromStorage());
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isWorking) {
                    isWorking = true;
                    permissions();
                    String id = userID.getText().toString().trim();
                    saveIdToStorage(id);
                    Download.downloadTask(WeGlideActivity.this, "https://api.weglide.org/v1/task/declaration/" + id + "?cup=false&tsk=true");
                }
            }
        });
    }

    private void permissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 999);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 999) {
            if (permissions.length != 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 999);
            }
        }
    }
    private void saveIdToStorage(String s) {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("id", s);
        editor.apply();
    }

    private String getIdFromStorage() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("id", "");
    }

}