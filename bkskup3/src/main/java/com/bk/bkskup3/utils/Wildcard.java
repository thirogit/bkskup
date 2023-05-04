package com.bk.bkskup3.utils;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 1/20/12
 * Time: 11:50 AM
 */
public class Wildcard
{
   static boolean matches(String pattern, String text) {
      // add sentinel so don't need to worry about *'s at end of pattern
      text    += '\0';
      pattern += '\0';

      int N = pattern.length();

      boolean[] states = new boolean[N+1];
      boolean[] old = new boolean[N+1];
      old[0] = true;

      for (int i = 0; i < text.length(); i++) {
         char c = text.charAt(i);
         states = new boolean[N+1];       // initialized to false
         for (int j = 0; j < N; j++) {
            char p = pattern.charAt(j);

            // hack to handle *'s that match 0 characters
            if (old[j] && (p == '*')) old[j+1] = true;

            if (old[j] && (p ==  c )) states[j+1] = true;
            if (old[j] && (p == '.')) states[j+1] = true;
            if (old[j] && (p == '*')) states[j]   = true;
            if (old[j] && (p == '*')) states[j+1] = true;
         }
         old = states;
      }
      return states[N];
   }

}
