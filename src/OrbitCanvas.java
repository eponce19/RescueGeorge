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

///////////////////////////////////
// OrbitCanvas
// 
// This canvas creates two OrbitingBody instances
// (one a spaceship and one a target) and evolves these
// bodies in two display areas. 
//
// The top half of the canvas displays the center of the force
// and shows the physical orbits of the two bodies. The lower half
// displays a graph of Effective Pottential Energy vs radius, shows
// the Veff curves for each body and plots the radius of each object
// as they evolve.
///////////////////////////////////

public class OrbitCanvas extends Canvas {

    boolean       evolving = true; // flag to indicate when to evolve orbits
    boolean       initialized = false; 
    SpaceShip     spaceship;   //the user's spaceship
    OrbitingBody  target;      // body ship must rendezvous with
    double 		  energy;
    
    private boolean launch = false;

    // pop-up dialog for MotionException

    //
    // constants which control canvas layout
    //
    public static final int MAXX = 600;
    public static final int MAXY = 600;
    
    public static String status="";

    // location of force center
    private static final int CENTER_X = 300;
    private static final int CENTER_Y = 150;

    // details for Potential Energy (PE) graph
    private static final int PE_ORIGIN_X = 100;
    private static final int PE_ORIGIN_Y = 330;
    private static final int PE_MAX_Y = 200;
    private static final int PE_MAX_R = 400;
   
    // position for text about thrust vectors
    private static final int THRUST_DISPLAY_X = 10;
    private static final int THRUST_DISPLAY_Y = 50;
    private static final Color BACKGROUND_COLOR = Color.black;   

    // flag to reset to the beginning of the scenario (by New in toolbar)
    private boolean resetPending = false;

    // flag to repaint instead of just updating
    // (used when dots turned off)
    private boolean redraw = false; 

   //-------------------------
   // constructor
   //-------------------------

   public OrbitCanvas() { 

     // give limits of operation to the Orbiting body classes
     OrbitingBodyLite.setmaxradius( PE_MAX_R);
     OrbitingBody.setPEparms( PE_MAX_R, PE_MAX_Y, PE_ORIGIN_X, PE_ORIGIN_Y);
  
     // create a spaceship and tell it where to display thrust info
     spaceship = new SpaceShip( CENTER_X, CENTER_Y, Color.yellow, OrbitForce.DEFAULT);
     spaceship.setDisplayBox( THRUST_DISPLAY_X, THRUST_DISPLAY_Y );

     // create a target object
     target = new OrbitingBody( CENTER_X, 
                                CENTER_Y, 
                                OrbitScenarios.DEFAULT, 
                                Color.green,
                                OrbitForce.DEFAULT); // shouldn't need target

     // build a motion exception modal dialog box and keep it hidden
     //baddl = new MotionExceptionDialog();

     setBackground( BACKGROUND_COLOR);
     initialized = true;

   }

    //---------------------------------------
    // graphics methods
    //---------------------------------------
    

    // paint()
    // - redraw the entire canvas from scratch  
    
    public void paint(Graphics g) {
    	
        // erase any stray dots...
        g.setColor( BACKGROUND_COLOR);
        g.fillRect( 0, 0, this.size().width, this.size().height);
        // central body
        g.setColor( Color.white);
        g.fillOval( CENTER_X - 3, CENTER_Y - 3, 6, 6);

        Font cfont = new Font("Arial", Font.PLAIN, 12);
        FontMetrics cfontm = getFontMetrics( cfont);
        g.setFont( cfont);
        int cfontHeight = cfontm.getHeight();
        String e1 = status=spaceship.getStatus();
        g.setColor( target.color);
        g.drawString( e1, PE_ORIGIN_X , PE_ORIGIN_Y-200 - cfontHeight - 50);
        
        // call the paint methods of the orbiting bodies
        if ( initialized) {          
          //spaceship.paint(g);
          target.paint(g); 
          changeDots( false );

        }
    }

    // update()
    //
    // update the spaceship and target positions
    // (use this most of the time - repeated paint()-ing causes wicked flicker
    // and double buffering seems excessive)

