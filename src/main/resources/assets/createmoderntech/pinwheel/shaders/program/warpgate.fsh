/**
    Shader based off of: https://www.shadertoy.com/view/lcfyDj
    By: https://github.com/MisterPrada    ,     https://www.shadertoy.com/user/misterprada
**/


in vec2 texCoord;
out vec4 fragColor;

uniform float Time;
uniform float Progress;
uniform float Closing;

vec4 permute_3d(vec4 x){ return mod(((x*34.0)+1.0)*x, 289.0); }
vec4 taylorInvSqrt3d(vec4 r){ return 1.79284291400159 - 0.85373472095314 * r; }
float simplexNoise3d(vec3 v) {
    const vec2 C = vec2(1.0/6.0, 1.0/3.0);
    const vec4 D = vec4(0.0, 0.5, 1.0, 2.0);
    vec3 i  = floor(v + dot(v, C.yyy));
    vec3 x0 = v - i + dot(i, C.xxx);
    vec3 g = step(x0.yzx, x0.xyz);
    vec3 l = 1.0 - g;
    vec3 i1 = min(g.xyz, l.zxy);
    vec3 i2 = max(g.xyz, l.zxy);
    vec3 x1 = x0 - i1 + C.xxx;
    vec3 x2 = x0 - i2 + 2.0*C.xxx;
    vec3 x3 = x0 - 1.0 + 3.0*C.xxx;
    i = mod(i, 289.0);
    vec4 p = permute_3d(permute_3d(permute_3d(
                                       i.z + vec4(0.0, i1.z, i2.z, 1.0))
                                   + i.y + vec4(0.0, i1.y, i2.y, 1.0))
                        + i.x + vec4(0.0, i1.x, i2.x, 1.0));
    float n_ = 1.0/7.0;
    vec3 ns = n_ * D.wyz - D.xzx;
    vec4 j = p - 49.0 * floor(p * ns.z * ns.z);
    vec4 x_ = floor(j * ns.z);
    vec4 y_ = floor(j - 7.0 * x_);
    vec4 x = x_ *ns.x + ns.yyyy;
    vec4 y = y_ *ns.x + ns.yyyy;
    vec4 h = 1.0 - abs(x) - abs(y);
    vec4 b0 = vec4(x.xy, y.xy);
    vec4 b1 = vec4(x.zw, y.zw);
    vec4 s0 = floor(b0)*2.0 + 1.0;
    vec4 s1 = floor(b1)*2.0 + 1.0;
    vec4 sh = -step(h, vec4(0.0));
    vec4 a0 = b0.xzyw + s0.xzyw*sh.xxyy;
    vec4 a1 = b1.xzyw + s1.xzyw*sh.zzww;
    vec3 p0 = vec3(a0.xy, h.x);
    vec3 p1 = vec3(a0.zw, h.y);
    vec3 p2 = vec3(a1.xy, h.z);
    vec3 p3 = vec3(a1.zw, h.w);
    vec4 norm = taylorInvSqrt3d(vec4(dot(p0,p0), dot(p1,p1), dot(p2,p2), dot(p3,p3)));
    p0 *= norm.x; p1 *= norm.y; p2 *= norm.z; p3 *= norm.w;
    vec4 m = max(0.6 - vec4(dot(x0,x0), dot(x1,x1), dot(x2,x2), dot(x3,x3)), 0.0);
    m = m * m;
    return 42.0 * dot(m*m, vec4(dot(p0,x0), dot(p1,x1), dot(p2,x2), dot(p3,x3)));
}

float fbm3d(vec3 x, const in int it) {
    float v = 0.0;
    float a = 0.5;
    vec3 shift = vec3(100);
    for (int i = 0; i < 32; ++i) {
        if (i < it) {
            v += a * simplexNoise3d(x);
            x = x * 2.0 + shift;
            a *= 0.5;
        }
    }
    return v;
}

vec3 rotateZ(vec3 v, float angle) {
    float c = cos(angle), s = sin(angle);
    return vec3(v.x*c - v.y*s, v.x*s + v.y*c, v.z);
}

float facture(vec3 v) {
    vec3 n = normalize(v);
    return max(max(n.x, n.y), n.z);
}

vec3 emission(vec3 color, float strength) {
    return color * strength;
}

void main() {
    // convert UV to centered [-1, 1]
    vec2 uv = (texCoord - 0.5) * 2.0;

    float radius = length(uv);

    //portal opens from edges inward
    float openRadius = Closing > 0.5
    ? Progress        // closing: threshold shrinks from edge inward (same as opening)
    : 0.95 - Progress; // opening: threshold grows from edge inward
    float radialFade = 0.0;
    if (Closing > 0.5) {
        radialFade = smoothstep(openRadius, openRadius - 0.05, radius);
    } else {
        radialFade = smoothstep(openRadius, openRadius + 0.05, radius);
    }
    float edgeFade = smoothstep(1.0, 0.85, radius);
    float alpha = radialFade * edgeFade;

    if (alpha <= 0.0) {
        fragColor = vec4(0.0);
        return;
    }

    vec3 color = vec3(uv.xy, 0.0);
    color.z += 0.5;
    color = normalize(color);
    color -= 0.2 * vec3(0.0, 0.0, Time);

    float angle = -log2(length(uv) + 0.001);
    color = rotateZ(color, angle);

    float frequency = 1.4;
    float distortion = 0.01;
    color.x = fbm3d(color * frequency + 0.0, 5) + distortion;
    color.y = fbm3d(color * frequency + 1.0, 5) + distortion;
    color.z = fbm3d(color * frequency + 2.0, 5) + distortion;
    vec3 noiseColor = color;

    noiseColor *= 2.0;
    noiseColor -= 0.1;
    noiseColor *= 0.188;
    noiseColor += vec3(uv.xy, 0.0);

    float noiseColorLength = length(noiseColor);
    noiseColorLength = 0.770 - noiseColorLength;
    noiseColorLength *= 4.2;

    vec3 emissionColor = emission(vec3(0.078, 0.961, 0.816), noiseColorLength * 0.4);

    float fac = length(uv) - facture(color + 0.32);
    fac += 0.1;
    fac *= 3.0;

    color = mix(emissionColor, vec3(fac), fac + 1.2);

    // fade in brightness with progress
    color *= Progress;

    fragColor = vec4(color, alpha * Progress);
}