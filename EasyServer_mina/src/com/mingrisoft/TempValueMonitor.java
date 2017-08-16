package com.mingrisoft;

import java.awt.BorderLayout;

/**
 * Java Swing 文本框控件
 * @author gao 
 */

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class TempValueMonitor extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public TempValueMonitor(){
		this.setTitle("临时变量");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setBounds(100, 100, 650, 200);
		JPanel contentPane=new JPanel();
		contentPane.setBorder(new EmptyBorder(5,5,5,5));
		this.setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(3,1,5,5));
		JPanel pane1=new JPanel();
		JPanel pane2=new JPanel();
		JPanel pane3=new JPanel();
		JPanel pane4=new JPanel();
		contentPane.add(pane1);
		contentPane.add(pane2);
		contentPane.add(pane3);
		JLabel label1=new JLabel("信号质量");		
		JTextArea textField1=new JTextArea();
		textField1.setColumns(20);
		JLabel label2=new JLabel("运行次数限定值");	
		 JTextArea textField2=new JTextArea();
			textField2.setColumns(20);
		JLabel label3=new JLabel("屏蔽E68");	
		 JTextArea textField3=new JTextArea();
			textField3.setColumns(20);
		JLabel label4=new JLabel("是否有人");	
		 JTextArea textField4=new JTextArea();
			textField4.setColumns(20);

		pane1.add(label1);
		pane1.add(textField1);
		pane2.add(label2);
		pane2.add(textField2);
		pane3.add(label3);
		pane3.add(textField3);
		pane4.add(label4);
		pane4.add(textField4);

		
		
		this.setVisible(true);
	}
}

