package com.mingrisoft;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

public class PcMonitor extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JScrollPane s_pan = null;
	private JButton button_0 = null;
    

	public PcMonitor(final JTextArea ta_info2Pc) {
		super();
		setTitle("PCº‡ ”");
		setBounds(500, 100, 385, 266);
		button_0 = new JButton("«Âø’");
		button_0.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ta_info2Pc.setText(null);
			}
		});
		JPanel pane = new JPanel();
		pane.add(button_0, BorderLayout.SOUTH);
		getContentPane().add(pane, BorderLayout.SOUTH);
		
		s_pan = new JScrollPane();
		getContentPane().add(s_pan, BorderLayout.CENTER);
		// final JScrollPane scrollPane2Pc = new JScrollPane();;
		// getContentPane().add(scrollPane2Pc);

		//ta_info2Pc = new JTextArea();
		ta_info2Pc.setPreferredSize(new Dimension(300, 320));
		s_pan.setViewportView(ta_info2Pc);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setVisible(true);

	}
}
