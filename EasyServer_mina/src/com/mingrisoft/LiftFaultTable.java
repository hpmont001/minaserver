package com.mingrisoft;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;


public class LiftFaultTable {
	Connection conn = null;
    private JFrame frame = null;
 
    private JTable table = null;
 
    private Table_Model_Fault model = null;
 
    private JScrollPane s_pan = null;
 
    private JButton button_0 = null,button_1 = null, button_2 = null, button_3 = null;
 
    private JPanel pane = null;
    LiftFaultDeal_SQL dbConn = null;
    ArrayList<LiftFaultRecord> pcArraylist = null;
    
    public LiftFaultTable(ArrayList<LiftFaultRecord> pcArraylist) {

		GetConnection connection = new GetConnection();
		this.conn = connection.getCon();
    	this.pcArraylist = pcArraylist;
    	
    	frame = new JFrame("故障信息");
        pane = new JPanel();
//        button_0 = new JButton("删除");
//        button_0.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//            	removeSelectData();
//            }
//        });
        button_1 = new JButton("刷新");
        button_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ArrayList<LiftFaultRecord> pcArraylist = null;
				LiftFaultDeal_SQL dbConn = new LiftFaultDeal_SQL(DBPoor.getConn());
        		pcArraylist = (ArrayList<LiftFaultRecord>) dbConn.selectAllLiftFaultRecord();

				DBPoor.closeConn(dbConn.conn);
        		FreshData(pcArraylist);

            }
        });
//        button_2 = new JButton("增加");
//        button_2.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                addData();
//            }
//        });
//        button_3 = new JButton("审核");
//        button_3.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//            	approveData();
//            }
//        });
//        pane.add(button_0);
        pane.add(button_1);
//        pane.add(button_2);
//        pane.add(button_3);
        model = new Table_Model_Fault(20);
        table = new JTable(model);
        table.setBackground(Color.white);
        //String[] age = { "16", "17", "18", "19", "20", "21", "22" };
        //JComboBox com = new JComboBox(age);
       // TableColumnModel tcm = table.getColumnModel();
       // tcm.getColumn(tcm.getColumnCount() - 1).setCellEditor(new DefaultCellEditor(new JCheckBox()));
        
		Iterator<LiftFaultRecord> iter = pcArraylist.iterator();
		LiftFaultRecord tempPcRegister = null;
		while (iter.hasNext()) {
			tempPcRegister = (LiftFaultRecord) iter.next();
			addData2Jtable(tempPcRegister);
		}
       // tcm.getColumn(3).setCellEditor(new DefaultCellEditor(com));
       // tcm.getColumn(0).setPreferredWidth(50);
       // tcm.getColumn(1).setPreferredWidth(100);
       // tcm.getColumn(2).setPreferredWidth(50);
 
        s_pan = new JScrollPane(table);
 
        frame.getContentPane().add(s_pan, BorderLayout.CENTER);
        frame.getContentPane().add(pane, BorderLayout.NORTH);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setVisible(true);
 
    }
 
//    private void addData() {
//    	LiftFaultRecord tempPcRegister = new LiftFaultRecord("1234", "hpmont", "hpmont@", "139", "西丽", "123","高级");
//		dbConn = new PcRegisterDeal_SQL();
//		pcArraylist = (ArrayList<PcliftFaultRecord>) dbConn.selectPcliftFaultRecord();
//		
//		if (dbConn.CheckUserNameExist(tempPcRegister.userName.trim()) == false) {
//			dbConn.insertPcRegister(tempPcRegister);
//		}
//		else{
//			System.out.println("不能添加重复用户");
//		}
//		pcArraylist = (ArrayList<PcliftFaultRecord>) dbConn.selectPcliftFaultRecord();
//		FreshData(pcArraylist);
//    }
 
    public void addData2Jtable(LiftFaultRecord liftFaultRecord) {
        model.addRow(liftFaultRecord.liftID, liftFaultRecord.innerID, liftFaultRecord.errorState, liftFaultRecord.errorName, liftFaultRecord.errorCode, liftFaultRecord.errorFloor,liftFaultRecord.InsMode,liftFaultRecord.Description,liftFaultRecord.Send_message,liftFaultRecord.Report_time,liftFaultRecord.Release_time);
        table.updateUI();
    }
 
    public void FreshData(ArrayList<LiftFaultRecord> faultArraylist) {
		Iterator<LiftFaultRecord> iter = faultArraylist.iterator();
		LiftFaultRecord tempPcRegister = null;
		clrData();
		while (iter.hasNext()) {
			tempPcRegister = (LiftFaultRecord) iter.next();
			addData2Jtable(tempPcRegister);
		}
    }
 
