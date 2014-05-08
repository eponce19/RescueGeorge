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

//////////////////////////////////
// OrbitApplet
//
// This top-level class creates a window plus
// toolbar. A thread "runner" is created so that
// one thread can do UI stuff and the other can
// evolve orbits.
//
// Most of the guts are in OrbitCanvas which in turn
// interfaces with two OrbitingBodies.
//
// Certain size/location details of the UI have been hardcoded
// to expedite development. Eventually the applet
// should handle size changes and radial scaling...   
//
//////////////////////////////////


public class OrbitApplet extends Applet implements Runnable{

  Thread runner;
  static boolean frameActive = false; // flag when start Applet button pressed
                                      // used in destroyFrame(), run()

  OrbitCanvas mycanvas; // the canvas which does all the work
  OrbitToolBar toolbar; // toolbar interface
  ToolBar toolbar2; // toolbar interface

  private Button startb = null; // start Applet button
  private MyFrame f = null; // the main Window 
  private Color MyColor;

  public void start() {

     if ( runner == null) {
        runner = new Thread(this);
        runner.start();
     }
  }

  public void stop() {

     f.hide();
     if ( runner != null) {
        runner.stop();
        runner = null;
     }
  }

  // init()
  //
  // initially the applet does not show the frame but only a
  // "click here to start" button
  // Do this as part of init-ing
  //
  public void init() {
     startb = new Button("Click here to start the Orbit Applet");
     add( startb);
  }

  public void run () {

     // do the frame init but leave it hidden until the start button is pressed
     
      OrbitScenarios.setScenario( OrbitScenarios.APPLET_DEFAULT); // must be before mycanvas
     
      mycanvas = new OrbitCanvas();
      toolbar = new OrbitToolBar();

      toolbar.init( mycanvas);

      // define the OrbitApplet frame

      f = new MyFrame();
      f.setLayout(new BorderLayout());
      f.setTitle("Rescatando a George");
      MyColor = new Color(102,153,204);
      setBackground( MyColor );
      setForeground( Color.white );
//      f.resize( new Dimension( OrbitCanvas.MAXX, OrbitCanvas.MAXY) );
      f.resize( new Dimension( OrbitCanvas.MAXX, 700) );

      // init the MenuBar
      MenuBar mb = new MenuBar();

      // center panel
      f.add("Center", mycanvas );

      // North
      f.add("North", toolbar );

      
    //
    // terminal loop: wait for a click to display the window
    //                display until Quit received from window
    //                wait for next click....

    while (true) {
      // wait for a button press before doing anything
      while (!frameActive) {
          try { Thread.sleep(10); } // let GUI catch up with us
                    // this must be => 10 or else Unix implementations
                    // of Netscape will hang forever since the AWT never wakes
                    // up! (Mentioned in comp.lang.java)
             catch (InterruptedException e) {};  
      }

      f.show();
      toolbar.resetquit();

      //
      // MAIN LOOP
      //
      while (frameActive && !toolbar.quit() ) {
         // tell the canvas to evolve the orbiting bodies
         mycanvas.evolve( 4); // arg is number of steps to take
       
         try { Thread.sleep(10); } // see previous sleep comment
             catch (InterruptedException e) {};  

         mycanvas.repaint();
      }
      frameActive = false;
      mycanvas.resetRequest(); // reset the canvas
      f.hide();  // go back to waiting for start button

   } // end while(true)

  }

  //
  // handle the "click to start" button
  //
  public boolean handleEvent(Event evt) {
     boolean handled = true;
     if (evt.target == startb) {
         frameActive = true;
     } else
       handled = false;

     return handled;
  }

  //
  // STATIC METHODS
  // This is klunky - but the window destroy events don't
  // get picked up by the handler in OrbitApplet so I need
  // a wrapper on Frame just to handle them.
  //
  // The method below allows the Frame wrapper to signal
  // WINDOW_DESTROY
  //
  
  public static void destroyFrame() {

      frameActive = false;     

  }

}


///////////////////////////////////
// OrbitToolBar
//
// In a panel across the top of the main window
// display buttons stop, go, new, quit, a checkbox
// for toggling trailing dots and choices for
// force and scenario.
//
// In many cases these actions require messaging
// to the orbit canvas, so this is a parameter to
// the constructor.
///////////////////////////////////

class ToolBar extends Panel {

	  private OrbitCanvas mycanvas;

	  // vars for buttons and stuff
	  // (this way the button text stays in one place)
	  private Button stopb;    // variety of GUI components
	  private Button newb;
	  private Button gob;
	  private Button quitb;
	  private TextField spaceshipEnergy;
	  private Label status;
	  private Label cohetes;
	  private Label labelenergy;
	  private Label labelcohete;
	  private Choice fchoice;
	  private Choice schoice;
	  private Checkbox dots;

