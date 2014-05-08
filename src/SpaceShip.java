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
// SpaceShip
//
// Extend an OrbitingBody by adding a thrust facility.
// Mouse clicks specify the thrust vector which is then
// resolved to determine the change in energy in angmomtm.
//
// Thrust is treated as an impulse = discontinuous change in momentum
// and we use M=1 throughout to simplify calculations.
// 
///////////////////////////////////////////

public class SpaceShip extends OrbitingBody {

    int thrustx = 0; // thrustV grid X position
    int thrusty = 0; // thrustV grid y position
    int last_thrustx = 0; // old thrustV grid X position
    int last_thrusty = 0; // old thrustV grid y position

    int displayx = 0; // (x,y) of location of thrust text information
    int displayy = 0; 

    private double new_energy = 0; // energy resulting from thrust
    private double new_angmomtm = 0; // angmomtm resulting from thrust

    // step used to find dtheta vector for resolving thrust
    // (arbitrary as long as it's reasonably small)
    private static final double DTHETA = 0.01; 

    // flag to paint from calcthrust 
    private boolean newPEcurve = false; 

    //---------------------------------------------
    // methods
    //---------------------------------------------
 
    // constructor

    public SpaceShip (int x, int y, Color c, int f) {
       // nothing unique to spaceship
       super( x, y, OrbitScenarios.SPACESHIP, c, f); 
    }



    // setDisplayBox
    // - somebody has to tell us where to display thrust messages

    public void setDisplayBox( int boxx, int boxy) {

      displayx = boxx;
      displayy = boxy;
    }

   // thrustOn()
   // - used to signal when evolution stops and "thrust mode" has
   //   started

   public void thruston() {
        
      // set the default thrust vector (towards the center, 20 units long)
      // (this is just an arbitrary vector so users see it)
      int fakex = (int) (super.centerx + 
                         (super.radius - 20.0) * Math.cos(super.theta) );
      int fakey = (int) (super.centery + 
                         (super.radius - 20.0) * Math.sin(super.theta) );
      this.calcthrust( fakex, fakey); // may get a MotionException
   }

   // calcthrust()
   // - resolve the thrust vector and determine how it changes
   //   the energy and angular momentum.
   // 
   // - these are stored in new_energy, new_angmomtm so the user can see
   //   the effect of the thrust on PE before commiting to this thrust
   //   vector (by pressing GO)
   //

   public void calcthrust( int mousex, int mousey) { // set new x,y based on mouse values

     last_thrustx = thrustx; // take a snapshot of the old position
     last_thrusty = thrusty; // of the thrust vector
     thrustx = mousex;
     thrusty = mousey;
        
     if ( !(thrustx == 0 && thrusty == 0) ) {
        // first resolve the thrust vector into radial
        // and angular components
        double rawx = (double) thrustx - super.x;
        double rawy = (double) thrusty - super.y;

        // find a normalized radial vector (from spaceship to center) 
        double nrx = (double) super.centerx - super.x;
        double nry = (double) super.y - super.centery;
     
        double nr_norm = Math.sqrt( nrx*nrx + nry*nry );
        nrx = nrx/nr_norm;
        nry = nry/nr_norm;

        // get a normed dtheta vector
        // - DTHETA since y +ve down
        double dthetax = super.radius * (Math.cos(super.theta - DTHETA) 
                       - Math.cos(theta));
        double dthetay = super.radius * (Math.sin(super.theta - DTHETA)
                       - Math.sin(theta));
        double dtheta_norm = Math.sqrt( dthetax*dthetax + dthetay*dthetay);
        dthetax = dthetax/dtheta_norm;
        dthetay = dthetay/dtheta_norm;

        // project existing thrust vector onto these unit vectors
        double tscale = OrbitForce.getThrustScale();
        double thrust_r = (rawx * nrx + rawy*nry)* tscale;
        double thrust_theta = (rawx * dthetax + rawy * dthetay)* tscale; 

        //
        // find the dr and dtheta. The thrust vector represents an impule
        // of momentum and since m=1 this translates into an impuse change
        // in dr and dtheta
        //
        double dr = drsign * Math.sqrt( 2.0*( super.energy - 
                             super.Veff(super.radius, super.angmomtm, super.force)) );
        double dth = super.angmomtm/(super.radius* super.radius);

        new_energy = 0.5*((dr + thrust_r)*(dr + thrust_r) + 
                 super.radius* super.radius* (dth + thrust_theta)*(dth + thrust_theta)) +
                 OrbitForce.V( super.radius, super.angmomtm, OrbitForce.getForce() );

        new_angmomtm = super.radius* super.radius*(dth + thrust_theta);
        //
        // for the black hole case we can end up with an energy which is smaller
        // (by approx. 0.00001) than the Veff at this point due to accumulated numerical
        // inaccuracy. Since this is physically impossible (and causes a motion
        // exception) test for this case and change the new energy if necessary
        //
        if ( new_energy < this.Veff(super.radius, new_angmomtm, force) ) 
            new_energy = this.Veff(super.radius, new_angmomtm, force) + 0.00001;

       
     }
   }

   // dothrust()
   // - use the present thrust to change the constants of the motion

   public void dothrust() { // fire engines

        super.energy = new_energy;  // cheesy - should method this in OBL
        super.angmomtm = new_angmomtm;
        thrustx = 0;   // hide the thrust vector
        thrusty = 0;        
   }

   // paint()
   // - draw the thrust vector and the modified PE curve

   public void paint(Graphics g) {

       super.paint(g);
       if ( !(thrustx == 0 && thrusty == 0) ) {
          g.setColor( Color.black ); // erase old vector
          g.drawLine( super.x, super.y, last_thrustx, last_thrusty);
          g.setColor( color ); // draw new vector
          g.drawLine( super.x, super.y, thrustx, thrusty);     
          
    
       }

   }

 
}
