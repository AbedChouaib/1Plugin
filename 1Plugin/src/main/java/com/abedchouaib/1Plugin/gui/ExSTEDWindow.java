package com.abedchouaib.ONEMicroscopy.gui;

/**
 * -------------------------------------------------------------------------------------------
 * This class is under development.
 * 
 * @author Abed Chouaib
 * @version 1.0.0
 * -------------------------------------------------------------------------------------------
 */
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.abedchouaib.ONEMicroscopy.ExSTED;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.FolderOpener;
import ij.plugin.PlugIn;
import loci.plugins.BF;

public class ExSTEDWindow implements PlugIn
{
	private JFrame frmOneMicroscopy;
	private JTextField SavePathF;
	private JTextField ImportF;
	private JLabel CropMask;
	private Checkbox CheckBox_Deconv;
	private JLabel TopStatusLabel;
	private JButton CreateMaskButton;
	static int StackLength = 0; // width = 0, height = 0;
	public static String ImportDirPath, SaveDirPath, BeadsDirPath;
	private boolean ImageDetected = false;
	static boolean MultiImageDetected = false;
	public String roiMaskPath, ImagesSpotsPath, CSVfilesPath;
	public int[] MaskCropValues = { 0, 0, 0, 0 };

	private boolean SaveButtonGaurd = true;
	public static ArrayList<String> StackDirectories = new ArrayList<String>();
	public static String OutPutDirectory;
	private static int StackNumber;
	static ArrayList<int[]> CropMaskListValues = new ArrayList<int[]>();
	ArrayList<int[]> CropMaskListValuesReset = new ArrayList<int[]>();
	static Choice CropMaskListGUI = new Choice();
	static ArrayList<String> SubFolderNameList = new ArrayList<String>();
	private Checkbox CheckBoxVideosIn = new Checkbox();
	static boolean TheInputIsVideos = true;
	static int stackLoopNum;
	static int NumberOfStacks;
	static boolean ImagePresent = false;
	static boolean lockMaskBut = false;
	static boolean OneVideo = true;
	public static boolean Calibrate;
	static int BeadsChannels;
	public static boolean SaveParentFolder = true;
	// ============ Metadata ============
	double[] Resolution = { 0, 0, 0 };
	int nChannels = 1;
	int zSlices;
	int nFrames;
	// ============ end ============
	String[] SRRFTypes = new String[4];
	String username;
	private ImagePlus OriginalImp;
	boolean lockImportField = false;
//	File DARTconfig;

	InputData.ImportType DataType = InputData.ImportType.MVi;

	private JTextField txtF_CAC;