	  // flag that Quit was pressed
	  // - set by handleEvent(), resetQuit()
	  // - tested by quit()
	  private boolean quitFlag = false; 

	  // flag to indicate orbits should be evolving
	  // - modified by stop/go buttons in handleEvent()
	  // - tested by evolving()
	  private boolean tevolve = true;   // flag that we're running
	  
	  //contar cohetes que quedan
	  int numcohetes=3;
	  
	  //tiempo restante

	  public void init( ) {
	    
	   
	    status = new Label();
	    status.setText("Preparando primer lanzamiento");
	    add(status);

	    
	  }

	 

	  // action()
	  // - handle the button press and chooser actions
	  //
	  public boolean action(Event event, Object obj) { // handle toolbar actions

		return true; 
	  }

	}



class OrbitToolBar extends Panel {

  private OrbitCanvas mycanvas;

  // vars for buttons and stuff
  // (this way the button text stays in one place)
  private Button stopb;    // variety of GUI components
  private Button newb;
  private Button gob;
  private Button quitb;
  private Button blackhole;
  private TextField spaceshipEnergy;
  public Label status;
  private Label cohetes;
  private Label labelenergy;
  private Label labelcohete;
  private Choice fchoice;
  private Choice schoice;
  private Checkbox dots;

  // flag that Quit was pressed
  // - set by handleEvent(), resetQuit()
  // - tested by quit()
  private boolean quitFlag = false; 

  // flag to indicate orbits should be evolving
  // - modified by stop/go buttons in handleEvent()
  // - tested by evolving()
  private boolean tevolve = true;   // flag that we're running
  
  //contar cohetes que quedan
  int numcohetes=3;
  
  //tiempo restante

  public void init( OrbitCanvas canv) {
    
    mycanvas = canv;
    setLayout( new FlowLayout(FlowLayout.LEFT));
    setFont( new Font( "Arial", Font.PLAIN, 12) );
    //stopb = new Button("Pausar");
    //add(stopb);
    labelenergy = new Label();
    labelenergy.setText("Energía del lanzamiento:");
    add(labelenergy);
    spaceshipEnergy = new TextField(4);
    spaceshipEnergy.setText("0.0");
    add(spaceshipEnergy);
    gob = new Button("Lanzar");
    gob.setBackground(Color.green);
    add(gob);
    labelcohete = new Label();
    labelcohete.setText("Cohetes:");
    add(labelcohete);
    cohetes = new Label();
    cohetes.setText(String.valueOf(numcohetes));
    add(cohetes);
    //status = new Label();
    //status.setText("Preparando primer lanzamiento");
    //add(status);
    blackhole = new Button("BlackHole");
    blackhole.setBackground(Color.black);
    blackhole.setForeground(Color.white);
    add(blackhole);

    //newb = new Button("Reiniciar");
    //add(newb);
    quitb = new Button("Reiniciar");
    add(quitb);
    
   
  }

  public boolean evolving() {
      
     return tevolve;
  }

  public void resetquit() {
 
     quitFlag = false;
  }

  public boolean quit() {

     return quitFlag;
  }

  public void changeStatus(String msg){
	  status.setText(msg);
  }
  // action()
  // - handle the button press and chooser actions
  //
  public boolean action(Event event, Object obj) { // handle toolbar actions

        boolean handled = true;
        mycanvas.changeDots(false);
        if ( event.target == stopb) {
             mycanvas.setEvolving(false);
        } else if ( event.target == gob) { 
             //mycanvas.setEvolving(false);
        	mycanvas.resetRequest();
            mycanvas.launchRequest(Double.parseDouble(spaceshipEnergy.getText())-100);
            numcohetes--;
            if(numcohetes<=0){
            	cohetes.setText(String.valueOf(numcohetes));
            	gob.disable();
            	cohetes.setForeground(Color.red);
            }else{
                cohetes.setText(String.valueOf(numcohetes));
            }
            
        } else if ( event.target == quitb) { 
             //quitFlag = true;
        	numcohetes=3;
        	cohetes.setText(String.valueOf(numcohetes));        	
        	gob.enable();
        } else if ( event.target == blackhole) { 
            //quitFlag = true;
        OrbitForce.setForce( 4 );
        }
      
        else
             handled = false;
         
	return handled; 
  }

}

 ///////////////////////////////////
 // MyFrame
 // Boneheaded wrapper to allow us
 // to catch WINDOW_DESTROY which for
 // reasons I don't understand is not
 // caught in the OrbitApplet handler.
 ///////////////////////////////////



  class MyFrame extends Frame {

   public MyFrame() {

      super();

   }

  public boolean handleEvent(Event evt) {
    boolean handled = true;
     if (evt.id == Event.WINDOW_DESTROY) {
         OrbitApplet.destroyFrame();
     } else
       handled = false;

     return handled;
  }

}
