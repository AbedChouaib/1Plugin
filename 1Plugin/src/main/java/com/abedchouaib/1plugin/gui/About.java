package com.abedchouaib.ONEMicroscopy.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import ij.plugin.PlugIn;

public class About implements PlugIn
{
	private JFrame frame;

//	public static void main(String[] args)
//	{
//		EventQueue.invokeLater(new Runnable()
//		{
//			public void run()
//			{
//				try
//				{
//					About window = new About();
//					window.initialize();
//					window.frame.setVisible(true);
//				} catch (Exception e)
//				{
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	public void initialize()
	{
		String[] ftxt = getFullText();
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.WHITE);
		frame.setBounds(100, 100, 540, 515);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setTitle("About ONE Platform");

//		JTextPane textPane = new JTextPane();
//		textPane.setEditable(false);
//		textPane.setFont(new Font("Tahoma", Font.PLAIN, 12));
//		textPane.setBounds(10, 20, 497, 53);
//		frame.getContentPane().add(textPane);
//		textPane.setText(ftxt[0]);
//
//		JTextPane textPane_1 = new JTextPane();
//		textPane_1.setEditable(false);
//		textPane_1.setText((String) null);
//		textPane_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
//		textPane_1.setBounds(10, 75, 497, 88);
//		frame.getContentPane().add(textPane_1);
//		textPane_1.setText(ftxt[1]);
//
//		JTextPane textPane_2 = new JTextPane();
//		textPane_2.setEditable(false);
//		textPane_2.setText((String) null);
//		textPane_2.setFont(new Font("Tahoma", Font.PLAIN, 12));
//		textPane_2.setBounds(10, 165, 497, 47);
//		frame.getContentPane().add(textPane_2);
//		textPane_2.setText(ftxt[2]);
//
//		JTextPane textPane_3 = new JTextPane();
//		textPane_3.setEditable(false);
//		textPane_3.setText((String) null);
//		textPane_3.setFont(new Font("Tahoma", Font.PLAIN, 12));
//		textPane_3.setBounds(10, 215, 497, 47);
//		frame.getContentPane().add(textPane_3);
//		textPane_3.setText(ftxt[3]);
//
//		JTextPane textPane_4 = new JTextPane();
//		textPane_4.setEditable(false);
//		textPane_4.setText((String) null);
//		textPane_4.setFont(new Font("Tahoma", Font.PLAIN, 12));
//		textPane_4.setBounds(10, 265, 497, 47);
//		frame.getContentPane().add(textPane_4);
//		textPane_4.setText(ftxt[4]);
//
//		JTextPane textPane_5 = new JTextPane();
//		textPane_5.setEditable(false);
//		textPane_5.setText((String) null);
//		textPane_5.setFont(new Font("Tahoma", Font.PLAIN, 12));
//		textPane_5.setBounds(10, 315, 497, 47);
//		frame.getContentPane().add(textPane_5);
//		textPane_5.setText(ftxt[5]);
//
//		JTextPane textPane_6 = new JTextPane();
//		textPane_6.setEditable(false);
//		textPane_6.setText((String) null);
//		textPane_6.setFont(new Font("Tahoma", Font.PLAIN, 12));
//		textPane_6.setBounds(10, 365, 497, 47);
//		frame.getContentPane().add(textPane_6);
//		textPane_6.setText(ftxt[6]);
		// ===================================================================

		// ===================================================================
		JLabel lblNewLabel_1 = new JLabel("<html>" + ftxt[0] + "</html>");
		lblNewLabel_1.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1.setBounds(10, 20, 497, 53);
		frame.getContentPane().add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("<html>" + ftxt[1] + "</html>");
		lblNewLabel_2.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_2.setBounds(10, 75, 497, 88);
		frame.getContentPane().add(lblNewLabel_2);

		JLabel lblNewLabel_3 = new JLabel("<html>" + ftxt[2] + "</html>");
		lblNewLabel_3.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_3.setBounds(10, 165, 497, 47);
		frame.getContentPane().add(lblNewLabel_3);

