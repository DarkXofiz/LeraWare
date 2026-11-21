#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec4 hoColor;
uniform vec4 hoColor2;
uniform float hoThickness;
uniform float hoBright;
uniform float hoOpacity;

out vec4 fragColor;

const int DIRS = 16;

bool hit(vec2 px) {
    return texture(DiffuseSampler, texCoord + px * oneTexel).a > 0.01;
}

void main() {
    // el / item'in kendi pikselleri: dokunma (oyunun cizimi kalir)
    if (texture(DiffuseSampler, texCoord).a > 0.01) {
        fragColor = vec4(0.0);
        return;
    }

    float halo = 2.0 + 10.0 * hoBright;
    float R = hoThickness + halo;

    // hizli on eleme: yakinda el var mi?
    bool near = false;
    for (int i = -3; i <= 3 && !near; i++) {
        for (int j = -3; j <= 3; j++) {
            vec2 o = vec2(float(i), float(j)) * (R / 3.0);
            if (dot(o, o) > R * R * 1.3) continue;
            if (hit(o)) { near = true; break; }
        }
    }
    if (!near) {
        fragColor = vec4(0.0);
        return;
    }

    // en yakin el pikseline mesafe (halka halka arama)
    float best = 1e9;
    int steps = int(ceil(R));
    for (int s = 1; s <= steps; s++) {
        float r = float(s);
        for (int d = 0; d < DIRS; d++) {
            float a = 6.2831853 * (float(d) + 0.5 * float(s & 1)) / float(DIRS);
            if (hit(vec2(cos(a), sin(a)) * r)) { best = r; break; }
        }
        if (best < 1e8) break;
    }
    if (best > 1e8) {
        fragColor = vec4(0.0);
        return;
    }

    float core = 1.0 - smoothstep(hoThickness - 0.5, hoThickness + 0.75, best);
    float glow = hoBright * exp(-max(best - hoThickness, 0.0) / (halo * 0.45)) * (1.0 - core);
    float a = clamp(core + glow * 0.9, 0.0, 1.0);

    vec3 base = mix(hoColor.rgb, hoColor2.rgb, clamp(texCoord.x * 0.6 + texCoord.y * 0.4, 0.0, 1.0));
    vec3 rgb = base * (0.65 + 0.85 * hoBright);
    rgb = mix(rgb, vec3(1.0), core * hoBright * 0.25);
    fragColor = vec4(min(rgb, vec3(1.0)), a * hoOpacity);
}
