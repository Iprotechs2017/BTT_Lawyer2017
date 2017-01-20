package com.VideoCalling.sample.groupchatwebrtc.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.VideoCalling.sample.groupchatwebrtc.fragments.UplodedDocs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Harishma Velagala on 09-01-2017.
 */
public class fileDownload {
    public Context context;
    public ArrayList<DocumentDetails> DocumentDetailsArray;
    int selectedDocument;
    String downlodeDocName;
    String fileName;
    File openfile;
    public fileDownload(Context context,ArrayList<DocumentDetails> DocumentDetailsArray,int selectedDocument) {
        this.context = context;
        this.DocumentDetailsArray=DocumentDetailsArray;
       this.selectedDocument=selectedDocument;
        new DownloadFileFromURL().execute();
    }


    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;

        /**
         * Before starting background thread Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Downloading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... f_url) {
            String type = null;
            int count;
            try {
                type = DocumentDetailsArray.get(selectedDocument).getDocType();
                downlodeDocName = "http://35.163.24.72:8080/VedioApp/service/downlaod/download123/" + DocumentDetailsArray.get(selectedDocument).getId();
                // downlodeDocName="http://192.168.0.2:8080/VedioApp/service/downlaod/download/name/"+DocumentDetailsArray.get(selectedDocument).getId();
                Log.e("downlodeDocName", downlodeDocName);
                URL url = new URL(downlodeDocName);

                URLConnection conection = url.openConnection();
                conection.connect();
                int lenghtOfFile = conection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);
                File myDirectory = new File(Environment.getExternalStorageDirectory(), "BTTLawyer");
                if (!myDirectory.exists()) {
                    myDirectory.mkdirs();
                }

                fileName = DocumentDetailsArray.get(selectedDocument).getName();
                // Output stream
                OutputStream output = new FileOutputStream(Environment
                        .getExternalStorageDirectory().toString()
                        + "/BTTLawyer/" + fileName + "." + DocumentDetailsArray.get(selectedDocument).getDocType().replace(".",""));
                openfile = new File(Environment
                        .getExternalStorageDirectory().toString()
                        + "/BTTLawyer/" + fileName + "." + DocumentDetailsArray.get(selectedDocument).getDocType().replace(".",""));
                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();
                type = "downloaded";
                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                type = " not downloaded";
                Log.e("Error: ", e.getMessage());

            }

            return type;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            //progressDialog.dismiss();

        }
        protected void onPostExecute(String  downloaded) {
            // dismiss the dialog after the file was downloaded
            progressDialog.dismiss();

            if(downloaded.equalsIgnoreCase("downloaded"))
            {
               UplodedDocs.refreshDocs();

                /*new AlertDialog.Builder(context)
                        .setMessage("File Downloaded Successfully...")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        })
                        .setNegativeButton("Open", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Uri uri_path = Uri.fromFile(openfile);
                                    String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension
                                            (MimeTypeMap.getFileExtensionFromUrl(openfile.getAbsolutePath()));


                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setType(mimeType);
                                    intent.setDataAndType(uri_path, mimeType);
                                    context.startActivity(intent);
                                } catch (Exception e) {
                                    Toast.makeText(context, DocumentDetailsArray.get(selectedDocument).getDocType() + " file supported app not installed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setCancelable(false)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();*/
                try {
                    Uri uri_path = Uri.fromFile(openfile);
                    String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension
                            (MimeTypeMap.getFileExtensionFromUrl(openfile.getAbsolutePath()));


                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setType(mimeType);
                    intent.setDataAndType(uri_path, mimeType);
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, DocumentDetailsArray.get(selectedDocument).getDocType() + " file supported app not installed", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                new AlertDialog.Builder(context)
                        .setMessage("Downloading failed...")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setCancelable(false)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }

        }

    }
}