		JLabel lblNewLabel_4 = new JLabel("<html>" + ftxt[3] + "</html>");
		lblNewLabel_4.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel_4.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_4.setBounds(10, 215, 497, 47);
		frame.getContentPane().add(lblNewLabel_4);

		JLabel lblNewLabel_5 = new JLabel("<html>" + ftxt[4] + "</html>");
		lblNewLabel_5.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel_5.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_5.setBounds(10, 265, 497, 47);
		frame.getContentPane().add(lblNewLabel_5);

		JLabel lblNewLabel_6 = new JLabel("<html>" + ftxt[5] + "</html>");
		lblNewLabel_6.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel_6.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_6.setBounds(10, 315, 497, 47);
		frame.getContentPane().add(lblNewLabel_6);

		JLabel lblNewLabel_7 = new JLabel("<html>" + ftxt[6] + "</html>");
		lblNewLabel_7.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel_7.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_7.setBounds(10, 365, 497, 47);
		frame.getContentPane().add(lblNewLabel_7);

		JLabel lblNewLabel_8 = new JLabel("<html>" + ftxt[7] + "</html>");
		lblNewLabel_8.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel_8.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_8.setBounds(10, 415, 497, 47);
		frame.getContentPane().add(lblNewLabel_8);
	}

	String[] getFullText()
	{
		String[] txt = new String[8];
		txt[0] = "The ONE Platform is a Java-written software that utilizes Fiji app, developed for Ali Shaib "
				+ "and Silvio Rizzoli, University Medical Center Göttingen, Germany, by Abed Chouaib, "
				+ "University of Saarland, Homburg Saar, Germany.";
		txt[1] = "This software is installed in the freeware Fiji app and provided without any express or implied "
				+ "warranty. Permission for Everyone to copy, modify and distribute <i>verbatim</i> copies of this software"
				+ " for any purpose without a fee is hereby granted, provided that this entire notice is included in"
				+ " all copies of any software which is or includes a copy or a modification of ONE Platform.";
		txt[2] = "Upon using, modifying or incorporating parts of ONE Platform, please cite the following reference: "
				+ "Shaib <i>et al.</i>, 2022, BioRxivs (DOI link).\r\n"
				+ "The software relies on the following opensource algorithms that we highly recommend to cite:\r\n" + "";
		txt[3] = "1. Gustafsson, N. <i>et al.</i> Fast live-cell conventional fluorophore nanoscopy with ImageJ through"
				+ " super-resolution radial fluctuations. Nat Commun 7, 12471 (2016). https://doi.org:10.1038/ncomms12471";
		txt[4] = "2. Laine, R. F. <i>et al.</i> High-fidelity 3D live-cell nanoscopy through data-driven enhanced super-resolution"
				+ " radial fluctuation. bioRxiv, 2022.2004.2007.487490 (2022). https://doi.org:10.1101/2022.04.07.487490";
		txt[5] = "3. K. Li, \"The image stabilizer plugin for ImageJ,\" February, (2008).\r\n"
				+ "http://www.cs.cmu.edu/~kangli/code/Image_Stabilizer.html\r\n";
		txt[6] = "4. Linkert, M. <i>et al.</i> Metadata matters: access to image data in the real world. J Cell Biol 189, 777-782"
				+ " (2010). https://doi.org:10.1083/jcb.201004104";
		txt[7] = "5. Schindelin, J., Arganda-Carreras, I., Frise, E., Kaynig, V., Longair, M., Pietzsch, T., … Cardona,"
				+ " A. (2012). Fiji: an open-source platform for biological-image analysis. Nature Methods, 9(7), 676–682."
				+ " doi:10.1038/nmeth.2019";
		return txt;
	}

	public void run(String arg)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					About window = new About();
					window.initialize();
					window.frame.setVisible(true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
}
