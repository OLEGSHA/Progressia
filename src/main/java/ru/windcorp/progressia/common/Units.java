package ru.windcorp.progressia.common;

public class Units {
	
	// Base units
	// We're SI.
	public static final float METERS                     = 1;
	public static final float KILOGRAMS                  = 1;
	public static final float SECONDS                    = 1;

	// Length                                            
	public static final float CENTIMETERS                = METERS / 100; 
	public static final float MILLIMETERS                = METERS / 1000;
	public static final float KILOMETERS                 = METERS * 1000;
	
	// Surface
	public static final float SQUARE_CENTIMETERS         = CENTIMETERS * CENTIMETERS;
	public static final float SQUARE_METERS              = METERS * METERS;
	public static final float SQUARE_MILLIMETERS         = MILLIMETERS * MILLIMETERS;
	public static final float SQUARE_KILOMETERS          = KILOMETERS * KILOMETERS;
	
	// Volume
	public static final float CUBIC_CENTIMETERS          = CENTIMETERS * CENTIMETERS * CENTIMETERS;
	public static final float CUBIC_METERS               = METERS * METERS * METERS;
	public static final float CUBIC_MILLIMETERS          = MILLIMETERS * MILLIMETERS * MILLIMETERS;
	public static final float CUBIC_KILOMETERS           = KILOMETERS * KILOMETERS * KILOMETERS;

	// Mass                                              
	public static final float GRAMS                      = KILOGRAMS / 1000;
	public static final float TONNES                     = KILOGRAMS * 1000;
	
	// Density
	public static final float KILOGRAMS_PER_CUBIC_METER  = KILOGRAMS / CUBIC_METERS;
	public static final float GRAMS_PER_CUBIC_CENTIMETER = GRAMS / CUBIC_CENTIMETERS;

	// Time                                              
	public static final float MILLISECONDS               = SECONDS / 1000;
	public static final float MINUTES                    = SECONDS * 60;
	public static final float HOURS                      = MINUTES * 60;
	public static final float DAYS                       = HOURS * 24;
	
	// Frequency
	public static final float HERTZ                      = 1 / SECONDS;
	public static final float KILOHERTZ                  = HERTZ * 1000;

	// Velocity                                          
	public static final float METERS_PER_SECOND          = METERS / SECONDS;
	public static final float KILOMETERS_PER_HOUR        = KILOMETERS / HOURS;

	// Acceleration                                      
	public static final float METERS_PER_SECOND_SQUARED  = METERS_PER_SECOND / SECONDS;

	// Force                                             
	public static final float NEWTONS                    = METERS_PER_SECOND_SQUARED * KILOGRAMS;

}
