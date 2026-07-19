in vec2 texCoord;
out vec4 fragColor;

uniform sampler2D DiffuseSampler0; // the scene texture
uniform vec2 GateScreenPos; // gate center in NDC (-1 to 1)
uniform float WarpStrength;
uniform float WarpRadius;

void main() {
    vec2 uv = texCoord;

    // convert texCoord to NDC for distance calculation
    vec2 ndcUV = uv * 2.0 - 1.0;

    float dist = length(ndcUV - GateScreenPos);

    if (dist < WarpRadius) {
        float factor = 1.0 - (dist / WarpRadius);
        float smoothh = smoothstep(0.0, 1.0, factor);

    // pull UVs toward gate center
    vec2 dir = normalize(ndcUV - GateScreenPos);
    uv -= dir * smoothh * WarpStrength * 0.05;
    }

    fragColor = texture(DiffuseSampler0, uv);
}