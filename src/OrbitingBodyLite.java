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
// OrbitingBodyLite (OBL)
//
// A simple Orbiting Body used by both OrbitApplet and
// OrbitDemo applets. 
//
// OBL handles the motion of the body (with mass=1 for simplicity)
// the physical space. Motion is based on conservation 
// of energy and angular momentum in conjunction with the
// effective potential energy.
//
// OBL is extended by OrbitingBody which handles the 
// motion of the body on the potential energy graph and
// the display of these graphs for each body.
///////////////////////////////////////////

public class OrbitingBodyLite extends java.lang.Object {

    String status="Escribe la energía con la que enviarás en cohete y presiona 'Lanzar'";
	int  x = 0; // grid X position (physical picture)
    int  y = 0; // grid y position
    int lastx = 0; // previously displayed x position
    int lasty = 0; // previously displayed y position

    int centerx; // x coordinate of central force
    int centery; // y coord

    int type = OrbitScenarios.TARGET; // either SPACESHIP or TARGET
                                      // needed for OrbitScenario calls in constructor

    int force = OrbitForce.DEFAULT; // allow different forces for different
                                    // objects (OrbitDemo applet uses this)

    Color color; // the color of the body

    double radius = 100.0; // grid radius position
    double theta = 0.0; // angular position (radians)
    double energy = 0; // energy of the body
    double angmomtm = 0; // angular momentum of the body

    // flag to leave trailing dots.
    // - set by drawDots()
    // - tested in paint()
    private boolean drawDots = true; 

    // paintReset flag
    // - set by reset() to notify paint that the old position should not
    //   be redrawn to create trailing dots
    // - cleared by paint()
    private boolean paintReset = false; 
    
    double  drsign = 1.0; // +/- 1   // direction of radial evolution

    private static int BOX_SIZE = 6; // size of the box drawn for an object

  /////////////////////////////
  // class methods
  /////////////////////////////

   private static int maxr = 200;

   // set the maximum radius (which when exceeded results in a MotionException)
   // This must apply to all orbiting bodies in an Applet and hence it's a
   // static method.

   public static void setmaxradius(int max) {

      maxr = max;
   }


   ////////////////////////////
   // instance methods
   ////////////////////////////

   // constructor

    public OrbitingBodyLite (int x, int y, int newtype, Color c, int f) {

       centerx = x;
       centery = y;
       force = f;
       type = newtype;
       color = c;
       // get inital parameters from OrbitScenario
       energy = OrbitScenarios.getValue( newtype, f, OrbitScenarios.E);
       angmomtm = OrbitScenarios.getValue( newtype, f, OrbitScenarios.L);
       radius = OrbitScenarios.getValue( newtype, f, OrbitScenarios.R);
       theta = OrbitScenarios.getValue( newtype, f, OrbitScenarios.THETA);
       drsign = OrbitScenarios.getValue( newtype, f, OrbitScenarios.DRSIGN);   
    }

    // reset()
    // - reset the orbit to the starting point

    public void reset() {

       energy = OrbitScenarios.getValue( type, force, OrbitScenarios.E);
       angmomtm = OrbitScenarios.getValue( type, force, OrbitScenarios.L);
       radius = OrbitScenarios.getValue( type, force, OrbitScenarios.R);
       theta = OrbitScenarios.getValue( type, force, OrbitScenarios.THETA);
       drsign = OrbitScenarios.getValue( type, force, OrbitScenarios.DRSIGN);   
       this.setPosition( radius, theta);
       paintReset = true; // tell paint we've reset
    }
   
   // position update and retrieval methods
   // x and y must be updated atomically so paint doesn't get a new x and old y (say)

   private synchronized void setPosition( double r, double th) {

        x = (int) (centerx + r * Math.cos(th) );
        y = (int) (centery + r * Math.sin(th) );
   }

   public synchronized Point getPosition() {
      
        return new Point( x, y);
   } 
   
   public synchronized String getStatus() {
	      
       return status;
  } 

    // paint()
    //
    // - draw the body's position in the orbit plot
    //   (position on PE plot is done by OrbitingBody.paint() )
    // - either erase old position or over-draw it to get the
    //   trailing dots effect