    public void update(Graphics g) {

        // if the redraw flag is set do everything from scratch...
        if (redraw) {
           paint(g);
           
           Font cfont = new Font("Arial", Font.PLAIN, 12);
           FontMetrics cfontm = getFontMetrics( cfont);
           g.setFont( cfont);
           int cfontHeight = cfontm.getHeight();
           String e1 = status=spaceship.getStatus();
           g.setColor( target.color);
           g.drawString( e1, PE_ORIGIN_X , PE_ORIGIN_Y-200 - cfontHeight - 50);
           
           redraw = false;
        } else {
          // central body (since in 1/R^4 can move through center re-draw this each time)
          g.setColor( Color.white);
          g.fillOval( CENTER_X - 3, CENTER_Y - 3, 6, 6);

          // call the paint methods of the orbiting bodies        
          if ( initialized) {
        	 if(launch)
        		  spaceship.paint(g);
            target.paint(g);
            
          }
        }
   }
    
    

   //---------------------------------------------------
   // evolve()
   //
   // - most of the "action" is here
   // - tell the objects to move themselves
   // - test for reset and pass this down 
   //---------------------------------------------------
    
    
 
   public void evolve(int numsteps) {
      
      int i;

         if( resetPending) {
        	 //launch=false;
        	 if (launch){
             spaceship.reset();
        	 spaceship.energy=energy;
        	 }
             //target.reset();
             resetPending = false;
             this.redraw(); // ask for a complete update of the screen
         } else if( evolving && initialized) 
            for ( i = 0; i < numsteps; i++) {
             
            	  if(launch)
            		  spaceship.move();
                 target.move();

                 int sx =spaceship.getPosition().x;
                 int sy =spaceship.getPosition().y;
                 int tx =target.getPosition().x;
                 int ty =target.getPosition().y;
                 
                 
                 
                 if((-8<(sx-tx) && (sx-tx)<8)&&(-8<(sy-ty) && (sy-ty)<8))
                 {
                	 //new SuccessDialog().show();
                	 System.out.println("success");
                	 setEvolving(false);
                	 this.redraw();
                 }
              
         }

   }      

   // badmotion()
   //
   // illegal motion -> reset the scenario 
   // but first show a bad motion dialog box
   private void badmotion() {
         // Netscape on the Sun (but *not* Win95 or apletviewer)
         // throws a null ptr when trying to show. So catch that
         // and reset the scenario without warning the user...
         // (or I have a bug and only Sun netscape is smart enough to
         //  notice...nah, it couldn't be me)
         try{
            // baddl.show(); 
             //while( !baddl.cleared()) ;
         } catch (NullPointerException e) {
             System.out.println("Null Pointer while showing MotionException\n" +
                   "Netscape bug on Sun's (and not on Win95 or in appletviewer)"); 
             
         };

         spaceship.reset();
         target.reset();
         this.redraw();
         try { 
            //baddl.hide();
         } catch (NullPointerException e) {}; // see previous try's comment
   }

  //------------------------------------------ 
  // Methods which interface to other classes
  //------------------------------------------
  
   public void setEvolving( boolean e) {

      evolving = e;
      
      if ( !evolving) 
         spaceship.thruston();  
        else 
         spaceship.dothrust();
      
      this.redraw();
   }

   public void resetRequest() {
        resetPending = true;
   }
   
   
   public void launchRequest(double x) {
       launch = true;
       energy = x;
  } 

   public void redraw() {
       redraw = true;
   }
   
   //
   // changeDots()
   // pass on message from tollbar that
   // drawing dots button has changed
   //
   public void changeDots(boolean drawDots) { 
        spaceship.drawDots( drawDots);
        target.drawDots( drawDots);
        if (!drawDots) {
           // want to erase all the old dots..set a flag for update
           this.redraw();
        }
   }

  //--------------------------------------------
  // EVENT HANDLING METHODS
  //--------------------------------------------

  public boolean mouseDown( Event ev, int x, int y) { // Mouse press

       if ( !evolving) {
         // set coords of the thrust vector and draw it
         spaceship.calcthrust(x, y);
       }
       return true; // indicate we have handled this event
   }

}