//    private void removeSelectData() {
//        PcliftFaultRecord tempPcRegister = new PcliftFaultRecord();
//    	int row = table.getSelectedRow();
//		if(row == -1){
//		}else{
//			tempPcRegister.userName = (String) model.getValueAt(row, 0);
//			dbConn.deleteJoinDepot(tempPcRegister.userName); 
//		}
//		ArrayList<LiftFaultRecord> pcArraylist = (ArrayList<LiftFaultRecord>) dbConn.selectPcliftFaultRecord();
//		FreshData(pcArraylist);
//    }
 
    private void clrData() {
        model.removeRows(0, model.getRowCount());
        table.updateUI();
    }
 
    // 审核数据
//    private void approveData() {
//        PcliftFaultRecord tempPcRegister = new PcliftFaultRecord();
//        
//        int row = table.getSelectedRow();
//		if(row == -1){
//		}else{
//			tempPcRegister.userName = (String) model.getValueAt(row, 0);
//			tempPcRegister.managerLimit = (String) model.getValueAt(row, 6);
//			tempPcRegister.register = (String) model.getValueAt(row, 7);
//			dbConn.updateJoinDepot(tempPcRegister,tempPcRegister.userName);            
//		}
//		ArrayList<LiftFaultRecord> pcArraylist = (ArrayList<LiftFaultRecord>) dbConn.selectPcliftFaultRecord();
//		FreshData(pcArraylist);
//    }
// 
//    public static void main(String args[]) {
//    	LiftFaultDeal_SQL dbConn = new LiftFaultDeal_SQL();
//    	ArrayList<LiftFaultRecord> pcArraylist = (ArrayList<LiftFaultRecord>) dbConn.selectAllLiftFaultRecord();
//    	LiftFaultTable liftFaultTable = new LiftFaultTable(pcArraylist,dbConn);
//		//accountCheck.FreshData(pcArraylist);
//        //new AccountCheck();
////        System.out
////                .println(
////"按下保存按钮将会把JTable中的内容显示出来\r\n------------------------------------");
//    }
 
}
 
/**
* TableModel类，继承了AbstractTableModel
*
* @author 五斗米
*
*/
class Table_Model_Fault extends AbstractTableModel {
 
    private static final long serialVersionUID = -7495940408592595397L;
 
    private Vector<Vector<String>> content = null;
 
    private String[] title_name = {"LiftID", "Errcode", "DisarmState", "InsState","Floor", "SendMSG","ReportTime","ReleaseTime"};
 
    public Table_Model_Fault() {
        content = new Vector<Vector<String>>();
    }
 
    public Table_Model_Fault(int count) {
        content = new Vector<Vector<String>>(count);
    }
 
    public void addRow(String LiftID, String InnerID, String DisarmState, String ErrName, String Errcode, String Floor,String InsState,String Disciption,String SendMSG,String ReportTime,String ReleaseTime) {
        Vector<String> v = new Vector<String>(8);
        v.add(0, LiftID);
        v.add(1, Errcode);
        v.add(2, DisarmState);
        v.add(3, InsState);
        v.add(4, Floor);
        v.add(5, SendMSG);
        v.add(6, ReportTime);
        v.add(7, ReleaseTime);
        content.add(v);
    }
 
    public void removeRow(int row) {
        content.remove(row);
    }
 
    public void removeRows(int row, int count) {
        for (int i = 0; i < count; i++) {
            if (content.size() > row) {
                content.remove(row);
            }
        }
    }
 
    /**
    * 让表格中某些值可修改，但需要setValueAt(Object value, int row, int col)方法配合才能使修改生效
    */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return false;
        }
        return true;
    }
 
    /**
    * 使修改的内容生效
    */
    public void setValueAt(Object value, int row, int col) {
        ((Vector<?>) content.get(row)).remove(col);
        ((Vector<String>) content.get(row)).add(col, (String) value);
        this.fireTableCellUpdated(row, col);
    }
 
    public String getColumnName(int col) {
        return title_name[col];
    }
 
    public int getColumnCount() {
        return title_name.length;
    }
 
    public int getRowCount() {
        return content.size();
    }
 
    public Object getValueAt(int row, int col) {
        return ((Vector<?>) content.get(row)).get(col);
    }
 
    /**
    * 返回数据类型
    */
//    public Class getColumnClass(int col) {
//        return getValueAt(0, col).getClass();
//    }
}
