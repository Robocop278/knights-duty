package com.gutierrez.knightsduty;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class MyGLSurfaceView extends GLSurfaceView
{
    private final float TOUCH_SCALE_FACTOR = 10000;
    private final int TRANSLATION_DIVISION_FACTOR = 100;
    private float mPreviousX; //X & Y variables to store touch movement coordinates
    private float mPreviousY;
    private float mXDown;     //X & Y variables to store touch down coordinates
    private float mYDown;
    private float mXUp;       //X & Y variables to store touch up coordinates
    private float mYUp;
    private boolean pressTether; //boolean to store if finger has drifted too far from initial touch down point
    private final float TETHER_THRESHOLD = 30; //distance to consider a touch broken if finger dragged (may need adjusting later on)
    private int mActivePointerId = INVALID_POINTER_ID;

    private MainActivity parentActivity;
    public void setParent(MainActivity parent){
        parentActivity = parent;
        mRenderer.setParent(parentActivity);
    }

    private boolean commandModeOn = false;

    private final GLRenderer mRenderer;

    private ScaleGestureDetector mScaleDetector;

    public void notifyUnitPurchase(int position)
    {
        final int fPos = position;
        //the call to build unit has to be placed in the queueEvent call to run the call on the renderer's context, rather than the UI context
        //otherwise, textures break due to inaccurate tracking of texture indices
        queueEvent(new Runnable()
        {
            @Override
            public void run()
            {
                mRenderer.buildUnit(fPos);
            }
        });
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //setEyeZ function automatically takes care of maximum/minimum zoom
            //System.out.println("Current scale factor: " + detector.getScaleFactor());
            //System.out.println("Previous EyeZ: " + mRenderer.getEyeZ());
            mRenderer.setEyeZ(mRenderer.getEyeZ()*(detector.getScaleFactor()));
            //System.out.println("New EyeZ: " + mRenderer.getEyeZ());
            pressTether = false;
            invalidate();
            return true;
        }

    }

    public MyGLSurfaceView(Context context, AttributeSet attrs){
        super(context, attrs);

        setEGLContextClientVersion(2);
        setPreserveEGLContextOnPause(true);
        mRenderer = new GLRenderer();
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        setRenderer(mRenderer);
        // Render the view only when there is a change in the drawing data (NOT USABLE IN A GAME CONTEXT)
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void toggleCommandMode(){
        commandModeOn = !commandModeOn;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        super.onTouchEvent(e);
        mScaleDetector.onTouchEvent(e); //pass touch events to the scale detector first before we do anything else
        float x;
        float y;
        int pointerIndex; //keeps track of the index(or otherwise primary) finger on screen

        if(!mScaleDetector.isInProgress())
        {
            //System.out.println("NOT SCALING, PERFORMING NORMAL ACTIONS");
            switch (e.getActionMasked()) {
                case MotionEvent.ACTION_MOVE:
                    //System.out.println("IN ACTION MOVE");
                    pointerIndex = e.findPointerIndex(mActivePointerId);
                    //System.out.println("POINTER INDEX FROM ACTIVE POINTER ID: " + pointerIndex);
                    x = e.getX(pointerIndex);
                    y = e.getY(pointerIndex);
                    //System.out.println("POINTER X AND Y FROM POINTER ID: " + x + ", " + y);
                    //check press down tether
                    if(pressTether)
                    {
                        System.out.println("RUNNING TETHER CHECK");
                        if(Math.abs(mXDown-x) > TETHER_THRESHOLD || Math.abs(mYDown-y) > TETHER_THRESHOLD)
                        {
                            System.out.println("TETHER BROKEN, SETTING EYEX TO: " + x + " + " + mXDown + ". SETTING EYEY TO: "  + y + " + " + mYDown);
                            //tether broken, no longer valid for a normal press and release
                            pressTether = false;
                            //perform movement from current camera position to current finger position
                            mRenderer.setEyeX(mRenderer.getEyeX() + ((x - mXDown)/TRANSLATION_DIVISION_FACTOR));
                            mRenderer.setEyeY(mRenderer.getEyeY() + ((y - mYDown)/TRANSLATION_DIVISION_FACTOR));
                        }
                    }
                    if(!pressTether)
                    {
                        //System.out.println("NORMAL MOVEMENT");
                        float dx = x - mPreviousX;
                        float dy = y - mPreviousY;
                        //System.out.println("DX AND DY AS CALCULATED: " + dx + ", " + dy);
                        mRenderer.setEyeX(mRenderer.getEyeX() + (dx/TRANSLATION_DIVISION_FACTOR));
                        mRenderer.setEyeY(mRenderer.getEyeY() + (dy/TRANSLATION_DIVISION_FACTOR));

                        invalidate();

                        mPreviousX = x;
                        mPreviousY = y;
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
                    System.out.println("action down, tether set");
                    if(mActivePointerId == INVALID_POINTER_ID)
                    {
                        pointerIndex = e.getActionIndex();
                        mActivePointerId = e.getPointerId(pointerIndex);
                        mXDown = e.getX(pointerIndex);
                        mYDown = e.getY(pointerIndex);
                        mPreviousX = mXDown;
                        mPreviousY = mYDown;
                        pressTether = true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    System.out.println("ACTIVE POINTER ID SET TO INVALID");
                    if(pressTether)
                    {
                        System.out.println("action up, tether OK, setting xUp and yUp");
                        //Normal press was not broken with drag, resume press
                        pointerIndex = e.getActionIndex();
                        mXUp = e.getX(pointerIndex);
                        mYUp = e.getY(pointerIndex);
                        queueEvent(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if(commandModeOn){
                                    mRenderer.commandUnits(mXUp,mYUp);
                                }
                                else
                                {
                                    mRenderer.selectEntity(mXUp,mYUp);
                                }
                            }
                        });
                    }
                    mActivePointerId = INVALID_POINTER_ID;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    System.out.println("ACTIVE POINTER ID SET TO INVALID");
                    mActivePointerId = INVALID_POINTER_ID;
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    System.out.println("POINTER UP CALLED");
                    pointerIndex = e.getActionIndex();
                    int pointerId = e.getPointerId(pointerIndex);
                    if(pointerId == mActivePointerId) {
                        System.out.println("OLD POINTER INDEX: " + pointerIndex);
                        int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                        System.out.println("NEW POINTER INDEX: " + pointerIndex);
                        mPreviousX = e.getX(newPointerIndex);
                        mPreviousY = e.getY(newPointerIndex);
                        mXDown = e.getX(newPointerIndex);
                        mYDown = e.getY(newPointerIndex);
                        System.out.println("OLD POINTER ID: " + mActivePointerId);
                        mActivePointerId = e.getPointerId(newPointerIndex);
                        System.out.println("NEW POINTER ID: " + mActivePointerId);
                    }
                    break;
            }
        }
        else
        {
            pointerIndex = e.findPointerIndex(mActivePointerId);
            x = e.getX(pointerIndex);
            y = e.getY(pointerIndex);
            mXDown = x;
            mYDown = y;
            mPreviousX = x;
            mPreviousY = y;
        }

        return true;
    }
}
