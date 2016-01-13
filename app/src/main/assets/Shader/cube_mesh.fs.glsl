#version 120

// TODO: integrate in \Ye\app\src\main\java\com\qualcomm\vuforia\samples\SampleApplication\utils\CubeShaders.java

precision mediump float;
varying vec2 texCoord;
varying vec4 normal;

uniform sampler2D texSampler2D;

void main() {
    gl_FragColor = texture2D(texSampler2D, texCoord);
}
