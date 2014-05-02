package com.wangzhenzhe.artdaily;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.wangzhenzhe.library.GalleryWidget.BasePagerAdapter.OnItemChangeListener;
import com.wangzhenzhe.library.GalleryWidget.DBPagerAdapter;
import com.wangzhenzhe.library.GalleryWidget.FilePagerAdapter;
import com.wangzhenzhe.library.GalleryWidget.GalleryViewPager;

import com.wangzhenzhe.artdaily.sqlite.ArtDailyDB;
import com.wangzhenzhe.artdaily.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.mm.sdk.openapi.*;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainscreenActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = false;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;
    
    private GalleryViewPager mViewPager;
    
    private IWXAPI api;
    
    private static final String APP_ID = "wx2bf3c79c55ab8b9b";
    
    private String imageIdtoShare;
    
    private ArtDailyDB db;
    
    private static final int THUMB_SIZE = 96;
    
    protected void onDestory()
    {
    	super.onDestroy();
    	db.close();
    	
    	api.unregisterApp();
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, APP_ID, true);
        
        api.registerApp(APP_ID);
        
        setContentView(R.layout.activity_mainscreen);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        mViewPager = (GalleryViewPager)findViewById(R.id.viewer);
        
        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        /*mSystemUiHider = SystemUiHider.getInstance(this, mViewPager, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });
*/
        
        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
//        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
        
        
        db = new ArtDailyDB(this);
        String[] urls = null;
        if (db.getArtImageCount() < 5)
        {
	        try {
				urls = getAssets().list("");
		
		        for (String filename : urls) 
		        {
		        	if (filename.matches(".+\\.jpg")) 
		        	{
		        		String path = getFilesDir() + "/" + filename;
		        		copy(getAssets().open(filename), new File(path) );
		        		db.addArtImage(path);
		        	}
		        }
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        List<String> items = null;
		try {
			items = db.loadArtImageIDs();
		} catch (Exception e) {
			e.printStackTrace();
		}

        DBPagerAdapter pagerAdapter = new DBPagerAdapter(this, items, db);
        pagerAdapter.setOnItemChangeListener(new OnItemChangeListener()
		{
			@Override
			public void onItemChange(int currentPosition)
			{
				Toast.makeText(MainscreenActivity.this, "Current item is " + currentPosition, Toast.LENGTH_SHORT).show();
				List<String> items = null;
				try {
					items = db.loadArtImageIDs();
				} catch (Exception e) {
					e.printStackTrace();
				}
				imageIdtoShare = items.get(currentPosition);
			}
		});
        
        
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(pagerAdapter);
  //      mViewPager.setSystemUiHider(mSystemUiHider);
        

        Button mExitBtn = (Button)findViewById(R.id.dummy_button);
        mExitBtn.setOnClickListener(new OnClickListener (){
        	public void onClick(View v){
        		System.exit(0);
        	}
        });
        
        Button mShareBtn = (Button)findViewById(R.id.weixin_button);
        mShareBtn.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		Bitmap bmp = db.loadArtImage(imageIdtoShare);;
				WXImageObject imgObj = new WXImageObject(bmp);
				
				WXMediaMessage msg = new WXMediaMessage();
				msg.mediaObject = imgObj;
				msg.title="ÐìÀÏÊ¦ÓÍ»­";
				
				Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
				msg.thumbData = ArtDailyDB.bmpToByteArray(thumbBmp, true);  //

				SendMessageToWX.Req req = new SendMessageToWX.Req();
				req.transaction = buildTransaction("img");
				req.message = msg;
				req.scene = SendMessageToWX.Req.WXSceneTimeline;
				boolean sent = api.sendReq(req);
				if (sent)
					Log.d("MainscreenActivity", "Message is sent to Wechat");
        	}
        });
        
    }
    
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
    
    public void copy(InputStream in, File dst) throws IOException {

        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
//        delayedHide(100);
        
        // Set up the user interaction to manually show or hide the system UI.
/*        mViewPager.mCurrentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });*/
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
/*    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            } else {
            	mSystemUiHider.hide();
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };
*/
    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
/*    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
  */  
    
}
