in vec2 texCoord;
out vec4 fragColor;

uniform float Time;

void main() {
    vec2 centered = texCoord - vec2(0.5);
    float radius = length(centered);
    float angle = atan(centered.y, centered.x);

    angle += Time * 0.5 + (1.0 - radius * 2.0) * 2.0; // swirl

    vec2 swirledUV = vec2(cos(angle), sin(angle)) * radius + 0.5;

    float noise = sin(swirledUV.x * 12.0 + Time) * 0.5 + 0.5;
    noise *= sin(swirledUV.y * 12.0 + Time * 0.7) * 0.5 + 0.5;

    // radial fade
    float alpha = smoothstep(0.5, 0.35, radius);

    // coloring
    vec3 color = mix(vec3(0.1, 0.1, 0.1), vec3(0.5, 0.5, 0.5), noise);

    vec4 final = vec4(color, alpha * noise);

    fragColor = vec4(final.rgb, final.r*2);
}