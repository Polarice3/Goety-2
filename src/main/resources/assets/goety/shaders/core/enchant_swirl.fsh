#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler3;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec2 texCoord1;

out vec4 fragColor;

void main() {
    if (texture(Sampler3, texCoord1).a < 0.1) {
        discard;
    }
    vec4 swirl = texture(Sampler0, texCoord0);
    if (swirl.a < 0.004) {
        discard;
    }
    vec4 color = swirl * vertexColor * ColorModulator;
    fragColor = color * linear_fog_fade(vertexDistance, FogStart, FogEnd);
}
