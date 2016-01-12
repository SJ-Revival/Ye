package de.ye.yeapp.objects;

import java.nio.Buffer;

public class Circle extends MeshObject {

    private Buffer mVertBuff;
    private Buffer mTexCoordBuff;
    private Buffer mNormBuff;
    private Buffer mIndBuff;

    private int indicesNumber = 0;
    private int verticesNumber = 0;


    public Circle() {
        setVerts();
        setTexCoords();
        setNorms();
        setIndices();
    }

    private void setVerts() {
        // TODO
    }

    private void setTexCoords() {
        // TODO
    }

    private void setNorms() {
        // TODO
    }

    private void setIndices() {
        // TODO
    }

    public int getNumObjectIndex() {
        return indicesNumber;
    }


    @Override
    public int getNumObjectVertex() {
        return verticesNumber;
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
            case BUFFER_TYPE_NORMALS:
                result = mNormBuff;
                break;
            case BUFFER_TYPE_INDICES:
                result = mIndBuff;
            default:
                break;

        }

        return result;
    }
}
