// package ca.queensu.astro.cforce;

// import ca.queensu.astro.cforce.*;
import java.awt.*;
import java.lang.*;
import java.applet.*;
import java.io.*;
import java.util.*;

/*
 * OrbitApplet classes developed by Peter Musgrave (musgrave@astro.queensu.ca)
 * See also http://astro.queensu.ca/~musgrave/cforce (this web site includes a
 * Booch-like diagram of the OrbitApplet class structure)
 *
 * This code has been entered in the First Java Cup International and
 * consequently I have waived all intellectual, property, copyright and
 * moral (??) rights to this code (assuming Canadian law permits this...)
 *
 */

//////////////////////////////////////////////////
// OrbitForce
//
// Details about the possible forces which can be
// used in the OrbitApplet are kept here.
//
// This is a static mix-in class. It is never instantiated.
//
//////////////////////////////////////////////////


public class OrbitForce extends java.lang.Object {


   public static final int MAX = 5; // max number of forces
   public static final int DEFAULT = 0; // default force is gravity
   public static final int SPEED_LIMIT = 20; // max dr allowed. (Need to impose this
                                             // to prevent tunneling through 1/R^4
                                             // potential)
   // constants to identify the forces
   public static final int GRAVITY = 0;
   public static final int YUKAWA = 1;
   public static final int SPRING = 2;
   public static final int R4 = 3;
   public static final int BLACKHOLE = 4;

   private static int force = DEFAULT;   // current force
   

   // names of the forces
   private static String name[] = {"Gravity", "Yukawa", "Spring", "1/R^4", "Black Hole"};

   //
   // force constants are chosen so an object with L=1000 has 
   // an effective potential with a minimum at r=100
   //

   private static double k[] = { 10000.0, 11000.0, 1.0, 33333333.3, 3.0};

   // potential energy scales for forces
   private static double minPE[] = {-70.0, -35.0, 0, 0.0, 0.9  };
   private static double maxPE[] = { 0.0, 0.0, 10000.0, 20.0, 1.0 };

   // scale dr and dtheta by this factor
   private static double scale[] = { 0.02, 0.02, 0.004, 0.01, 0.7}; 

   private static double thrustScale[] = { 0.0002, 0.0002, 0.002, 0.0002, 0.00002 }; 
                                   // scale applied to thrust vector

   //-------------------------
   // methods
   // - collection of obvious one lines to get/set
   //   force info
   //-------------------------

   public static String getName(int f) { // name of the force with index f

      return name[f];
   }

   public static int getForce() { // get the current force index

      return force;
   }

   public static double getScale(int f) { // get the current force index

      return scale[f];
   }

   public static double getThrustScale() { // get the current force index

      return thrustScale[force];
   }

   public static double minPE() { // return min PE for current force

      return minPE[force];
   }

   public static double maxPE() { // ditto for the max PE

      return maxPE[force];
   }

   public static double minPE(int f) { // return min PE for specified force

      return minPE[f];
   }

   public static double maxPE(int f) { // ditto for the max PE

      return maxPE[f];
   }

   public static void setForce(int newforce) {

      // ignore bad input for now
      if ( newforce > -1 && newforce <= MAX)
            force = newforce;
   }

   public static double getBlackHoleMass() {

      return k[BLACKHOLE];
   }

   // V()
   // - given a (radius, angmomtm, force) return the potential energy

   public static double V(double r, double l, int f) { // return value of the potential
                                      // for a given r

     double v = 0.0;

     switch(f) {
          case 0: // gravity
                  v = - k[GRAVITY]/r;
                  break;
          case 1: // Yukawa
                  v = - k[YUKAWA] * Math.exp(-r/200.0)/r;
                  break;
          case 2: // Spring
                  v = 0.5 * k[SPRING] * r *r ;
                  break;
          case 3: // 1/R^4 Force
                  v = - k[R4]/(r*r*r);
                  break;
          case 4: // Black Hole Effective Potential (mass = k[4])
                  v = 1 - 2*k[BLACKHOLE]/r - k[4]*l*l/(r*r*r);
                  break;
      }

      return v;
   }


                       

}
