package com.abedchouaib.ONEMicroscopy.gui;

/**
 * -------------------------------------------------------------------------------------------
 * This class is the GUI for ONE-Microscopy option.
 * 
 * @author Abed Chouaib
 * @version 1.0.0
 * -------------------------------------------------------------------------------------------
 */
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SystemColor;
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
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.abedchouaib.ONEMicroscopy.Start_eSRRF;
import com.abedchouaib.ONEMicroscopy.gui.InputData.ImportType;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.FolderOpener;
import ij.plugin.PlugIn;

public class Window_eSRRF implements PlugIn
{
	private JFrame frmOneMicroscopy;
	private JTextField txtF_MaxFrames;
	private JTextField txtF_Sensitivity;
	private JLabel lblTracOrder;
	private JTextField txtF_RadMag;
	private JTextField txtF_Radius;
	private JTextField txtF_DistScale;
	private JTextField txtF_Chn;
	private JTextField txtF_KnownDist;
	private JLabel lblRadMag;
	private JLabel lblRingAxes;
	private JLabel DistScaleL;
	private JLabel Label1_4;
	private JLabel Label1_5;
	private JTextField SavePathF;
	private JTextField ImportF;
	private JLabel TopStatusLabel;
	static int StackLength = 0; // width = 0, height = 0;
	public static String ImportDirPath, SaveDirPath, BeadsDirPath;
	private boolean ImageDetected = false;
	static boolean MultiImageDetected = false;
	public String roiMaskPath, ImagesSpotsPath, CSVfilesPath;
	public int[] MaskCropValues = { 0, 0, 0, 0 };

	public static int EndFrame;
//	static String FileExtension;
	static String Unit;
	private boolean SaveButtonGaurd = true;
	private Color FocusColor;
	public static ArrayList<String> StackDirectories = new ArrayList<String>();
	static ArrayList<int[]> CropMaskListValues = new ArrayList<int[]>();
	ArrayList<int[]> CropMaskListValuesReset = new ArrayList<int[]>();
	static ArrayList<String> SubFolderNameList = new ArrayList<String>();
	static int TimeUnit;
	private Checkbox CheckBoxVideosIn = new Checkbox();
	static boolean TheInputIsVideos = true;
	static int stackLoopNum;
	static int NumberOfStacks;
	static boolean ImagePresent = false;
	static boolean lockMaskBut = false;
	static boolean OneVideo = true;
	public static boolean Calibrate;
	public static int FPS;
	static int BeadsChannels;

	public static String OutPutDirectory;
	public static boolean SaveParentFolder = true;
	public static int StartFromVideo = 1;
	// ============ Advance Options ============
	String AdOptPref = "nanoj.liveSRRF.gui.LiveSRRF_optimised_.";
	// ============ end ============

	// ============ Metadata ============
	private double[] Resolution = { 0, 0, 0 };
	private int nChannels = 1;
	private int zSlices;
	private int nFrames;
	private double tempResolution;
	// ============ end ============
	String username;
	private ImagePlus OriginalImp;

	boolean lockImportField = false;
	ImportType DataType = InputData.ImportType.MVi;
	private JTextField txtF_CAC;
	private JTextField ViewChannels;
	private JTextField ViewFrames;
	private JTextField ViewDistance;
	private JTextField txtF_fullScale;
	private JTextField txtF_ExpFactor;
	private JPanel panel_1;
	private JTextField txtF_StartFromVideo;
	private JTextField txtF_zSlices;
	private JLabel lblZaxis;
	private JPanel panel_2;
	private Checkbox CheckBox_AVG;
	private Checkbox CheckBox_VAR;
	private Checkbox CheckBox_TAC2;
	private Checkbox CheckBox_WideField;

