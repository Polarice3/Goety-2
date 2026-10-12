#version 150

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float GameTime;

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

const float COORDINATE_MULTIPLIER = 1.5;
const float CLOUD_STRENGTH = 2.0;
const float INNER_BEAM = 8.0;
const float POWER = 0.7;
const float GRADIENT_X = 2.5;

void main() {
    float time = GameTime * 24000.0;
    vec2 uv = vec2(texCoord0.x, texCoord0.y * COORDINATE_MULTIPLIER);

    float a = texture(Sampler0, uv * 1.0 + vec2( 0.00, -0.030 * time)).r;
    float b = texture(Sampler0, uv * 0.6 + vec2( 0.01, -0.012 * time)).r;
    float c = texture(Sampler0, uv * 1.7 + vec2(-0.01,  0.007 * time)).r;

    float clouds = clamp((a * b * 2.0 + c) * CLOUD_STRENGTH * 0.5, 0.0, 1.0);

    float across = abs(texCoord0.x - 0.5) * GRADIENT_X;
    float edge = max(1.0 - across, 0.0);
    float falloff = pow(edge, POWER);
    float core = pow(edge, INNER_BEAM);

    vec3 colour = vertexColor.rgb * falloff * clouds + vec3(core);
    float alpha = clamp(falloff * clouds + core, 0.0, 1.0) * vertexColor.a;

    if (alpha < 0.01) {
        discard;
    }

    fragColor = vec4(colour, alpha) * ColorModulator;
}