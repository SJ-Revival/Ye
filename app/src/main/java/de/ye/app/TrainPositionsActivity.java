/*===============================================================================
Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of QUALCOMM Incorporated, registered in the United States 
and other countries. Trademarks of QUALCOMM Incorporated are used with permission.
===============================================================================*/

package de.ye.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;
import com.qualcomm.vuforia.*;
import de.ye.app.objects.LoadingDialogHandler;
import de.ye.app.objects.Texture;
import de.ye.app.objects.Train;
import de.ye.app.objects.TrainLine;
import de.ye.app.ui.AppMenu.AppMenu;
import de.ye.app.ui.AppMenu.AppMenuGroup;
import de.ye.app.ui.AppMenu.AppMenuInterface;
import de.ye.app.utils.ApplicationGLView;
import de.ye.app.utils.JsonParser;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Vector;


public class TrainPositionsActivity extends Activity implements ApplicationControl, AppMenuInterface {
    final public static int CMD_BACK = -1;
    final public static int CMD_EXTENDED_TRACKING = 1;
    final public static int CMD_AUTOFOCUS = 2;
    final public static int CMD_FLASH = 3;
    final public static int CMD_CAMERA_FRONT = 4;
    final public static int CMD_CAMERA_REAR = 5;
    final public static int CMD_DATASET_START_INDEX = 6;
    private static final String LOGTAG = TrainPositionsActivity.class.getSimpleName();
    ApplicationSession vuforiaAppSession;
    LoadingDialogHandler loadingDialogHandler = new LoadingDialogHandler(this);
    boolean mIsDroidDevice = false;
    private DataSet mCurrentDataset;
    private int mCurrentDatasetSelectionIndex = 0;
    private int mStartDatasetsIndex = 0;
    private int mDatasetsNumber = 0;
    private ArrayList<String> mDatasetStrings = new ArrayList<>();
    private JSONArray trainFeed; // our feed that is updated every 30 seconds
    private ApplicationGLView mGlView; // Our OpenGL view
    private TrainPositionsRenderer mRenderer; // Our renderer
    private GestureDetector mGestureDetector;
    private Vector<Texture> mTextures; // The textures we will use for rendering
    private ArrayList<TrainLine> mTrainLines; // The train line with the path position data
    private ArrayList<Train> mTrains; // The train symbols we will show
    private boolean mSwitchDatasetAsap = false;
    private boolean mFlash = false;
    private boolean mContAutofocus = false;


    // We want to load specific textures from the APK, which we will later use
    // for rendering.
    private boolean mExtendedTracking = false;
    private View mFlashOptionView;
    private RelativeLayout mUILayout;
    private AppMenu mAppMenu;
    // Alert Dialog used to display SDK errors
    private AlertDialog mErrorDialog;

    // Called when the activity first starts or the user navigates back to an
    // activity.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOGTAG, "onCreate");
        super.onCreate(savedInstanceState);

        vuforiaAppSession = new ApplicationSession(this);

        startLoadingAnimation();
        mDatasetStrings.add("Berlin_SU.xml");

        vuforiaAppSession.initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mGestureDetector = new GestureDetector(this, new GestureListener());

        // Load any sample specific textures:
        mTextures = new Vector<>();
        loadTextures();

        // Load all used train lines... currently only S42
        mTrainLines = new ArrayList<>();
        loadTrainLines();

        // Load all trains we get from the live feed
        mTrains = new ArrayList<>();
        loadLiveFeed();

