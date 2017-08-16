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


public class AccountCheck {
	Connection conn = null;
    private JFrame frame = null;
 
    private JTable table = null;
 
    private Table_Model model = null;
 
    private JScrollPane s_pan = null;
 
    private JButton button_0 = null,button_1 = null, button_2 = null, button_3 = null;
 
    private JPanel pane = null;
    ArrayList<PcRegisterinfo> pcArraylist = null;
    
    public AccountCheck(ArrayList<PcRegisterinfo> pcArraylist) {

//		GetConnection connection = new GetConnection();
//		this.conn = connection.getCon();
		
    	this.pcArraylist = pcArraylist;
    	
    	frame = new JFrame("注册信息");
        pane = new JPanel();
        button_0 = new JButton("删除");
        button_0.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	removeSelectData();
            }
        });
        button_1 = new JButton("刷新");
        button_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ArrayList<PcRegisterinfo> pcArraylist = null;

                PcRegisterDeal_SQL dbConn = new PcRegisterDeal_SQL(DBPoor.getConn());
        		pcArraylist = (ArrayList<PcRegisterinfo>) dbConn.selectPcRegisterInfo();
        		DBPoor.closeConn(dbConn.conn);
        		FreshData(pcArraylist);
            }
        });
        button_2 = new JButton("增加");
        button_2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              //  addData();
            }
        });
        button_3 = new JButton("审核");
        button_3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	//approveData();
            }
        });
        pane.add(button_0);
        pane.add(button_1);
        pane.add(button_2);
        pane.add(button_3);
        model = new Table_Model(20);
        table = new JTable(model);
        table.setBackground(Color.white);
        //String[] age = { "16", "17", "18", "19", "20", "21", "22" };
        //JComboBox com = new JComboBox(age);
       // TableColumnModel tcm = table.getColumnModel();
       // tcm.getColumn(tcm.getColumnCount() - 1).setCellEditor(new DefaultCellEditor(new JCheckBox()));
        
		Iterator<PcRegisterinfo> iter = pcArraylist.iterator();
		PcRegisterinfo tempPcRegister = null;
		while (iter.hasNext()) {
			tempPcRegister = (PcRegisterinfo) iter.next();
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
//		PcRegisterinfo tempPcRegister = new PcRegisterinfo("1234", "hpmont", "hpmont@", "139", "西丽", "123","高级");
//
//		GetConnection connection = new GetConnection();
//		Connection conn = connection.getCon();
//		dbConn = new PcRegisterDeal_SQL();
//		pcArraylist = (ArrayList<PcRegisterinfo>) dbConn.selectPcRegisterInfo();
//		
//		if (dbConn.CheckUserNameExist(tempPcRegister.userName.trim()) == false) {
//			dbConn.insertPcRegister(tempPcRegister);
//		}
//		else{
//			System.out.println("不能添加重复用户");
//		}
//		pcArraylist = (ArrayList<PcRegisterinfo>) dbConn.selectPcRegisterInfo();
//		FreshData(pcArraylist);
//    }
 
    public void addData2Jtable(PcRegisterinfo registerInfo) {
        model.addRow(registerInfo.AccountName,registerInfo.password,registerInfo.userName, registerInfo.phone, registerInfo.RoleName, registerInfo.company, registerInfo.email, registerInfo.addr);
        table.updateUI();
    }
 
    public void FreshData(ArrayList<PcRegisterinfo> pcArraylist) {
		Iterator<PcRegisterinfo> iter = pcArraylist.iterator();
		PcRegisterinfo tempPcRegister = null;
		clrData();
		while (iter.hasNext()) {
			tempPcRegister = (PcRegisterinfo) iter.next();
			addData2Jtable(tempPcRegister);
		}
    }
 
    private void removeSelectData() {
        PcRegisterinfo tempPcRegister = new PcRegisterinfo();
    	int row = table.getSelectedRow();
        PcRegisterDeal_SQL dbConn = new PcRegisterDeal_SQL(DBPoor.getConn());
		if(row == -1){
		}else{
			tempPcRegister.userName = (String) model.getValueAt(row, 0);
			dbConn.deleteJoinDepot(tempPcRegister.userName); 
		}
		ArrayList<PcRegisterinfo> pcArraylist = (ArrayList<PcRegisterinfo>) dbConn.selectPcRegisterInfo();
		FreshData(pcArraylist);
		DBPoor.closeConn(dbConn.conn);
    }
 
    private void clrData() {
        model.removeRows(0, model.getRowCount());
        table.updateUI();
    }
 
    // 审核数据
//    private void approveData() {
//        PcRegisterinfo tempPcRegister = new PcRegisterinfo();
//        
//        int row = table.getSelectedRow();
//		if(row == -1){
//		}else{
//			tempPcRegister.userName = (String) model.getValueAt(row, 0);
//			tempPcRegister.managerLimit = (String) model.getValueAt(row, 6);
//			tempPcRegister.register = (String) model.getValueAt(row, 7);
//			dbConn.updateJoinDepot(tempPcRegister,tempPcRegister.userName);            
//		}
//		ArrayList<PcRegisterinfo> pcArraylist = (ArrayList<PcRegisterinfo>) dbConn.selectPcRegisterInfo();
//		FreshData(pcArraylist);
//    }
 
//    public static void main(String args[]) {
//    	PcRegisterDeal_SQL dbConn = new PcRegisterDeal_SQL();
//    	ArrayList<PcRegisterinfo> pcArraylist = (ArrayList<PcRegisterinfo>) dbConn.selectPcRegisterInfo();
//    	AccountCheck accountCheck = new AccountCheck(pcArraylist,dbConn);
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
class Table_Model extends AbstractTableModel {
 
    private static final long serialVersionUID = -7495940408592595397L;
 
    private Vector<Vector<String>> content = null;
 
    private String[] title_name = {"用户名", "密码", "姓名", "手机", "角色", "公司", "邮箱","地址"};
 
    public Table_Model() {
        content = new Vector<Vector<String>>();
    }
 
    public Table_Model(int count) {
        content = new Vector<Vector<String>>(count);
    }
 
	public void addRow(String name, String Coperate, String mail, String phone, String addr, String password,String manage_limit,String isCheck) {
        Vector<String> v = new Vector<String>(8);
        v.add(0, name);
        v.add(1, Coperate);
        v.add(2, mail);
        v.add(3, phone);
        v.add(4, addr);
        v.add(5, password);
        v.add(6, manage_limit);
        v.add(7, isCheck);
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
