package com.abedchouaib.ONEMicroscopy.gui;

/**
 * -------------------------------------------------------------------------------------------
 * This class used to initialize variables.
 * 
 * @author Abed Chouaib
 * @version 1.0.0
 * -------------------------------------------------------------------------------------------
 */
import ij.Prefs;

public class ONEsetup
{
	// ---------- ONE Setup ----------
	public static double StrechColor = 0.15;
	// ---------- end ----------
	// ---------- eSRRF Main Parameters ----------
	public static int eSRRF_Channels;
//	public static int eSRRF_SpecificChannel;
	public static double eSRRF_magnification;
	public static double eSRRF_fwhm;
	public static double eSRRF_sensitivity;
	public static double eSRRF_nFrameForSRRFfromUser;
	public static double eSRRF_DistScale;
	public static double eSRRF_KnownDist;
	public static double eSRRF_ExpFactor;
	// ---------- end ----------

	// ---------- Temporal Analysis ----------
	public static boolean eSRRF_calculateAVG;
	public static boolean eSRRF_calculateVAR;
	public static boolean eSRRF_calculateTAC2;
	public static boolean eSRRF_getInterpolatedImage;
	// ---------- end ----------

	// ---------- eSRRF Advance Settings ----------
	public static boolean eSRRF_correctVibration;
	public static boolean eSRRF_doRollingAnalysis;
	public static double eSRRF_frameGapFromUser;
	public static boolean eSRRF_do3DSRRF;

	public static boolean eSRRF_dintWeighting;
	public static boolean eSRRF_doFHTinterpolation;
	public static boolean eSRRF_doMPmapCorrection;
	// ---------- end ----------
	// ---------- eSRRF PC Advance Settings ----------
	public static double eSRRF_blockSize;
	public static double eSRRF_maxMemoryGPU;
	public static String eSRRF_chosenDeviceName;
	// ---------- end ----------
	static String AdOptPref = "nanoj.liveSRRF.gui.LiveSRRF_optimised_.";
	static String ONEoptPref = "onemic.liveSRRF.java.gui.Expansion_Factor";

	public static void LoadPrefs()
	{
		// ---------- ONE Main Parameters ----------
		eSRRF_ExpFactor = Prefs.get(ONEoptPref + "Expansion_Factor", 5.0);
		// ---------- end ----------

		// ---------- eSRRF Main Parameters ----------
		eSRRF_magnification = Prefs.get(AdOptPref + "magnification", 5.0);
		eSRRF_fwhm = Prefs.get(AdOptPref + "fwhm", 1.5);
		eSRRF_sensitivity = Prefs.get(AdOptPref + "sensitivity", 1.0);
		eSRRF_nFrameForSRRFfromUser = Prefs.get(AdOptPref + "nFrameForSRRFfromUser", 0.0);
		// ---------- end ----------

		// ---------- Temporal Analysis ----------
		eSRRF_calculateAVG = Prefs.get(AdOptPref + "calculateAVG", true);
		eSRRF_calculateVAR = Prefs.get(AdOptPref + "calculateVAR", false);
		eSRRF_calculateTAC2 = Prefs.get(AdOptPref + "calculateTAC2", false);
		eSRRF_getInterpolatedImage = Prefs.get(AdOptPref + "getInterpolatedImage", false);
		// ---------- end ----------

		// ---------- eSRRF Advance Settings ----------
		eSRRF_correctVibration = Prefs.get(AdOptPref + "correctVibration", true);
		eSRRF_doRollingAnalysis = Prefs.get(AdOptPref + "doRollingAnalysis", true);
		eSRRF_frameGapFromUser = Prefs.get(AdOptPref + "frameGapFromUser", 0.0);
		eSRRF_do3DSRRF = Prefs.get(AdOptPref + "do3DSRRF", true);

		eSRRF_dintWeighting = Prefs.get(AdOptPref + "dintWeighting", true);
		eSRRF_doFHTinterpolation = Prefs.get(AdOptPref + "doFHTinterpolation", true);
		eSRRF_doMPmapCorrection = Prefs.get(AdOptPref + "doMPmapCorrection", true);
		// ---------- end ----------

		// ---------- eSRRF PC Advance Settings ----------
		eSRRF_blockSize = Prefs.get(AdOptPref + "blockSize", 1000);
		eSRRF_maxMemoryGPU = Prefs.get(AdOptPref + "maxMemoryGPU", 1000);
		eSRRF_chosenDeviceName = Prefs.get(AdOptPref + "chosenDeviceName", "Default device");
		// ---------- end ----------
	}

	public static void savePrefs()
	{
		// ---------- ONE Main Parameters ----------
		Prefs.set(ONEoptPref + "Expansion_Factor", eSRRF_ExpFactor);
		// ---------- end ----------

		// ---------- eSRRF Main Parameters ----------
		Prefs.set(AdOptPref + "magnification", eSRRF_magnification);
		Prefs.set(AdOptPref + "fwhm", eSRRF_fwhm);
		Prefs.set(AdOptPref + "sensitivity", eSRRF_sensitivity);
		Prefs.set(AdOptPref + "nFrameForSRRFfromUser", eSRRF_nFrameForSRRFfromUser);
		// ---------- end ----------

		// ---------- Temporal Analysis ----------
		Prefs.set(AdOptPref + "calculateAVG", eSRRF_calculateAVG);
		Prefs.set(AdOptPref + "calculateVAR", eSRRF_calculateVAR);
		Prefs.set(AdOptPref + "calculateTAC2", eSRRF_calculateTAC2);
		Prefs.set(AdOptPref + "getInterpolatedImage", eSRRF_getInterpolatedImage);
		// ---------- end ----------

		// ---------- eSRRF Advance Settings ----------
		Prefs.set(AdOptPref + "correctVibration", eSRRF_correctVibration);
		Prefs.set(AdOptPref + "doRollingAnalysis", eSRRF_doRollingAnalysis);
		Prefs.set(AdOptPref + "frameGapFromUser", eSRRF_frameGapFromUser);
		Prefs.set(AdOptPref + "do3DSRRF", eSRRF_do3DSRRF);

		Prefs.set(AdOptPref + "dintWeighting", eSRRF_dintWeighting);
		Prefs.set(AdOptPref + "doFHTinterpolation", eSRRF_doFHTinterpolation);
		Prefs.set(AdOptPref + "doMPmapCorrection", eSRRF_doMPmapCorrection);
		// ---------- end ----------
	}

}
