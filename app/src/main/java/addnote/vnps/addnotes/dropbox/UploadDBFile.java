package addnote.vnps.addnotes.dropbox;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.WriteMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class UploadDBFile extends AsyncTask<Void, Long, Boolean> {

    private DbxClientV2 dropboxApi;
    private String path;
    private Context context;
    private ProgressDialog progressDialog;
    private RelativeLayout relativeLayout;

    public UploadDBFile(Context context1, DbxClientV2 dropboxApi,
                        String path, RelativeLayout dropbox_relative) {
        this.context = context1;
        this.dropboxApi = dropboxApi;
        this.path = path;
        this.relativeLayout = dropbox_relative;
        progressDialog = CustomProgressDialog.myCustomProgressDialog(context1, 0);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        FileInputStream inputStream = null;
        String databasePath = context.getDatabasePath("notesManager").getPath();
        try {
            File file = new File(databasePath);
            inputStream = new FileInputStream(file);
            dropboxApi.files().uploadBuilder("/"+path) //Path in the user's Dropbox to save the file.
                    .withMode(WriteMode.OVERWRITE) //always overwrite existing file
                    .uploadAndFinish(inputStream);

        } catch (DbxException e) {
            Log.d("DbExampleLog", "User has unlinked.");
            Toast.makeText(context, "Please Sign In to Dropbox again.", Toast.LENGTH_SHORT).show();
            return false;
        }catch (FileNotFoundException e) {
            Toast.makeText(context, "No file to upload.", Toast.LENGTH_SHORT).show();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.d("DbExampleLog", "File not found.");
                }
            }
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        progressDialog.hide();
        if (result) {
            Snackbar snackbar = Snackbar
                    .make(relativeLayout, "Notes are successfully uploaded...", Snackbar.LENGTH_SHORT);
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        } else {
            Snackbar snackbar = Snackbar
                    .make(relativeLayout, "An error occured while processing the upload request.Please try again...", Snackbar.LENGTH_SHORT);
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }
}
