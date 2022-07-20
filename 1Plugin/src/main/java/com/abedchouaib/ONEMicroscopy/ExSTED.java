package com.abedchouaib.ONEMicroscopy;

/**
 * -------------------------------------------------------------------------------------------
 * under development.
 * 
 * @author Abed Chouaib
 * @version 1.0.0
 * -------------------------------------------------------------------------------------------
 */
import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import com.abedchouaib.ONEMicroscopy.gui.ExSTEDWindow;
import com.abedchouaib.ONEMicroscopy.gui.InputData;
import com.abedchouaib.ONEMicroscopy.gui.MainWindow;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import loci.formats.FormatException;
import loci.plugins.BF;

public class ExSTED
{
	private int channels;
	private String InputDirPath = ExSTEDWindow.ImportDirPath;
	private String BeadsDirPath = ExSTEDWindow.BeadsDirPath;
	private String OutputDir;
	private String OutputBeadsDir;
	private String BeadsImageName = "";
	private boolean Calibrate = ExSTEDWindow.Calibrate;
	private ArrayList<String> StackDirectories = ExSTEDWindow.StackDirectories;
	public static final String MACRO_CANCELED = "Macro canceled";
	private boolean SaveParentFolder = ExSTEDWindow.SaveParentFolder;
	private String DefaultSavePath = ExSTEDWindow.OutPutDirectory;

	// ************************** #Region Progress Bar **************************
	boolean Cancel = false;
	static JFrame frame = new JFrame();
	JProgressBar progressBar = new JProgressBar();
	JLabel progressStatusLabel = new JLabel();
	// ========================== #End Progress Bar ==========================

	public void run(String arg)
	{
		runAnalysis();
	}

