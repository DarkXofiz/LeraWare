package ru.donyka.chunkanimator.config;

public enum EasingFunction {
    LINEAR {
        @Override
        protected float apply(float t, float b, float c, float d) {
            return c * t / d + b;
        }
    },
    QUAD {
        @Override
        protected float apply(float t, float b, float c, float d) {
            t /= d;
            return -c * t * (t - 2.0F) + b;
        }
    },
    CUBIC {
        @Override
        protected float apply(float t, float b, float c, float d) {
            t = t / d - 1.0F;
            return c * (t * t * t + 1.0F) + b;
        }
    },
    QUART {
        @Override
        protected float apply(float t, float b, float c, float d) {
            t = t / d - 1.0F;
            return -c * (t * t * t * t - 1.0F) + b;
        }
    },
    QUINT {
        @Override
        protected float apply(float t, float b, float c, float d) {
            t = t / d - 1.0F;
            return c * (t * t * t * t * t + 1.0F) + b;
        }
    },
    EXPO {
        @Override
        protected float apply(float t, float b, float c, float d) {
            return t == d ? b + c : (float)(c * (-Math.pow(2.0, -10.0F * t / d) + 1.0) + b);
        }
    },
    SINE {
        @Override
        protected float apply(float t, float b, float c, float d) {
            return (float)(c * Math.sin(t / d * (Math.PI / 2)) + b);
        }
    },
    CIRC {
        @Override
        protected float apply(float t, float b, float c, float d) {
            t = t / d - 1.0F;
            return (float)(c * Math.sqrt(1.0F - t * t) + b);
        }
    },
    BACK {
        @Override
        protected float apply(float t, float b, float c, float d) {
            float s = 1.70158F;
            t = t / d - 1.0F;
            return c * (t * t * ((s + 1.0F) * t + s) + 1.0F) + b;
        }
    },
    BOUNCE {
        @Override
        protected float apply(float t, float b, float c, float d) {
            t /= d;
            if (t < 0.36363637F) {
                return c * (7.5625F * t * t) + b;
            } else if (t < 0.72727275F) {
                t -= 0.54545456F;
                return c * (7.5625F * t * t + 0.75F) + b;
            } else if (t < 0.90909094F) {
                t -= 0.8181818F;
                return c * (7.5625F * t * t + 0.9375F) + b;
            } else {
                t -= 0.95454544F;
                return c * (7.5625F * t * t + 0.984375F) + b;
            }
        }
    },
    ELASTIC {
        @Override
        protected float apply(float t, float b, float c, float d) {
            if (t == 0.0F) {
                return b;
            } else {
                t /= d;
                if (t == 1.0F) {
                    return b + c;
                } else {
                    float p = d * 0.3F;
                    float s = p / 4.0F;
                    return (float)(c * Math.pow(2.0, -10.0F * t) * Math.sin((t * d - s) * (Math.PI * 2) / p) + c + b);
                }
            }
        }
    };

    public float easeOut(float t, float b, float c, float d) {
        return !(d <= 0.0F) && !(t >= d) ? this.apply(Math.max(0.0F, t), b, c, d) : b + c;
    }

    protected abstract float apply(float var1, float var2, float var3, float var4);
}
