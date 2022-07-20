package com.abedchouaib.ONEMicroscopy.gui;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;

public class ShowAbout implements PlugIn
{
	protected ImagePlus image;

	public void run(String arg)
	{
		showAbout();
	}

	public void showAbout()
	{
		IJ.showMessage("Coming Soon!");
	}
}
