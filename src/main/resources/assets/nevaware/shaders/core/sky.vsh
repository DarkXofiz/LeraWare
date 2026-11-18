#version 150

in vec3 Position;

uniform mat4 ProjMat;
uniform mat4 uView;
uniform float Radius;

out vec3 dir;

void main() {
    dir = Position;
    gl_Position = ProjMat * uView * vec4(Position * Radius, 1.0);
    // kubbe her zaman en arkada: sadece bos (gokyuzu) piksellere cizilir, arazinin onune asla gecmez
    gl_Position.z = gl_Position.w * 0.99999;
}
