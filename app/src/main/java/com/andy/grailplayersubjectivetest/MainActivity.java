package com.andy.grailplayersubjectivetest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

public class MainActivity extends Activity {

    public String TAG = "ANDYBAO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button start = (Button) this.findViewById(R.id.start);
        final Button play_A = (Button) this.findViewById(R.id.play_A);
        final Button play_B = (Button) this.findViewById(R.id.play_B);
        final Button rating = (Button) this.findViewById(R.id.rating);
        final Button next = (Button) this.findViewById(R.id.next);
        final Button finish = (Button) this.findViewById(R.id.finish);
        final Button sign_out = (Button) this.findViewById(R.id.sign_out);
        Button admin = (Button) this.findViewById(R.id.admin_only);
        final User user_test = new User();

        user_test.displayTextVideDefaultInfo();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                user_test.userInitialization();

            }
        });

        play_A.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Content content = new Content();
                content.renameContentToTestName(user_test.getPlayATestingContentPath());
                content.testingContentPlay();
                Intent intent = new Intent(MainActivity.this, MonitorService.class);
                startService(intent);
                Log.d(TAG, "Button A  " + user_test.getPlayATestingContentPath());

            }
        });

        play_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Content content = new Content();
                content.renameContentToTestName(user_test.getPlayBTestingContentPath());
                content.testingContentPlay();
                Intent intent = new Intent(MainActivity.this, MonitorService.class);
                startService(intent);

                Log.d(TAG, "Button B " + user_test.getPlayBTestingContentPath());
            }
        });

        rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userProgressIndex = String.valueOf(user_test.getUserProgressNextIndex());

                user_test.userRatingDialogPopup(userProgressIndex);

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int userProgressIndexCurrentInt;
                int userProgressIndexNextInt;
                String userProgressIndexNextString;

                userProgressIndexCurrentInt = user_test.getUserProgressNextIndex();

                if (user_test.checkTestCaseFinish(userProgressIndexCurrentInt)) {

                    user_test.setUserProgressIndex(userProgressIndexCurrentInt);

                    //Todo: check current content have a result

                    if (user_test.userTestingIsFinished()) {

                        finish.setEnabled(true);
                        play_A.setEnabled(false);
                        play_B.setEnabled(false);
                        rating.setEnabled(false);
                        next.setEnabled(false);
                        sign_out.setEnabled(false);

                        //Log.d(TAG, "Testing is finished, thanks " + user_test.getUserName());

                        String toastText = "Testing is finished, thanks " + user_test.getUserName();
                        Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_SHORT).show();

                    }
                    else {
                        next.setEnabled(false);
                        userProgressIndexNextInt = user_test.getUserProgressNextIndex();
                        userProgressIndexNextString = String.valueOf(userProgressIndexNextInt);
                        user_test.setUserTestingContentPathRandomly(userProgressIndexNextString);

                        user_test.displayTextViewInfo(userProgressIndexNextString);
                    }
                }
                else {

                    String toastText = "Please rating case " + userProgressIndexCurrentInt;

                    Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_SHORT).show();
                }



            }
        });

        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder userSignOut = new AlertDialog.Builder(MainActivity.this);

                userSignOut.setTitle("Sign Out");
                userSignOut.setIcon(android.R.drawable.ic_dialog_info);
                userSignOut.setMessage("You will Sign Out");
                userSignOut.setCancelable(false);

                userSignOut.setPositiveButton("Sign Out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        start.setEnabled(true);
                        play_A.setEnabled(false);
                        play_B.setEnabled(false);
                        rating.setEnabled(false);
                        next.setEnabled(false);
                        sign_out.setEnabled(false);

                        user_test.displayTextVideDefaultInfo();

                    }
                });

                userSignOut.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                    }
                });

                userSignOut.show();
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, MonitorService.class);
                stopService(intent);

                user_test.updateSummary();
                user_test.displayTextVideDefaultInfo();
                finish.setEnabled(false);
                start.setEnabled(true);
                Toast.makeText(MainActivity.this, "Testing is finished, thank you!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Admin admin = new Admin();

                admin.adminPWDiglogPopup();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class Admin {

        Boolean pwFlag;

        public void adminPWDiglogPopup() {

            final EditText editText = new EditText(MainActivity.this);
            AlertDialog.Builder passWord = new AlertDialog.Builder(MainActivity.this);

            passWord.setTitle("Input password");
            passWord.setIcon(android.R.drawable.ic_dialog_info);
            passWord.setView(editText);
            passWord.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            passWord.setNegativeButton("Sign In", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String passWord = editText.getText().toString();

                    //Todo: need to put all method to if

                    if (passWord.equals("")) {

                        Intent intent = new Intent(MainActivity.this, AdminSettingsActivity.class);
                        startActivity(intent);

                        pwFlag = true;
                    }
                    else {
                        pwFlag = false;
                        Toast.makeText(MainActivity.this, "PW is wrong, please try again",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
            passWord.show();
        }

    }

    public class User {

        String userName;
        String playATestingContentPath;
        String playBTestingContentPath;
        String contentFlagA;
        String contentFlagB;
        String monitorServerInfoFile = "montior_server_info";
        String monitorServerInfoKey = "motior_server_key";
        String hostInfo = "Host info: Andy Bao(2841)";

        public void userInitialization() {

            userSignInDialogPopup();

        }

        public String getUserName() {

            return userName;
            //Todo: more investigate

        }

        public void setUserTestingContentPathRandomly(String content_number) {

            int randomContentFlagIndex;
            String contentCodecA;
            String contentCodecB;
            String contentFormatA;
            String contentFormatB;
            Content contentA = new Content();
            Content contentB = new Content();
            Random random = new Random();

            randomContentFlagIndex = random.nextInt(2);

            if (randomContentFlagIndex == 0) {
                contentCodecA = "264";
                contentCodecB = "0";
                contentFormatA = "mp4";
                contentFormatB = "rmhd";
                contentFlagA = "0";
                contentFlagB = "1";

            }
            else {
                contentCodecA = "0";
                contentCodecB = "264";
                contentFormatA = "rmhd";
                contentFormatB = "mp4";
                contentFlagA = "1";
                contentFlagB = "0";
            }

            contentA.setContentPath(content_number, contentCodecA, contentFormatA);
            contentB.setContentPath(content_number, contentCodecB, contentFormatB);
            playATestingContentPath = contentA.getContentPath();
            playBTestingContentPath = contentB.getContentPath();

        }

        public String getPlayATestingContentPath() {

            SharedPreferences.Editor editor = getSharedPreferences(monitorServerInfoFile, MODE_PRIVATE).edit();
            editor.putString(monitorServerInfoKey, playATestingContentPath);
            editor.commit();

            return playATestingContentPath;
        }

        public String getPlayBTestingContentPath() {

            SharedPreferences.Editor editor = getSharedPreferences(monitorServerInfoFile, MODE_PRIVATE).edit();
            editor.putString(monitorServerInfoKey, playBTestingContentPath);
            editor.commit();

            return playBTestingContentPath;

        }

        public void userRatingDialogPopup(final String content_number) {

            //0: 264
            //1: rmhd

            final String contentFlagATemp = content_number + "-264_" + contentFlagA;
            final String contentFlagBTemp = content_number + "-264_" + contentFlagB;
            final String contentFlagSimilar = content_number + "-264_2";

            final Button next = (Button) findViewById(R.id.next);

            final UserData userData = new UserData();

            AlertDialog.Builder userRating = new AlertDialog.Builder(MainActivity.this);

            userRating.setTitle(userName + "'s choice");
            userRating.setIcon(android.R.drawable.ic_dialog_info);
            userRating.setMessage("Which one is better?");
            userRating.setPositiveButton("Similar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    userData.updateData(userName, contentFlagATemp, 0);
                    userData.updateData(userName, contentFlagBTemp, 0);
                    userData.updateData(userName, contentFlagSimilar, 1);
                    next.setEnabled(true);

                }
            });
            userRating.setNeutralButton("B", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    userData.updateData(userName, contentFlagATemp, 0);
                    userData.updateData(userName, contentFlagBTemp, 1);
                    userData.updateData(userName, contentFlagSimilar, 0);
                    next.setEnabled(true);

                }
            });
            userRating.setNegativeButton("A", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    userData.updateData(userName, contentFlagATemp, 1);
                    userData.updateData(userName, contentFlagBTemp, 0);
                    userData.updateData(userName, contentFlagSimilar, 0);
                    next.setEnabled(true);

                }
            });
            userRating.show();
        }

        public void setUserProgressIndex(int userProgressIndex) {

            UserData userData = new UserData();
            userData.updateUserSummary(userName, userProgressIndex);

        }

        public int getUserProgressIndex() {

            UserData userData = new UserData();
            return  userData.getUserProgress(userName);
        }

        public int getUserProgressNextIndex() {
            return getUserProgressIndex() + 1;
        }

        public void updateUserSummary(int userProgressIndex) {

            UserData userData = new UserData();

            userData.updateUserSummary(userName, userProgressIndex);

        }

        public void updateSummary() {

            UserData userData = new UserData();

            String contentXMLFolder = "/data/data/com.andy.grailplayersubjectivetest/shared_prefs";
            String summaryFileName = "summary.xml";

            if (userData.checkDataFileExist(contentXMLFolder, summaryFileName)) {
                userData.margeSummary(userName);
                Log.d(TAG, "summary.xml exist");
            }
            else {
                userData.createSummary();
                userData.margeSummary(userName);
            }

        }

        public boolean checkTestCaseFinish (int content_number) {

            UserData userData = new UserData();

            String contentFlag0 = content_number + "-264_0";
            String contentFlag1 = content_number + "-264_1";
            String contentFlag2 = content_number + "-264_2";

            int flag_0 = userData.getDataValue(userName, contentFlag0);
            int flag_1 = userData.getDataValue(userName, contentFlag1);
            int flag_2 = userData.getDataValue(userName, contentFlag2);

            int result = flag_0 + flag_1 + flag_2;

            if (result == 1) {
                return true;
            }
            else {
                return false;
            }
        }

        public void displayTextViewInfo (String  content_number) {

            Content content = new Content();
            int contentNumberTemp = content.getContentNumber();

            String firstLine = "Welcome to subjective testing" + "," + userName;
            String secondLine = "Current progress: " + contentNumberTemp + "-" + content_number;
            String thirdLine = hostInfo;


            TextView info_dispaly = (TextView) MainActivity.this.findViewById(R.id.info_display);
            info_dispaly.setText(firstLine + "\n" + secondLine + "\n" + thirdLine);

        }

        public void displayTextVideDefaultInfo() {
            String firstLine = "Welcome to subjective testing";
            String secondLine = "Current progress: 0-0";
            String thirdLine = hostInfo;


            TextView info_dispaly = (TextView) MainActivity.this.findViewById(R.id.info_display);
            info_dispaly.setText(firstLine + "\n" + secondLine + "\n" + thirdLine);

        }

        private void userSignInDialogPopup() {

            final UserData userData = new UserData();
            final EditText editText = new EditText(MainActivity.this);
            AlertDialog.Builder userSignIn = new AlertDialog.Builder(MainActivity.this);
            final Button play_A = (Button) findViewById(R.id.play_A);
            final Button play_B = (Button) findViewById(R.id.play_B);
            final Button start = (Button) findViewById(R.id.start);
            final Button rating = (Button) findViewById(R.id.rating);
            final Button sign_out = (Button) findViewById(R.id.sign_out);

            userSignIn.setTitle("Input Your Name");
            userSignIn.setIcon(android.R.drawable.ic_dialog_info);
            userSignIn.setView(editText);
            userSignIn.setCancelable(false);
            userSignIn.setPositiveButton("Sign Up", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    userName = editText.getText().toString();

                    if (userName.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Input your name first",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        if (userData.checkUserKeyExist(userName)) {
                            String toastText = userName + " exist, please Sign In";
                            Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_SHORT).show();
                        } else {
                            userData.updateUserSummary(userName, 0);
                            userData.createDataFile(userName, "mp4"); //- user_name.xml will cteated by rating
                            play_A.setEnabled(true);
                            play_B.setEnabled(true);
                            rating.setEnabled(true);
                            sign_out.setEnabled(true);
                            start.setEnabled(false);

                            String userProgressIndex;

                            userProgressIndex = String.valueOf(getUserProgressNextIndex());
                            setUserTestingContentPathRandomly(userProgressIndex);

                            displayTextViewInfo(userProgressIndex);

                        }
                    }
                }
            });
            userSignIn.setNegativeButton("Sign In", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    userName = editText.getText().toString();

                    if (userName.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Input your name first",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (userData.checkUserKeyExist(userName)) {

                            if(userTestingIsFinished()) {

                                String toaskText = userName + ": Your testing is finished.";
                                Toast.makeText(MainActivity.this, toaskText, Toast.LENGTH_SHORT).show();
                            }
                            else {

                                play_A.setEnabled(true);
                                play_B.setEnabled(true);
                                rating.setEnabled(true);
                                sign_out.setEnabled(true);
                                start.setEnabled(false);

                                String userProgressIndex;

                                userProgressIndex = String.valueOf(getUserProgressNextIndex());
                                setUserTestingContentPathRandomly(userProgressIndex);

                                displayTextViewInfo(userProgressIndex);

                                Log.d(TAG, "Test " + userProgressIndex);
                                //Todo: Continue test if test case number isn't to the end
                                //Todo: user testing progress index is wrong, 1 content is missed
                                //Todo: this bug can be fixed on sign out button, user index -1
                            }
                        }
                        else {
                            String toastText = userName + " doesn't exist, please Sign Up";
                            Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            userSignIn.show();
        }

        private boolean userTestingIsFinished() {

            //ContentData contentData = new ContentData();
            Content content = new Content();

            //int contentNumber = contentData.getTestContentNumber();
            int contentNumber = content.getContentNumber();
            int userData = getUserProgressIndex();

            if (userData == contentNumber) {
                return true;
            }
            else {
                return false;
            }
        }

        private void createUserData() {

        }

        private void createUserInfo() {
            /*Data userData = new Data();
            userData.createInfo("123");*/
        }

    }

    public class Content {

        private String contentRootPath = Environment.getExternalStorageDirectory().toString()
                + "/Test-Folder/";
        private String testingContentPath = contentRootPath + "Test.mp4";
        String contentPath;
        String contentName;

        public void contentInitialization() {


        }

        public String getContentPath() {
            return contentPath;
        }

        public String getContentName() {
            return contentName;
        }

        public int getContentNumber() {

            ContentData contentData = new ContentData();

            return contentData.getTestContentNumber();
        }

        public void setContentPath (String content_number, String content_flag, String content_format) {

            if (content_flag == "0") {
                contentName = content_number + "." + content_format;
            }
            else {
                contentName = content_number + "-" + content_flag + "." + content_format;
            }
            contentPath = contentRootPath + contentName;
        }

        public void testingContentPlay() {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            ComponentName componentName = new ComponentName("org.libsdl.app",
                    "org.libsdl.app.SDLActivity");
            intent.setComponent(componentName);
            intent.setData(Uri.parse(testingContentPath));
            Log.d("VideoPlay:playing", testingContentPath);
            startActivity(intent);
        }

        public void renameContentToTestName(String userTestContentPath) {

            File file = new File(userTestContentPath);
            file.renameTo(new File(testingContentPath));

        }

        public void renameContentToOriginal(String contentPathTemp) {

            File file = new File(testingContentPath);
            file.renameTo(new File(contentPathTemp));
        }

    }

    public class UserData extends Data {

        String USER_SUMMARY_FILE_NAME = "user_summary";
        String SUMMARY_FILE_NAME = "summary";


        public void updateUserSummary(String user_name, int value) {

            updateData(USER_SUMMARY_FILE_NAME, user_name, value);

        }

        public boolean checkUserKeyExist(String user_name) {

            return checkDataKeyExist(USER_SUMMARY_FILE_NAME, user_name);

        }

        public int getUserProgress(String user_name) {

            // Return how many videos had been checked
            return getDataValue(USER_SUMMARY_FILE_NAME, user_name);

        }

        public void setUserProgress(String user_name, int userProgressIndex) {

            updateData(USER_SUMMARY_FILE_NAME, user_name, userProgressIndex);
        }

        public void createSummary() {

            createDataFile(SUMMARY_FILE_NAME, "mp4");
        }

        public void margeSummary(String user_summary_file_name) {

            mergeData(SUMMARY_FILE_NAME, user_summary_file_name);

        }
    }

    public class ContentData extends Data {

        String contentRootFolder = Environment.getExternalStorageDirectory().toString() + "/Test-Folder";

        public int getTestContentNumber() {

            ArrayList arrayList = createDataIndex(contentRootFolder, "rmhd");
            return arrayList.size();

        }
    }

    public class Data {

        String data;
        String contentRootFolder = Environment.getExternalStorageDirectory().toString() + "/Test-Folder";

        public void setData (String d) {
            data = d;
        }

        public String getData () {
            return data;
        }

        public void createDataFile(String file_name, String content_format) {

            String[] fileList = scanDataFromFolder(contentRootFolder, content_format);

            for(String fileTitle:fileList) {

                for(int i = 0; i < 3; i++) {
                    int dot = fileTitle.lastIndexOf(".");
                    String fileTitleTemp = fileTitle.substring(0, dot);
                    updateData(file_name, fileTitleTemp + "_" + i, 0);
                }
            }
        }

        public ArrayList<String> createDataIndex (String folder_path, String file_name_filter) {

            ArrayList<String> dataIndexArrayList = new ArrayList<String>();
            String[] dataIndexTemp = scanDataFromFolder(folder_path, file_name_filter);

            for(int i = 0; i < dataIndexTemp.length; i++) {
                int dot = dataIndexTemp[i].lastIndexOf(".");
                dataIndexArrayList.add(dataIndexTemp[i].substring(0, dot));
            }

            //String[] dataIndex = (String[]) dataIndexArrayList.toArray();

            return dataIndexArrayList;

        }

        public boolean checkDataFileExist(String folder_path, String file_name) {

            /*ArrayList<String> dataFileIndex = createDataIndex(folder_path, "0");

            return dataFileIndex.contains(file_name);*/

            String[] dataIndexTemp = scanDataFromFolder(folder_path, "0");

            return Arrays.asList(dataIndexTemp).contains(file_name);


        }

        public void updateData(String file_name, String key, int value) {

            SharedPreferences.Editor editor = getSharedPreferences(file_name, MODE_PRIVATE).edit();
            editor.putInt(key, value);
            editor.commit();

        }

        public void mergeData(String main_file_name, String file_name) {

            SharedPreferences mainFile = getSharedPreferences(main_file_name, MODE_PRIVATE);

            SharedPreferences file = getSharedPreferences(file_name, MODE_PRIVATE);

            int mainFileValue;
            int fileValue;

            Map<String, ?> mainFileMap = mainFile.getAll();
            Map<String, ?> fileMap = file.getAll();

            for (String mainFileKey: mainFileMap.keySet()) {
                for (String fileKey: fileMap.keySet()) {
                    if (mainFileKey.equals(fileKey)) {
                        mainFileValue = getDataValue(main_file_name, mainFileKey);
                        fileValue = getDataValue(file_name, fileKey);
                        mainFileValue = mainFileValue + fileValue;
                        updateData(main_file_name, mainFileKey, mainFileValue);
                    }
                }
            }
        }

        public void backupData() {

        }

        protected boolean checkDataKeyExist(String file_name, String key) {

            SharedPreferences pref = getSharedPreferences(file_name, MODE_PRIVATE);
            return pref.contains(key);

        }

        protected int getDataValue(String file_name, String key) {
            SharedPreferences pref = getSharedPreferences(file_name, MODE_PRIVATE);
            return pref.getInt(key, 0);
        }

        protected String[] scanDataFromFolder (String folder_path, final String file_name_filter) {

            FilenameFilter fileNameFilter = new FilenameFilter() {
                @Override
                public boolean accept(File file, String s) {
                    return (s.endsWith(file_name_filter));
                }
            };

            File file = new File(folder_path);

            if (file_name_filter.equals("0")){
                return file.list();
            }
            else {
                return file.list(fileNameFilter);
            }


        }

    }


}
