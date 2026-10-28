#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;
flat in int mode;

out vec4 fragColor;

const float TAIL_OPACITY = 12.0;

void main() {
    float a;
    if (mode == 0) {
        a = texture(Sampler0, texCoord0).a;
    } else if (mode == 1) {
        float band = texture(Sampler1, texCoord0).r;
        a = clamp(band * (1.0 - texCoord0.x) * TAIL_OPACITY, 0.0, 1.0);
    } else {
        a = 1.0;
    }
    a *= vertexColor.a * linear_fog_fade(vertexDistance, FogStart, FogEnd);
    if (a < 0.004) {
        discard;
    }
    fragColor = vec4(vertexColor.rgb, a) * ColorModulator;
}