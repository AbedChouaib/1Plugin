package com.abedchouaib.ONEMicroscopy;

/**
 * -------------------------------------------------------------------------------------------
 * The ONE Microscopy is a Java-written software that utilizes Fiji app,
 * developed for Ali Shaib and Silvio Rizzoli, University Medical Center
 * Göttingen, Germany, by Abed Chouaib, University of Saarland, Homburg Saar,
 * Germany. This software is installed in the freeware Fiji app and provided
 * without any express or implied warranty. Permission for Everyone to copy,
 * modify and distribute verbatim copies of this software for any purpose
 * without a fee is hereby granted, provided that this entire notice is included
 * in all copies of any software which is or includes a copy or a modification
 * of ONE Platform. One Microscopy is licensed under the GNU General Public
 * License v3.0.
 * 
 * @author Abed Chouaib
 * @version 1.0.0
 * -------------------------------------------------------------------------------------------
 */
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import org.apache.commons.io.FilenameUtils;

import com.abedchouaib.ONEMicroscopy.gui.InputData;
import com.abedchouaib.ONEMicroscopy.gui.ONEsetup;
import com.abedchouaib.ONEMicroscopy.gui.Window_eSRRF;

import ij.IJ;
import ij.ImagePlus;
import ij.Macro;
import ij.WindowManager;
import ij.measure.Measurements;
import ij.plugin.Duplicator;
import ij.plugin.PlugIn;
import ij.plugin.frame.Editor;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import ij.process.ShortProcessor;
import loci.formats.FormatException;
import loci.plugins.BF;

public class Start_eSRRF implements PlugIn
{
//	private String unit = Window_eSRRF.Unit;
//	private String AnalysisType = Window_eSRRF.SRRFType;
	private int zSlices;
	private String OutputDir;
	private String OutputBeadsDir;
	private String BeadsImageName = "";

	private boolean jump = false;

	private ArrayList<String> StackDirectories = Window_eSRRF.StackDirectories;
	private double ScaleNumber;
//	public static final String MACRO_CANCELED = "Macro canceled";
	private boolean SaveParentFolder = Window_eSRRF.SaveParentFolder;
	private String DefaultSavePath = Window_eSRRF.OutPutDirectory;
	private int FPS = Window_eSRRF.FPS;
	private int StartFromVideo = Window_eSRRF.StartFromVideo;
	String OutputSRRF = "-OUTPUT 3D";
//	String OutputCAC = "-CAC_OUTPUT";
	String CAC = "-CAC";
	private String OutName = "ONE";
	private ArrayList<String> LogFile = new ArrayList<String>();
	private boolean CreateLogFileOnce = false;
	// ============ Advance Settings ============
	private boolean Calibrate = Window_eSRRF.Calibrate;
	private int SRRF_MaxImgNum = Window_eSRRF.EndFrame;
	private String InputDirPath = Window_eSRRF.ImportDirPath;
	private String BeadsDirPath = Window_eSRRF.BeadsDirPath;

	private int channels = ONEsetup.eSRRF_Channels;

	private double Distance = ONEsetup.eSRRF_DistScale;
	private double KnownDist = ONEsetup.eSRRF_KnownDist;
	private double ExpFactor = ONEsetup.eSRRF_ExpFactor;
	private double radius = ONEsetup.eSRRF_fwhm;
	private double sensitivity = ONEsetup.eSRRF_sensitivity;
	private boolean CVibration = ONEsetup.eSRRF_correctVibration;
	private boolean intWeight = ONEsetup.eSRRF_dintWeighting;
	private boolean FHTinterpol = ONEsetup.eSRRF_doFHTinterpolation;
	private boolean MapPixelC = ONEsetup.eSRRF_doMPmapCorrection;
	private boolean RollFrame = ONEsetup.eSRRF_doRollingAnalysis;
	// ---------- eSRRF Main Parameters ----------
	private double RadMagnification = ONEsetup.eSRRF_magnification;
//	private double AxesRing = ONEsetup.eSRRF_fwhm;
//	private double eSRRF_sensitivity = ONEsetup.eSRRF_sensitivity;
//	private double eSRRF_nFrameForSRRFfromUser = ONEsetup.eSRRF_nFrameForSRRFfromUser;
//	// ---------- end ----------
//	// ---------- Temporal Analysis ----------
	private boolean eSRRF_calculateAVG = ONEsetup.eSRRF_calculateAVG;
	private boolean eSRRF_calculateVAR = ONEsetup.eSRRF_calculateVAR;
	private boolean eSRRF_calculateTAC2 = ONEsetup.eSRRF_calculateTAC2;
	private boolean eSRRF_getInterpolatedImage = ONEsetup.eSRRF_getInterpolatedImage;
//	// ---------- end ----------
	private String[] AnaTypes = { " - eSRRF (AVG)", " - eSRRF (VAR)", " - eSRRF (TAC2)", " - interpolated" };
	private String AnaTypeName;
	// IJ.run(imp, "eSRRF - Analysis", "magnification=4 radius=1.50 sensitivity=1
	// #=0 vibration avg wide-field perform #_0=45 axial=400");
	private String Options;
//	// ---------- eSRRF Advance Settings ----------
//	private boolean eSRRF_correctVibration = ONEsetup.eSRRF_correctVibration;
//	private boolean eSRRF_doRollingAnalysis = ONEsetup.eSRRF_doRollingAnalysis;
//	private double eSRRF_frameGapFromUser = ONEsetup.eSRRF_frameGapFromUser;
//	private boolean eSRRF_do3DSRRF = ONEsetup.eSRRF_do3DSRRF;
//
//	private boolean eSRRF_dintWeighting = ONEsetup.eSRRF_dintWeighting;
//	private boolean eSRRF_doFHTinterpolation = ONEsetup.eSRRF_doFHTinterpolation;
//	private boolean eSRRF_doMPmapCorrection = ONEsetup.eSRRF_doMPmapCorrection;
//	// ---------- end ----------

