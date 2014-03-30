/*
 Copyright (c) 2014 Wang Zhen Zhe

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.wangzhenzhe.library.TouchView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import java.io.File;
import java.io.FileInputStream;

import com.wangzhenzhe.artdaily.sqlite.ArtDailyDB;
import com.wangzhenzhe.library.TouchView.InputStreamWrapper.InputStreamProgressListener;

public class DBTouchImageView extends UrlTouchImageView 
{
	
	public ArtDailyDB mDB;
	
    public DBTouchImageView(Context ctx, ArtDailyDB db)
    {
        super(ctx);
        mDB = db;
    }
    
    public DBTouchImageView(Context ctx, AttributeSet attrs, ArtDailyDB db)
    {
        super(ctx, attrs);
        mDB = db;
    }

    public void setUrl(String imageid)
    {
        new ImageLoadTask().execute(imageid);
    }
    
    //No caching load
    public class ImageLoadTask extends UrlTouchImageView.ImageLoadTask
    {
        @Override
        protected Bitmap doInBackground(String... strings) {
            String sID = strings[0];
            Bitmap bm = null;
            try {
            	bm = mDB.loadArtImage(sID);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bm;
        }
    }
}
