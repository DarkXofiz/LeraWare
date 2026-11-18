package com.ezzenix.chatanimation.util;

import net.minecraft.class_303.class_7590;

public interface TimestampedMessageLine {
   long chatAnimation$getAddedTime();

   void chatAnimation$setAddedTime(long var1);

   static TimestampedMessageLine of(class_7590 line) {
      return (TimestampedMessageLine)line;
   }

   static float age(class_7590 line) {
      return (float)(System.currentTimeMillis() - of(line).chatAnimation$getAddedTime());
   }
}