	ColorModel[] ChnColor;

	// ============ end ============

	// ************************** #Region Progress Bar **************************
	static boolean Cancel = false;
	JFrame frame = new JFrame();
	JProgressBar progressBar = new JProgressBar();
	JLabel progressStatusLabel = new JLabel();
	// ========================== #End Progress Bar ==========================

	public void run(String arg)
	{
//		AdjustOption();
//		LogFile.add("Options used:");
//		LogFile.add("SRRF Type = " + AnalysisType + " -- " + SRRForder + " -- " + psf);
//		LogFile.add("");
//		LogFile.add("----------------------------------------------------------------");
		GetTemporalAnalysisType();
		Options = GetOptions();
		runAnalysis();
	}

	// ========================== ==========================
	private void runAnalysis()
	{
		String BeadsFoldeName = "";
		// ************************** #Region System IO **************************
		if (Calibrate)
		{
			File BeadsFolder = new File(BeadsDirPath);
			if (SaveParentFolder)
			{
				BeadsFoldeName = BeadsFolder.getName();
				OutputBeadsDir = BeadsFolder.getParent() + File.separator + BeadsFoldeName + OutputSRRF + File.separator;
			} else
			{
				BeadsFoldeName = BeadsFolder.getName();
				OutputBeadsDir = DefaultSavePath + File.separator + BeadsFoldeName + OutputSRRF + File.separator;
			}
			CreateDir(OutputBeadsDir);
			File[] BeadsList = BeadsFolder.listFiles();
			AdjustParameters(BeadsList, 0); // read metadata and user input.
			StartAnalysis(BeadsList, 0, OutputBeadsDir, true, false, 0);
		}
		File folder = new File(InputDirPath);
		if (SaveParentFolder)
		{
			OutputDir = folder.getParent() + File.separator + folder.getName() + OutputSRRF + File.separator;
		} else
		{
			OutputDir = DefaultSavePath + File.separator + folder.getName() + OutputSRRF + File.separator;
		}
		CreateDir(OutputDir);
		File[] FileList = GetFiles(StackDirectories);
		// ------------------- #Call Progress Bar Window -------------------
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					ShowProgressBarWindow();
					SetPrograssBarMax(FileList.length);
					UpdateProgressBar(0);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		// ------------------- #Call Progress Bar Window -------------------
		// ========================== #End System IO ==========================

		// ************************** #Region Active loop **************************
		for (int i = StartFromVideo; i < FileList.length; i++)
		{
			if (!Cancel)
			{
				AdjustParameters(FileList, i); // read metadata and user input.
				LogFile.add("------------------------------");
				LogFile.add("Video name " + FileList[i].getName());
				LogFile.add("parameters used: Distance = " + Distance + "  --  Ragdiality Magnification = " + RadMagnification
						+ " --  Expansion Factor " + ExpFactor);
				LogFile.add("  --  channels number  = " + channels + "  --  Delta Time " + FPS);
				if (zSlices == 1)
				{
					StartAnalysis(FileList, i, OutputDir, false, false, 0);
				} else
				{
					StartAnalysis5D(FileList, i, OutputDir, false, false, 0);
				}
			}
			LogFile.add("------------------------------");
			UpdateProgressBar(i);
		}
		if (!Cancel)
		{
			try
			{
				PrintLogFile(OutputDir, LogFile);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			frame.dispose();
			IJ.showMessage("Computation complete!");
		}
		// ========================== #End Active loop ==========================
	}
	// ========================== ==========================

	// ************************** **************************
	// ************************** #Region Methods **************************
	/**
	 * Start analysis method.
	 *
	 *
	 * @param FileList      array of files.
	 * @param i             loop number.
	 * @param Output        output directory link.
	 * @param GenerateLog   perform chromatic aberration if true.
	 * @param SwitchChannel automatically switch drift correction to another channel
	 *                      if the current fails.
	 * @param ChnPos        is used to determine the channel position to process.
	 */

	private void StartAnalysis(File[] FileList, int i, String Output, boolean GenerateLog, boolean SwitchChannel, int ChnPos)
	{
		jump = false;
		ChnColor = new ColorModel[channels];
		String Path = FileList[i].getPath();
		try
		{
			ImagePlus[] OImgArray = BF.openImagePlus(Path);
			// (width[0], height[1], nChannels[2],nSlices[3], nFrames[4]).
			int[] imDim = OImgArray[0].getDimensions(); // 0 get first series.
			if (imDim[4] == 1) // correct dimension order if frames = 1.
			{
				String Options = "order=xyczt(default) channels=" + channels + " slices=" + 1; // length instead of 1
				Options = Options + " frames=" + imDim[3];
				IJ.run(OImgArray[0], "Stack to Hyperstack...", Options);
				imDim = OImgArray[0].getDimensions();
			}
			ScaleNumber = Distance * ExpFactor * RadMagnification;
			ImagePlus[] OImg = new ImagePlus[imDim[2]];
			for (int k = 0; k < OImg.length; k++)
			{
				int chn = k + 1;
				if (chn <= channels)
				{
					OImg[k] = new Duplicator().run(OImgArray[0], chn, chn, 1, 1, 1, imDim[4]);
					ChnColor[k] = OImg[k].getStack().getColorModel();
				}
			}
			String ImageName = FileList[i].getName();
			String FileExt = GetFileExtension(ImageName);
			if (FileExt != null)
			{
				ImageName = ImageName.replace(FileExt, "");
			}
			String[] ChannelNames = new String[imDim[2]];
			// ------------------------- Creating Directories -------------------------
			String OutSubFolder = Output + ImageName + File.separator;
			CreateDir(OutSubFolder); // 2nd level subfolder
			String OutSRRF = OutSubFolder + OutName + File.separator;
			CreateDir(OutSRRF); // 3rd level subfolder
			String OutResults = OutSubFolder + ImageName + File.separator;
			CreateDir(OutResults); // 3rd level subfolder
			// ------------------------- end -------------------------

			ImagePlus[] ImgSRRF = new ImagePlus[imDim[2]];
			// createHyperStack(title, width, height, channels, slices, frames, bitdepth);
//			ImagePlus HyperImg = IJ.createHyperStack(ImageName, (int) (imDim[0] * RadMagnification), (int) (imDim[1] * RadMagnification),
//					channels, 1, 1, 32);
			ImagePlus HyperImg = IJ.createImage(ImageName, "32-bit", (int) (imDim[0] * RadMagnification),
					(int) (imDim[1] * RadMagnification), channels, 1, 1);
			for (int k = 0; k < imDim[2]; k++)
			{
				if (!Cancel)
				{
					int chn = k + 1;
//					int currentChn = k;
					// Channel reference if exists
					if (chn <= channels)
					{
						ChannelNames[k] = ImageName + "_" + chn;
						OImg[k].setTitle(ChannelNames[k]);
						String[] PathAndName = { ChannelNames[k], OutResults, OutSRRF };
						ImgSRRF[k] = eSRRFanalysis(OImg[k], PathAndName);
						if (jump)
						{
							break;
						}
						ChnPos = k;
						ColorizeChannel(ImgSRRF[k], chn); // colorize ImgSRRF
						ChannelNames[k] = "ONE_" + ChannelNames[k];
						ImgSRRF[k].setTitle(ChannelNames[k]);
						ImgSRRF[k].createImagePlus();
					}
				}
			}
			if (!Cancel)
			{
				if (!jump)
				{
					if (channels == 1)
					{
						HyperImg = ImgSRRF[0];
						IJ.run(HyperImg, "Enhance Contrast", "saturated=" + ONEsetup.StrechColor);
					} else
					{
						for (int k = 0; k < imDim[2]; k++)
						{
							int chn = k + 1;
							if (chn <= channels)
							{
								ImageProcessor ip = ImgSRRF[k].getProcessor();
								HyperImg.getStack().setProcessor(ip, chn);
								HyperImg.setC(chn);
//								HyperImg.resetDisplayRange();
								IJ.run(HyperImg, "Enhance Contrast", "saturated=" + ONEsetup.StrechColor);
							}
						}
						ImageProcessor ip = ImgSRRF[0].getProcessor();
						HyperImg.getStack().setProcessor(ip, 1);
						HyperImg.setC(1);
//						HyperImg.resetDisplayRange();
						IJ.run(HyperImg, "Enhance Contrast", "saturated=" + ONEsetup.StrechColor);
						Local_CompositeConverter LocalComp = new Local_CompositeConverter();
						LocalComp.StartConverting(HyperImg);
					}
					if (channels == 1)
					{
						IJ.run(HyperImg, "NanoJ-Orange", "");
					}
					IJ.run(HyperImg, "Set Scale...", "distance=" + ScaleNumber + " known=" + KnownDist + " unit=micron");
					HyperImg.setTitle(OutName + "_");
					IJ.save(HyperImg, OutSRRF + OutName + "_" + ImageName + ".tif");
					if (GenerateLog)
					{
						// Generate optical flow log file and get its name.
						BeadsImageName = GenerateOpticalFlowLogFile(HyperImg, channels, 1);
					}
					if (Calibrate && !GenerateLog)
					{
						// Performing chromatic correction for the final image.
						ChromaticCorrection(HyperImg, BeadsImageName, channels, 1, OutSRRF, ImageName);
					}
					HyperImg.changes = false;
					HyperImg.close();
				} else
				{
					LogFile.add("SRRF failed to analyze this video");
				}
			}
			if (Calibrate)
			{
				GetLogFile(Output, false);
			}
			IJ.run("Close All");
		} catch (FormatException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Calls eSRRF plugin and start analysis, returns super-resolution image.
	 * 
	 * @param Image       Stack ImagePlus used to perform analysis on.
	 * @param PathAndName Array of Strings related to the image name and its path.
	 */
	private ImagePlus eSRRFanalysis(ImagePlus Img, String[] PathAndName)
	{
		ImagePlus result;
		String OutPath = PathAndName[1];
//		String OptionsSRRF = "magnification=10 radius=3 sensitivity=1 #=0 avg tac2 #_0=0 axial=400 show";
//		Img.show();
		IJ.run(Img, "eSRRF - Analysis", Options);
		try
		{
			ImagePlus SRRF_Img = WindowManager.getImage(PathAndName[0] + AnaTypeName);
//			IJ.showMessage(PathAndName[0]);
			if (SRRF_Img == null)
			{
				jump = true;
				return null;
			} else
			{
//				SRRF_Img.show();
				result = SRRF_Img.duplicate();
				String SRRF_Name = OutName + "_" + PathAndName[0];
				IJ.save(SRRF_Img, OutPath + SRRF_Name + ".tif");
				SRRF_Img.close();
//				IJ.selectWindow("Log");
//				IJ.saveAs("Text", OutPath + SRRF_Name + "_Log.txt");
				jump = false;
			}
		} catch (Exception e)
		{
			System.out.println("Image doesn't exist");
			jump = true;
			return null;
		}
//		Img.changes = false;
//		Img.close();
		return result;
	}

	// ------------------------- Computing log file -------------------------
	/**
	 * Calls Image Stabilizer plugin and start analysis, returns image title.
	 * 
	 * @param Image  Stack ImagePlus used to perform analysis on.
	 * @param chn    Channel number.
	 * @param length Z slice length.
	 */
	private String GenerateOpticalFlowLogFile(ImagePlus Beads, int chn, int length)
	{
		// ------------------------- Computing log file -------------------------
		IJ.run(Beads, "Hyperstack to Stack", "");
//		IJ.run("Next Slice [>]");
		Beads.setSlice(1);
		String Options = "";
		Options = Options + "transformation=Translation maximum_pyramid_levels=0";
		Options = Options + " template_update_coefficient=0.90 maximum_iterations=400";
		Options = Options + " error_tolerance=0.0000001 log_transformation_coefficients";
		Beads.show();
		IJ.selectWindow(Beads.getTitle());
		IJ.run(Beads, "Image Stabilizer", Options);
		String Options2 = "";
		Options2 = Options2 + "order=xyczt(default) channels=" + chn + " slices=" + 1; // length instead of 1
		Options2 = Options2 + " frames=1 display=Composite";
		IJ.run(Beads, "Stack to Hyperstack...", Options2);
		return Beads.getTitle();
	}
	// ------------------------- end -------------------------

	// ------------------------- Calibrating -------------------------
	/**
	 * Calls Image Stabilizer plugin to perform log applier.
	 * 
	 * @param Image     Merged SRRF images.
	 * @param chn       Number of channels.
	 * @param Zax       Number of Z slices.
	 * @param Path      Output path.
	 * @param ImageName Image Name.
	 */
	private void ChromaticCorrection(ImagePlus imp, String Name, int chn, int Zax, String Path, String ImageName)
	{
		String Options = "";
		Options = Options + "order=xyczt(default) channels=" + chn + " slices=" + Zax;
		Options = Options + " frames=1 display=Composite";
		IJ.run(imp, "Hyperstack to Stack", "");
		imp.show();
		IJ.selectWindow(imp.getTitle());
		IJ.run(imp, "Image Stabilizer Log Applier", " ");
		IJ.run(imp, "Stack to Hyperstack...", Options);
		String name = ImageName + CAC;
		imp.setTitle(name);
		IJ.save(imp, Path + name + ".tif");
	}
	// ------------------------- end -------------------------

	/**
	 * Update metadata for each image stack.
	 * 
	 * @param FileList Array of files.
	 * @param i        loop number.
	 */
	private void AdjustParameters(File[] FileList, int i)
	{
		String Path = FileList[i].getPath();
		InputData IData;
		try
		{
			IData = new InputData(Path, InputData.ImportType.Vi, 1);
			AutomateUserInput(IData); // adjust inputs to conditions.
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Read and convert List of strings to array of files.
	 * 
	 * @param ListofFiles List of strings.
	 */
	private File[] GetFiles(ArrayList<String> ListofFiles)
	{
		int L_ = ListofFiles.size();
		String[] ArrayofFiles = ListofFiles.toArray(new String[L_]);
		File[] files = new File[L_];
		for (int i = 0; i < L_; i++)
		{
			File temp = new File(ArrayofFiles[i]);
			files[i] = temp;
		}
		return files;
	}

	public static double getMin(double[] inputArray)
	{
		double minValue = inputArray[0];
		for (int i = 1; i < inputArray.length; i++)
		{
			if (inputArray[i] < minValue)
			{
				minValue = inputArray[i];
			}
		}
		return minValue;
	}

	/**
	 * Adjust input settings relative to user input and image metadata.
	 * 
	 * @param IData Input data
	 */
	void AutomateUserInput(InputData IData)
	{
		double[] Resolution = IData.GetResolutionsXYZ();
		int nChannels = IData.GetnChannels();
//		int nFrames = IData.GetnFrames();
		Distance = ONEsetup.eSRRF_DistScale == 0 ? Window_eSRRF.roundDec3(1 / Resolution[0]) : ONEsetup.eSRRF_DistScale;
		channels = ONEsetup.eSRRF_Channels == 0 ? nChannels : ONEsetup.eSRRF_Channels;
//		SRRF_MaxImgNum = Window_eSRRF.EndFrame == 0 ? nFrames : Window_eSRRF.EndFrame;
		zSlices = IData.GetzSlices();
	}

	private void ColorizeChannel(ImagePlus imp, int chn)
	{
		switch (chn)
		{
		case 1:
			IJ.run(imp, "Red", ""); // new DirectColorModel(24, 0xff0000, 0xff00, 0xff);
			break;
		case 2:
			IJ.run(imp, "Green", "");
			break;
		case 3:
			IJ.run(imp, "Cyan", "");
			break;
		case 4:
			IJ.run(imp, "Magenta", "");
			break;
		case 5:
			IJ.run(imp, "Yellow", "");
			break;
		case 6:
			IJ.run(imp, "Blue", "");
			break;
		default:
			IJ.run(imp, "Grays", "");
			break;
		}
	}

	private String GetFileExtension(String name)
	{
		String ext = "." + FilenameUtils.getExtension(name);
		return ext;
	}

	private void CreateDir(String path)
	{
		File OutLocation = new File(path);
		if (!OutLocation.exists())
		{
			OutLocation.mkdir();
		}
	}

	private void GetTemporalAnalysisType()
	{
		if (eSRRF_calculateAVG)
		{
			AnaTypeName = AnaTypes[0];
		} else if (eSRRF_calculateVAR)
		{
			AnaTypeName = AnaTypes[1];
		} else if (eSRRF_calculateTAC2)
		{
			AnaTypeName = AnaTypes[2];
		} else if (eSRRF_getInterpolatedImage)
		{
			AnaTypeName = AnaTypes[3];
		}
	}

	// ======================= Dialog Window =======================
	private String GetOptions()
	{
		// IJ.run(imp, "eSRRF - Analysis", "processing=[Default device] maximum=4500
		// analysis=30000 intensity use macro-pixel magnification=4 radius=1.50
		// sensitivity=1 #=0 vibration avg wide-field perform #_0=45 axial=400 show");
		String Mag, Rad, Sens, Vibration, CalAvg, CalVar, CalTac2, CalInterpol, FrameRol, FrameGap, Axial, nFrames, intW, FHTinter,
				MapPinter;
		String Opt = "processing=[" + ONEsetup.eSRRF_chosenDeviceName + "] maximum=" + ONEsetup.eSRRF_maxMemoryGPU + "  analysis="
				+ ONEsetup.eSRRF_blockSize;
		intW = intWeight ? " intensity" : "";
		FHTinter = FHTinterpol ? " use" : "";
		MapPinter = MapPixelC ? " macro-pixel" : "";

		Mag = " magnification=" + RadMagnification;
		Rad = " radius=" + radius;
		Sens = " sensitivity=" + sensitivity;
		nFrames = " #=" + SRRF_MaxImgNum;
		Vibration = CVibration ? " vibration" : "";
		CalAvg = eSRRF_calculateAVG ? " avg" : "";
		CalVar = eSRRF_calculateVAR ? " var" : "";
		CalTac2 = eSRRF_calculateTAC2 ? " tac2" : "";
		CalInterpol = eSRRF_getInterpolatedImage ? " wide-field" : "";
		FrameRol = RollFrame ? " perform" : "";
		FrameGap = " #_0=" + ONEsetup.eSRRF_frameGapFromUser;
		Axial = " axial=" + 400;
		Opt = Opt + intW + FHTinter + MapPinter + Mag + Rad + Sens + nFrames + Vibration + CalAvg + CalVar + CalTac2 + CalInterpol
				+ FrameRol + FrameGap + Axial;
		return Opt;
	}

	public void PrintLogFile(String OutDir, ArrayList<String> LogFile) throws IOException
	{
		FileWriter LogF = new FileWriter(OutDir + File.separator + "Log file.txt");

		for (int i = 0; i < LogFile.size(); i++)
		{
			LogF.write(LogFile.get(i) + "\n");
		}
		LogF.close();
	}

	// ======================= #Region Progress Bar Window =======================
	public void ShowProgressBarWindow()
	{
		frame.setVisible(true);
		frame.setResizable(false);
		progressStatusLabel = new JLabel();
		progressStatusLabel.setText("Analyzing data please wait...");
		frame.setBounds(100, 100, 320, 160);
		frame.getContentPane().setLayout(null);
		frame.setTitle("Status Window");

//		ClassLoader cl = this.getClass().getClassLoader();
//		ImageIcon mainWindoIcon = new ImageIcon(cl.getResource("DART_icon_64.png"));
//		frame.setIconImage(mainWindoIcon.getImage());

		progressBar.setStringPainted(true);
		progressBar.setBounds(10, 50, 294, 25);
		frame.getContentPane().add(progressBar);

		JButton CancelButton = new JButton("Cancel");
		CancelButton.setBounds(110, 97, 89, 23);
		frame.getContentPane().add(CancelButton);
		// progressStatusLabel = new JLabel("Processing data please wait...");
		progressStatusLabel.setBounds(10, 30, 251, 14);
		frame.getContentPane().add(progressStatusLabel);
		CancelButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				progressStatusLabel.setText("Canceling in progress...");
				Cancel = true;
				frame.dispose();
				throw new RuntimeException(Macro.MACRO_CANCELED);
			}
		});
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				progressStatusLabel.setText("Canceling in progress...");
				Cancel = true;
				frame.dispose();
				throw new RuntimeException(Macro.MACRO_CANCELED);
			}
		});
	}

	public void SetPrograssBarMax(int Max)
	{
		progressBar.setMaximum(Max);
	}

	public void UpdateProgressBar(int value)
	{
		progressBar.setValue(value);
	}

	// ======================= #Region Progress Bar Window =======================
	// ======================= =======================

	// ======================= =======================
	// ======================= #Region ONE Z Analysis =======================
	/**
	 * Start zONE analysis.
	 *
	 *
	 * @param FileList      array of files.
	 * @param i             loop number.
	 * @param Output        output directory link.
	 * @param GenerateLog   perform chromatic aberration if true.
	 * @param SwitchChannel automatically switch drift correction to another channel
	 *                      if the current fails.
	 * @param ChnPos        is used to determine the channel position to process.
	 */
	private void StartAnalysis5D(File[] FileList, int i, String Output, boolean GenerateLog, boolean SwitchChannel, int ChnPos)
	{
		ChnColor = new ColorModel[channels];
		String Path = FileList[i].getPath();
		try
		{
			ImagePlus[] OImgArray = BF.openImagePlus(Path);
			// (width[0], height[1], nChannels[2],nSlices[3], nFrames[4]).
			int[] imDim = OImgArray[0].getDimensions(); // 0 get first series.
			ScaleNumber = Distance * ExpFactor * RadMagnification;
//			OImgArray[0].get
//			System.out.println(""+imDim[0]+"  "+imDim[1]+"  "+imDim[2]+"  "+imDim[3]+"  "+imDim[4]);
			ImagePlus[][] OImg = new ImagePlus[imDim[2]][zSlices];
			for (int k = 0; k < OImg.length; k++)
			{
				int chn = k + 1;
				if (chn <= channels)
				{
					for (int z = 0; z < zSlices; z++)
					{
						int Zaxis = z + 1;
						OImg[k][z] = new Duplicator().run(OImgArray[0], chn, chn, Zaxis, Zaxis, 1, imDim[4]);
						ChnColor[k] = OImg[k][0].getStack().getColorModel(); //
//						ChnColor[k] = getColorMOdel(chn);
					}
				}
			}
			int width = OImg[0][0].getWidth();
			int height = OImg[0][0].getHeight();
//			int depth = OImg[0][0].getProcessor().getBitDepth();

			String ImageName = FileList[i].getName();
			String FileExt = GetFileExtension(ImageName);
			if (FileExt != null)
			{
				ImageName = ImageName.replace(FileExt, "");
			}
			String[] ChannelNames = new String[imDim[3]];
			// ------------------------- Creating Directories -------------------------
			String OutSubFolder = Output + ImageName + File.separator;
			CreateDir(OutSubFolder); // 2nd level subfolder
			String OutSRRF = OutSubFolder + OutName + File.separator;
			CreateDir(OutSRRF); // 3rd level subfolder
			String OutResults = OutSubFolder + ImageName + File.separator;
			CreateDir(OutResults); // 3rd level subfolder
			// ------------------------- end -------------------------
			ImagePlus[] HyperImg = new ImagePlus[channels];
			for (int k = 0; k < channels; k++)
			{
				HyperImg[k] = IJ.createHyperStack(ImageName, (int) (width * RadMagnification), (int) (height * RadMagnification), 1,
						zSlices, 1, 32);
//				int chn = k + 1;
				for (int z = 0; z < zSlices; z++)
				{
					if (!Cancel)
					{
						zONE(OImg, HyperImg[k], k, z, ChannelNames, ImageName, 1, OutResults, OutSRRF, false);
					}
				}
			}
			if (!Cancel)
			{
//				ImagePlus MasterStackONE = IJ.createHyperStack(ImageName, (int) (width * RadMagnification), (int) (height * RadMagnification),
//				1, zSlices, 1, 32);
//				ImagePlus MasterStack = IJ.createImage(ImageName, "32-bit", (int) (width * RadMagnification), (int) (height * RadMagnification),
//				channels, zSlices, 1);
				ImagePlus MasterStack;
				if (channels == 1)
				{
					MasterStack = IJ.createHyperStack(ImageName, (int) (width * RadMagnification), (int) (height * RadMagnification),
							channels, zSlices, 1, 32);
					MasterStack = HyperImg[0];
					MasterStack.resetDisplayRange();
//					MasterStack.getStack().setColorModel(getColorMOdel(1));
//					IJ.run(MasterStack, "NanoJ-Orange", "");
//					IJ.run(MasterStack, "Enhance Contrast", "saturated=0.35"); // TODO: new
				} else
				{
					MasterStack = IJ.createImage(ImageName, "32-bit", (int) (width * RadMagnification), (int) (height * RadMagnification),
							channels, zSlices, 1);
					String options = "";
					for (int k = 0; k < imDim[2]; k++)
					{
						int chn = k + 1;
						if (chn <= channels)
						{
							options = options + "c" + chn + "=[" + ChannelNames[k] + "] ";
							HyperImg[k].setTitle(ChannelNames[k]);
							for (int z = 1; z <= zSlices; z++)
							{
								int ndx5D = get5Dindex(chn, z, 1, channels, zSlices);
								ImageProcessor zIP = HyperImg[k].getStack().getProcessor(z);
								MasterStack.getStack().setProcessor(zIP, ndx5D);
							}
							MasterStack.setC(chn);
							IJ.run(MasterStack, "Enhance Contrast", "saturated=" + ONEsetup.StrechColor); // TODO: new
						}
					}
					MasterStack.getStack().setProcessor(HyperImg[0].getStack().getProcessor(1), 1); // Initialize first image
					MasterStack.setC(1);
					IJ.run(MasterStack, "Enhance Contrast", "saturated=" + ONEsetup.StrechColor);
					Local_CompositeConverter LocalComp = new Local_CompositeConverter();
					LocalComp.StartConverting(MasterStack);
				}
				IJ.run(MasterStack, "Set Scale...", "distance=" + ScaleNumber + " known=" + KnownDist + " unit=&unit");
				IJ.save(MasterStack, OutSRRF + OutName + "-" + ImageName + ".tif");
				if (Calibrate)
				{
					if (!CreateLogFileOnce)
					{
						String[] txt = GetLogFile(Output, true); // locate log file.
						CreateHyperStackLogFile(channels, zSlices, txt); // create Hyperstack compatible log file.
						CreateLogFileOnce = true;
					}
					ChromaticCorrection(MasterStack, BeadsImageName, channels, zSlices, OutSRRF, ImageName);
				}
				MasterStack.changes = false;
				MasterStack.close();
			}
			IJ.run("Close All");
		} catch (FormatException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Start Z analysis.
	 *
	 * @param OImg          2D array of images.
	 * @param HyperImg      Final hyper-image.
	 * @param k             Channel number -1 .
	 * @param z             Z slice number.
	 * @param ChannelNames  Array of channel's names.
	 * @param ImageName     Image title.
	 * @param chn           Channel number.
	 * @param OutResults    Output directory sub directory.
	 * @param OutSRRF       Output directory.
	 * @param SwitchChannel automatically switch drift correction to another channel
	 *                      if the current fails.
	 */
	void zONE(ImagePlus[][] OImg, ImagePlus HyperImg, int k, int z, String[] ChannelNames, String ImageName, int chn, String OutResults,
			String OutSRRF, boolean SwitchChannel)
	{
		int zi = z + 1;
		jump = false;
		if (chn <= channels)
		{
			ChannelNames[z] = ImageName + "_" + chn + "_" + zi;
			String[] PathAndName = { ChannelNames[z], OutResults, OutSRRF };
			ImagePlus Imgz = OImg[k][z].duplicate();
			Imgz.setTitle(ChannelNames[z]);
			ImagePlus ImgSRRF = eSRRFanalysis(Imgz, PathAndName);
			if (!jump)
			{
//				int ndx5D = get5Dindex(chn, zi, 1, 1, zSlices);
				HyperImg.getStack().setProcessor(ImgSRRF.getProcessor(), zi);
				ImgSRRF.changes = false;
				ImgSRRF.close();
			}
			Imgz.changes = false;
			Imgz.close();
		}
	}
	// ======================= #Region ONE Z Analysis =======================
	// ======================= =======================

	// ======================= =======================
	// ======================= #Region experimenting with 5D=======================

	/**
	 * Get log file.
	 *
	 * @param OutPath 2D array of images.
	 * @param close   Close when finish.
	 */
	String[] GetLogFile(String OutPath, boolean close)
	{
		Frame[] LogWins = WindowManager.getNonImageWindows();
		String[] txt = null;
		for (int i = 0; i < LogWins.length; ++i)
		{
			if (LogWins[i] instanceof Editor)
			{
				String temp = ((Editor) LogWins[i]).getText();
				if (!temp.startsWith("Image Stabilizer Log File"))
					continue;
				txt = temp.split("\n");
				String name = LogWins[i].getTitle();
				IJ.selectWindow(name);
				IJ.saveAs("Text", OutPath + name);
				if (close)
				{
					IJ.selectWindow(name);
					IJ.run("Close", "");
				}
			}
		}
		return txt;
	}

	/**
	 * Modify log file to suit 5D hperstack.
	 *
	 * @param chn Number of channels.
	 * @param Zax Number of Z slices.
	 * @param txt Log file text.
	 */
	public void CreateHyperStackLogFile(int chn, int Zax, String[] txt)
	{
		int L_ = chn * Zax;
		int transform = 0;
		int interval = 1;
		Editor eLog = new Editor();
		String name = "HyperStack_CAC_Log";
		eLog.create(name, "Image Stabilizer Log File for " + "\"" + name + "\"\n" + transform + "\n");
		Double[][] values = new Double[chn][2];
		String[] lines = new String[L_ + 1];
		lines[0] = ""; // lines start from [1] not 0;
		for (int i = 0; i < chn; i++)
		{
			String[] fields = txt[i + 2].split(",");
			values[i][0] = Double.parseDouble(fields[2]);
			values[i][1] = Double.parseDouble(fields[3]);
		}
		for (int c = 1; c <= chn; c++)
		{
			for (int z = 1; z <= Zax; z++)
			{
				int ki = get5Dindex(c, z, 1, chn, Zax);
				String temp = Integer.toString(ki) + "," + Integer.toString(interval) + "," + Double.toString(values[c - 1][0]) + ","
						+ Double.toString(values[c - 1][1]);
				lines[ki] = temp;
			}
		}
		for (int i = 1; i <= L_; i++)
		{
			eLog.append(lines[i] + "\n");
		}
	}

	private int get5Dindex(int c, int z, int t, int chn, int zSlices)
	{
		return chn * zSlices * (t - 1) + chn * (z - 1) + c;
	}

	class Local_ContrastEnhancer implements Measurements
	{
		double min;
		double max;

		public double getMin()
		{
			return min;
		}

		public double getMax()
		{
			return max;
		}

		public Local_ContrastEnhancer(ImageProcessor ip, double saturated)
		{
			ImageStatistics stats = ImageStatistics.getStatistics(ip, MIN_MAX, null);
			int[] a = getMinAndMax(ip, saturated, stats);
			int hmin = a[0], hmax = a[1];

			min = stats.histMin + hmin * stats.binSize;
			max = stats.histMin + hmax * stats.binSize;
			if (stats.histogram16 != null && ip instanceof ShortProcessor)
			{
				min = hmin;
				max = hmax;
			}
			if (min > max)
			{
				double temp = min;
				min = max;
				max = temp;
			}
		}

		int[] getMinAndMax(ImageProcessor ip, double saturated, ImageStatistics stats)
		{
			int hmin, hmax;
			int threshold;
			int[] histogram = stats.histogram;
			if (stats.histogram16 != null && ip instanceof ShortProcessor)
				histogram = stats.histogram16;
			int hsize = histogram.length;
			if (saturated > 0.0)
				threshold = (int) (stats.pixelCount * saturated / 200.0);
			else
				threshold = 0;
			int i = -1;
			boolean found = false;
			int count = 0;
			int maxindex = hsize - 1;
			do
			{
				i++;
				count += histogram[i];
				found = count > threshold;
			} while (!found && i < maxindex);
			hmin = i;

			i = hsize;
			count = 0;
			do
			{
				i--;
				count += histogram[i];
				found = count > threshold;
			} while (!found && i > 0);
			hmax = i;
			int[] a = new int[2];
			a[0] = hmin;
			a[1] = hmax;
			return a;
		}
	}
	// ======================= #Region experimenting with 5D =======================
	// ======================= =======================
}
