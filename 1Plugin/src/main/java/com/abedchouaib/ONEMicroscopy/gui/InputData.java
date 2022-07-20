package com.abedchouaib.ONEMicroscopy.gui;

/**
 * -------------------------------------------------------------------------------------------
 * This class is used to import, check and sort data.
 * This class uses Bio-Formats to read images Metadata. 
 * 
 * Bio-Formats citation DOI: 10.1083/jcb.201004104 PMID: 20513764 PMCID: PMC2878938
 * 
 * @author Abed Chouaib
 * @version 1.0.0
 * -------------------------------------------------------------------------------------------
 */
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import loci.formats.FormatException;
import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import ome.units.quantity.Length;
import ome.units.quantity.Time;

public class InputData
{

	boolean Cancel = false;
	JFrame frame = new JFrame();
	JButton CancelButton;
	JLabel progressStatusLabel = new JLabel();
	JLabel loaderImg = new JLabel("");
	private ArrayList<String> FilePath;
	private double ResolutionX;
	private double ResolutionY;
	private double ResolutionZ;
	private int nChannels;
	private int zSlices;
	private int nFrames;
	private String Unit;
	private double time;
	private int imNum;

	public static enum ImportType
	{
		Vi, MVi, ImgS, ets
	}

	public InputData(int imnum)
	{
		this.imNum = imnum;
	}

	public InputData(String path, ImportType DataType, int imnum) throws Exception
	{
		this.imNum = imnum;
		switch (DataType)
		{
		case Vi:
			FilePath = CheckVideo(path);
			break;
		case MVi:
			FilePath = CheckForVideos(path);
			break;
		default:
			break;
		}
	}

	public ArrayList<String> GetListofFiles()
	{
		return FilePath;
	}

	public double[] GetResolutionsXYZ()
	{
		double[] args = { ResolutionX, ResolutionY, ResolutionZ };
		return args;
	}

	public int GetnChannels()
	{
		return nChannels;
	}

	public int GetzSlices()
	{
		return zSlices;
	}

	public int GetnFrames()
	{
		return nFrames;
	}

	public String GetUnit()
	{
		return Unit;
	}

	public double GetTime()
	{
		return time;
	}

	public ArrayList<String> CheckVideo(String Path) throws Exception
	{
		ArrayList<String> FilePath = new ArrayList<String>();
		initialiseReader IR = new initialiseReader(Path);
		ImageReader reader = IR.getReader();
//		nFrames = reader.getImageCount();
		nFrames = reader.getSizeT();
		zSlices = reader.getSizeZ();
		nChannels = reader.getSizeC();
		if (nFrames == 1) // switch zSlice if it was inverted.
		{
			nFrames = zSlices;
			zSlices = 1;
		}
		if (nFrames >= imNum)
		{
			IMetadata omeMeta = IR.getIMetadata();
			GetImportantValues(omeMeta);
			FilePath.add(Path);
		}
		return FilePath;
	}