	// main method will not be called inside ImageJ, Java editors testing only!
	public static void main(String[] args) throws Exception
	{
		Class<?> clazz = ExSTEDWindow.class;
		java.net.URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
		File file = new File(url.toURI());
		System.setProperty("plugins.dir", file.getAbsolutePath());
		new ImageJ();
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					ExSTEDWindow window = new ExSTEDWindow();
					window.initialize();
					window.frmOneMicroscopy.setVisible(true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

//	public MainWindow()
//	{
//		initialize();
//	}

	// ==================================================================================================
	// ==================================================================================================
	// <<<<<<<<<<>>>>>>>>>> GUI JFrame <<<<<<<<<<>>>>>>>>>>
	public void initialize()
	{
		SRRFTypes[0] = "Temporal Radiality Maximum (TRM - activate in high-magnification)";
		SRRFTypes[1] = "Temporal Radiality Average (TRA - default)";
		SRRFTypes[2] = "Temporal Radiality Pairwise Product Mean (TRPPM)";
		SRRFTypes[3] = "Temporal Radiality Auto-Correlations (TRAC)";
		username = System.getProperty("user.home"); // get the user.name

		// load nanoJ SRRF parameters.
		frmOneMicroscopy = new JFrame();
		frmOneMicroscopy.setResizable(false);
		frmOneMicroscopy.setBackground(Color.LIGHT_GRAY);
		frmOneMicroscopy.getContentPane().setBackground(SystemColor.control);
		frmOneMicroscopy.setTitle("ExSTED");
//		ClassLoader cl = this.getClass().getClassLoader();
//		ImageIcon mainWindoIcon = new ImageIcon(cl.getResource("DART_icon_64.png"));
//		Frame.setIconImage(mainWindoIcon.getImage());
		frmOneMicroscopy.setBounds(100, 100, 600, 300);
		frmOneMicroscopy.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmOneMicroscopy.getContentPane().setLayout(null);
		frmOneMicroscopy.setLocationRelativeTo(null);

		SavePathF = new JTextField();
		SavePathF.setEnabled(false);
		SavePathF.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent arg0)
			{
				SavePathF.setBackground(Color.WHITE);
			}
		});
		if (username != null)
		{
			String CurrentTime = "" + java.time.LocalDateTime.now();
			SavePathF.setText(username + File.separator + "Desktop" + File.separator + "ONE Microscopy" + File.separator + "Analysis "
					+ CurrentTime.replace(":", "-"));
		}
		SavePathF.setToolTipText("");
		SavePathF.setColumns(10);
		SavePathF.setBounds(10, 59, 313, 20);
		frmOneMicroscopy.getContentPane().add(SavePathF);
		ImportF = new JTextField();

		ImportF.setToolTipText("");
		ImportF.setColumns(10);
		ImportF.setBounds(10, 26, 313, 20);
		frmOneMicroscopy.getContentPane().add(ImportF);

