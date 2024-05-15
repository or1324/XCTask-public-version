package or.nevet.newxctask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class OrActivity extends AppCompatActivity{
    static Toast toast;

    public ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    File file = new File("/storage/emulated/0/"+ Environment.DIRECTORY_DOWNLOADS+"/"+Download.name);
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            Uri uri = result.getData().getData();
                            OutputStream os = getContentResolver().openOutputStream(uri);
                            Files.copy(file.toPath(), os);
                            try {
                                Files.delete(file.toPath());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            openXCSoar(OrActivity.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    WeGlideActivity.isWorking = false;
                }
            });

    private static void openXCSoar(Context c) {
        String p = "org.xcsoar.testing";
        if (!isPackageExisted(p, c))
            p = "org.xcsoar";
        Intent launchIntent = c.getPackageManager().getLaunchIntentForPackage(p);
        if (launchIntent != null)
            c.startActivity(launchIntent);
        else {
            if (toast != null)
                toast.cancel();
            toast = Toast.makeText(c, "You do not have xcsoar installed", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private static boolean isPackageExisted(String targetPackage, Context c) {
        PackageManager pm = c.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }
}