	public ArrayList<String> CheckForVideos(String Path) throws Exception
	{
		ArrayList<String> FilesPath = new ArrayList<String>();
		ArrayList<File> files = new ArrayList<File>();
		GetFiles(Path, files);
		int count = 0;
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				ShowProgressBarWindow();
			}
		});
		for (int i = 0; i < files.size(); i++)
		{
			if (Cancel)
			{
				FilesPath.clear();
				break;
			}
			if (files.get(i).length() > 100000)
			{
				initialiseReader IR = new initialiseReader(files.get(i).getPath());
				ImageReader reader = IR.getReader();
				int NumFrames = reader.getImageCount();
				if (NumFrames >= imNum)
				{
					if (count == 0)
					{
						IMetadata omeMeta = IR.getIMetadata();
						nFrames = NumFrames;
						zSlices = reader.getSizeZ();
						nChannels = reader.getSizeC();
						if (nFrames == 1) // switch zSlice if it was inverted.
						{
							nFrames = zSlices;
							zSlices = 1;
						}
						GetImportantValues(omeMeta);
					}
					FilesPath.add(files.get(i).getPath());
					count++;
				}
			}
			if (files.size() > 1 && i == files.size() - 1)
			{
				progressStatusLabel.setText("Done!");
				loaderImg.setVisible(false);
				CancelButton.setText("Close");
			}
		}
		Thread.sleep(200);
		frame.dispose();
		return FilesPath;
	}

	// ======================= Get Metadata =======================
	private void GetImportantValues(IMetadata omeMeta)
	{
		Length dpiX = omeMeta.getPixelsPhysicalSizeX(0);
		Length dpiY = omeMeta.getPixelsPhysicalSizeY(0);
		Length dpiZ = omeMeta.getPixelsPhysicalSizeZ(0);
		Time tempx = null;
		try
		{
			tempx = omeMeta.getPlaneDeltaT(0, nChannels);
		} catch (Exception e)
		{
			System.out.println("no timestamp detected!");
		}

//		nChannels = omeMeta.getChannelCount(0);
		ResolutionX = dpiX == null ? 1 : (double) dpiX.value();
		ResolutionY = dpiY == null ? 1 : (double) dpiY.value();
		ResolutionZ = dpiZ == null ? 1 : (double) dpiZ.value();
		time = tempx == null ? 1 : (double) tempx.value();
//		Unit = omeMeta.
	}

	// ======================= end =======================
	// ======================= Crop Mask Creation =======================
	public int[] GetCropMaskPosition(ImagePlus imp, int userIn)
	{
		int width = imp.getWidth();
		int height = imp.getHeight();
		int[] Results = new int[5];
		int y = height - (int) Math.floor(height / 12);
		int x = 0;
		int[] Values1 = { x, y, (int) Math.floor(width), (int) Math.floor(height / 12), userIn };
		int[] Values2 = { x, 0, (int) Math.floor(width), (int) Math.floor(height / 12), userIn };
		Double Mean1 = GetStackMean(imp, Values1, 1);
		Double Mean2 = GetStackMean(imp, Values2, 1);
		if (Mean1 > Mean2)
		{
			Results = Values1;
		} else
		{
			Results = Values2;
		}
		return Results;
	}

	public Double GetStackMean(ImagePlus imp, int[] Mask, int frame)
	{
		Double FinalValues = 0.0;
		ResultsTable rtTemp = new ResultsTable();
		ImagePlus impTemp = new ImagePlus("Slice_" + Integer.toString(frame), imp.getStack().getProcessor(frame));
		impTemp.setRoi(Mask[0], Mask[1], Mask[2], Mask[3]);
		impTemp.updateAndDraw();
		int measurements = Measurements.MEAN;
		Analyzer analyzerTemp = new Analyzer(impTemp, measurements, rtTemp);
		analyzerTemp.measure();
		FinalValues = rtTemp.getValue("Mean", 0);
		impTemp.changes = false;
		impTemp.close();
		return FinalValues;
	}
	// ======================= Crop Mask Creation =======================

	public void GetFiles(String Path, ArrayList<File> files)
	{
		File directory = new File(Path);
		File[] fList = directory.listFiles();
		if (fList != null)
		{
			for (File file : fList)
			{
				if (file.isFile())
				{
					files.add(file);
				} else if (file.isDirectory())
				{
					GetFiles(file.getAbsolutePath(), files);
				}
			}
		}
	}

	public void printLine(ArrayList<String> data)
	{
		for (int i = 0; i < data.size(); i++)
		{
			System.out.println(data.get(i));
		}
	}

	private void ShowProgressBarWindow()
	{
		frame.setVisible(true);
		frame.setResizable(false);
//		progressStatusLabel.setText("Importing videos please wait...");
		frame.setBounds(100, 100, 350, 100);
		frame.getContentPane().setLayout(null);
		frame.setTitle("Importing videos please wait...");

//		ImageIcon loader = new ImageIcon(this.getClass().getResource(""));
//		frame.getContentPane().add(loader);

//		ClassLoader cl = this.getClass().getClassLoader();
//		ImageIcon mainWindoIcon = new ImageIcon(cl.getResource("DART_icon_64.png"));
//		frame.setIconImage(mainWindoIcon.getImage());

//		JLabel LoaderImg = new JLabel(" ");
//		frame.getContentPane().add(LoaderImg);

		CancelButton = new JButton("Cancel");
		CancelButton.setBounds(50, 30, 90, 25);
		frame.getContentPane().add(CancelButton);
		// progressStatusLabel = new JLabel("Processing data please wait...");
//		progressStatusLabel.setBounds(25, 36, 207, 14);
//		frame.getContentPane().add(progressStatusLabel);

//		Image img = new ImageIcon(cl.getResource("loading_32.gif")).getImage();
//		loaderImg.setIcon(new ImageIcon(img));
//		loaderImg.setBounds(206, 22, 45, 45);
//		frame.getContentPane().add(loaderImg);

		Icon imgIcon = new ImageIcon(this.getClass().getResource("/loading_32.gif"));
		JLabel ImGif = new JLabel(imgIcon);
		ImGif.setBounds(180, 20, 45, 45);
		frame.getContentPane().add(ImGif);

		CancelButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				Cancel = true;
				frame.dispose();
			}
		});
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				Cancel = true;
				// progressStatusLabel.setText("Canceling in progress...");
				frame.dispose();
			}
		});
//		frame.setUndecorated(true);
	}

	void printArray(int[] data)
	{
		for (int i = 0; i < data.length; i++)
		{
			System.out.println(data[i]);

		}
	}

	// ======================= #Region Classes =======================
	final class initialiseReader
	{
		private final String fileName;
		private final ImageReader reader = new ImageReader();
		private IMetadata omeMeta;

		public initialiseReader(String Filename) throws FormatException, IOException
		{
			this.fileName = Filename;
			omeMeta = MetadataTools.createOMEXMLMetadata();
			reader.setMetadataStore(omeMeta);
			try
			{
				reader.setId(fileName);
			} catch (Exception e)
			{
				IJ.log("Bio-Formats, reading parent file path error, please change file path");
			}
			reader.setSeries(0);
		}

		public ImageReader getReader()
		{
			return reader;
		}

		public IMetadata getIMetadata()
		{
			return omeMeta;
		}
	}

}