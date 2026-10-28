#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform sampler2D Sampler3;
uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;
flat in vec3 particleData;

out vec4 fragColor;

const float RADIAL_DENSITY = 2.33;
const float SMOKE_DENSITY = 3.0;

float exponentialDensity(float depth, float density) {
    return 1.0 - exp(-depth * density);
}

vec3 toLdr(vec3 c) {
    float m = max(c.r, max(c.g, c.b));
    if (m <= 1.0) {
        return c;
    }
    float w = clamp(log(m) / log(10.0) / 3.0, 0.0, 0.9);
    return mix(c / m, vec3(1.0), w);
}

void main() {
    vec2 uv = texCoord0 * particleData.x + vec2(particleData.y, fract(particleData.y * 7.31));

    float radial = exponentialDensity(clamp(1.0 - length(texCoord0 - 0.5) / 0.5, 0.0, 1.0), RADIAL_DENSITY);
    float smoke = texture(Sampler0, uv).r;
    float opacity = exponentialDensity(radial * smoke, SMOKE_DENSITY) * vertexColor.a;

    float dither = texelFetch(Sampler3, ivec2(mod(gl_FragCoord.xy, 64.0)), 0).r;
    if (opacity <= dither) {
        discard;
    }

    float spirit = texture(Sampler1, uv).r;
    vec3 color = toLdr(vertexColor.rgb * (1.0 + spirit * particleData.z));
    fragColor = linear_fog(vec4(color, 1.0) * ColorModulator, vertexDistance, FogStart, FogEnd, FogColor);
}