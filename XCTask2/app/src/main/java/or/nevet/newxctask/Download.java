package or.nevet.newxctask;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Download {
    static String name;
    static long id;
    static Toast toast;

    static BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context c, Intent intent) {
            // your code
            File file = new File("/storage/emulated/0/" + Environment.DIRECTORY_DOWNLOADS + "/" + name);

            File newFile = new File("/storage/emulated/0/" + "XCSoarData/tasks");
            if (!file.exists())
                new AlertDialog.Builder(c).setMessage("the downloaded task does not exist on the downloads folder. Please contact the developer for updating the app").show();
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String s = reader.readLine();
                if (s.equals("null")) {
                    if (toast != null)
                        toast.cancel();
                    toast = Toast.makeText(c, "There is no task declared for this id", Toast.LENGTH_SHORT);
                    toast.show();
                    WeGlideActivity.isWorking = false;
                    return;
                }
            } catch (IOException e) {
                WeGlideActivity.isWorking = false;
                e.printStackTrace();
            }
            if (!newFile.exists()) {
                newFile.mkdirs();
            }
                Intent intent2 = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent2.addCategory(Intent.CATEGORY_OPENABLE);
                intent2.setType("*/*");
                intent2.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse("content://com.android.externalstorage.documents/document/primary%3AXCSoarData%2Ftasks"));
                intent2.putExtra(Intent.EXTRA_TITLE, "task.tsk");
                new AlertDialog.Builder(c).setMessage("The next window is supposed to bring you to the XCSoarData folder. If it does not do that, please navigate into there. On the next window, please press 'Save'.").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        ((OrActivity) c).mGetContent.launch(intent2);
                    }
                }).show();
        }
    };

    public static void downloadTask(AppCompatActivity a, String taskURL){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(taskURL));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI| DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Downloading Task");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        DownloadManager manager = (DownloadManager) a.getSystemService(Context.DOWNLOAD_SERVICE);
        name = "downloadedTask"+System.currentTimeMillis()+".tsk";
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);
        a.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        id = manager.enqueue(request);
    }



}
