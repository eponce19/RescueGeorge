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
// OrbitScenarios
//
// Initial conditions for each scenario for each force.
// (sort of a brute force feel to this one...)
//
// This is a static mix-in class. It is never instantiated.
//
//
//////////////////////////////////////////////////


public class OrbitScenarios extends java.lang.Object {

   public static final int DEMO = 0;  // default scenario for OrbitDemo Applet
   public static final int APPLET_DEFAULT = 1; // default scenario for OrbitApplet
   public static final int SPACESHIP = 0;
   public static final int TARGET = 1;

   // Fields in the record for each object
   public static final int E = 0;
   public static final int L = 1;
   public static final int R = 2;
   public static final int THETA = 3;
   public static final int DRSIGN = 4;

   public static final int DEFAULT = 1;

   private static final int NUM_FIELDS = 5;
   private static final int MAX_SCENARIOS = 5;


   private static String sname[] = new String[MAX_SCENARIOS];
   private static double s[][][][] = 
           new double[2][MAX_SCENARIOS][OrbitForce.MAX][NUM_FIELDS];
   private static boolean initialized = false;
   private static int numScenario = -1;
   private static int currents = 0; // the current scenario

   // addName()
   // - signals the start of a new scenario

   private static void addName( String name) {

        numScenario++;
        sname[numScenario] = name;
   }

   // add()
   // - add initial conditions for a force & object type to the scenario database
   //   for the current scenario number
   // - scenario counter controlled by addName()

   private static void 
       add(int force, int type, double e, double l, double r, double th, double drsign) {

        s[type][numScenario][force][E] = e;
        s[type][numScenario][force][L] = l;
        s[type][numScenario][force][R] = r;
        s[type][numScenario][force][THETA] = th;
        s[type][numScenario][force][DRSIGN] = drsign;
          
   }

   // init()
   // -  intialize the scenario database

   private static void init() {
       // Scenario 0 is reserved for the Demo Applet. Orbit Applet 
       // scenarios start at 1
	   
       addName("Demo");
       add(0, TARGET, -54.7, 900.0, 81.0, 0, 1.0 ); // Gravity
       add(1, TARGET, -24.815, 900.0, 78.1, 1.26, 1.0 ); // Yukawa
       add(2, TARGET, 3500, 2500, 50.0, 2.5, 1.0 ); // Spring
       add(3, TARGET, 14.0, 980.0, 50.0, 3.8, -1.0 );
       add(4, TARGET, 0.955, 20, 55.94, 5, 1.0 ); // BlackHole
	
	   
       // Scenario 1  (DEFAULT for OrbitApplet)
       addName("Circular1");
       // the initial data for spaceship and target are here
       //   F    object    E      L      R    Th  drsign
       add(0, SPACESHIP, -41.50, 900.0, 81.0, 0, 1.0 ); // Gravityss
       add(0, TARGET   , -41.32, 1100.0, 121.0, 0, 1.0 );
       add(1, SPACESHIP, -28.815, 900.0, 78.1, 0, 1.0 ); // Yukawa
       add(1, TARGET   , -8.1898, 1100.0, 126.6, 0, 1.0 );
       add(2, SPACESHIP, 2500, 2500, 50.0, 0, 1.0 ); // Spring
       add(2, TARGET   , 5001, 5000, 70.71, 0, 1.0 );
       add(3, SPACESHIP, 12.0, 1000.0, 50.0, 0, 1.0 ); // 1/R^4
       add(3, TARGET   , 14.0, 980.0, 50.0, 0, 1.0 );
       add(4, SPACESHIP, 0.9498, 20, 55.94, 0, 1.0 ); // BlackHole
       add(4, TARGET   , 0.95944, 22, 70.346, 0, 1.0 );
       //
       // Scenario 2
       //  
       addName("Circular2");
       add(0, TARGET, -61.7, 900.0, 81.0, 0, 1.0 ); // Gravity
       add(0, SPACESHIP   , -41.32, 1100.0, 121.0, 0, 1.0 );
       add(1, TARGET, -28.815, 900.0, 78.1, 0, 1.0 ); // Yukawa
       add(1, SPACESHIP   , -8.1898, 1100.0, 126.6, 0, 1.0 );
       add(2, TARGET, 2500, 2500, 50.0, 0, 1.0 ); // Spring
       add(2, SPACESHIP, 5001, 5000, 70.71, 0, 1.0 );
       add(3, TARGET, 12.0, 1000.0, 50.0, 0, 1.0 ); // 1/R^4
       add(3, SPACESHIP, 14.0, 980.0, 50.0, 0, 1.0 );
       add(4, TARGET, 0.9498, 20, 55.94, 0, 1.0 ); // BlackHole
       add(4, SPACESHIP   , 0.95944, 22, 70.346, 0, 1.0 );


       //
       // Scenario 3
       //
       addName("Ellipses1");
       add(0, SPACESHIP, -48.7, 900.0, 81.0, 0, 1.0 ); // Gravity
       add(0, TARGET   , -40.32, 1100.0, 121.0, 0, 1.0 );
       add(1, SPACESHIP, -18.815, 900.0, 78.1, 0, 1.0 ); // Yukawa
       add(1, TARGET   , -7.1898, 1100.0, 126.6, 0, 1.0 );
       add(2, SPACESHIP, 3500, 2500, 50.0, 0, 1.0 ); // Spring
       add(2, TARGET   , 6001, 5000, 70.71, 0, 1.0 );
       add(3, SPACESHIP, 12.0, 1000.0, 50.0, 0, 1.0 ); // 1/R^4
       add(3, TARGET   , 14.0, 980.0, 50.0, 0, 1.0 );
       add(4, SPACESHIP, 0.955, 20, 55.94, 0, 1.0 ); // BlackHole
       add(4, TARGET   , 0.965, 22, 70.346, 0, 1.0 );
       //
       // Scenario 4
       //

       addName("Ellipses2");
       add(0, TARGET, -51.7, 900.0, 81.0, 0, 1.0 ); // Gravity
       add(0, SPACESHIP   , -40.32, 1100.0, 121.0, 0, 1.0 );
       add(1, TARGET, -18.815, 900.0, 78.1, 0, 1.0 ); // Yukawa
       add(1, SPACESHIP   , -7.1898, 1100.0, 126.6, 0, 1.0 );
       add(2, TARGET, 3500, 2500, 50.0, 0, 1.0 ); // Spring
       add(2, SPACESHIP, 6001, 5000, 70.71, 0, 1.0 );
       add(3, TARGET, 12.0, 1000.0, 50.0, 0, 1.0 ); // 1/R^4
       add(3, SPACESHIP, 14.0, 980.0, 50.0, 0, 1.0 );
       add(4, TARGET, 0.955, 20, 55.94, 0, 1.0 ); // BlackHole
       add(4, SPACESHIP, 0.965, 22, 70.346, 0, 1.0 );
       initialized = true;



   }

   //--------------------------------------
   // interface methods
   //--------------------------------------
   
   public static void setScenario( int s) {
     
     if (!initialized) init();
     currents = s;
     if ( s < 0 || s > max()) currents = DEFAULT;
   }


   public static int max() {
 
      if (!initialized) init();
      return numScenario;
   }

   public static String getName(int i) {
 
      if (!initialized) init();
      if ( i > numScenario || i < 0) i = 0; // out of bounds action
      return sname[i];
   }

   public static double getValue(int type, int f, int field) {
 
      if (!initialized) init();
      // check type & field later
      return s[type][currents][f][field];
   }

}

