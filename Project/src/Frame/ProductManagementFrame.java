package Frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EventObject;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import DBInterface.DBInterface;

public class ProductManagementFrame extends JFrame {
	private String[] header = {"상품 번호", "상품 카테고리", "상품명", "상품 가격", "상품 재고", "상품 설명"};
	private DefaultTableModel model = new DefaultTableModel(header, 0) {
		public boolean isCellEditable(int row, int column) {
			return false;
		};
	};
	private JTable table = new JTable(model);
	private JScrollPane scroll = new JScrollPane(table);
	
	private JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	private JComboBox<String> combo = new JComboBox<String>();
	
	public ProductManagementFrame() {
		setTitle("상품관리");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		formDesign();
		eventHandler();
		setSize(500, 500);
		setVisible(true);
	}
	
	public void formDesign() {
		addComboItem();
		refresh();
		
		add(scroll, BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);
		southPanel.add(combo);
	}
	
	public void eventHandler() {
		combo.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				refresh();
			}
		});
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				table.setSelectionBackground(Color.YELLOW);
				
				if (e.getClickCount() == 2)
					new ProductUpdateFrame(Integer.parseInt(table.getValueAt(table.getSelectedRow(), 0).toString()));
			}
		});
	}
	
	public void refresh() {
		model.setNumRows(0);
		
		try {
			int category = combo.getSelectedIndex();
			
			ResultSet rs;
			
			if (category == 0)
				rs = DBInterface.stmt.executeQuery("SELECT p.p_no, c.c_name, p.p_name, format(p.p_price,'#,###'), p.p_stock, p.p_explanation "
						+ "FROM product as p "
						+ "join category as c "
						+ "on p.c_no=c.c_no");
			else 
				rs = DBInterface.stmt.executeQuery("SELECT p.p_no, c.c_name, p.p_name, format(p.p_price,'#,###'), p.p_stock, p.p_explanation "
						+ "FROM product as p "
						+ "join category as c "
						+ "on p.c_no=c.c_no "
						+ "where p.c_no=" +  category);
			
			String[] row = new String[6];
			while(rs.next()) {
				row[0] = rs.getString(1);
				row[1] = rs.getString(2);
				row[2] = rs.getString(3);
				row[3] = rs.getString(4);
				row[4] = rs.getString(5);
				row[5] = rs.getString(6);
				
				model.addRow(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addComboItem() {
		combo.addItem("전체");
		
		try {
			ResultSet rs = DBInterface.stmt.executeQuery("select * from category");
			
			while (rs.next()) {
				combo.addItem(rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