	// main method will not be called inside ImageJ, Java editors testing only!
	public static void main(String[] args) throws Exception
	{
		Class<?> clazz = Window_eSRRF.class;
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
					Window_eSRRF window = new Window_eSRRF();
					window.initialize();
					window.frmOneMicroscopy.setVisible(true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	// ==================================================================================================
	// ==================================================================================================
	// <<<<<<<<<<>>>>>>>>>> GUI JFrame <<<<<<<<<<>>>>>>>>>>
	public void initialize()
	{
		ONEsetup.LoadPrefs();
		username = System.getProperty("user.home"); // get the user.name
		// load nanoJ SRRF parameters.
		frmOneMicroscopy = new JFrame();
		frmOneMicroscopy.setResizable(false);
		frmOneMicroscopy.setBackground(Color.LIGHT_GRAY);
		frmOneMicroscopy.getContentPane().setBackground(SystemColor.control);
		frmOneMicroscopy.setTitle("ONE 3D");
//		ClassLoader cl = this.getClass().getClassLoader();
//		ImageIcon mainWindoIcon = new ImageIcon(cl.getResource("DART_icon_64.png"));
//		Frame.setIconImage(mainWindoIcon.getImage());
		frmOneMicroscopy.setBounds(100, 100, 705, 550);
		frmOneMicroscopy.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmOneMicroscopy.getContentPane().setLayout(null);
		frmOneMicroscopy.setLocationRelativeTo(null);
		FocusColor = Color.decode("#ddeeff"); // #eeddff

		// ImportDir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		// int response = ImportDir.showOpenDialog(DirectoryChooser);
		txtF_MaxFrames = new JTextField("0"); // ================= Mean Brightness Tolerance
		txtF_MaxFrames.setHorizontalAlignment(SwingConstants.CENTER);
		txtF_MaxFrames.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent arg0)
			{
				txtF_MaxFrames.setBackground(FocusColor);
			}

			@Override
			public void focusLost(FocusEvent e)
			{
				txtF_MaxFrames.setBackground(Color.white);
			}
		});
		txtF_MaxFrames.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyTyped(KeyEvent evt)
			{
				char c = evt.getKeyChar();
				if (!Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)
				{
					evt.consume();
				}
			}
		});
		// BrightToleranceF.setToolTipText("");
		txtF_MaxFrames.setBounds(194, 127, 86, 20);
		frmOneMicroscopy.getContentPane().add(txtF_MaxFrames);
		txtF_MaxFrames.setColumns(10);

		JLabel Label1 = new JLabel("Frames to analyze (0-auto)");
		Label1.setBounds(9, 125, 182, 24);
		frmOneMicroscopy.getContentPane().add(Label1);

		txtF_Sensitivity = new JTextField("" + ONEsetup.eSRRF_sensitivity);
		txtF_Sensitivity.setHorizontalAlignment(SwingConstants.CENTER);
		txtF_Sensitivity.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyTyped(KeyEvent evt)
			{
				char c = evt.getKeyChar();
				if (Character.isAlphabetic(c))
				{
					evt.consume();
				}
			}
		});
		txtF_Sensitivity.setColumns(10);
		txtF_Sensitivity.setBounds(194, 224, 86, 20);
		frmOneMicroscopy.getContentPane().add(txtF_Sensitivity);
		txtF_Sensitivity.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent arg0)
			{
				txtF_Sensitivity.setBackground(FocusColor);
			}

			@Override
			public void focusLost(FocusEvent e)
			{
				txtF_Sensitivity.setBackground(Color.white);
			}
		});

		lblTracOrder = new JLabel("Sensitivity (default,1)");
		lblTracOrder.setBounds(9, 222, 175, 24);
		frmOneMicroscopy.getContentPane().add(lblTracOrder);

		txtF_RadMag = new JTextField("" + ONEsetup.eSRRF_magnification);
		txtF_RadMag.setHorizontalAlignment(SwingConstants.CENTER);
		txtF_RadMag.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyTyped(KeyEvent evt)
			{
				char c = evt.getKeyChar();
				if (Character.isAlphabetic(c))
				{
					evt.consume();
				}
			}
		});
		txtF_RadMag.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent arg0)
			{
				txtF_RadMag.setBackground(FocusColor);
			}

			@Override
			public void focusLost(FocusEvent e)
			{
				txtF_RadMag.setBackground(Color.white);
			}
		});
		txtF_RadMag.setColumns(10);
		txtF_RadMag.setBounds(194, 160, 86, 20);
		frmOneMicroscopy.getContentPane().add(txtF_RadMag);

		txtF_Radius = new JTextField("" + ONEsetup.eSRRF_fwhm);
		txtF_Radius.setHorizontalAlignment(SwingConstants.CENTER);
		txtF_Radius.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent arg0)
			{
				txtF_Radius.setBackground(FocusColor);
			}

			@Override
			public void focusLost(FocusEvent e)
			{
				txtF_Radius.setBackground(Color.white);
			}
		});
		txtF_Radius.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyTyped(KeyEvent evt)
			{
				char c = evt.getKeyChar();
				if (!Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)
				{
					evt.consume();
				}
			}
		});
		txtF_Radius.setColumns(10);
		txtF_Radius.setBounds(194, 191, 86, 20);
		frmOneMicroscopy.getContentPane().add(txtF_Radius);

		txtF_DistScale = new JTextField("0");
		txtF_DistScale.setHorizontalAlignment(SwingConstants.CENTER);
		txtF_DistScale.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
				char c = e.getKeyChar();
				if (!Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)
				{
					e.consume();
				}
			}
		});
		txtF_DistScale.setColumns(10);
		txtF_DistScale.setBounds(194, 257, 86, 20);
		frmOneMicroscopy.getContentPane().add(txtF_DistScale);
		txtF_DistScale.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent arg0)
			{
				txtF_DistScale.setBackground(FocusColor);
			}

			@Override
			public void focusLost(FocusEvent e)
			{
				txtF_DistScale.setBackground(Color.white);
				updateFullScale();
			}
		});

		txtF_Chn = new JTextField("0");
		txtF_Chn.setHorizontalAlignment(SwingConstants.CENTER);
		txtF_Chn.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
				char c = e.getKeyChar();
				if (!Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)
				{
					e.consume();
				}
			}
		});
		txtF_Chn.setColumns(10);
		txtF_Chn.setBounds(194, 94, 86, 20);
		frmOneMicroscopy.getContentPane().add(txtF_Chn);
		txtF_Chn.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent arg0)
			{
				txtF_Chn.setBackground(FocusColor);
			}

			@Override
			public void focusLost(FocusEvent e)
			{
				txtF_Chn.setBackground(Color.white);
			}
		});

		txtF_KnownDist = new JTextField("1");
		txtF_KnownDist.setHorizontalAlignment(SwingConstants.CENTER);
		txtF_KnownDist.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
				char c = e.getKeyChar();
				if (!Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)
				{
					e.consume();
				}
			}
		});
		txtF_KnownDist.setColumns(10);
		txtF_KnownDist.setBounds(194, 292, 86, 20);
		frmOneMicroscopy.getContentPane().add(txtF_KnownDist);
		txtF_KnownDist.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent arg0)
			{
				txtF_KnownDist.setBackground(FocusColor);
			}

			@Override
			public void focusLost(FocusEvent e)
			{
				txtF_KnownDist.setBackground(Color.white);
			}
		});

		lblRadMag = new JLabel("Magnification (default, 5)");
		lblRadMag.setBounds(9, 160, 182, 24);
		frmOneMicroscopy.getContentPane().add(lblRadMag);

		lblRingAxes = new JLabel("Radius (default, 1)");
		lblRingAxes.setBounds(9, 191, 153, 24);
		frmOneMicroscopy.getContentPane().add(lblRingAxes);

		DistScaleL = new JLabel("Distance to scale (0-auto)");
		DistScaleL.setBounds(9, 257, 175, 24);
		frmOneMicroscopy.getContentPane().add(DistScaleL);

		Label1_4 = new JLabel("Channels to process (0-auto)");
		Label1_4.setBounds(9, 94, 182, 24);
		frmOneMicroscopy.getContentPane().add(Label1_4);

		Label1_5 = new JLabel("Known distance");
		Label1_5.setBounds(9, 290, 175, 24);
		frmOneMicroscopy.getContentPane().add(Label1_5);

		SavePathF = new JTextField(); // save path
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
		panel.setBounds(10, 393, 669, 101);
		frmOneMicroscopy.getContentPane().add(panel);
		panel.setLayout(null);

		Checkbox CheckBox_CAC = new Checkbox("Chromatic aberration correction");
		CheckBox_CAC.setFont(new Font("Tahoma", Font.PLAIN, 11));
		CheckBox_CAC.setBounds(10, 10, 215, 22);
		panel.add(CheckBox_CAC);

		JButton StartButton = new JButton("Start");
		StartButton.setBounds(566, 67, 89, 23);
		panel.add(StartButton);

		txtF_CAC = new JTextField();
		txtF_CAC.setEnabled(false);
		txtF_CAC.setBounds(147, 40, 298, 20);
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
		Import_CAC.setEnabled(false);
		Import_CAC.setBounds(10, 38, 121, 23);
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

		ViewChannels = new JTextField("");
		ViewChannels.setHorizontalAlignment(SwingConstants.CENTER);
		ViewChannels.setEditable(false);
		ViewChannels.setColumns(10);
		ViewChannels.setBounds(290, 94, 86, 20);
		frmOneMicroscopy.getContentPane().add(ViewChannels);

		ViewFrames = new JTextField("");
		ViewFrames.setHorizontalAlignment(SwingConstants.CENTER);
		ViewFrames.setEditable(false);
		ViewFrames.setColumns(10);
		ViewFrames.setBounds(290, 127, 86, 20);
		frmOneMicroscopy.getContentPane().add(ViewFrames);

		ViewDistance = new JTextField("");
		ViewDistance.setHorizontalAlignment(SwingConstants.CENTER);
		ViewDistance.setEditable(false);
		ViewDistance.setColumns(10);
		ViewDistance.setBounds(290, 257, 86, 20);
		frmOneMicroscopy.getContentPane().add(ViewDistance);

		txtF_fullScale = new JTextField("");
		txtF_fullScale.setHorizontalAlignment(SwingConstants.CENTER);
		txtF_fullScale.setEditable(false);
		txtF_fullScale.setColumns(10);
		txtF_fullScale.setBounds(290, 327, 86, 20);
		frmOneMicroscopy.getContentPane().add(txtF_fullScale);

		txtF_ExpFactor = new JTextField("" + ONEsetup.eSRRF_ExpFactor);
		txtF_ExpFactor.setHorizontalAlignment(SwingConstants.CENTER);
		txtF_ExpFactor.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				updateFullScale();
			}
		});
		txtF_ExpFactor.setColumns(10);
		txtF_ExpFactor.setBounds(194, 327, 86, 20);
		frmOneMicroscopy.getContentPane().add(txtF_ExpFactor);

		JLabel lblExpansionFactor = new JLabel("Expansion factor");
		lblExpansionFactor.setBounds(9, 325, 175, 24);
		frmOneMicroscopy.getContentPane().add(lblExpansionFactor);

		panel_1 = new JPanel();
		panel_1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_1.setBounds(456, 26, 223, 161);
		frmOneMicroscopy.getContentPane().add(panel_1);
		panel_1.setLayout(null);

		Checkbox CheckBoxOneVideo = new Checkbox("Video");
		CheckBoxOneVideo.setBounds(10, 10, 125, 23);
		panel_1.add(CheckBoxOneVideo);

		CheckBoxVideosIn = new Checkbox("Multi-videos");
		CheckBoxVideosIn.setBounds(10, 39, 125, 23);
		panel_1.add(CheckBoxVideosIn);
		CheckBoxVideosIn.setState(true);

		Checkbox CheckBoxImages = new Checkbox("Olympus files (.ets)");
		CheckBoxImages.setBounds(10, 68, 141, 23);
		panel_1.add(CheckBoxImages);
		CheckBoxImages.setEnabled(false);

		Checkbox CheckBox_SaveParentFolder = new Checkbox("Save in parent folder");
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
		CheckBox_SaveParentFolder.setState(true);
		CheckBox_SaveParentFolder.setBounds(10, 97, 141, 22);
		panel_1.add(CheckBox_SaveParentFolder);
		CheckBox_SaveParentFolder.setFont(new Font("Tahoma", Font.PLAIN, 11));

		txtF_StartFromVideo = new JTextField("1");
		txtF_StartFromVideo.setHorizontalAlignment(SwingConstants.CENTER);
		txtF_StartFromVideo.setColumns(10);
		txtF_StartFromVideo.setBounds(105, 127, 46, 20);
		panel_1.add(txtF_StartFromVideo);

		JLabel Label1_4_2 = new JLabel("Start from video");
		Label1_4_2.setBounds(10, 125, 125, 24);
		panel_1.add(Label1_4_2);

		txtF_zSlices = new JTextField("");
		txtF_zSlices.setHorizontalAlignment(SwingConstants.CENTER);
		txtF_zSlices.setEditable(false);
		txtF_zSlices.setColumns(10);
		txtF_zSlices.setBounds(194, 360, 86, 20);
		frmOneMicroscopy.getContentPane().add(txtF_zSlices);

		lblZaxis = new JLabel("zONE");
		lblZaxis.setHorizontalAlignment(SwingConstants.LEFT);
		lblZaxis.setBounds(9, 358, 86, 24);
		frmOneMicroscopy.getContentPane().add(lblZaxis);

		panel_2 = new JPanel();
		panel_2.setToolTipText("");
		panel_2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_2.setBounds(456, 205, 223, 177);
		frmOneMicroscopy.getContentPane().add(panel_2);
		panel_2.setLayout(null);

		JButton btn_AdOpt = new JButton("Advanced Options");
		btn_AdOpt.setBounds(10, 143, 141, 23);
		panel_2.add(btn_AdOpt);

		CheckBox_AVG = new Checkbox("AVG reconstruction (default, on)");
		CheckBox_AVG.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if (CheckBox_AVG.getState())
				{
					CheckBox_VAR.setState(false);
					CheckBox_TAC2.setState(false);
					CheckBox_WideField.setState(false);
				}
			}
		});
		CheckBox_AVG.setBounds(10, 10, 209, 23);
		panel_2.add(CheckBox_AVG);
		CheckBox_AVG.setState(ONEsetup.eSRRF_calculateAVG);

		CheckBox_VAR = new Checkbox("VAR reconstruction (default, off)");
		CheckBox_VAR.setBounds(10, 39, 209, 23);
		panel_2.add(CheckBox_VAR);
		CheckBox_VAR.setState(ONEsetup.eSRRF_calculateVAR);
		CheckBox_VAR.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if (CheckBox_VAR.getState())
				{
					CheckBox_AVG.setState(false);
					CheckBox_TAC2.setState(false);
					CheckBox_WideField.setState(false);
				}
			}
		});

		CheckBox_TAC2 = new Checkbox("TAC2 reconstruction (default, off)");
		CheckBox_TAC2.setBounds(10, 68, 209, 23);
		panel_2.add(CheckBox_TAC2);
		CheckBox_TAC2.setState(ONEsetup.eSRRF_calculateTAC2);
		CheckBox_TAC2.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if (CheckBox_TAC2.getState())
				{
					CheckBox_AVG.setState(false);
					CheckBox_VAR.setState(false);
					CheckBox_WideField.setState(false);
				}
			}
		});

		CheckBox_WideField = new Checkbox("Wide-field interpolation (default, off)");
		CheckBox_WideField.setBounds(10, 97, 209, 23);
		panel_2.add(CheckBox_WideField);
		CheckBox_WideField.setState(ONEsetup.eSRRF_getInterpolatedImage);
		CheckBox_WideField.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if (CheckBox_WideField.getState())
				{
					CheckBox_AVG.setState(false);
					CheckBox_VAR.setState(false);
					CheckBox_TAC2.setState(false);
				}
			}
		});

		btn_AdOpt.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent e)
			{
				showDialog();
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
				txtF_StartFromVideo.setEnabled(true);
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
				txtF_StartFromVideo.setEnabled(true);
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
				txtF_StartFromVideo.setEnabled(false);
				txtF_StartFromVideo.setText("1");
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
		updateFullScale();
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
						if ((SavePathF.getText() != null && !SavePathF.getText().isEmpty()) || SaveParentFolder)
						{
							if (ImageDetected)
							{
								frmOneMicroscopy.dispose();
//								frmOneMicroscopy.setVisible(false);
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
								RefreshGUI();
								EndFrame = Integer.parseInt(txtF_MaxFrames.getText());
								ONEsetup.eSRRF_Channels = Integer.parseInt(txtF_Chn.getText());
//								ONEsetup.eSRRF_SpecificChannel = Integer.parseInt(txtF_SpecChn.getText());
								ONEsetup.eSRRF_magnification = (double) Double.parseDouble(txtF_RadMag.getText());
								ONEsetup.eSRRF_fwhm = (double) Double.parseDouble(txtF_Radius.getText());
								ONEsetup.eSRRF_sensitivity = (double) Double.parseDouble(txtF_Sensitivity.getText());
								ONEsetup.eSRRF_DistScale = (double) Double.parseDouble(txtF_DistScale.getText());
								ONEsetup.eSRRF_KnownDist = (double) Double.parseDouble(txtF_KnownDist.getText());
								ONEsetup.eSRRF_ExpFactor = (double) Double.parseDouble(txtF_ExpFactor.getText());

								ONEsetup.eSRRF_calculateAVG = CheckBox_AVG.getState();
								ONEsetup.eSRRF_calculateVAR = CheckBox_VAR.getState();
								ONEsetup.eSRRF_calculateTAC2 = CheckBox_TAC2.getState();
								ONEsetup.eSRRF_getInterpolatedImage = CheckBox_WideField.getState();

								SaveParentFolder = CheckBox_SaveParentFolder.getState();
								StartFromVideo = Integer.parseInt(txtF_StartFromVideo.getText()) - 1;
								Calibrate = CheckBox_CAC.getState();
								BeadsDirPath = txtF_CAC.getText();
								ImportDirPath = ImportF.getText();
								stackLoopNum = 0;

								ONEsetup.savePrefs();
								NumberOfStacks = StackDirectories.size(); // <----------------- Start Analysis
								Start_eSRRF cl = new Start_eSRRF();
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
	private void RefreshGUI()
	{
		double temp = (1 / Resolution[0]);
		temp = roundDec3(temp);
		ViewChannels.setText("" + nChannels);
		ViewFrames.setText("" + nFrames);
		ViewDistance.setText("" + temp);
		txtF_zSlices.setText("" + zSlices);
		tempResolution = temp;
		updateFullScale();
//		txtF_Unit.setText(Unit);
	}

	public void CreateDir(String OutPutDirectory)
	{
		File OutPutDir = new File(OutPutDirectory);
		if (!OutPutDir.exists())
		{
			OutPutDir.mkdir();
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
					Window_eSRRF window = new Window_eSRRF();
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
				InputData IData = new InputData(Path, DataType, 3);
				ImgDirectories = IData.GetListofFiles();
				ReadMetaData(IData);
				RefreshGUI();
			} catch (Exception e1)
			{
				e1.printStackTrace();
			}
			break;

		case MVi:
			try
			{
				InputData IData = new InputData(Path, DataType, 3);
				ImgDirectories = IData.GetListofFiles();
				ReadMetaData(IData);
				RefreshGUI();
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
		FPS = (int) Math.round(1 / IData.GetTime());
//		Unit = IData.GetUnit();
	}

//============================================== Mask Creation Window ==============================================
	public void PrintConfigFile(String OutDir, ArrayList<String> LogFile) throws IOException
	{
		FileWriter LogF = new FileWriter(OutDir + "/Log file.txt");

		for (int i = 0; i < LogFile.size(); i++)
		{
			LogF.write(LogFile.get(i) + "\n");
		}
		LogF.close();
	}

	void updateFullScale()
	{
		ONEsetup.eSRRF_DistScale = (double) Double.parseDouble(txtF_DistScale.getText());
		tempResolution = ONEsetup.eSRRF_DistScale == 0 ? tempResolution : ONEsetup.eSRRF_DistScale;
		double ScaleNumber = tempResolution * (double) Double.parseDouble(txtF_ExpFactor.getText())
				* (double) Double.parseDouble(txtF_RadMag.getText());
		ScaleNumber = roundDec3(ScaleNumber);
		txtF_fullScale.setText("" + ScaleNumber);
	}

	public static double roundDec3(double num)
	{
		num = Math.round(num * 1000);
		return num / 1000;
	}

	public void LoadImportedData()
	{
		StackDirectories.clear();
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
								TopStatusLabel.setText("video loaded successfully!");
							} else
							{
								ImageDetected = true;
								MultiImageDetected = true;
								TopStatusLabel.setText(StackDirectories.size() + " videos were detected");
							}
							updateFullScale();
						} catch (Exception e)
						{
							ImageDetected = false;
							MultiImageDetected = false;
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
					TopStatusLabel.setText("Images stack loaded successfully!");
				}
			}
		});
		GetValues.start();
	}

	public boolean showDialog()
	{
		GenericDialog GD = new GenericDialog("eSRRF - Advance Settings");

		GD.addMessage(" 		-- eSRRF Settings --		");
		GD.addCheckbox("Vibration correction", ONEsetup.eSRRF_correctVibration);
		GD.addCheckbox("Perform rolling analysis (default, off)", ONEsetup.eSRRF_doRollingAnalysis);
		GD.addNumericField("Frame gap between SR frame (0-auto)", ONEsetup.eSRRF_frameGapFromUser, 1);
		GD.addCheckbox("Perform 3D-eSRRF from MFM data", ONEsetup.eSRRF_do3DSRRF);

		GD.addMessage(" 		-- Advance Reconstruction Settings --		");
		GD.addCheckbox("Intensity weighting (default, true)", ONEsetup.eSRRF_dintWeighting);
		GD.addCheckbox("Use FHT for interpolation (default, true)", ONEsetup.eSRRF_doFHTinterpolation);
		GD.addCheckbox("Macro-pixel patterning correction (default, true)", ONEsetup.eSRRF_doMPmapCorrection);

		GD.showDialog();
		if (GD.wasCanceled())
		{
			return false;
		}
		ONEsetup.eSRRF_correctVibration = GD.getNextBoolean();
		ONEsetup.eSRRF_doRollingAnalysis = GD.getNextBoolean();
		ONEsetup.eSRRF_frameGapFromUser = GD.getNextNumber();
		ONEsetup.eSRRF_do3DSRRF = GD.getNextBoolean();

		ONEsetup.eSRRF_dintWeighting = GD.getNextBoolean();
		ONEsetup.eSRRF_doFHTinterpolation = GD.getNextBoolean();
		ONEsetup.eSRRF_doMPmapCorrection = GD.getNextBoolean();
		return true;
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
