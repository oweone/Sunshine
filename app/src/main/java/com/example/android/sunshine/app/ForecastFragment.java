/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import net.cliseau.runtime.javacor.EnforcementDecision;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import ftp.cliseau.FTP_CriticalEvent;
import ftp.cliseau.FTP_CriticalEventFactory;
import ftp.cliseau.FTP_EnforcementDecision;
import ftp.cliseau.FTP_LocalPolicy;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
 */
public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> mForecastAdapter;

    public FTPClient client = new FTPClient();
    public String ipaddr = "130.83.218.241";
    public int port = 2021;
    public String username = "auditor";
    public String password = "password";

    public ForecastFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    private boolean login() {
        boolean isLogin = false;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        ipaddr = prefs.getString(getString(R.string.pref_ServerIP_key), getString(R.string.pref_ServerIP_default));
        port = Integer.parseInt(prefs.getString(getString(R.string.pref_port_key), getString(R.string.pref_port_default)));
        username = prefs.getString(getString(R.string.pref_username_key), getString(R.string.pref_username_default));
        password = prefs.getString(getString(R.string.pref_password_key), getString(R.string.pref_password_default));

        try {
            client.connect(ipaddr, port);
            client.enterLocalPassiveMode();
            isLogin = client.login(username, password);
            client.enterLocalActiveMode();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return isLogin;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {


            FetchListTask fetchListTask = new FetchListTask();
            fetchListTask.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String[] data = {};
        List<String> weekForecast = new ArrayList<String>(Arrays.asList(data));

        mForecastAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_forecast, // The name of the layout ID.
                        R.id.list_item_forecast_textview, // The ID of the textview to populate.
                        weekForecast);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                String fileName = mForecastAdapter.getItem(position);
                /** here specify the download task
                 * request permitted
                 * request rejected
                 * request unknown, need to send to the server side to decide
                 *
                 */
                int accessPermission = analyzePermission(fileName);

                if (accessPermission == 1) {
                    Toast.makeText(getActivity(), "access permission at file " + fileName + " permitted", Toast.LENGTH_SHORT).show();
                    //requestPermitted(fileName,username);

                    SendTokenTask sendTokenTask = new SendTokenTask();
                    sendTokenTask.execute(fileName, username);
                } else if (accessPermission == 0) {
                    Toast.makeText(getActivity(), "access permission at file " + fileName + " rejected", Toast.LENGTH_SHORT).show();
                    // do nothing
                } else {
                    Toast.makeText(getActivity(), "delegate access at " + fileName + " to server", Toast.LENGTH_SHORT).show();

                    DownloadFileTask downloadFileTask = new DownloadFileTask();
                    downloadFileTask.execute(fileName);
                }
            }
        });

        return rootView;
    }

    public int analyzePermission(String fileName) {
        int accessPermission = -1;
        String userRole = "", roleProperties = "";

        AssetManager assetManager = getActivity().getAssets();

        try {
            InputStream inputStream = assetManager.open("user_role.txt");
            Properties role = new Properties();
            role.load(inputStream);
            userRole = (String) role.get(username);

            inputStream = assetManager.open("role_property.txt");
            Properties properties = new Properties();
            properties.load(inputStream);
            roleProperties = (String) properties.get(userRole);

            Log.e("roleProperties", roleProperties);

            FTP_CriticalEvent ftp_criticalEvent = FTP_CriticalEventFactory.readFile(username, roleProperties, fileName);

            FTP_LocalPolicy localPolicy = new FTP_LocalPolicy("id-2");
            FTP_EnforcementDecision ftp_enforcementDecision = (FTP_EnforcementDecision) localPolicy.makeDecision(ftp_criticalEvent);
            switch (ftp_enforcementDecision.decision) {
                case PERMIT:
                    accessPermission = 1;
                    break;
                case REJECT:
                    accessPermission = 0;
                    break;
                case UNKNOWN:
                    accessPermission = -1;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return accessPermission;
    }

    public class SendTokenTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String serverFileName = params[0];
            String fileName = params[0];
            String username = params[1];

            try {
                login();

                String filePath = getActivity().getFilesDir().getPath().toString() + "/token";
                Log.e("file", filePath);
                File file = new File(filePath);
                if (!file.exists()) {
                    Log.e("file", "error");
                    file.createNewFile();
                }
                file.setWritable(true);
                file.setReadable(true);

                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileName = "filename=" + fileName;
                username = "username=" + username;
                fileOutputStream.write(fileName.getBytes());
                fileOutputStream.write("\n".getBytes());
                fileOutputStream.write(username.getBytes());
                fileOutputStream.flush();
                fileOutputStream.close();

                String remoteFile = "token";
                InputStream inputStream = new FileInputStream(file);


                client.setFileType(FTP.BINARY_FILE_TYPE);

                boolean done = client.storeFile(remoteFile, inputStream);
                inputStream.close();
                if (done) {
                    System.out.println("The first file is uploaded successfully.");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return serverFileName;
        }

        @Override
        protected void onPostExecute(String fileName) {
            DownloadFileTask downloadFileTask = new DownloadFileTask();
            downloadFileTask.execute(fileName);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    public class FetchListTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {

            try {
                login();
                FTPFile[] ftpFiles = client.listFiles(client.printWorkingDirectory());
                ArrayList<String> name = new ArrayList<String>();

                for (int i = 0; i < ftpFiles.length; i++) {
                    String fname = ftpFiles[i].getName();
                    //Log.i("FTP", "File " + i + " : " + fname);
                    name.add(fname);
                    long length = ftpFiles[i].getSize();
                    //adapter2.add(ftpFiles[i].getName());
                    //String readableLength = FileUtils.byteCountToDisplaySize( length );
                    ///System.out.println( name + ":\t\t" + readableLength );
                }
                String[] files = name.toArray(new String[name.size()]);
                return files;

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    client.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mForecastAdapter.clear();
                for (String dayForecastStr : result) {
                    mForecastAdapter.add(dayForecastStr);
                }
                // New data is back from the server.  Hooray!
            }
        }
    }

    public class DownloadFileTask extends AsyncTask<String, Integer, String> {

        String name;

        protected String doInBackground(String... args) {
            boolean status = false;
            name = args[0];
            //String path = args[1];
            //Log.d(TAG, "name: " + name);
            //Log.d(TAG, "path: " + path);
            System.out.println(name);

            String localFilePath = "";
            try {
                login();
                //client.changeWorkingDirectory(filePath);
                System.out.println("2" + client.printWorkingDirectory());

                File newFolder = new File(Environment.getExternalStorageDirectory(), "ftpClient");
                if (!newFolder.exists()) {
                    newFolder.mkdir();
                }

                client.setControlKeepAliveTimeout(300);
                client.enterLocalPassiveMode();
                client.setFileType(FTP.BINARY_FILE_TYPE);
                client.setBufferSize(2224 * 2224);
                localFilePath = newFolder.getAbsolutePath() + "/" + name;
                Log.d("file_path", "localFilePath: " + localFilePath);
                FileOutputStream fos = new FileOutputStream(localFilePath);

                client.enterLocalActiveMode();
                status = client.retrieveFile(name, fos);
                fos.close();
                String error = client.getReplyString();
                System.out.println("Replay from server: " + error);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (status == true) {
                Log.d("test1", "Download success");
            } else {
                localFilePath = "";
                Log.e("test2", "Download failed");
            }

            return localFilePath;
        }

        protected void onPostExecute(String localFilePath) {

            if (localFilePath != null && localFilePath != "") {
                //download success
                File file = new File(localFilePath);

                if (file.exists()) {

                    String fileName = file.getName();
                    String fileExtension = "";//fileExtension could be txt,jpg,png
                    int i = fileName.lastIndexOf('.');
                    if (i > 0) {
                        fileExtension = fileName.substring(i + 1).toLowerCase();
                    }

                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    if (fileExtension.contains("txt")) {
                        intent.putExtra("txtFile", file);
                    }
                    if (fileExtension.contains("png") || fileExtension.contains("jpg")) {
                        intent.putExtra("BitmapImage", file);
                    }

                    intent.putExtra("FileName", fileName);
                    startActivity(intent);
                }

            } else {
                Toast.makeText(getActivity(), "access at this file rejected", Toast.LENGTH_SHORT).show();
            }
        }

    }

}