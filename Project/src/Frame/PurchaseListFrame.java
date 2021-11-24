package Frame;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import DBInterface.DBInterface;
import Dialog.MonthChooseDialog;
import OptionPane.OptionPane;

public class PurchaseListFrame extends JFrame {
	private int u_no;
	private String u_id;
	private int[] months;
	private JPanel northPanel = new JPanel();
	private JPanel centerPanel = new JPanel();
	private JPanel southPanel = new JPanel();
	private JLabel nameLabel = new JLabel();
	private JLabel monthLabel = new JLabel();
	private JButton monthBtn = new JButton("월 선택");
	private JButton allViewBtn = new JButton("전체보기");
	private String[] header = {"구매날짜", "상품 번호","상품명", "상품 가격", "주문 개수", "금액", ""};
	private DefaultTableModel model = new DefaultTableModel(header, 0) {
		public boolean isCellEditable(int row, int column) {
			return false;
		};
	};
	private JTable table = new JTable(model);
	private JScrollPane tableScroll = new JScrollPane(table);
	private JTextField costField = new JTextField(10);
	private MonthChooseDialog frame;
	boolean[] allTrue = {true, true, true, true, true, true, true, true, true, true, true, true};
//	private JMenuBar menuBar = new JMenuBar();
	private JPopupMenu pMenu = new JPopupMenu();
	private JMenuItem menu = new JMenuItem("구매내역 삭제");
	
	public PurchaseListFrame(String u_id) throws Exception {
		setTitle("구매리스트");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		this.u_id = u_id;
		frame = new MonthChooseDialog(PurchaseListFrame.this, "월 선택");
		
		formDesign();
		eventHandler();
		
		refresh(allTrue);
		monthLabel.setText("전체");
		
		setSize(500, 500);
		setVisible(true);
	}
	
	public void formDesign() {
		add(northPanel, BorderLayout.NORTH);
		northPanel.add(nameLabel);
		northPanel.add(monthLabel);
		northPanel.add(monthBtn);
		northPanel.add(allViewBtn);
		
		add(centerPanel, BorderLayout.CENTER);
		centerPanel.add(tableScroll);
		
		add(southPanel, BorderLayout.SOUTH);
		southPanel.add(new JLabel("총 금액"));
		southPanel.add(costField);
		
		centerPanel.add(pMenu);
		pMenu.add(menu);
	}
	
	public void eventHandler() throws Exception {
		monthBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					frame.setVisible(true);
					refresh(frame.getProposition());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		allViewBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					refresh(allTrue);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JTable t = (JTable) e.getSource();
				
				if (!(t.getSelectedRow() == -1)) {
					if (e.getButton() == e.BUTTON1 && e.getClickCount() == 2) {
						int yn = JOptionPane.showConfirmDialog(null, "재구매 하시겠습니까?", "정보", JOptionPane.YES_NO_OPTION);
						if (yn == JOptionPane.YES_OPTION) {
							// 해당 상품의 '구매' 폼으로 이동
							System.out.println(t.getValueAt(t.getSelectedRow(), 6));
						}
					}
					if (e.getButton() == e.BUTTON3) {
						pMenu.show(PurchaseListFrame.this, e.getXOnScreen(), e.getYOnScreen());
						
					}
				}
			}
		});
		
		menu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Date today = new SimpleDateFormat("yyyy-MM-dd").parse(LocalDate.now().toString());
					Date selectDay = new SimpleDateFormat("yyyy-MM-dd").parse(table.getValueAt(table.getSelectedRow(), 0).toString());
					
					if (today.equals(selectDay)) {
						try {
							DBInterface.stmt.execute("DELETE FROM `2021지방_1`.`purchase` WHERE (`pu_no` = '" + table.getValueAt(table.getSelectedRow(), 6).toString() + "')");
							
							OptionPane.showInfoMessage("삭제되었습니다.");
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					} else {
						//재고 내역 수정
						try {
							// 취소(삭제)
							DBInterface.stmt.execute("DELETE FROM `2021지방_1`.`purchase` WHERE (`pu_no` = '" + table.getValueAt(table.getSelectedRow(), 6).toString() + "')");
							
							// 재고 누적(업데이트)
							DBInterface.stmt.execute("UPDATE `2021지방_1`.`product` SET `p_stock` = `p_stock` + " + table.getValueAt(table.getSelectedRow(), 4).toString() + " WHERE (`p_no` = '" + table.getValueAt(table.getSelectedRow(), 6).toString() + "');");
							
							// 쿠폰 반환
							//??
							
							
							OptionPane.showInfoMessage("취소되었습니다.");
						} catch (SQLException e1) {
							e1.printStackTrace();
						}	
					}
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				System.out.println(table.getValueAt(table.getSelectedRow(), 6));
			}
		});
	}
	
	public void refresh(boolean[] months) throws Exception {
		// 회원 이름
		ResultSet rs = DBInterface.stmt.executeQuery("select * from user where u_id='" + u_id + "'");
		if(rs.next()) {
			u_no = rs.getInt(1);
			nameLabel.setText(rs.getString(4));
		}
		
		
		String monthStr = "";
		
		// 선택한 월
		if (frame.getPropositionCnt() == 12)
			monthLabel.setText("전체");
		else {
			for (int i = 0; i < months.length; i++) {
				if (months[i]) {
					if (monthStr.equals(""))
						monthStr = (i + 1) + "월";
					else
						monthStr += ", " + (i + 1) + "월";
				}
			}
			monthLabel.setText(monthStr);
		}
		
		// 테이블 데이터 추가
		monthStr = "";
		
		for (int i = 0; i < months.length; i++) {
			if (months[i]) {
				if (monthStr.equals(""))
					monthStr = (i + 1) + "";
				else
					monthStr += ", " + (i + 1);
			}
		}
		
		model.setNumRows(0);
		String[] row = new String[7];
		rs = DBInterface.stmt.executeQuery("select pu.pu_date, pu.p_no, p.p_name, format(pu.pu_price, '#,###'), pu.pu_count, format(pu.pu_price*pu.pu_count, '#,###'), pu.pu_no "
				+ "from purchase as pu "
				+ "join user as u on pu.u_no=u.u_no "
				+ "join product as p on pu.p_no=p.p_no "
				+ "where pu.u_no=" + u_no + " and month(pu_date) in (" + monthStr + ")");
		int cnt = 0;
		while(rs.next()) {
			row[0] = rs.getString(1);
			row[1] = rs.getString(2);
			row[2] = rs.getString(3);
			row[3] = rs.getString(4);
			row[4] = rs.getString(5);
			row[5] = rs.getString(6);
			row[6] = rs.getString(7);
			
			cnt++;
			model.addRow(row);
		}
		
		// id 행 숨기기
		table.getColumnModel().getColumn(6).setMinWidth(0);
		table.getColumnModel().getColumn(6).setMaxWidth(0);
		
		rs = DBInterface.stmt.executeQuery("select format(sum(pu_price*pu_count), '#,###') "
				+ "from purchase "
				+ "where u_no=" + u_no + " "
				+ "and month(pu_date) in (" + monthStr + ")");
		
		if (rs.next()) {
			costField.setText(rs.getString(1));
		}
		
		if (cnt == 0)
			OptionPane.showInfoMessage("구매 내역이 없습니다.");
	}
}
