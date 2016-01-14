/*===============================================================================
Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of QUALCOMM Incorporated, registered in the United States 
and other countries. Trademarks of QUALCOMM Incorporated are used with permission.
===============================================================================*/

package de.ye.yeapp.actions;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import com.qualcomm.vuforia.*;
import de.ye.yeapp.ApplicationSession;
import de.ye.yeapp.objects.*;
import de.ye.yeapp.utils.TimeParser;
import de.ye.yeapp.utils.Utils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

// The renderer class for the ImageTargets sample. 
public class ImageTargetRenderer implements GLSurfaceView.Renderer {
    private static final String LOGTAG = ImageTargetRenderer.class.getSimpleName();
    private static final float OBJECT_SCALE_FLOAT = 6.0f;
    boolean mIsActive = false;
    private ApplicationSession vuforiaAppSession;
    private ImageTargets mActivity;
    private Vector<Texture> mTextures;
    private int shaderProgramID;
    private int vertexHandle;
    private int normalHandle;
    private int textureCoordHandle;
    private int mvpMatrixHandle;
    private int texSampler2DHandle;
    private Quad mQuad;
    private Quad mCube2; // TODO only for testing... replace with Array
    private Vec3F mCubeTransform;
    private Vec3F mCube2Transform;
    private Renderer mRenderer;
    private float maxTranslateX = 210f;
    private float maxTranslateY = 148.485489f;


    public ImageTargetRenderer(ImageTargets activity, ApplicationSession session) {
        mActivity = activity;
        vuforiaAppSession = session;
    }

    // Called to draw the current frame.
    @Override
    public void onDrawFrame(GL10 gl) {
        if (!mIsActive) {
            return;
        }

        // Call our function to render content
        renderFrame();
    }

    // Called when the surface is created or recreated.
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(LOGTAG, "GLRenderer.onSurfaceCreated");

        initRendering();

