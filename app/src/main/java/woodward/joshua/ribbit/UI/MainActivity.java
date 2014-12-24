package woodward.joshua.ribbit.UI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import woodward.joshua.ribbit.Model.ParseConstants;
import woodward.joshua.ribbit.R;

public class MainActivity extends android.support.v4.app.FragmentActivity implements ActionBar.TabListener {

    private final String TAG=MainActivity.class.getSimpleName();

    //these static ints are used in the camera / video related requests
    public static final int TAKE_PHOTO_REQUEST=0;
    public static final int TAKE_VIDEO_REQUEST=1;
    public static final int PICK_PHOTO_REQUEST=2;
    public static final int PICK_VIDEO_REQUEST=3;

    //definitials that we use in getOutputMediaFileUri method
    public static final int MEDIA_TYPE_IMAGE=4;
    public static final int MEDIA_TYPE_VIDEO=5;

    public static final int FILE_SIZE_LIMIT = 1024 * 1024 * 1; //limit is in bytes, this is 1 MB

    //NOTE: Different from URI class
    //Uniform Resource Identifier
    //(Path to specific file in android system)
    protected Uri mMediaUri;

    protected DialogInterface.OnClickListener mDialogListener=new DialogInterface.OnClickListener(){
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            switch(i){
                case 0:
                    //take picture
                    Intent takePhotoIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //depending on MEDIA_TYPE_IMAGE, get available path to store picture
                    mMediaUri=getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                    if(mMediaUri==null){
                        //there was an issue accessing the external storage
                        Toast.makeText(MainActivity.this, R.string.error_external_storage,Toast.LENGTH_LONG).show();
                    }else{
                        //external storage is available
                        //add to the activity the location in which to store the output from the camera intent
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,mMediaUri);
                        startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
                    }
                    break;
                case 1:
                    //take video
                    Intent takeVideoIntent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    mMediaUri=getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
                    if(mMediaUri==null){
                        Toast.makeText(MainActivity.this, R.string.error_external_storage,Toast.LENGTH_LONG).show();
                    }else{
                        //external storage is available
                        //add to the activity the location in which to store the output from the camera intent
                        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT,mMediaUri);
                        //add params to the video capture intent
                        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,10);
                        //SAD FACE - we have to take HIGH quality video, or SUPER LOW WAAAAAHHHHH
                        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0);
                        startActivityForResult(takeVideoIntent,TAKE_VIDEO_REQUEST);
                    }
                    break;
                case 2:
                    //choose picture
                    Intent choosePhotoIntent=new Intent(Intent.ACTION_GET_CONTENT);
                    //need to specify the type of the "content" (file), otherwise any type of content may be selected
                    choosePhotoIntent.setType("image/*");
                    startActivityForResult(choosePhotoIntent,PICK_PHOTO_REQUEST);
                    break;
                case 3:
                    //choose video
                    Intent chooseVideoIntent=new Intent(Intent.ACTION_GET_CONTENT);
                    //need to specify the type of the "content" (file), otherwise any type of content may be selected
                    chooseVideoIntent.setType("video/*");
                    Toast.makeText(MainActivity.this,R.string.video_limit_warning,Toast.LENGTH_LONG).show();
                    startActivityForResult(chooseVideoIntent, PICK_VIDEO_REQUEST);
                    break;
            }
        }

        //get the Uri of the media from the intent
        private Uri getOutputMediaFileUri(int mediaType) {
            //to be safe, you should check that the 'external storage' is available
            if(isExternalStorageAvailable()){

                //1. get the external storage directory
                //get the name of the application
                String appName=MainActivity.this.getString(R.string.app_name);
                File mediaStorageDir=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appName);

                //2. create our subdirectory
                //if the subdirectory doesnt exist, attempt to create it.
                //log failure if applicable
                if(! mediaStorageDir.exists()){
                    //media storage DNE
                    if(! mediaStorageDir.mkdirs()){
                        Log.e(TAG, "Failed to create directory.");
                        return null;
                    };
                }

                //3. create a filename
                //4. create the file
                File mediaFile;
                Date now=new Date();
                String timestamp=new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.US).format(now);
                //filename will include the filename in addition to the relative path TO the file
                String path=mediaStorageDir.getPath() + File.separator;
                //create different filenames for different types of media
                if(mediaType==MEDIA_TYPE_IMAGE){
                    mediaFile=new File(path + "IMG_" + timestamp + ".jpg");
                }
                else if(mediaType==MEDIA_TYPE_VIDEO){
                    mediaFile=new File(path + "VID_" + timestamp + ".mp4");
                }
                else{
                    return null;
                }

                Log.d(TAG, "File: " + Uri.fromFile(mediaFile));

                //return the path to the newly generated media file
                return Uri.fromFile(mediaFile);
            }else{
                //external storage is not available, return null Uri
                return null;
            }
        }

        //returns true if external storage is mounted
        private boolean isExternalStorageAvailable(){
            String state= Environment.getExternalStorageState();
            if(state.equals(Environment.MEDIA_MOUNTED)){
                return true;
            }else{
                return false;
            }
        }
    };

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_main);

        Log.d(TAG, "On create starting");

        //retrieves parse user and will redirect if nobody signed in
        ParseUser currentUser=ParseUser.getCurrentUser();
        if(currentUser==null){
            navigateToLogin();
        }else{
            Toast.makeText(this,currentUser.getUsername() + "'s Inbox!",Toast.LENGTH_LONG).show();
        }

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this,getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }
    // end on create

    //Request Code is code passed in through activity start
    //Result Code indicates success / fail
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){

            if(requestCode==PICK_PHOTO_REQUEST || requestCode == PICK_VIDEO_REQUEST){

                if(data==null){
                    Toast.makeText(MainActivity.this, R.string.general_error,Toast.LENGTH_LONG);
                }else{
                    //our intent has data
                    mMediaUri=data.getData();
                }

                //Log.i(TAG,"Media Uri: " + mMediaUri);

                //check if video
                if(requestCode==PICK_VIDEO_REQUEST){
                    //make sure that the file is less than 10 MB
                    int fileSize=0;
                    //we are doing all of this wacky stuff because the Uri of a video from gallery
                    //is structured differently than a video that has just been recorded.
                    InputStream inputStream=null;
                    try {
                        inputStream=getContentResolver().openInputStream(mMediaUri);
                        fileSize=inputStream.available();
                    } catch (FileNotFoundException e) {
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(MainActivity.this, R.string.video_pick_error,Toast.LENGTH_LONG).show();
                        return;
                    } catch (IOException e) {
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(MainActivity.this, R.string.video_pick_error,Toast.LENGTH_LONG).show();
                        return;
                    }
                    //finally blocks ALWAYS get called
                    finally{
                        try {
                            //we are good people and want to close the inputStream
                            inputStream.close();
                        } catch (IOException e) {
                            //RED HAIR DONT CARE *Intentionally Blank*
                        }
                    }

                    Log.d(TAG,"File Size: "+fileSize);
                    //check file size
                    if(fileSize>=FILE_SIZE_LIMIT){
                        Toast.makeText(MainActivity.this,R.string.video_limit_error,Toast.LENGTH_LONG ).show();
                        return;
                    }
                }
            }else{
                //add it to the gallery by broadcasting the intent
                //the gallery will be able to "listen" for the intents actions
                Intent mediaScanIntent=new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                //intent has special field that holds the URI
                mediaScanIntent.setData(mMediaUri);
                //send the broadcast
                sendBroadcast(mediaScanIntent);
            }

            //we have the right Uri, send the user to the recipients activity
            Intent recipientsIntent=new Intent(MainActivity.this, RecipientsActivity.class);
            recipientsIntent.setData(mMediaUri);

            String fileType;
            if(requestCode==PICK_PHOTO_REQUEST || requestCode==TAKE_PHOTO_REQUEST){
                fileType= ParseConstants.TYPE_IMAGE;
            }else{
                fileType=ParseConstants.TYPE_VIDEO;
            }
            recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE, fileType);




            startActivity(recipientsIntent);
        }
        //case hit when the user cancels out of the result activity
        else if(resultCode!= RESULT_CANCELED){
            Toast.makeText(MainActivity.this, R.string.general_error,Toast.LENGTH_LONG).show();
        }
    }


    private void navigateToLogin() {
        //create an Intent that will bring us to LoginActivity
        Intent loginIntent=new Intent(this,LoginActivity.class);
        //we are indicating that "logging in" should be a new task
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //we are indicating that "logging in" should clear the current task
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();

        switch(itemId) {
            case R.id.action_camera:
                AlertDialog.Builder cameraDialogBuilder=new AlertDialog.Builder(this);
                cameraDialogBuilder.setItems(R.array.camera_choices,mDialogListener);
                AlertDialog cameraDialog=cameraDialogBuilder.create();
                cameraDialog.show();
                break;
            case R.id.action_logout:
                ParseUser.logOut();
                navigateToLogin();
                break;
            case R.id.action_edit_friends:
                Intent editFriendsIntent = new Intent(MainActivity.this, EditFriendsActivity.class);
                startActivity(editFriendsIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */

    /**
     * A placeholder fragment containing a simple view.
     */


}