	// ========================== ==========================
	private void runAnalysis()
	{

		// ************************** #Region System IO **************************
		if (Calibrate)
		{
			File BeadsFolder = new File(BeadsDirPath);
			if (SaveParentFolder)
			{
				OutputBeadsDir = BeadsFolder.getParent() + File.separator + "Output " + BeadsFolder.getName() + File.separator;
			} else
			{
				OutputBeadsDir = DefaultSavePath + File.separator + "Output " + BeadsFolder.getName() + File.separator;
			}
			CreateDir(OutputBeadsDir);
			File[] BeadsList = BeadsFolder.listFiles();
			StartAnalysis(BeadsList, 0, OutputBeadsDir, true);
		}
		File folder = new File(InputDirPath);
		if (SaveParentFolder)
		{
			OutputDir = folder.getParent() + File.separator + "Output " + folder.getName() + File.separator;
		} else
		{
			OutputDir = DefaultSavePath + File.separator + "Output " + folder.getName() + File.separator;
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
		for (int i = 0; i < FileList.length; i++)
		{
			if (!Cancel)
			{
				StartAnalysis(FileList, i, OutputDir, false);
			}
			UpdateProgressBar(i);
		}
		frame.dispose();
		IJ.showMessage("Analysis is Complete!");
		// ========================== #End Active loop ==========================
	}
	// ========================== ==========================

	// ************************** **************************
	// ************************** #Region Methods **************************
	private void StartAnalysis(File[] FileList, int i, String Output, boolean GenerateLog)
	{
		String Path = FileList[i].getPath();
		try
		{
			ImagePlus[] OImgArray = BF.openImagePlus(Path);
			ImagePlus OImg = OImgArray[0].duplicate();
			// (width[0], height[1], nChannels[2],nSlices[3], nFrames[4]).
//			int[] ImDimension = OImgArray[0].getDimensions(); // 0 get first series.
			InputData IData;
			try
			{
				IData = new InputData(Path, InputData.ImportType.Vi, 1);
				AutomateUserInput(IData); // adjust inputs to conditions.
			} catch (Exception e)
			{
				e.printStackTrace();
			}
//			ImagePlus[] OImg = new ImagePlus[ImDimension[2]];
//			for (int k = 0; k < OImg.length; k++)
//			{
//				int chn = k + 1;
//				if (chn <= channels)
//				{
//					OImg[k] = new Duplicator().run(OImgArray[0], chn, chn, 1, 1, 1, ImDimension[4]);
//				}
//			}
			String ImageName = FileList[i].getName();
			String FileExt = GetFileExtension(ImageName);
			if (FileExt != null)
			{
				ImageName = ImageName.replace(FileExt, "");
			}
//			String[] ChannelNames = new String[ImDimension[2]];
			ImageName = i + "-" + ImageName;
			// ------------------------- Creating Directories -------------------------
			String OutSubFolder = Output + ImageName + File.separator;
			CreateDir(OutSubFolder); // 2nd level subfolder
			// ------------------------- end -------------------------
			if (GenerateLog)
			{
				// Generate optical flow log file and get its name.
				BeadsImageName = GenerateOpticalFlowLogFile(OImgArray[0], channels, 1);
			}
			if (Calibrate)
			{
				// Performing chromatic correction for the final image.
				ChromaticCorrection(OImg, BeadsImageName, channels, 1, OutSubFolder, ImageName);
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

	private String GenerateOpticalFlowLogFile(ImagePlus Beads, int chn, int length)
	{
		// ------------------------- Computing log file -------------------------
		IJ.run(Beads, "Hyperstack to Stack", "");
		Beads.setSlice(chn);
		String Options = "";
		Options = Options + "transformation=Translation maximum_pyramid_levels=0";
		Options = Options + " template_update_coefficient=0.90 maximum_iterations=400";
		Options = Options + " error_tolerance=0.0000001 log_transformation_coefficients";
		Beads.show();
		IJ.selectWindow(Beads.getTitle());
		IJ.run(Beads, "Image Stabilizer", Options);
		Options = "";
		Options = Options + "order=xyczt(default) channels=" + chn + " slices=" + 1; // length instead of 1
		Options = Options + " frames=1 display=Composite";
		IJ.run(Beads, "Stack to Hyperstack...", Options);
		return Beads.getTitle();
		// ------------------------- end -------------------------
	}

	private void ChromaticCorrection(ImagePlus imp, String Name, int chn, int length, String Path, String ImageName)
	{
		// ------------------------- Calibrating -------------------------
		String Options = "";
		Options = Options + "order=xyczt(default) channels=" + chn + " slices=" + 1;
		Options = Options + " frames=1 display=Composite";
		IJ.run(imp, "Hyperstack to Stack", "");
//		Window WinLog = WindowManager.getWindow(Name + ".log");
		imp.show();
		IJ.selectWindow(imp.getTitle());
		IJ.run(imp, "Image Stabilizer Log Applier", " ");
		IJ.run(imp, "Stack to Hyperstack...", Options);
		String name = "Calibrated_" + ImageName;
		imp.setTitle(name);
		IJ.save(imp, Path + name + ".tif");
//		IJ.saveAs("Text", "OutPath" + Name + ".Log.txt");
//		WinLog.dispose();
		// ------------------------- end -------------------------
	}

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

	void AutomateUserInput(InputData IData)
	{
//		double[] Resolution = IData.GetResolutionsXYZ();
		int nChannels = IData.GetnChannels();
//		int nFrames = IData.GetnFrames();
		channels = MainWindow.ChnNum == 0 ? nChannels : MainWindow.ChnNum;
	}

//	private void ColorizeChannel(ImagePlus imp, int chn)
//	{
//		switch (chn)
//		{
//		case 1:
//			IJ.run(imp, "Red", "");
//			break;
//		case 2:
//			IJ.run(imp, "Green", "");
//			break;
//		case 3:
//			IJ.run(imp, "Cyan", "");
//			break;
//		case 4:
//			IJ.run(imp, "Magenta", "");
//			break;
//		case 5:
//			IJ.run(imp, "Yellow", "");
//			break;
//		case 6:
//			IJ.run(imp, "Blue", "");
//			break;
//		default:
//			IJ.run(imp, "Grays", "");
//			break;
//		}
//	}

	private String GetFileExtension(String name)
	{
		String[] temp = name.split("\\.");
		if (temp.length == 0)
		{
			return null;
		}
		String ext = "." + temp[temp.length - 1];
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

	// ======================= #Region Progress Bar Window =======================
	public void ShowProgressBarWindow()
	{
		frame.setVisible(true);
		frame.setResizable(false);
		progressStatusLabel = new JLabel();
		progressStatusLabel.setText("Analyzing stack please wait...");
		frame.setBounds(100, 100, 320, 160);
		frame.getContentPane().setLayout(null);
		frame.setTitle("Status Window");

//		ClassLoader cl = this.getClass().getClassLoader();
//		ImageIcon mainWindoIcon = new ImageIcon(cl.getResource("ONE_icon_64.png"));
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
				try
				{
					terminateThread();
				} catch (AWTException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				progressStatusLabel.setText("Canceling in progress...");
				Cancel = true;
				try
				{
					terminateThread();
				} catch (AWTException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}

	static void terminateThread() throws AWTException
	{
		frame.dispose();
		try
		{
			String title = WindowManager.getCurrentImage().getTitle();
			IJ.selectWindow(title);
		} catch (Exception e)
		{
		}
		for (int i = 0; i < 10; i++) // Iterate over thread Set
		{
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_ESCAPE);
			robot.delay(50);
			robot.keyRelease(KeyEvent.VK_ESCAPE);
			robot.delay(50);
		}
	}
//	static void terminateThread()
//	{
//		Set<Thread> setOfThread = Thread.getAllStackTraces().keySet();
//		for (Thread thread : setOfThread) // Iterate over thread Set
//		{
//			ij.Macro.abort();
//			if (Thread.currentThread().getName().endsWith("Macro$"))
//			{
//				thread.interrupt();
//				throw new RuntimeException(MACRO_CANCELED);
//			}
//		}
//	}

	public void SetPrograssBarMax(int Max)
	{
		progressBar.setMaximum(Max);
	}

	public void UpdateProgressBar(int value)
	{
		progressBar.setValue(value);
	}
	// ======================= #Region Progress Bar Window =======================
}
