package com.sungjae.cokaru.whereami;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by sungjaefly on 16. 5. 3..
 */
public class GLView extends GLSurfaceView
{
    public GLView(Context context)
    {
        this(context, null);
    }

    public GLView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public GLView(Context context,AttributeSet attrs, int defStyle)
    {
        super(context, attrs);

        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        setRenderer(new GLRenderer(context));
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);
    }



}
