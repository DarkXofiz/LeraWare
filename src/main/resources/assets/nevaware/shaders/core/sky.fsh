#version 150

in vec3 dir;

uniform float Time;
uniform float Style;
uniform float Quality;
uniform vec4 Cloud;    // speed, scale, density, swirl
uniform vec4 Bright;   // brightness, opacity, flowSpeed, unused
uniform vec3 Sun;
uniform vec3 Top;
uniform vec3 Hor;
uniform vec3 Bot;
uniform vec3 Dark;
uniform vec3 Light;
uniform vec4 Flash0;
uniform vec4 Flash1;
uniform vec4 Flash2;
uniform vec4 Flash3;
uniform vec3 FlashCol;

out vec4 fragColor;

const float TAU = 6.28318530718;

uint hash3(uvec3 v) {
    v = v * 1664525u + 1013904223u;
    v.x += v.y * v.z; v.y += v.z * v.x; v.z += v.x * v.y;
    v ^= v >> 16u;
    v.x += v.y * v.z; v.y += v.z * v.x; v.z += v.x * v.y;
    return v.x;
}

float hv(vec3 i) {
    return float(hash3(uvec3(ivec3(i))) & 0xFFFFu) * (1.0 / 65535.0);
}

float noise(vec3 p) {
    vec3 i = floor(p);
    vec3 f = p - i;
    vec3 u = f * f * (3.0 - 2.0 * f);
    float a = hv(i), b = hv(i + vec3(1, 0, 0)), c = hv(i + vec3(0, 1, 0)), d = hv(i + vec3(1, 1, 0));
    float e = hv(i + vec3(0, 0, 1)), g = hv(i + vec3(1, 0, 1)), h = hv(i + vec3(0, 1, 1)), k = hv(i + vec3(1, 1, 1));
    return mix(mix(mix(a, b, u.x), mix(c, d, u.x), u.y), mix(mix(e, g, u.x), mix(h, k, u.x), u.y), u.z);
}

float fbm(vec3 p, int oct) {
    float s = 0.0, a = 0.5, n = 0.0;
    for (int i = 0; i < oct; i++) {
        s += a * noise(p);
        n += a;
        p = p * 2.03 + vec3(17.3, 9.1, 31.7);
        a *= 0.5;
    }
    return s / n;
}

// Perspektifli bulut katmani (ufka dogru sikisan net bulutlar): Storm / Sunset / Gradient
void cloudN(vec3 d, float tt, float sc, float sw, out float n, out float nl) {
    float h = max(abs(d.y) + 0.16, 0.05);
    vec2 uv = d.xz / h * sc * 2.4;
    vec3 p = vec3(uv.x + tt * 0.07, tt * 0.03, uv.y - tt * 0.05);
    float wx = 0.0, wz = 0.0;
    if (sw > 0.001) {
        wx = (fbm(vec3(p.x * 0.45 + 3.1, p.y * 0.7 + tt * 0.02, p.z * 0.45), 2) - 0.5) * sw * 1.5;
        wz = (fbm(vec3(p.x * 0.45 + 7.7, p.y * 0.7 + 1.3, p.z * 0.45 + 5.5), 2) - 0.5) * sw * 1.5;
    }
    int oct = 3 + int(Quality + 0.5);
    vec3 q = vec3(p.x + wx, p.y, p.z + wz);
    n = fbm(q, oct);
    nl = fbm(q + vec3(0.10, 0.0, 0.16), oct - 1);
}

// Kuresel gurultu (nebula gazi)
void nebN(vec3 d, float tt, float sc, float sw, out float n) {
    vec3 p = d * sc * 3.4 + vec3(tt * 0.04, 0.0, -tt * 0.025);
    float wx = 0.0, wz = 0.0;
    if (sw > 0.001) {
        wx = (fbm(p * 0.5 + vec3(3.1, 0.0, 0.0), 2) - 0.5) * sw * 1.6;
        wz = (fbm(p * 0.5 + vec3(7.7, 1.3, 5.5), 2) - 0.5) * sw * 1.6;
    }
    n = fbm(vec3(p.x + wx, p.y, p.z + wz), 3 + int(Quality + 0.5));
}

vec3 hsv2rgb(vec3 c) {
    vec3 k = clamp(abs(mod(c.x * 6.0 + vec3(0.0, 4.0, 2.0), 6.0) - 3.0) - 1.0, 0.0, 1.0);
    return c.z * mix(vec3(1.0), k, c.y);
}

float flashAdd(vec3 d, vec4 f) {
    return f.w * exp((dot(d, f.xyz) - 1.0) * 2.2);
}

