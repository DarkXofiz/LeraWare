package com.ezzenix.chatanimation.config;

import java.util.function.Function;

public enum EasingStyle {
   FLAT(x -> x),
   SINE(x -> 0.5 - Math.cos(x * Math.PI) / 2.0),
   QUAD(x -> x * x),
   CUBIC(x -> x * x * x),
   QUART(x -> x * x * x * x),
   QUINT(x -> x * x * x * x * x),
   EXPO(x -> x == 0.0 ? 0.0 : Math.pow(2.0, 10.0 * x - 10.0)),
   CIRC(x -> 1.0 - Math.sqrt(1.0 - Math.pow(x, 2.0))),
   BACK(x -> 2.70158 * x * x * x - 1.70158 * x * x),
   ELASTIC(x -> x == 0.0 ? 0.0 : (x == 1.0 ? 1.0 : -Math.pow(2.0, 10.0 * x - 10.0) * Math.sin((x * 10.0 - 10.75) * (Math.PI * 2.0 / 3.0))));

   final Function<Double, Number> function;

   private EasingStyle(Function<Double, Number> function) {
      this.function = function;
   }

   public Double apply(Double x) {
      return this.function.apply(x).doubleValue();
   }
}
