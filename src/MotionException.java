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
// MotionException
//
// Thrown by OrbitingBodyLite when the motion
// is illegal (i.e. dr^2 is negative) or out of bounds.
//
// Results in a reset of the scneario plus
// a dialog box
///////////////////////////////////////////

public class MotionException extends Exception {

  public MotionException() {

     super();

  }

}