void main() {
    vec3 d = normalize(dir);
    float y = d.y;
    float az = atan(d.z, d.x);
    int st = int(Style + 0.5);
    float tt = Time * Cloud.x;
    float flow = Time * Bright.z;
    float sc = Cloud.y;
    float den = Cloud.z * (st == 4 ? 0.6 : 1.0);

    float yy = y;
    if (st == 4) yy += 0.06 * sin(az * 2.0 + flow * 0.5);
    vec3 col = yy >= 0.0 ? mix(Hor, Top, pow(min(1.0, yy), 0.55)) : mix(Hor, Bot, pow(min(1.0, -yy), 0.6));
    float cv = 0.3;

    if (st == 0 || st == 3 || st == 4) {
        cv = 0.0;
        if (den > 0.001) {
            float n, nl;
            cloudN(d, tt, sc, Cloud.w, n, nl);
            float c = smoothstep(1.0 - den - 0.07, 1.0 - den + 0.05, n);
            c *= 0.3 + 0.7 * smoothstep(-0.5, 0.05, y);
            c *= mix(0.12, 1.0, smoothstep(0.0, 0.32, abs(y)));
            if (st == 0) c *= 0.6 + 0.4 * smoothstep(0.0, 0.5, y);
            float shade = clamp(0.5 + (n - nl) * 6.0, 0.0, 1.0);
            float thick = smoothstep(0.55, 0.9, n);
            float lit = clamp(shade * 0.85 + 0.15 * (1.0 - thick), 0.0, 1.0);
            vec3 cc = mix(Dark, Light, lit);
            cc = mix(cc, Dark, thick * 0.45);
            col = mix(col, cc, c * 0.96);
            cv = c;
        }
        if (st == 3) {
            float dd = max(0.0, dot(d, Sun));
            float glow = pow(dd, 26.0) * 1.1 + pow(dd, 5.0) * 0.28;
            if (dd > 0.9986) glow += 2.0;
            col += vec3(1.0, 0.78, 0.46) * glow;
        }
    } else if (st == 2) {
        cv = 0.0;
        if (den > 0.001) {
            float n1, n2;
            nebN(d, tt, sc, Cloud.w, n1);
            nebN(d + vec3(0.7, -0.4, 0.2), tt + 40.0, sc * 1.2, Cloud.w, n2);
            float c1 = smoothstep(0.38, 0.85, n1) * den * 1.6;
            float c2 = smoothstep(0.42, 0.9, n2) * den * 1.4;
            col += Dark * c1 * 0.9 + Light * c2 * 0.75;
            vec3 bn = normalize(vec3(0.35, 0.80, 0.49));
            float bd = dot(d, bn);
            float band = exp(-bd * bd / 0.03) * (0.35 + 0.65 * n1) * 0.55;
            col += vec3(0.7, 0.75, 1.0) * band;
        }
    } else if (st == 1) {
        cv = 0.0;
        if (y > 0.0) {
            float el = asin(min(1.0, y));
            float fl = flow;
            float base = 0.42 + 0.13 * sin(az * 3.0 + fl * 0.25) + 0.08 * sin(az * 7.0 - fl * 0.4);
            float w = 0.24 + 0.06 * sin(az * 2.0 + fl * 0.2);
            float x = (el - base) / w;
            float inten = exp(-x * x);
            float nz = fbm(vec3(cos(az) * 2.0 + 5.0, el * 4.0 - fl * 0.1, sin(az) * 2.0 + fl * 0.15), 3);
            float ray = 0.55 + 0.45 * sin(az * 46.0 + nz * 9.0 + fl * 0.6);
            inten *= (0.35 + 0.9 * nz) * (0.5 + 0.5 * ray) * smoothstep(0.0, 0.08, y);
            float m = smoothstep(base - 0.12, base + 0.45, el);
            vec3 ac = mix(vec3(0.15, 1.0, 0.50), vec3(0.65, 0.28, 1.0), m);
            col += ac * inten * 1.5;
        }
    } else {
        cv = 0.0;
        float hue = fract(az / TAU + y * 0.7 + flow * 0.03);
        col = hsv2rgb(vec3(hue, 0.62, 0.62 + 0.38 * max(0.0, y)));
    }

    col *= Bright.x;

    float add = flashAdd(d, Flash0) + flashAdd(d, Flash1) + flashAdd(d, Flash2) + flashAdd(d, Flash3);
    col += FlashCol * add * 0.75 * (0.3 + 0.7 * cv);

    float dither = (float(hash3(uvec3(uvec2(gl_FragCoord.xy), 7u)) & 0xFFu) / 255.0 - 0.5) / 255.0;
    fragColor = vec4(clamp(col + dither, 0.0, 1.0), Bright.y);
}