		JButton ImportFolderButton = new JButton("Import ");
		ImportFolderButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				if (DataType == InputData.ImportType.Vi)
				{
					ImportDirPath = IJ.getFilePath("Select Input Video...");
				} else
				{
					ImportDirPath = IJ.getDirectory("Select Input Folder...");
				}
				if (ImportDirPath != null)
				{
					ImportF.setText(ImportDirPath);
					LoadImportedData();
				}
			}
		});
		ImportFolderButton.setBounds(343, 25, 89, 23);
		frmOneMicroscopy.getContentPane().add(ImportFolderButton);

		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel.setBackground(UIManager.getColor("Button.background"));
		panel.setBounds(10, 133, 561, 127);
		frmOneMicroscopy.getContentPane().add(panel);
		panel.setLayout(null);

		Checkbox CheckBox_CAC = new Checkbox("Chromatic aberration correction");
		CheckBox_CAC.setState(true);
		CheckBox_CAC.setFont(new Font("Tahoma", Font.PLAIN, 11));
		CheckBox_CAC.setBounds(10, 10, 215, 22);
		panel.add(CheckBox_CAC);

		CropMask = new JLabel("Point Spread Function");
		CropMask.setEnabled(false);
		CropMask.setBounds(10, 96, 153, 24);
		panel.add(CropMask);

		CheckBox_Deconv = new Checkbox("Deconvolve image");
		CheckBox_Deconv.setFont(new Font("Tahoma", Font.PLAIN, 11));
		CheckBox_Deconv.setBounds(10, 70, 153, 22);
		panel.add(CheckBox_Deconv);

		JButton StartButton = new JButton("Start");
		StartButton.setBounds(462, 97, 89, 23);
		panel.add(StartButton);

		CreateMaskButton = new JButton("Create Mask");
		CreateMaskButton.setEnabled(false);
		CreateMaskButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				Thread OpenCropMaskWindow = new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							if (!lockMaskBut)
							{
								lockMaskBut = true;
								CreateMaskSelection();
							}
						} catch (Exception e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						} // TODO:
					}
				});
				OpenCropMaskWindow.start();
			}
		});
		CreateMaskButton.setBounds(131, 97, 136, 23);
		panel.add(CreateMaskButton);
		CropMaskListGUI.setEnabled(false);

		// CropMaskListGUI.add("0,0,0,0");
		CropMaskListGUI.setBounds(273, 100, 136, 20);
		panel.add(CropMaskListGUI);

		txtF_CAC = new JTextField();
		txtF_CAC.setBounds(10, 39, 313, 20);
		panel.add(txtF_CAC);
		txtF_CAC.setToolTipText("");
		txtF_CAC.setColumns(10);

		JButton Import_CAC = new JButton("Import Beads");
		Import_CAC.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent e)
			{
				BeadsDirPath = IJ.getDirectory("Select Input Folder...");
//				BeadsChannels
				if (BeadsDirPath != null)
				{
					txtF_CAC.setText(BeadsDirPath);
				}
			}
		});
		Import_CAC.setBounds(343, 38, 121, 23);
		panel.add(Import_CAC);

		TopStatusLabel = new JLabel("");
		TopStatusLabel.setFont(new Font("Tahoma", Font.PLAIN, 10));
		TopStatusLabel.setForeground(SystemColor.textHighlight);
		TopStatusLabel.setBounds(10, 0, 412, 24);
		frmOneMicroscopy.getContentPane().add(TopStatusLabel);

		JButton SaveButton = new JButton("Save To");
		SaveButton.setEnabled(false);
		SaveButton.setBounds(343, 58, 89, 23);
		frmOneMicroscopy.getContentPane().add(SaveButton);

		CheckBoxVideosIn = new Checkbox("Multi Videos");
		CheckBoxVideosIn.setState(true);
		CheckBoxVideosIn.setBounds(453, 39, 97, 23);
		frmOneMicroscopy.getContentPane().add(CheckBoxVideosIn);

		Checkbox CheckBoxImages = new Checkbox("Olympus ets");
		CheckBoxImages.setEnabled(false);
		CheckBoxImages.setBounds(453, 68, 131, 23);
		frmOneMicroscopy.getContentPane().add(CheckBoxImages);

		Checkbox CheckBoxOneVideo = new Checkbox("Video");
		CheckBoxOneVideo.setBounds(453, 10, 97, 23);
		frmOneMicroscopy.getContentPane().add(CheckBoxOneVideo);

		Checkbox CheckBox_SaveParentFolder = new Checkbox("Save in parent folder");
		CheckBox_SaveParentFolder.setBounds(453, 97, 125, 22);
		frmOneMicroscopy.getContentPane().add(CheckBox_SaveParentFolder);
		CheckBox_SaveParentFolder.setState(true);
		CheckBox_SaveParentFolder.setFont(new Font("Tahoma", Font.PLAIN, 11));
		CheckBox_SaveParentFolder.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if (CheckBox_SaveParentFolder.getState() == false)
				{
					SavePathF.setEnabled(true);
					SaveButton.setEnabled(true);
				} else
				{
					SavePathF.setEnabled(false);
					SaveButton.setEnabled(false);
				}
			}
		});
		CheckBoxOneVideo.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				CheckBoxImages.setState(!CheckBoxOneVideo.getState());
				CheckBoxVideosIn.setState(!CheckBoxOneVideo.getState());
				TheInputIsVideos = !CheckBoxOneVideo.getState();
				OneVideo = true;
				DataType = InputData.ImportType.Vi;
			}
		});

		CheckBoxVideosIn.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				CheckBoxImages.setState(!CheckBoxVideosIn.getState());
				CheckBoxOneVideo.setState(!CheckBoxVideosIn.getState());
				TheInputIsVideos = CheckBoxVideosIn.getState();
				OneVideo = false;
				DataType = InputData.ImportType.MVi;
			}
		});
		CheckBoxImages.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				CheckBoxVideosIn.setState(!CheckBoxImages.getState());
				CheckBoxOneVideo.setState(!CheckBoxImages.getState());
				TheInputIsVideos = CheckBoxVideosIn.getState();
				OneVideo = false;
				DataType = InputData.ImportType.ImgS;
			}
		});

		SaveButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				SaveDirPath = IJ.getDirectory("Select OutPut Folder...");
				if (SaveDirPath != null)
				{
					SavePathF.setText(SaveDirPath);
					OutPutDirectory = SaveDirPath;
					// CreateDir(SaveDirPath);
					SaveButtonGaurd = false;
				}
			}
		});
		CheckBox_CAC.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					txtF_CAC.setEnabled(true);
					Import_CAC.setEnabled(true);
				} else if (e.getStateChange() == ItemEvent.DESELECTED)
				{
					txtF_CAC.setEnabled(false);
					Import_CAC.setEnabled(false);
				}
			}
		});

		// ====================== Search for Images ======================
