package de.ye.app.objects;

import java.nio.Buffer;
import java.util.Arrays;

public class Quad extends MeshObject {

    private static final float[] mVertices = new float[]{
            -1.5f, -1f, 0.0f, // bottom-left corner
            1.5f, -1f, 0.0f,  // bottom-right corner
            1.5f, 1f, 0.0f,   // top-right corner
            -1.5f, 1f, 0.0f   // top-left corner
    };

    private static final float[] mNormals = new float[]{
            0.0f, 0.0f, 1.0f, // normal at bottom-left corner
            0.0f, 0.0f, 1.0f, // normal at bottom-right corner
            0.0f, 0.0f, 1.0f, // normal at top-right corner
            0.0f, 0.0f, 1.0f  // normal at top-left corner
    };

    private static final float[] mTexCoords = new float[]{
            0.0f, 0.0f, // tex-coords at bottom-left corner
            1.0f, 0.0f, // tex-coords at bottom-right corner
            1.0f, 1.0f, // tex-coords at top-right corner
            0.0f, 1.0f  // tex-coords at top-left corner
    };

    private static final short[] mIndices = new short[]{
            0, 1, 2, // triangle 1
            2, 3, 0  // triangle 2
    };

    private Buffer mVertBuff;
    private Buffer mTexCoordBuff;
    private Buffer mNormBuff;
    private Buffer mIndBuff;

    public Quad() {
        // init vertex buffers
        mVertBuff = fillBuffer(mVertices);
        mNormBuff = fillBuffer(mNormals);
        mTexCoordBuff = fillBuffer(mTexCoords);
        mIndBuff = fillBuffer(mIndices);
    }

    public String getVerticesArray() {
        return Arrays.toString(mVertices);
    }

    @Override
    public Buffer getBuffer(BUFFER_TYPE bufferType) {
        Buffer result = null;
        switch (bufferType) {
            case BUFFER_TYPE_VERTEX:
                result = mVertBuff;
                break;
            case BUFFER_TYPE_TEXTURE_COORD:
                result = mTexCoordBuff;
                break;
            case BUFFER_TYPE_INDICES:
                result = mIndBuff;
                break;
            case BUFFER_TYPE_NORMALS:
                result = mNormBuff;
            default:
                break;
        }
        return result;
    }


    @Override
    public int getNumObjectVertex() {
        return mVertices.length / 3;
    }


    @Override
    public int getNumObjectIndex() {
        return mIndices.length;
    }
}
