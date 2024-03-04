#version 150

#moj_import <matrix.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform float GameTime;
uniform int EndPortalLayers;

in vec4 texProj0;

const vec3[] COLORS = vec3[](
    vec3(0.04,0.03,0.1),
    vec3(0.04,0.06,0.11),
    vec3(0.1,0.1,0.05),
    vec3(0.1,0.07,0.04),
    vec3(0.04,0.02,0.09),
    vec3(0.1,0.05,0.08),
    vec3(0.01,0.1,0.02),
    vec3(0.02,0.04,0.1),
    vec3(0.1,0.04,0.03),
    vec3(0.1,0.22,0.11),
    vec3(0.09,0.01,0.04),
    vec3(0.06,0.05,0.07),
    vec3(0.22,0.08,0.16),
    vec3(0.1,0.13,0.22),
    vec3(0.13,0.35,0.27),
    vec3(0.18,0.34,0.54)
);

const mat4 SCALE_TRANSLATE = mat4(
    0.5, 0.0, 0.0, 0.25,
    0.0, 0.5, 0.0, 0.25,
    0.0, 0.0, 1.0, 0.0,
    0.0, 0.0, 0.0, 1.0
);

mat4 end_portal_layer(float layer) {
    mat4 translate = mat4(
        1.0, 0.0, 0.0, 17.0 / layer,
        0.0, 1.0, 0.0, (2.0 + layer / 1.5) * (GameTime * 1.5),
        0.0, 0.0, 1.0, 0.0,
        0.0, 0.0, 0.0, 1.0
    );

    mat2 rotate = mat2_rotate_z(radians((layer * layer * 4321.0 + layer * 9.0) * 2.0));

    mat2 scale = mat2((4.5 - layer / 4.0) * 2.0);

    return mat4(scale * rotate) * translate * SCALE_TRANSLATE;
}

out vec4 fragColor;

void main() {
    vec3 color = textureProj(Sampler0, texProj0).rgb * COLORS[0];
    for (int i = 0; i < EndPortalLayers; i++) {
        color += textureProj(Sampler1, texProj0 * end_portal_layer(float(i + 1))).rgb * COLORS[i];
    }
    fragColor = vec4(color, 1.0);
}