        mIsDroidDevice = Build.MODEL.toLowerCase().startsWith("droid");
    }

    private void loadTextures() {
        mTextures.add(Texture.loadTextureFromApk("TrainSymbols/S41.png", getAssets()));
        mTextures.add(Texture.loadTextureFromApk("TrainSymbols/S42.png", getAssets()));
        mTextures.add(Texture.loadTextureFromApk("TrainSymbols/U8.png", getAssets()));
    }

    private void loadTrainLines() { // TODO implement json loading
        JsonParser jsonParser = new JsonParser();
        InputStream s42InputStream = null;

        try {
            s42InputStream = getAssets().open("TrainLineData/S42.json");
            // TODO load all the train lines
        } catch (IOException e) {
            e.printStackTrace();
        }

        mTrainLines.add(new TrainLine(jsonParser.readJsonObject(s42InputStream)));
    }

    // TODO should load new data every 30 seconds
    private void loadLiveFeed() {
        JsonParser jsonParser = new JsonParser();
        InputStream s42InputStream = null;

        new Handler().postDelayed(new Runnable() {
            public void run() {
                // call JSON methods here
                new AttemptJson().execute();

                Log.d(LOGTAG, "loadLiveFeed async event");
                // mRenderer.setTrains(mTrains);
            }
        }, 1000); // TODO set to 30000

        try {
            s42InputStream = getAssets().open("JSON_Samples/bahnfeed_new.json");
            // TODO load the data feed from the external resource
        } catch (IOException e) {
            e.printStackTrace();
        }

        trainFeed = jsonParser.readJsonArray(s42InputStream);

        Log.i(LOGTAG, "loadLiveFeed");

        for (int i = 0; i < trainFeed.length(); i++) {
            try {
                mTrains.add(new Train(trainFeed.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // Called when the activity will start interacting with the user.
    @Override
    protected void onResume() {
        Log.d(LOGTAG, "onResume");
        super.onResume();

        // This is needed for some Droid devices to force portrait
        if (mIsDroidDevice) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        try {
            vuforiaAppSession.resumeAR();
        } catch (ApplicationException e) {
            Log.e(LOGTAG, e.getString());
        }

        // Resume the GL view:
        if (mGlView != null) {
            mGlView.setVisibility(View.VISIBLE);
            mGlView.onResume();
        }

    }

    // Callback for configuration changes the activity handles itself
    @Override
    public void onConfigurationChanged(Configuration config) {
        Log.d(LOGTAG, "onConfigurationChanged");
        super.onConfigurationChanged(config);

        vuforiaAppSession.onConfigurationChanged();
    }

    // Called when the system is about to start resuming a previous activity.
    @Override
    protected void onPause() {
        Log.d(LOGTAG, "onPause");
        super.onPause();

        if (mGlView != null) {
            mGlView.setVisibility(View.INVISIBLE);
            mGlView.onPause();
        }

        // Turn off the flash
        if (mFlashOptionView != null && mFlash) {
            // OnCheckedChangeListener is called upon changing the checked state
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                ((Switch) mFlashOptionView).setChecked(false);
            } else {
                ((CheckBox) mFlashOptionView).setChecked(false);
            }
        }

        try {
            vuforiaAppSession.pauseAR();
        } catch (ApplicationException e) {
            Log.e(LOGTAG, e.getString());
        }
    }

    // The final call you receive before your activity is destroyed.
    @Override
    protected void onDestroy() {
        Log.d(LOGTAG, "onDestroy");
        super.onDestroy();

        try {
            vuforiaAppSession.stopAR();
        } catch (ApplicationException e) {
            Log.e(LOGTAG, e.getString());
        }

        // Unload texture:
        mTextures.clear();
        mTextures = null;

        System.gc();
    }

    // Initializes AR application components.
    private void initApplicationAR() {
        // Create OpenGL ES view:
        int depthSize = 16;
        int stencilSize = 0;
        boolean translucent = Vuforia.requiresAlpha();

        mGlView = new ApplicationGLView(this);
        mGlView.init(translucent, depthSize, stencilSize);

        mRenderer = new TrainPositionsRenderer(this, vuforiaAppSession);
        mRenderer.setTextures(mTextures);
        mRenderer.setTrainLines(mTrainLines);
        mRenderer.setTrains(mTrains);
        mGlView.setRenderer(mRenderer);
    }

    private void startLoadingAnimation() {
        mUILayout = (RelativeLayout) View.inflate(this, R.layout.camera_overlay, null);

        mUILayout.setVisibility(View.VISIBLE);
        mUILayout.setBackgroundColor(Color.BLACK);

        // Gets a reference to the loading dialog
        loadingDialogHandler.mLoadingDialogContainer = mUILayout.findViewById(R.id.loading_indicator);

        // Shows the loading indicator at start
        loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);

        // Adds the inflated layout to the view
        addContentView(mUILayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    // Methods to load and destroy tracking data.
    @Override
    public boolean doLoadTrackersData() {
        TrackerManager tManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) tManager.getTracker(ObjectTracker.getClassType());
        if (objectTracker == null)
            return false;

        if (mCurrentDataset == null)
            mCurrentDataset = objectTracker.createDataSet();

        if (mCurrentDataset == null)
            return false;

        if (!mCurrentDataset.load(
                mDatasetStrings.get(mCurrentDatasetSelectionIndex),
                STORAGE_TYPE.STORAGE_APPRESOURCE))
            return false;

        if (!objectTracker.activateDataSet(mCurrentDataset))
            return false;

        int numTrackables = mCurrentDataset.getNumTrackables();
        for (int count = 0; count < numTrackables; count++) {
            Trackable trackable = mCurrentDataset.getTrackable(count);
            if (isExtendedTrackingActive()) {
                trackable.startExtendedTracking();
            }

            String name = "Current Dataset : " + trackable.getName();
            trackable.setUserData(name);
            Log.d(LOGTAG, "UserData:Set the following user data " + trackable.getUserData());
        }

        return true;
    }

    @Override
    public boolean doUnloadTrackersData() {
        // Indicate if the trackers were unloaded correctly
        boolean result = true;

        TrackerManager tManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) tManager.getTracker(ObjectTracker.getClassType());
        if (objectTracker == null)
            return false;

        if (mCurrentDataset != null && mCurrentDataset.isActive()) {
            if (objectTracker.getActiveDataSet().equals(mCurrentDataset)
                    && !objectTracker.deactivateDataSet(mCurrentDataset)) {
                result = false;
            } else if (!objectTracker.destroyDataSet(mCurrentDataset)) {
                result = false;
            }

            mCurrentDataset = null;
        }

        return result;
    }

    @Override
    public void onInitARDone(ApplicationException exception) {

        if (exception == null) {
            initApplicationAR();

            mRenderer.mIsActive = true;

            // Now add the GL surface view. It is important
            // that the OpenGL ES surface view gets added
            // BEFORE the camera is started and video
            // background is configured.
            addContentView(mGlView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

            // Sets the UILayout to be drawn in front of the camera
            mUILayout.bringToFront();

            // Sets the layout background to transparent
            mUILayout.setBackgroundColor(Color.TRANSPARENT);

            try {
                vuforiaAppSession.startAR(CameraDevice.CAMERA.CAMERA_DEFAULT);
            } catch (ApplicationException e) {
                Log.e(LOGTAG, e.getString());
            }

            boolean result = CameraDevice.getInstance().setFocusMode(
                    CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);

            if (result) {
                mContAutofocus = true;
            } else {
                Log.e(LOGTAG, "Unable to enable continuous autofocus");
            }

            mAppMenu = new AppMenu(this, this, "Image Targets", mGlView, mUILayout, null);
            setAppMenuSettings();

        } else {
            Log.e(LOGTAG, exception.getString());
            showInitializationErrorMessage(exception.getString());
        }
    }

    // Shows initialization error messages as System dialogs
    public void showInitializationErrorMessage(String message) {
        final String errorMessage = message;
        runOnUiThread(new Runnable() {
            public void run() {
                if (mErrorDialog != null) {
                    mErrorDialog.dismiss();
                }

                // Generates an Alert Dialog to show the error message
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        TrainPositionsActivity.this);
                builder.setMessage(errorMessage)
                        .setTitle(getString(R.string.INIT_ERROR))
                        .setCancelable(false)
                        .setIcon(0)
                        .setPositiveButton(getString(R.string.button_OK),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                    }
                                });

                mErrorDialog = builder.create();
                mErrorDialog.show();
            }
        });
    }

    @Override
    public void onQCARUpdate(State state) {
        if (mSwitchDatasetAsap) {
            mSwitchDatasetAsap = false;
            TrackerManager tm = TrackerManager.getInstance();
            ObjectTracker ot = (ObjectTracker) tm.getTracker(ObjectTracker
                    .getClassType());
            if (ot == null || mCurrentDataset == null || ot.getActiveDataSet() == null) {
                Log.d(LOGTAG, "Failed to swap datasets");
                return;
            }

            doUnloadTrackersData();
            doLoadTrackersData();
        }
    }

    @Override
    public boolean doInitTrackers() {
        // Indicate if the trackers were initialized correctly
        boolean result = true;

        TrackerManager tManager = TrackerManager.getInstance();
        Tracker tracker;

        // Trying to initialize the image tracker
        tracker = tManager.initTracker(ObjectTracker.getClassType());
        if (tracker == null) {
            Log.e(LOGTAG, "Tracker not initialized. Tracker already initialized or the camera is already started");
            result = false;
        } else {
            Log.i(LOGTAG, "Tracker successfully initialized");
        }
        return result;
    }

    @Override
    public boolean doStartTrackers() {
        // Indicate if the trackers were started correctly
        boolean result = true;

        Tracker objectTracker = TrackerManager.getInstance().getTracker(
                ObjectTracker.getClassType());
        if (objectTracker != null) {
            objectTracker.start();
        }

        return result;
    }

    @Override
    public boolean doStopTrackers() {
        // Indicate if the trackers were stopped correctly
        boolean result = true;

        Tracker objectTracker = TrackerManager.getInstance().getTracker(ObjectTracker.getClassType());
        if (objectTracker != null) {
            objectTracker.stop();
        }

        return result;
    }

    @Override
    public boolean doDeinitTrackers() {
        // Indicate if the trackers were deinitialized correctly
        boolean result = true;

        TrackerManager tManager = TrackerManager.getInstance();
        tManager.deinitTracker(ObjectTracker.getClassType());

        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Process the Gestures
        return mAppMenu != null && mAppMenu.processEvent(event) || mGestureDetector.onTouchEvent(event);
    }

    boolean isExtendedTrackingActive() {
        return mExtendedTracking;
    }

    // This method sets the menu's settings
    private void setAppMenuSettings() {
        AppMenuGroup group;

        group = mAppMenu.addGroup("", false);
        group.addTextItem(getString(R.string.menu_back), -1);

        group = mAppMenu.addGroup("", true);
        group.addSelectionItem(getString(R.string.menu_extended_tracking), CMD_EXTENDED_TRACKING, false);
        group.addSelectionItem(getString(R.string.menu_contAutofocus), CMD_AUTOFOCUS, mContAutofocus);
        mFlashOptionView = group.addSelectionItem(getString(R.string.menu_flash), CMD_FLASH, false);


        boolean deviceHasFrontCamera = false;
        boolean deviceHasBackCamera = false;

        // TODO support the new camera2 package for Lollipop and higher builds
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
        CameraInfo ci = new CameraInfo();

        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, ci);
            if (ci.facing == CameraInfo.CAMERA_FACING_FRONT) {
                deviceHasFrontCamera = true;
            } else if (ci.facing == CameraInfo.CAMERA_FACING_BACK) {
                deviceHasBackCamera = true;
            }
        }