        // Call Vuforia function to (re)initialize rendering after first use
        // or after OpenGL ES context was lost (e.g. after onPause/onResume):
        vuforiaAppSession.onSurfaceCreated();
    }

    // Called when the surface changed size.
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(LOGTAG, "GLRenderer.onSurfaceChanged");

        // Call Vuforia function to handle render surface size changes:
        vuforiaAppSession.onSurfaceChanged(width, height);
    }

    // Function for initializing the renderer.
    private void initRendering() {
        mQuad = new Quad();

        mRenderer = Renderer.getInstance();

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f : 1.0f);

        for (Texture t : mTextures) {
            GLES20.glGenTextures(1, t.mTextureID, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t.mTextureID[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, t.mWidth, t.mHeight, 0,
                    GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, t.mData);
        }

        shaderProgramID = Utils.createProgramFromShaderSrc(
                CubeShaders.CUBE_MESH_VERTEX_SHADER,
                CubeShaders.CUBE_MESH_FRAGMENT_SHADER);

        vertexHandle = GLES20.glGetAttribLocation(shaderProgramID, "vertexPosition");
        normalHandle = GLES20.glGetAttribLocation(shaderProgramID, "vertexNormal");
        textureCoordHandle = GLES20.glGetAttribLocation(shaderProgramID, "vertexTexCoord");
        mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgramID, "modelViewProjectionMatrix");
        texSampler2DHandle = GLES20.glGetUniformLocation(shaderProgramID, "texSampler2D");

        // Hide the Loading Dialog
        mActivity.loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);
    }

    // TODO try to translate only the 3D object
    private void computeTargetTranslationFromScreenVector(float screenDeltaX, float screenDeltaY,
                                                          Matrix44F modelViewMatrix) {

        Vec3F localTargetDisplacement = new Vec3F(screenDeltaX, screenDeltaY, 0);
        // Tool.setTranslation(modelViewMatrix, localTargetDisplacement);
    }

    // The render function.
    private void renderFrame() {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);

        int seconds = calendar.get(Calendar.SECOND);
        int milliseconds = calendar.get(Calendar.MILLISECOND);

        TimeParser timeParser = new TimeParser();
        float animationFactor = timeParser.combineSecondsAndMilliseconds(seconds, milliseconds);
        animationFactor = (animationFactor - 30000) / 30000f;

        float translateX = maxTranslateX * animationFactor;
        float translateY = maxTranslateY * animationFactor;

        // Log.d("ANIMATION: ", Float.toString(animationFactor));

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        State state = mRenderer.begin();
        mRenderer.drawVideoBackground();

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // handle face culling, we need to detect if we are using reflection
        // to determine the direction of the culling
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);

        if (Renderer.getInstance().getVideoBackgroundConfig().getReflection() == VIDEO_BACKGROUND_REFLECTION.VIDEO_BACKGROUND_REFLECTION_ON)
            GLES20.glFrontFace(GLES20.GL_CW); // Front camera
        else
            GLES20.glFrontFace(GLES20.GL_CCW); // Back camera

        // did we find any trackables this frame? If yes, show all objects on the found trackables
        for (int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++) {
            TrackableResult result = state.getTrackableResult(tIdx);
            Trackable trackable = result.getTrackable();
            Matrix44F modelViewMatrix_Vuforia = Tool.convertPose2GLMatrix(result.getPose());
            float[] modelViewMatrix = modelViewMatrix_Vuforia.getData();

            // TODO try to translate only the 3D object
//            float screenDeltaX = mCubeTransform.getData()[0];
//            float screenDeltaY = mCubeTransform.getData()[1];
//            computeTargetTranslationFromScreenVector(screenDeltaX, screenDeltaY, modelViewMatrix_Vuforia);


            // set the texture index according to the found trackable
            // the string should match the *.dat and the *.xml file in /assets
            int textureIndex = trackable.getName().equalsIgnoreCase("berlin_su") ? 0 : 1;

            // deal with the modelview and projection matrices
            float[] modelViewProjection = new float[16];

            // translate and scale Matrix
            // Matrix.translateM(modelViewMatrix, 0, translateX, translateY, OBJECT_SCALE_FLOAT);
            Matrix.translateM(modelViewMatrix, 0, 0, 0, OBJECT_SCALE_FLOAT);
            Matrix.scaleM(modelViewMatrix, 0, OBJECT_SCALE_FLOAT, OBJECT_SCALE_FLOAT, OBJECT_SCALE_FLOAT);

            Matrix.multiplyMM(modelViewProjection, 0, vuforiaAppSession
                    .getProjectionMatrix().getData(), 0, modelViewMatrix, 0);

            // activate the shader program and bind the vertex/normal/tex coords
            GLES20.glUseProgram(shaderProgramID);

            // -----------------------------------------------------------------
            // pass the data to the shader
            GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, mQuad.getVertices());
            GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 0, mQuad.getNormals());
            GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0,
                    mQuad.getTexCoords());

            GLES20.glEnableVertexAttribArray(vertexHandle);
            GLES20.glEnableVertexAttribArray(normalHandle);
            GLES20.glEnableVertexAttribArray(textureCoordHandle);

            // activate texture 0, bind it, and pass to shader
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures.get(textureIndex).mTextureID[0]);
            GLES20.glUniform1i(texSampler2DHandle, 0);

            // pass the model view matrix to the shader
            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);

            // finally draw the object
            GLES20.glDrawElements(GLES20.GL_TRIANGLES,
                    mQuad.getNumObjectIndex(), GLES20.GL_UNSIGNED_SHORT, mQuad.getIndices());

            // disable the enabled arrays
            GLES20.glDisableVertexAttribArray(vertexHandle);
            GLES20.glDisableVertexAttribArray(normalHandle);
            GLES20.glDisableVertexAttribArray(textureCoordHandle);
            // -----------------------------------------------------------------

            Utils.checkGLError("Render Frame");
        }
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        mRenderer.end();
    }

    private void printUserData(Trackable trackable) {
        String userData = (String) trackable.getUserData();
        Log.d(LOGTAG, "User Data: \"" + userData + "\"");
    }

    public void setTextures(Vector<Texture> textures) {
        mTextures = textures;
    }
}
