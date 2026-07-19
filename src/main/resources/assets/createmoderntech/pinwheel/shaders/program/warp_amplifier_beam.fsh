in vec2 texCoord;
out vec4 fragColor;

uniform float Time;

void main() {
    float angle = texCoord.x * 6.28318;
    float height = texCoord.y;

    float strand1 = sin(angle * 4.0 - Time * 3.0 + height * 6.0) * 0.5 + 0.5;
    float strand2 = sin(angle * 4.0 + Time * 3.0 - height * 6.0) * 0.5 + 0.5;
    float pattern = max(strand1, strand2);

    vec3 coreColor = vec3(0.078, 0.961, 0.816);
    vec3 highlightColor = vec3(0.5, 1.0, 1.0);
    vec3 color = mix(coreColor, highlightColor, pow(pattern, 2.0));

    fragColor = vec4(color, 1.0);
}