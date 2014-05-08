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

///////////////////////////////////////////
// OrbitingBody
//
// Add Potential energy plots to OrbitingBodyLite
// 
///////////////////////////////////////////

public class OrbitingBody extends OrbitingBodyLite {


    int pex = 0; // grid X position (potential energy)
    int pey = 0; // grid y position
    int lastpex = 0; // previously displayed x position
    int lastpey = 0; // previously displayed y position

    // constants
    private static final int MAX_PE_POINTS = 300;

   
  //////////////////////////
  // Class Methods
  //////////////////////////

   static int max_pex = 100;
   static int max_pey = 100;
   static int origin_pey = 0;
   static int origin_pex = 0;

   // setPEparms
   // - receive info on where the (0,0) point for the PE graph is and
   //   limits on how big it is 
   public static void setPEparms(int maxx, int maxy, int originx, int originy) {

      max_pey = maxy;
      max_pex = maxx;
      origin_pey = originy;     
      origin_pex = originx;     
   }

   // scalePE()
   // - use knowledge of min and max PE
   //   to scale the PE value into a pixel value for the orbit canvas
   public static int scalePE(double V) { 

      // know that Origin_y is a value maxPE so take (maxPE - V) and scale it

      double scale = max_pey/(OrbitForce.maxPE() - OrbitForce.minPE() ); 
      return  origin_pey + (int)( (OrbitForce.maxPE() - V)*scale); 

  }

  //////////////////////////
  // Instance Methods
  //////////////////////////

    // constructor

    public OrbitingBody (int x, int y, int newtype, Color c, int force) {

       super( x, y, newtype, c, force);
       
       
    }

    // reset()
    // - reset to starting position

    public void reset() {

       // "Lite" orbiting bodies cannot change their force during execution
       // but orbiting bodies can. Update the force before calling super.reset()
       force = OrbitForce.getForce();
       super.reset();
       
    }

    // paint()
    // - update objects position on the PE graph
    // - update the curve on the PE graph (since objects move over it)

    public void paint( Graphics g) { // want a fancy image passed in to constructor later

      super.paint(g);

    }

    
    // move()
    // - update the position of the objects on the PE graph

    public void move() {

         super.move(); // this may throw a motion exception
         pey = (int) scalePE( energy);
         pex = (int) radius;
    }



 }            