//		loadImages();
		// ====================== Search for Images ======================
		ImportF.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent arg0)
			{
				ImportF.setBackground(Color.WHITE);
				lockImportField = true;
			}

			@Override
			public void focusLost(FocusEvent e)
			{
				if (!lockImportField)
				{
					LoadImportedData();
				}
			}

		});
		ImportF.getDocument().addDocumentListener(new DocumentListener()
		{
			public void changedUpdate(DocumentEvent e)
			{
			}

			public void removeUpdate(DocumentEvent e)
			{
			}

			public void insertUpdate(DocumentEvent e)
			{
				lockImportField = false;
			}
		});
		// ====================== Search for Images ======================
		// ====================== Search for Images ======================
		frmOneMicroscopy.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent evt)
			{
				char c = evt.getKeyChar();
				if (c == KeyEvent.VK_ENTER)
				{
//					RefreshGUI();
				}
			}
		});
		frmOneMicroscopy.getContentPane().addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent arg0)
			{
//				RefreshGUI();
			}
		});
		// ==================== Start Program
		// ====================================================================================
		StartButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				Thread StartProgram = new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						if (SavePathF.getText() != null && !SavePathF.getText().isEmpty() || SaveParentFolder)
						{
							if (ImageDetected)
							{
								frmOneMicroscopy.dispose();
								if (SaveButtonGaurd)
								{
									if (username != null)
									{
										File OutDir = new File(
												username + File.separator + "Desktop" + File.separator + "DART Analysis" + File.separator);
										// File AnalysisDir = new File(SavePathF.getText());
										if (!OutDir.exists())
										{
											OutDir.mkdir();
										}
									}
									// CreateDir(SavePathF.getText());
									OutPutDirectory = SavePathF.getText();
								}
								String temp1 = txtF_CAC.getText();
								BeadsDirPath = temp1;
								String temp2 = ImportF.getText();
								ImportDirPath = temp2;
								Calibrate = CheckBox_CAC.getState();
								stackLoopNum = 0;
								SaveParentFolder = CheckBox_SaveParentFolder.getState();

								NumberOfStacks = StackDirectories.size(); // <----------------- Start Analysis
								ExSTED cl = new ExSTED();
								cl.run(null);

							} else
							{
								ImportF.setBackground(Color.decode("#ff5555"));
							}
						} else
						{
							SavePathF.setBackground(Color.decode("#ff5555"));
						}
					}
				});
				StartProgram.start();
			}
		});
		// ==================== Start Program
		// ====================================================================================
	}
	// <<<<<<<<<<>>>>>>>>>> GUI JFrame <<<<<<<<<<>>>>>>>>>>
	// ==================================================================================================
	// ==================================================================================================

	// ================================ Methods ================================

	public void CreateDir(String OutPutDirectory)
	{
		roiMaskPath = OutPutDirectory + "/Mask Selection";
		ImagesSpotsPath = OutPutDirectory + "/Image Signals";
		CSVfilesPath = OutPutDirectory + "/Signals Data";
		File roiMaskDirectory = new File(roiMaskPath);
		File imagesDirectoryFile = new File(ImagesSpotsPath);
		File CSVfilesDirectory = new File(CSVfilesPath);
		if (!roiMaskDirectory.exists())
		{
			roiMaskDirectory.mkdir();
		}
		if (!imagesDirectoryFile.exists())
		{
			imagesDirectoryFile.mkdir();
		}
		if (!CSVfilesDirectory.exists())
		{
			CSVfilesDirectory.mkdir();
		}
	}

	// ============================= Run DART ===============================
	// ============================= Run DART ===============================
	public void run(String arg)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					ExSTEDWindow window = new ExSTEDWindow();
					window.initialize();
					window.frmOneMicroscopy.setVisible(true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	// ============================= Run DART ===============================
	// ============================= Run DART ===============================

	public int[] SplitCommaReturn(String str)
	{
		String[] array = str.split(",", 0);
		int[] FinalArray = new int[array.length];
		if (array.length == 4)
		{
			for (int i = 0; i < FinalArray.length; i++)
			{
				try
				{
					FinalArray[i] = Integer.parseInt(array[i]);
				} catch (Exception e)
				{
				}
			}
		}
		return FinalArray;
	}

	// ================================ Methods ================================
	public void printLine(String[] data)
	{
		for (int i = 0; i < data.length; i++)
		{
			System.out.println(data[i]);
		}
	}

	public void printLine(ArrayList<String> data)
	{
		for (int i = 0; i < data.size(); i++)
		{
			System.out.println(data.get(i));
		}
	}

	public ArrayList<String> CheckDirectory(String Path) // TODO write to InputData class
	{
		ArrayList<String> ImgDirectories = new ArrayList<String>();
		SubFolderNameList.clear();
		switch (DataType)
		{
		case Vi:

			try
			{
				InputData IData = new InputData(Path, DataType, 1);
				ImgDirectories = IData.GetListofFiles();
				ReadMetaData(IData);
			} catch (Exception e1)
			{
				e1.printStackTrace();
			}
			break;

		case MVi:
			try
			{
				InputData IData = new InputData(Path, DataType, 1);
				ImgDirectories = IData.GetListofFiles();
				ReadMetaData(IData);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			if (ImgDirectories != null)
			{
				for (int i = 0; i < ImgDirectories.size(); i++)
				{
					File file = new File(ImgDirectories.get(i));
					SubFolderNameList.add(file.getName());
				}
			}
			break;

		case ImgS:
			boolean MultiStack = false;
			File newDir = new File(Path);
			if (newDir.isDirectory())
			{
				String[] files = newDir.list();
				for (String file : files)
				{
					File TempFile = new File(Path + File.separator + file);
					if (TempFile.isDirectory())
					{
						OriginalImp = FolderOpener.open(TempFile.getPath(), "virtual");
						StackLength = OriginalImp.getStackSize();
						if (StackLength >= 3)
						{
							ImgDirectories.add(TempFile.getPath());
							SubFolderNameList.add(TempFile.getName());
							MultiStack = true;
						}
					}
				}
				if (!MultiStack)
				{
					OriginalImp = FolderOpener.open(newDir.getPath(), "virtual");
					StackLength = OriginalImp.getStackSize();
					if (StackLength >= 3)
					{
						ImgDirectories.add(newDir.getPath());
						SubFolderNameList.add(newDir.getName());
					}
				}
			}
			break;
		default:
			break;
		}
		return ImgDirectories;
	}

	void ReadMetaData(InputData IData)
	{
		Resolution = IData.GetResolutionsXYZ();
		nChannels = IData.GetnChannels();
		nFrames = IData.GetnFrames();
		zSlices = IData.GetzSlices();
//		Unit = IData.GetUnit();
	}

	public void CreateMaskSelection() throws Exception // TODO:
	{
		if (ImageDetected)
		{
			if (MultiImageDetected)
			{
				if (CropMaskListGUI.getSelectedIndex() >= 0)
				{
					StackNumber = CropMaskListGUI.getSelectedIndex();
				} else
				{
					StackNumber = 0;
				}
				try
				{
					GetMaskSelection(true);
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (!MultiImageDetected)
			{
				StackNumber = 0;
//				if (TheInputIsVideos)
//				{
//					OriginalImp = IJ.openImage(StackDirectories.get(StackNumber));
//				}
				try
				{
					GetMaskSelection(true);
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

//============================================== Mask Creation Window ==============================================
	public void GetMaskSelection(boolean ImageChecker) throws Exception
	{
		if (ImageChecker)
		{
			IJ.showStatus("Opening next video please wait...");
			if (TheInputIsVideos)
			{
				if (StackDirectories.get(StackNumber).endsWith(".tf2"))
				{
					ImagePlus[] imps = BF.openImagePlus(StackDirectories.get(StackNumber));
					OriginalImp = imps[0];
				} else
				{
					OriginalImp = IJ.openImage(StackDirectories.get(StackNumber));
				}
			} else
			{
				OriginalImp = FolderOpener.open(StackDirectories.get(StackNumber), "virtual");

			}
			OriginalImp.show();
		}
		// -------------------- Check if the user changed something --------------------
		int userInTemp = CropMaskListValues.get(StackNumber)[4];
		if (userInTemp == 0)
		{
			InputData indata = new InputData(1);
			int[] Values2 = indata.GetCropMaskPosition(OriginalImp, 0); // TODO: Crop Mask used.
			CropMaskListValues.set(StackNumber, Values2);
//			showMessage("Check values (" + Values2[0] + "," + Values2[1] + "," + Values2[2] + "," + Values2[3] + ") & " + Values2[4]
//					+ " Size is" + CropMaskListValues.size());
			CropMaskListGUI.add(Values2[0] + "," + Values2[1] + "," + Values2[2] + "," + Values2[3]);
			UpdateCropMaskListGUI();
		}
		// -------------------- End --------------------
		IJ.setTool("rectangle");
		JFrame CropMaskWindow = new JFrame();
		CropMaskWindow.toFront();
		CropMaskWindow.requestFocus();
		frmOneMicroscopy.setEnabled(false);
		CropMaskWindow.setTitle("Crop Mask Window");

//		ClassLoader cl = this.getClass().getClassLoader();
//		ImageIcon mainWindoIcon = new ImageIcon(cl.getResource("loading_32.gif"));
//		CropMaskWindow.setIconImage(mainWindoIcon.getImage());

//		JLabel loaderImg = new JLabel("");
//		Image img = new ImageIcon(cl.getResource("loading_32.gif")).getImage();
//		loaderImg.setIcon(new ImageIcon(img));
//		loaderImg.setBounds(350, 258, 86, 20);
//		frmOneMicroscopy.getContentPane().add(loaderImg);

		CropMaskWindow.setResizable(false);
		CropMaskWindow.setBounds(100, 100, 350, 160);
		CropMaskWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		CropMaskWindow.getContentPane().setLayout(null);
		CropMaskWindow.setLocationRelativeTo(null);

		JButton DoneButton = new JButton("Close");
		DoneButton.setBounds(245, 101, 89, 23);
		CropMaskWindow.getContentPane().add(DoneButton);

		JButton NextButton = new JButton("Next");
		NextButton.setBounds(166, 67, 89, 23);
		CropMaskWindow.getContentPane().add(NextButton);

		JTextField CropppingValue = new JTextField();
		CropppingValue.setEditable(false);
		CropppingValue.setBounds(108, 36, 103, 20);
		CropMaskWindow.getContentPane().add(CropppingValue);
		CropppingValue.setColumns(10);

		int[] Values = CropMaskListValues.get(StackNumber);
		CropppingValue.setText(Values[0] + "," + Values[1] + "," + Values[2] + "," + Values[3]);
		OriginalImp.setRoi(Values[0], Values[1], Values[2], Values[3]);

		JButton PreviousButton = new JButton("Previous");
		PreviousButton.setBounds(67, 67, 89, 23);
		CropMaskWindow.getContentPane().add(PreviousButton);

		JButton btnForceToAll = new JButton("Force to all");
		btnForceToAll.setBounds(10, 101, 100, 23);
		CropMaskWindow.getContentPane().add(btnForceToAll);

		JButton SelectButton = new JButton("Retrieve");
		SelectButton.setBounds(224, 35, 89, 23);
		CropMaskWindow.getContentPane().add(SelectButton);

		JLabel lblNewLabel = new JLabel("Create a crop mask selection to retrieve it.");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel.setBounds(10, 11, 269, 14);
		CropMaskWindow.getContentPane().add(lblNewLabel);
		CropMaskWindow.setVisible(true);
		if (StackNumber == 0)
		{
			PreviousButton.setEnabled(false);
		}
		if (StackDirectories.size() > 0)
		{
			if (StackNumber == StackDirectories.size() - 1)
			{
				NextButton.setEnabled(false);
			}
		} else
		{
			NextButton.setEnabled(false);
			btnForceToAll.setEnabled(false);
		}

		SelectButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				Roi TempRoi = OriginalImp.getRoi();
				if (TempRoi != null)
				{
					Double RoiX = TempRoi.getXBase();
					Double RoiY = TempRoi.getYBase();
					Double _W = TempRoi.getFloatWidth();
					Double _H = TempRoi.getFloatHeight();
					OriginalImp.deleteRoi();
					int[] Values = { (int) Math.floor(RoiX), (int) Math.floor(RoiY), (int) Math.floor(_W), (int) Math.floor(_H), 1 };
					CropMaskListValues.set(StackNumber, Values);
					CropppingValue.setText(Values[0] + "," + Values[1] + "," + Values[2] + "," + Values[3]);
					if (ImageChecker)
					{
						OriginalImp.changes = false;
						OriginalImp.close();
					}
					CropMaskWindow.dispose();
					if (StackDirectories.size() > 0)
					{
						StackNumber++;
						if (StackNumber < StackDirectories.size())
						{
							try
							{
								GetMaskSelection(true);
							} catch (Exception e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else if (StackNumber == StackDirectories.size())
						{
							frmOneMicroscopy.setEnabled(true);
							lockMaskBut = false;
							UpdateCropMaskListGUI();
						}
					} else
					{
						frmOneMicroscopy.setEnabled(true);
						lockMaskBut = false;
						UpdateCropMaskListGUI();
					}
				} else
				{
					IJ.showMessage("Make sure there is a selection!");
				}
			}
		});

		NextButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				StackNumber++;
				OriginalImp.changes = false;
				OriginalImp.close();
				CropMaskWindow.dispose();
				if (StackNumber < StackDirectories.size())
				{
					try
					{
						GetMaskSelection(true);
					} catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		PreviousButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				StackNumber--;
				OriginalImp.changes = false;
				OriginalImp.close();
				CropMaskWindow.dispose();
				if (StackNumber >= 0)
				{
					try
					{
						GetMaskSelection(true);
					} catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		DoneButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				UpdateCropMaskListGUI();
				OriginalImp.deleteRoi();
				if (ImageChecker)
				{
					OriginalImp.changes = false;
					OriginalImp.close();
				}
				lockMaskBut = false;
				CropMaskWindow.dispose();
				frmOneMicroscopy.setEnabled(true);
			}
		});
		btnForceToAll.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				Roi TempRoi = OriginalImp.getRoi();
				if (TempRoi != null)
				{
					Double RoiX = TempRoi.getXBase();
					Double RoiY = TempRoi.getYBase();
					Double _W = TempRoi.getFloatWidth();
					Double _H = TempRoi.getFloatHeight();
					int[] Values = { (int) Math.floor(RoiX), (int) Math.floor(RoiY), (int) Math.floor(_W), (int) Math.floor(_H), 1 };
					CropppingValue.setText(Values[0] + "," + Values[1] + "," + Values[2] + "," + Values[3]);
					for (int i = 0; i < CropMaskListValues.size(); i++)
					{
						CropMaskListValues.set(i, Values);
					}
					UpdateCropMaskListGUI();
				} else
				{
					IJ.showMessage("Make sure there is a selection to force it to all stacks!");
				}
			}
		});
	}

	static void UpdateCropMaskListGUI()
	{
		CropMaskListGUI.removeAll();
		for (int i = 0; i < CropMaskListValues.size(); i++)
		{
			int[] Values = CropMaskListValues.get(i);
			CropMaskListGUI.add(Values[0] + "," + Values[1] + "," + Values[2] + "," + Values[3]);
		}
	}

	public void PrintConfigFile(String OutDir, ArrayList<String> LogFile) throws IOException
	{
		FileWriter LogF = new FileWriter(OutDir + "/Log file.txt");

		for (int i = 0; i < LogFile.size(); i++)
		{
			LogF.write(LogFile.get(i) + "\n");
		}
		LogF.close();
	}

	public static double roundDec3(double num)
	{
		num = Math.round(num * 1000);
		return num / 1000;
	}

	public void LoadImportedData()
	{
		CropMaskListValues.clear();
		CropMaskListGUI.removeAll();
		StackDirectories.clear();
		StackNumber = 0;
		Thread GetValues = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				String ImgsPath = ImportF.getText();
				File CheckPath = new File(ImgsPath);
				if (CheckPath.isDirectory())
				{
					StackDirectories = CheckDirectory(ImgsPath);
					if (StackDirectories == null)
					{
						if (!ImageDetected)
						{
							CreateMaskButton.setEnabled(false);
							TopStatusLabel.setText("");
						}
					} else
					{
						try
						{
							if (!TheInputIsVideos)
							{
								{
									OriginalImp = FolderOpener.open(StackDirectories.get(0), "virtual");
									StackLength = OriginalImp.getStackSize();
								}
							}
							if (StackDirectories.size() == 1)
							{
								ImageDetected = true;
								MultiImageDetected = false;
								CreateMaskButton.setEnabled(true);
								TopStatusLabel.setText("video loaded successfully!");
							} else
							{
								ImageDetected = true;
								MultiImageDetected = true;
								CreateMaskButton.setEnabled(true);
								TopStatusLabel.setText(StackDirectories.size() + " videos were detected");
							}
						} catch (Exception e)
						{
							ImageDetected = false;
							MultiImageDetected = false;
							CreateMaskButton.setEnabled(false);
							TopStatusLabel.setText("Nothing detected! please import image sequence/s or video/s.");
						}
					}
				} else if (OneVideo)
				{
					StackDirectories = CheckDirectory(ImgsPath);
					SubFolderNameList.add("Null");
					ImagePresent = false;
					ImageDetected = true;
					MultiImageDetected = false;
					CreateMaskButton.setEnabled(true);
					TopStatusLabel.setText("Images stack loaded successfully!");
				}
			}
		});
		GetValues.start();
	}

//	void loadImages()
//	{
//		ClassLoader cl = this.getClass().getClassLoader();
////		ImageIcon mainWindoIcon = new ImageIcon(cl.getResource("loading_32.gif"));
//
////		JLabel loaderImg = new JLabel("");
////		Image img = new ImageIcon(cl.getResource("loading_32.gif")).getImage();
////		loaderImg.setIcon(new ImageIcon(img));
////		loaderImg.setBounds(450, 258, 86, 40);
////		frmOneMicroscopy.getContentPane().add(loaderImg);
////		
////		Icon imgIcon = new ImageIcon(this.getClass().getResource("loading_32.gif")); // resources/
////		JLabel ImGif = new JLabel(imgIcon);
////		ImGif.setBounds(450, 258, 86, 40);
////		frmOneMicroscopy.getContentPane().add(ImGif);
//
//	}
}
