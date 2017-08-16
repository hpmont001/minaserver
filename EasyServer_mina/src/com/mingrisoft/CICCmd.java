package com.mingrisoft;

import java.awt.BorderLayout;

/**
 * Java Swing �ı���ؼ�
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

public class CICCmd extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public CICCmd(){
		this.setTitle("CIC��������");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setBounds(100, 100, 650, 200);
		JPanel contentPane=new JPanel();
		contentPane.setBorder(new EmptyBorder(5,5,5,5));
		this.setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(3,1,5,5));
		JPanel pane1=new JPanel();
		JPanel pane2=new JPanel();
		JPanel pane3=new JPanel();
		JPanel pane10=new JPanel();
		JPanel pane11=new JPanel();
		JPanel pane12=new JPanel();
		JPanel pane13=new JPanel();
		contentPane.add(pane1);
		contentPane.add(pane2);
		contentPane.add(pane10);
		contentPane.add(pane11);
		contentPane.add(pane12);
		contentPane.add(pane13);
		JLabel label1=new JLabel("IMSI��");		
		final JTextField textField1=new JTextField();
		textField1.setColumns(20);

		JLabel label2=new JLabel("���д�����");	
		final JTextField textField2=new JTextField();
		textField2.setColumns(10);
		pane1.add(label1,BorderLayout.WEST);
		pane1.add(textField1,BorderLayout.EAST);
		pane2.add(label2,BorderLayout.WEST);
		pane2.add(textField2,BorderLayout.EAST);


		JLabel label10=new JLabel("�ź�����");		
		final JTextArea textTemp1=new JTextArea();
		textTemp1.setColumns(10);
		pane10.add(label10,BorderLayout.WEST);
		pane10.add(textTemp1);
		JLabel label21=new JLabel("���д����޶�ֵ");	
		 final JTextArea textTemp2=new JTextArea();
			textTemp2.setColumns(10);
			pane11.add(label21,BorderLayout.WEST);
			pane11.add(textTemp2,BorderLayout.EAST);
		JLabel label3=new JLabel("����E68");	
		 final JTextArea textTemp3=new JTextArea();
			textTemp3.setColumns(10);
			pane12.add(label3,BorderLayout.WEST);
			pane12.add(textTemp3,BorderLayout.EAST);
		JLabel label4=new JLabel("�Ƿ�����");	
		 final JTextArea textTemp4=new JTextArea();
			textTemp4.setColumns(10);
			pane13.add(label4,BorderLayout.WEST);
			pane13.add(textTemp4,BorderLayout.EAST);
		
		
		JButton button_0 = new JButton("����");
		button_0.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String Imsi = textField1.getText();
				if(Imsi != null){
					Imsi = Imsi.trim();
				}
				String RunningTimesLimit = textField2.getText();

				String liftID = LiftStsDeal_SQL.InquireLiftID(Imsi);
				if (IotServer.map_IMSI_Lift.containsKey(liftID) == false) {
					return;
				}
				Protocol value = IotServer.map_IMSI_Lift.get(liftID);// ���ָ������ֵ

				value.f_LockLift = 1;
				try {
					value.RunningTimesLimit = Integer.valueOf(RunningTimesLimit);
				} catch (Exception e2) {
					// TODO: handle exception
					IotServer.logger.error("", e2);
				}
				
			}
		});
		JButton button_1 = new JButton("Keypad");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String Imsi = textField1.getText();
				if(Imsi != null){
					Imsi = Imsi.trim();
				}
				String liftID = LiftStsDeal_SQL.InquireLiftID(Imsi);
				if (IotServer.map_IMSI_Lift.containsKey(liftID) == false) {
					return;
				}
				Protocol value = IotServer.map_IMSI_Lift.get(liftID);// ���ָ������ֵ
				
				value.f_KeyPadPort = "Keypad";//2����ͨѶ��1����ͨѶ
			}
		});
		JButton button_2 = new JButton("Hallcall");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String Imsi = textField1.getText();
				if(Imsi != null){
					Imsi = Imsi.trim();
				}
				String liftID = LiftStsDeal_SQL.InquireLiftID(Imsi);
				if (IotServer.map_IMSI_Lift.containsKey(liftID) == false) {
					return;
				}
				Protocol value = IotServer.map_IMSI_Lift.get(liftID);// ���ָ������ֵ
				
				value.f_KeyPadPort = "Hallcall";//2����ͨѶ��1����ͨѶ
			}
		});
		JButton button_3 = new JButton("ComErrLock");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String Imsi = textField1.getText();
				if(Imsi != null){
					Imsi = Imsi.trim();
				}
				String liftID = LiftStsDeal_SQL.InquireLiftID(Imsi);
				if (IotServer.map_IMSI_Lift.containsKey(liftID) == false) {
					return;
				}
				Protocol value = IotServer.map_IMSI_Lift.get(liftID);// ���ָ������ֵ
				
				value.f_ComErrLockLift = "LockLift";//2����ͨѶ��1����ͨѶ
			}
		});
		JButton button_4 = new JButton("ComErrNoLock");
		button_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String Imsi = textField1.getText();
				if(Imsi != null){
					Imsi = Imsi.trim();
				}
				String liftID = LiftStsDeal_SQL.InquireLiftID(Imsi);
				if (IotServer.map_IMSI_Lift.containsKey(liftID) == false) {
					return;
				}
				Protocol value = IotServer.map_IMSI_Lift.get(liftID);// ���ָ������ֵ
				
				value.f_ComErrLockLift = "nolock";//2����ͨѶ��1����ͨѶ
			}
		});
		JButton button_5 = new JButton("ˢ��");
		button_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String Imsi = textField1.getText();
				if(Imsi != null){
					Imsi = Imsi.trim();
				}
				String liftID = LiftStsDeal_SQL.InquireLiftID(Imsi);
				if (IotServer.map_IMSI_Lift.containsKey(liftID) == false) {
					return;
				}
				Protocol value = IotServer.map_IMSI_Lift.get(liftID);// ���ָ������ֵ
				
				textTemp1.setText(value.LiftSts[36] + "");
				textTemp2.setText(value.LiftSts[37] + "");
				
				textTemp3.setText((value.LiftSts[38]&0x01) + "");
				textTemp4.setText((value.LiftSts[38]&0x02) + "");
			}
		});
		

		pane3.add(button_0);
		pane3.add(button_1);
		pane3.add(button_2);
		pane3.add(button_3);
		pane3.add(button_4);
		pane3.add(button_5);
		contentPane.add(pane3,BorderLayout.SOUTH);
		
		this.setVisible(true);
	}
}

