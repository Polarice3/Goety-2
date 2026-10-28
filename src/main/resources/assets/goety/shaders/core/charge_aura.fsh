#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform vec4 ColorModulator;

in vec4 vertexColor;
in vec2 texCoord0;
in vec2 baseUV;

out vec4 fragColor;

const float TEXTURE_SIZE = 64.0;
const float STREAK_STRETCH = 0.25;

void main() {
    vec2 uv = floor(texCoord0 * TEXTURE_SIZE) / TEXTURE_SIZE;

    float n = texture(Sampler0, vec2(uv.x * STREAK_STRETCH, uv.y)).b;
    float n2 = n * n;
    float streak = n2 * n2;
    float minor = texture(Sampler0, uv * 2.0 + 0.37).g * 0.12;

    float lat = baseUV.x;
    float gradient = smoothstep(0.0, 0.3, lat) * (0.45 + 0.55 * lat);

    float cloud = texture(Sampler1, texCoord0 * vec2(0.5, 1.0 / 6.0)).r;
    float mask = smoothstep(vertexColor.a - 0.15, vertexColor.a + 0.15, cloud);

    vec3 color = vertexColor.rgb * (streak * 4.0 + minor + 0.03) * gradient * mask;
    float strength = max(vertexColor.r, max(vertexColor.g, vertexColor.b));
    color += vec3(streak * streak * 2.0 * strength * gradient * mask);
    fragColor = vec4(color, 1.0) * ColorModulator;
}