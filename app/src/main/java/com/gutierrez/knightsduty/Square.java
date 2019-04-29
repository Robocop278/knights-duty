package com.gutierrez.knightsduty;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glTexParameterf;
import static android.opengl.GLES20.glUniform1f;

public class Square {

    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;

    private float currentFrame = 0;
    private float animationSpeed = 0.0f;
    private float NUM_COLS = 1;
    private float NUM_ROWS = 1;
    private int TOT_FRAMES = 1;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    private float squareCoords[];

    private short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

    float color[] = { 0.03671875f, 0.70953125f, 0.72265625f, 1.0f };

    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
                    "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +

                    "attribute vec2 a_TexCoordinate;" + // Per-vertex texture coordinate information we will pass in.
                    "varying vec2 v_TexCoordinate;" +   // This will be passed into the fragment shader.

                    "uniform float u_Frame;" + //current frame to draw
                    "uniform float u_Cols;" + //total columns
                    "uniform float u_Rows;" + //total rows

                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "gl_Position = uMVPMatrix * vPosition;" +

                    // Pass through the texture coordinate.
                    "float currentCol = mod(u_Frame,u_Cols);" + //get current column through modulo
                    "float currentRow = floor(u_Frame/u_Rows);" + //get current row through floored division
                    "v_TexCoordinate.x = (currentCol+a_TexCoordinate.x)/u_Cols;" +
                    "v_TexCoordinate.y = (currentRow+a_TexCoordinate.y)/u_Rows;" +
                    "}";

    // Use to access and set the view transformation
    private int mMVPMatrixHandle;

    private final String fragmentShaderCode =
                    "precision mediump float;" +
                    //"uniform vec4 vColor;" + //OLD - draws raw color
                    "uniform sampler2D u_Texture;" +    // The input texture.
                    "varying vec2 v_TexCoordinate;" + // Interpolated texture coordinate per fragment.

                    "void main() {" +
                    //"gl_FragColor = vColor;" +

                    // Add attenuation.
                    //"diffuse = diffuse * (1.0 / (1.0 + (0.10 * distance)));" +
                    // Add ambient lighting
                    //"diffuse = diffuse + 0.3;" +
                    // Multiply the color by the diffuse illumination level and texture value to get final output color.
                    "gl_FragColor = texture2D(u_Texture, v_TexCoordinate);" + //'v_Color * diffuse' added after at start for 3d, don't think its needed in this context
                    "}";

    private final int mProgram;

    //private final FloatBuffer vertexBuffer;
    private final FloatBuffer textureBuffer;
    //private final ShortBuffer drawListBuffer;
    final float[] squareTexCoords =  {
            1.0f, 0.0f,   // top right
            0.0f, 0.0f,   // top left
            0.0f, 1.0f,   // bottom left
            1.0f, 1.0f};  // bottom right

    private int textureDataHandle;
    private int textureUniformHandle;
    private int textureCoordinateHandle;

    private int loadTexture(final Context context, final int resourceId){
        final int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);
        if (textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling
            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GL_TEXTURE_2D, textureHandle[0]);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
            // Set filtering
            GLES20.glTexParameteri(GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST); //set to nearest to allow pixels to display normally instead of aliased
            GLES20.glTexParameteri(GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
            //Turn on blending to allow alpha channel of PNGs to go through
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            GLES20.glEnable(GLES20.GL_BLEND);
            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }
        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }
    public void setSpritesheet(final Context context, final int resourceId, int numFrames, float numCols, float numRows, float animSpeed){
        //load new sprite sheet parameters
        setTOT_FRAMES(numFrames);
        setNUM_COLS(numCols);
        setNUM_ROWS(numRows);
        setAnimationSpeed(animSpeed);
        //Proceed with normal texture mapping
        textureDataHandle = loadTexture(context,resourceId);
    }

    public void setCurrentFrame(float newFrame){
        currentFrame = Math.min(getTOT_FRAMES(), newFrame); //don't allow a frame higher than the maximum amount of frames in the current loaded texture
    }

    public Square(float width, float height) {
        //define square coords according to width and height
        squareCoords = new float[] {
                -width,  height, 0.0f,     // top left
                 width,  height, 0.0f,     // top right
                 width, -height, 0.0f,     // bottom right
                -width, -height, 0.0f };   // bottom left

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        ByteBuffer texCoordinates = ByteBuffer.allocateDirect(squareTexCoords.length * 4);
        texCoordinates.order(ByteOrder.nativeOrder());
        textureBuffer = texCoordinates.asFloatBuffer();
        textureBuffer.put(squareTexCoords);
        textureBuffer.position(0);

        int vertexShader = GLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = GLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        textureDataHandle = loadTexture(MyApplication.getAppContext(), R.drawable.test_checker_pattern);
        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();
        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);
        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);
        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);
    }

    private int mPositionHandle;
    private int mColorHandle;

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);
        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        // Enable a handle to the square vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        // Prepare the square coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);


        textureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");
        GLES20.glVertexAttribPointer(textureCoordinateHandle, 2, GLES20.GL_FLOAT, false,
                0, textureBuffer);
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);


        int frameHandle = glGetUniformLocation(mProgram, "u_Frame");
        glUniform1f(frameHandle, (float) Math.floor(currentFrame));
        currentFrame = (currentFrame+animationSpeed)%TOT_FRAMES;
        int colHandle = glGetUniformLocation(mProgram, "u_Cols");
        glUniform1f(colHandle,NUM_COLS);
        int rowHandle = glGetUniformLocation(mProgram, "u_Rows");
        glUniform1f(rowHandle,NUM_ROWS);

        textureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
        //GLRenderer.checkGlError("glGetUniformLocation");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureDataHandle);
        GLES20.glUniform1i(textureUniformHandle, 0);


        // get handle to fragment shader's vColor member
        //mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        // Set color for drawing the Square
        //GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        // Draw the Square
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,drawOrder.length,GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    public float getNUM_COLS()
    {
        return NUM_COLS;
    }

    public void setNUM_COLS(float NUM_COLS)
    {
        this.NUM_COLS = NUM_COLS;
    }

    public float getNUM_ROWS()
    {
        return NUM_ROWS;
    }

    public void setNUM_ROWS(float NUM_ROWS)
    {
        this.NUM_ROWS = NUM_ROWS;
    }

    public int getTOT_FRAMES()
    {
        return TOT_FRAMES;
    }

    public void setTOT_FRAMES(int TOT_FRAMES)
    {
        this.TOT_FRAMES = TOT_FRAMES;
    }

    public float getAnimationSpeed()
    {
        return animationSpeed;
    }

    public void setAnimationSpeed(float animationSpeed)
    {
        this.animationSpeed = animationSpeed;
    }
}