    public void paint( Graphics g) { // want a fancy image passed in to constructor later
        // 
        // image on the physical space
        //
        if (!drawDots || paintReset) {
           g.setColor( Color.black ); // first erase last image by drawing a too large black box
           g.fillRect(lastx-(BOX_SIZE+4)/2, lasty-(BOX_SIZE+4)/2, BOX_SIZE+4, BOX_SIZE+4);       
        } else {
           // redraw old box to erase the black dot in the middle 
           g.setColor( color); 
           g.fillRect(lastx-BOX_SIZE/2, lasty-BOX_SIZE/2, BOX_SIZE, BOX_SIZE);
        }

        // get x and y
        Point here = this.getPosition();

        if (!paintReset) {
        // draw new position
        g.setColor( color); 
        g.fillRect(here.x-BOX_SIZE/2, here.y-BOX_SIZE/2, BOX_SIZE, BOX_SIZE);

        // empty out the box so when dots are trailing the object can be seen
        g.setColor( Color.black);
        g.fillRect(here.x-1, here.y-1, 2, 2); 
        }
        lastx = here.x; // record position so we can erase/re-draw on next paint()
        lasty = here.y; 

        paintReset = false;
    }

    // Veff
    // - given a position, angular momtm and force return the value
    //   of the effective potential energy

    public double Veff( double r, double l, int f) { // use given angular momtm

       return 0.5*l*l/(r*r) + OrbitForce.V(r, l, f);
    }

    // move()
    // - calculate the new position of body assuming an arbitrary small
    //   step in radius and angle
    //  - if motion results in an illegal position or energy < Veff throw
    //    a motion exception

    public void move()  {

         double dr;       // incremental change in radius
         double newr;     // test value for radius
         double fraction; // fraction of dr used to reach turning point

         double scale = OrbitForce.getScale( force);

         //
         // dr comes from effective potential BUT
         // there's a catch   dr = +/- sqrt( 2(E - Veff(r) ) ) and we
         // need to be smart about the +/- 
         //
         // Code below ensures we don't get sqrt exceptions here
         // (save for bogus initial conditions) but throw them anyway
         // (This will result in a warning dialog + reset)
         //
         if ( energy < this.Veff(radius, angmomtm, force) ) {
            //System.out.println("Energy = " + energy + " Veff = " + 
			//this.Veff(radius, angmomtm, force) );
            //status="Regreso al planeta y chocó";
            System.out.println("Regreso al planeta y chocó");
            
            //throw new MotionException();
         }
         dr = Math.sqrt( 2.0*( energy - this.Veff(radius, angmomtm, force)) );
         //System.out.println("dr: "+dr);
         //
         // tres cheezy but if dr is too big we can "numerically tunnel"
         // through a forbidden region of the 1/R^4 potential - so to
         // avoid this resort to...
         //
         if ( dr > OrbitForce.SPEED_LIMIT) 
             dr = OrbitForce.SPEED_LIMIT;

         // test for turning point, if we've hit one then use up
         // that portion of dr which bring us to the turning
         // point and then change drsign and continue with
         // whatever portion of dr is left
         //
         //
         newr = radius + dr * drsign * scale;  
         fraction = 0.0;
         if ( this.Veff(newr, angmomtm, force) > energy) {
           // turning point
           while ( this.Veff(newr, angmomtm, force) > energy && fraction < 1.0) {
               fraction += 0.02;
               newr = radius + dr * drsign * scale * fraction;             
           }
           drsign = -1.0 * drsign;
           radius = newr + dr * drsign * scale * ( 1.0 - fraction) ;
           theta = theta + angmomtm/(radius*radius) * scale * fraction
                         + angmomtm/(newr*newr) * scale * ( 1.0 - fraction);
         } else {
           // no turning point
           radius = radius + dr * drsign * scale;
           theta = theta + angmomtm/(radius*radius) * scale;
         }

         // if we've passed through r=0 flip drsign and r
         if ( radius < 0 ) {
             drsign = 1.0;
             radius = -1.0 * radius;
             theta = theta - 3.1415;
         } else if ((radius > maxr) ||
                    (OrbitForce.getForce() == OrbitForce.BLACKHOLE &&
                     radius < 2*OrbitForce.getBlackHoleMass() )) {
             // if radius is too large or inside r=2*m for a black hole
             // throw a motion exception
             //System.out.println("Out of bounds r=" + radius);
             //status="Se fue al infinito";
             System.out.println("Se fue al infinito");
             //throw new MotionException();
             
         }
         // set position (atomically)
         this.setPosition( radius, theta );

    }

   public void drawDots( boolean dotChoice) {

       drawDots = dotChoice;

   }



 }            