//        }

        if (deviceHasBackCamera && deviceHasFrontCamera) {
            group = mAppMenu.addGroup(getString(R.string.menu_camera), true);
            group.addRadioItem(getString(R.string.menu_camera_front), CMD_CAMERA_FRONT, false);
            group.addRadioItem(getString(R.string.menu_camera_back), CMD_CAMERA_REAR, true);
        }

        group = mAppMenu.addGroup(getString(R.string.menu_datasets), true);
        mStartDatasetsIndex = CMD_DATASET_START_INDEX;
        mDatasetsNumber = mDatasetStrings.size();

        //group.addRadioItem("Stones & Chips", mStartDatasetsIndex, true);
        group.addRadioItem(getString(R.string.menu_radio_navigation), mStartDatasetsIndex, true);
        //group.addRadioItem("{item name}", mStartDatasetsIndex + 1, false);

        mAppMenu.attachMenu();
    }

    @Override
    public boolean menuProcess(int command) {

        boolean result = true;

        switch (command) {
            case CMD_BACK:
                finish();
                break;

            case CMD_FLASH:
                result = CameraDevice.getInstance().setFlashTorchMode(!mFlash);

                if (result) {
                    mFlash = !mFlash;
                } else {
                    showToast(getString(mFlash ? R.string.menu_flash_error_off : R.string.menu_flash_error_on));
                    Log.e(LOGTAG, getString(mFlash ? R.string.menu_flash_error_off : R.string.menu_flash_error_on));
                }
                break;

            case CMD_AUTOFOCUS:

                if (mContAutofocus) {
                    result = CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_NORMAL);

                    if (result) {
                        mContAutofocus = false;
                    } else {
                        showToast(getString(R.string.menu_contAutofocus_error_off));
                        Log.e(LOGTAG, getString(R.string.menu_contAutofocus_error_off));
                    }
                } else {
                    result = CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);

                    if (result) {
                        mContAutofocus = true;
                    } else {
                        showToast(getString(R.string.menu_contAutofocus_error_on));
                        Log.e(LOGTAG, getString(R.string.menu_contAutofocus_error_on));
                    }
                }
                break;

            case CMD_CAMERA_FRONT:
            case CMD_CAMERA_REAR:

                // Turn off the flash
                if (mFlashOptionView != null && mFlash) {
                    // OnCheckedChangeListener is called upon changing the checked state
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        ((Switch) mFlashOptionView).setChecked(false);
                    } else {
                        ((CheckBox) mFlashOptionView).setChecked(false);
                    }
                }

                vuforiaAppSession.stopCamera();

                try {
                    vuforiaAppSession
                            .startAR(command == CMD_CAMERA_FRONT ? CameraDevice.CAMERA.CAMERA_FRONT
                                    : CameraDevice.CAMERA.CAMERA_BACK);
                } catch (ApplicationException e) {
                    showToast(e.getString());
                    Log.e(LOGTAG, e.getString());
                    result = false;
                }
                doStartTrackers();
                break;

            case CMD_EXTENDED_TRACKING:
                for (int tIdx = 0; tIdx < mCurrentDataset.getNumTrackables(); tIdx++) {
                    Trackable trackable = mCurrentDataset.getTrackable(tIdx);

                    if (!mExtendedTracking) {
                        if (!trackable.startExtendedTracking()) {
                            Log.e(LOGTAG,
                                    "Failed to start extended tracking target");
                            result = false;
                        } else {
                            Log.d(LOGTAG,
                                    "Successfully started extended tracking target");
                        }
                    } else {
                        if (!trackable.stopExtendedTracking()) {
                            Log.e(LOGTAG,
                                    "Failed to stop extended tracking target");
                            result = false;
                        } else {
                            Log.d(LOGTAG,
                                    "Successfully started extended tracking target");
                        }
                    }
                }

                if (result)
                    mExtendedTracking = !mExtendedTracking;

                break;

            default:
                if (command >= mStartDatasetsIndex
                        && command < mStartDatasetsIndex + mDatasetsNumber) {
                    mSwitchDatasetAsap = true;
                    mCurrentDatasetSelectionIndex = command
                            - mStartDatasetsIndex;
                }
                break;
        }

        return result;
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    // Process Single Tap event to trigger autofocus
    private class GestureListener extends
            GestureDetector.SimpleOnGestureListener {
        // Used to set autofocus one second after a manual focus is triggered
        private final Handler autofocusHandler = new Handler();


        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }


        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            // Generates a Handler to trigger autofocus
            // after 1 second
            autofocusHandler.postDelayed(new Runnable() {
                public void run() {
                    boolean result = CameraDevice.getInstance().setFocusMode(
                            CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO);

                    if (!result)
                        Log.e("SingleTapUp", "Unable to trigger focus");
                }
            }, 1000L);

            return true;
        }
    }

    private class AttemptJson extends AsyncTask<String, String, String> {

        private final String LOGTAG = AttemptJson.class.getSimpleName();

        @Override
        protected String doInBackground(String... params) {
            JsonParser jsonParser = new JsonParser();

            String REQUEST_URL = getString(R.string.VBB_FEED_URL);

            JSONArray json = jsonParser.getJSONArrayFromUrl(REQUEST_URL);
            Log.d(LOGTAG, "JSON requested");

            return null;
        }
    }
}
