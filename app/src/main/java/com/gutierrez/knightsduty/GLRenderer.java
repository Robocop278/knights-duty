package com.gutierrez.knightsduty;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class GLRenderer implements GLSurfaceView.Renderer
{
    private MainActivity parentActivity;
    public void setParent(MainActivity parentActivity)
    {
        this.parentActivity = parentActivity;
    }

    private Triangle mTriangle;
    private Square mSquare;
    private Square mBackground;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private int[] mView = new int[4];

    private ArrayList<GameUnit> entities = new ArrayList<>();
    private ArrayList<Castle> playerCastles = new ArrayList<>();
    private Queue<ShopUnit> mUnitBuildQueue = new LinkedList<>();
    private boolean mUnitBuilding = false; //flag that indicates that this game instance's castle is building a unit
    private int mUnitBuildTime = 0; //timer for the unit building queue


    private final float MAX_ZOOM = -2f; //anything above or below these values can cause some graphical errors if the view frustrum is not adjusted
    private final float MIN_ZOOM = -10f;

    private volatile float mAngle;
    private volatile float mEyeX = 0f;
    private volatile float mEyeY = 0f;
    private volatile float mEyeZ = -4f;

    private int thisPlayer = 0; //DO NOT KNOW IF THIS IS WHERE WE SHOULD PUT THIS, MIGHT HAVE TO BE DEFINED ELSEWHERE BUT IM NOT SURE YET


    public float getEyeX()
    {
        return mEyeX;
    }

    public void setEyeX(float eyeX)
    {
        mEyeX = eyeX;
    }

    public float getEyeY()
    {
        return mEyeY;
    }

    public void setEyeY(float eyeY)
    {
        mEyeY = eyeY;
    }

    public float getEyeZ()
    {
        return mEyeZ;
    }

    public void setEyeZ(float eyeZ)
    {
        mEyeZ = Math.max(MIN_ZOOM, Math.min(eyeZ, MAX_ZOOM));
        System.out.println("Eyez: " + mEyeZ);
    }


    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }

    public void spawnUnit(float x, float y)
    {
        entities.add(new Knight(x, y,0.4f,0.4f,-1));
    }

    public void buildUnit(int index){
        for(Castle castle: playerCastles)
        {
            if(castle.getOwner() == thisPlayer && castle.canPurchase(index)){
//                entities.add(castle.buildUnit(index));
                mUnitBuildQueue.add(castle.buildUnit(index));
                if(!mUnitBuilding){
                    mUnitBuilding = true;
                    mUnitBuildTime = mUnitBuildQueue.peek().getBuildTime();
                }
            }
        }
    }

    private float[] dereferenceTouch(float x, float y){
        System.out.println("Coordinates of touch before matrix transformation: X: " + x + ", Y: " + y);
        y = mView[3] - y;
        System.out.println("Coordinates of touch after flipping Y: X: " + x + ", Y: " + y);


        float[] nearPos = new float[4];
        float[] farPos = new float[4];

//        FloatBuffer winZ = FloatBuffer.allocate(4);

//        GLES20.glReadPixels((int)x,(int)y,1,1,GLES20.GL_DEPTH_COMPONENT,GLES20.GL_FLOAT, winZ);

        boolean unprojectedNear = (GLU.gluUnProject(x, y, 1,
                mMVPMatrix, 0, mProjectionMatrix, 0,
                mView, 0, nearPos, 0) == GLES20.GL_TRUE);
        boolean unprojectedFar = (GLU.gluUnProject(x, y, 10,
                mMVPMatrix, 0, mProjectionMatrix, 0,
                mView, 0, farPos, 0) == GLES20.GL_TRUE);

        if (unprojectedNear && unprojectedFar)
        {
            // To convert the transformed 4D vector to 3D, you must divide
            // it by the W component
            //nearPos = convertTo3d(nearPos);
            //farPos = convertTo3d(farPos);

            System.out.println("Converted point coords at farpos: X: " + farPos[0]/farPos[3] + ", Y: " + farPos[1]/farPos[3] + ", Z(?): " + farPos[2]/farPos[3] );
            System.out.println("Converted point coords at nearpos: X: " + nearPos[0]/nearPos[3] + ", Y: " + nearPos[1]/nearPos[3] + ", Z(?): " + nearPos[2]/nearPos[3] );


            nearPos[0] /= nearPos[3];
            nearPos[1] /= nearPos[3];
            nearPos[2] /= nearPos[3];
            nearPos[3] /= nearPos[3];

            farPos[0] /= farPos[3];
            farPos[1] /= farPos[3];
            farPos[2] /= farPos[3];
            farPos[3] /= farPos[3];


            float floorViewX = (((farPos[0] - nearPos[0]) / (10 - 1)) * nearPos[2]) + nearPos[0];
            float floorViewY = ((((farPos[1] - nearPos[1]) / (10 - 1)) * nearPos[2])  + nearPos[1]) + (2f);
            System.out.println("Point coords after vectorization(?): X: " + floorViewX + ", Y: " + floorViewY);

            return new float[]{nearPos[0],nearPos[1]};

            // Use the near and far instead of the assumed camera position
                /*float floorViewX = (((farPos[0] - nearPos[0]) / (perspectiveFar - perspectiveNear)) * nearPos[2]) + nearPos[0];
                float floorViewY = ((((farPos[1] - nearPos[1]) / (perspectiveFar - perspectiveNear)) * nearPos[2])  + nearPos[1]) + (2f * positionY);


                touchPoint = new PointF(floorViewX, floorViewY);

                if (floorViewDrawable.getArea()
                        .contains(touchPoint.x, touchPoint.y))
                {
                    floorViewPoint = new PointF(nearPos[0], nearPos[1]);
                    Log.e(HostessMobileApp.TAG, "floorViewTouched");
                }*/
        }
        return null;
    }

    public int selectEntity(float x, float y)
    {
        //first, we must translate the touch coordinates into view/projection space
        float[] deRefTouch = dereferenceTouch(x,y);
        for(GameUnit entity: entities)
        {
            if(entity.touched(deRefTouch[0], deRefTouch[1]))
            {
                System.out.println("I AM A UNIT AND I HAVE BEEN TOUCHED");
                if (entity.getOwner() != thisPlayer){
                    System.out.println("I AM AN ENEMY UNIT");
                }
                else {
                    System.out.println("I AM A FRIEND UNIT");
                    entity.setSelected(true);
                }
                return 0;
            }
        }
        for(final Castle castle: playerCastles)
        {
            if(castle.touched(deRefTouch[0],deRefTouch[1])){
                System.out.println("I AM A CASTLE AND I HAVE BEEN TOUCHED");
                if (castle.getOwner() != thisPlayer){
                    System.out.println("I AM AN ENEMY CASTLE >:(");
                }
                else {
                    System.out.println("I'M YOUR CASTLE DUMBASS");
                    //entities.add(castle.buildUnit(0));
                    parentActivity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            parentActivity.showShop(castle.getUnitList(), castle.getAvailableUpgrades());
                        }
                    });
                }
                return 0;
            }
        }
        //spawnUnit(deRefTouch[0],deRefTouch[1]); spawns a unit when you touch empty ground, good for testing the touch de-referencing issue
        return -1;
    }

    public int commandUnits(float x, float y)
    {
        float[] deRefTouch = dereferenceTouch(x,y);
        GameObj touchedObj = null;
        for(GameUnit entity: entities)
        {
            if(entity.touched(deRefTouch[0], deRefTouch[1]))
            {
                System.out.println("I AM A UNIT AND I HAVE BEEN TOUCHED");
                if (entity.getOwner() != thisPlayer){
                    System.out.println("I AM AN ENEMY UNIT");
                    touchedObj = entity;
                    break;
                }
                else {
                    System.out.println("I AM A FRIEND UNIT");
                }
            }
        }
        if(touchedObj == null){
            for(Castle castle: playerCastles)
            {
                if(castle.touched(deRefTouch[0],deRefTouch[1])){
                    System.out.println("I AM A CASTLE AND I HAVE BEEN TOUCHED");
                    if (castle.getOwner() != thisPlayer){
                        System.out.println("I AM AN ENEMY CASTLE >:(");
                        touchedObj = castle;
                    }
                    else {
                        System.out.println("I'M YOUR CASTLE DUMBASS");
                        //entities.add(castle.buildUnit(0));
                    }
                }
            }
        }

        for(GameUnit entity: entities)
        {
            if(entity.isSelected())
            {
                if(touchedObj == null){
                    System.out.println("Setting destination to: (" + deRefTouch[0] + ", " + deRefTouch[1] + ")");
                    entity.move(deRefTouch[0],deRefTouch[1]);
                }
                else {
                    if(entity.getSTATE() != GameUnit.State.COOLDOWN) //prevents spamming click on enemies to attack faster
                        entity.attack(touchedObj);
                }
            }
        }
        return -1;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        mBackground = new Square(50,50);
        mBackground.setSpritesheet(MyApplication.getAppContext(),R.drawable.background,1,0.1f,0.1f,0f);
        Castle player1Castle = new Castle(20, 0, 0);
        Castle player2Castle = new Castle(-20,0, 1);
        playerCastles.add(player1Castle);
        playerCastles.add(player2Castle);
        //loading resources whenever screen is initially rendered
        // Set the background frame color
        GLES20.glClearColor(0.6f, 0.6f, 0.65f, 1.0f);

        // initialize a triangle
        mTriangle = new Triangle();
        // initialize a square
        mSquare = new Square(0.5f,0.5f);

        Knight mTestGameObj = new Knight(5f,5f,0.8f,0.8f,-1);

        //mTestGameObj.setDx(0.03f);
        //mTestGameObj.setDy(0.03f);

        entities.add(mTestGameObj);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        //will probably hold all the "viewport" related code
        GLES20.glViewport(0, 0, width, height);
        System.out.println("Width and Height of screen onSurfaceChanged: " + width + ", " + height);
        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 10);

        mView = new int[]{0,0,width,height};
    }

    private float[] mRotationMatrix = new float[16];
    private float[] mTranslationMatrix = new float[16];

    @Override
    public void onDrawFrame(GL10 gl)
    {
        //drawing of each frame
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, mEyeX, mEyeY, mEyeZ, mEyeX, mEyeY, 0f, 0f, 1.0f, 0.0f);
        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        float[] scratch = new float[16];
        // Create a rotation transformation for the triangle
        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);
        Matrix.setRotateM(mRotationMatrix, 0, angle, 0, 0, -1.0f);


        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);

        // Draw shape
        mBackground.draw(mMVPMatrix);
        mSquare.draw(scratch);

        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, -1.0f);
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        mTriangle.draw(scratch);

        Iterator<GameUnit> unitIterator = entities.iterator();

        while (unitIterator.hasNext()){
            //System.out.println("We are within the entity loop");
            //System.out.println("Current entity's X and Y: (" + entity.getX() + ", " + entity.getY() + ")");
            GameUnit entity = unitIterator.next();
            if(entity.getHealth() > 0){
                entity.update();
                Matrix.translateM(scratch, 0, mMVPMatrix, 0, entity.getX(), entity.getY(), 0);
                entity.draw(scratch);
            }
            else {
                unitIterator.remove(); //kill the unit
            }
        }
        /*
        for(GameUnit entity: entities)
        {
            //System.out.println("We are within the entity loop");
            //System.out.println("Current entity's X and Y: (" + entity.getX() + ", " + entity.getY() + ")");
            if(entity.getHealth() > 0){
                entity.update();
            }
            else {
                entities.remove(entity); //kill the unit
            }
            Matrix.translateM(scratch, 0, mMVPMatrix, 0, entity.getX(), entity.getY(), 0);
            entity.draw(scratch);
//            if(Math.abs(entity.getX()) > 9)
//            {
//                entity.setDx(entity.getDx()*-1);
//            }
//            if(Math.abs(entity.getY()) > 5)
//            {
//                entity.setDy(entity.getDy()*-1);
//            }
        }
        */
        if(mUnitBuilding){
            if(mUnitBuildTime > 0){
                mUnitBuildTime--;
                System.out.println("BUILDING UNIT, TIME LEFT: " + mUnitBuildTime);
            }
            else {
                if(mUnitBuildQueue.peek() != null){
                    entities.add(mUnitBuildQueue.poll().unitInstance().copy());
                    if(mUnitBuildQueue.peek() != null){
                        mUnitBuildTime = mUnitBuildQueue.peek().getBuildTime();
                    }
                    else {
                        mUnitBuilding = false;
                    }
                }
                else {
                    mUnitBuilding = false;
                }
            }
        }

        for(Castle castle: playerCastles){
            castle.update();
            Matrix.translateM(scratch, 0, mMVPMatrix, 0, castle.getX(), castle.getY(), 0);
            castle.draw(scratch);

            if(castle.getOwner() == thisPlayer){
                final float mGold = castle.getGold();
                final int mHealth = castle.getHealth();
                parentActivity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        parentActivity.updateGoldDisplay(mGold);
                        parentActivity.updateHealthDisplay(mHealth);
                    }
                });
            }
            else{
                if(castle.canPurchase(0)){
                    castle.setGold(castle.getGold()-castle.getUnitPrice(0));
                    GameUnit toAdd = castle.buildUnit(0).unitInstance().copy();
                    for(Castle enemyCastle: playerCastles){
                        if(enemyCastle.getOwner() != castle.getOwner())
                            toAdd.attack(enemyCastle);
                    }
                    entities.add(toAdd);
                }
            }
        }
    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}
