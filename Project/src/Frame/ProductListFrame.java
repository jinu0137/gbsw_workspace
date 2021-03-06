package Frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import DBInterface.DBInterface;
import OptionPane.OptionPane;

public class ProductListFrame extends JFrame {
	private String id;
	private int categoryCnt = 0;
	private JLabel nameLabel;
	private JLabel category = new JLabel("카테고리");
	private JTextField productNameField = new JTextField();
	private JTextField minimumPriceField = new JTextField();
	private JTextField maximumPriceField = new JTextField();
	private JButton searchBtn = new JButton("검색");
	private JButton purchaseListBtn = new JButton("구매목록");
	private JButton eventBtn = new JButton("출석 이벤트");
	private JPanel topPanel = new JPanel(new BorderLayout());
	private JPanel leftPanel = new JPanel(new BorderLayout());
	private JPanel searchPanel = new JPanel(new BorderLayout());
	private JPanel categoryPanel = new JPanel(new BorderLayout());
	private JPanel centerPanel = new JPanel();
	private JPanel searchFieldPanel = new JPanel(new GridLayout(3, 2, 5, 5));
	private JPanel categoryListPanel = new JPanel();
	private JScrollPane categoryScroll = new JScrollPane(categoryListPanel);
	private JLabel[] categoryLabel;
	
	private JPanel picturePanel;
	private JPanel[] productPanel;
	private JScrollPane pictureScroll;
	
	private String[] header = {"상품번호	", "상품 카테고리", "상품 이름", "상품 가격", "상품 재고", "상품 설명"};
	private DefaultTableModel model = new DefaultTableModel(header, 0);
	private JTable table = new JTable(model);
	private JScrollPane tableScroll = new JScrollPane(table);
	
	public ProductListFrame(String id, String name) throws Exception {
		setTitle("상품목록");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.id = id;
		nameLabel = new JLabel("회원 : " + name);
		formDesign();
		eventHandler();
		setSize(700, 500);
		setVisible(true);
	}
	
	public void formDesign() throws Exception {
		add(topPanel, BorderLayout.NORTH);
		topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		topPanel.add(nameLabel, BorderLayout.WEST);
		nameLabel.setFont(new Font(null, Font.BOLD, 20));
		topPanel.add(eventBtn, BorderLayout.EAST);
		
		add(leftPanel, BorderLayout.WEST);
		leftPanel.add(searchPanel, BorderLayout.NORTH);
		searchPanel.add(searchFieldPanel, BorderLayout.NORTH);
		searchFieldPanel.add(new JLabel("상품몀"));
		searchFieldPanel.add(productNameField);
		searchFieldPanel.add(new JLabel("최저 가격"));
		searchFieldPanel.add(minimumPriceField);
		searchFieldPanel.add(new JLabel("최대 가격"));
		searchFieldPanel.add(maximumPriceField);
		searchPanel.add(searchBtn, BorderLayout.SOUTH);
		
		leftPanel.add(categoryPanel, BorderLayout.CENTER);
		categoryPanel.add(categoryScroll, BorderLayout.CENTER);
		categoryListPanel.setLayout(new BoxLayout(categoryListPanel, BoxLayout.Y_AXIS));
		categoryListPanel.add(category);
		category.setFont(new Font(null, Font.BOLD, 20));
		ResultSet rs = DBInterface.stmt.executeQuery("select count(*) from category");
		rs.next();
		categoryLabel = new JLabel[rs.getInt(1)];
		rs = DBInterface.stmt.executeQuery("select * from category");
		int cnt = 0;
		while (rs.next()) {
			categoryLabel[cnt] = new JLabel(rs.getString(2));
			categoryLabel[cnt].setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
			categoryListPanel.add(categoryLabel[cnt]);
			cnt++;
		}
		categoryPanel.add(purchaseListBtn, BorderLayout.SOUTH);
		
		add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		categoryRefresh();
		
	}
	
	public void eventHandler() {
		for (int i = 0; i < categoryLabel.length; i++) {
			categoryLabel[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					try {
						if (e.getClickCount() == 2) {
							for (int j = 0; j < categoryLabel.length; j++) {
								if (e.getSource().equals(categoryLabel[j])) {
									categoryCnt = j;
									productNameField.setText("");
									minimumPriceField.setText("");
									maximumPriceField.setText("");
									categoryRefresh();
								}
							}
						}
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
		}
		
		searchBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String productName = "";
					String minPrice = "";
					String maxPrice = "";
					
					if (!minimumPriceField.getText().equals("") && !maximumPriceField.getText().equals("")) {
						if (Integer.parseInt(minimumPriceField.getText()) > Integer.parseInt(maximumPriceField.getText())) {
							OptionPane.showErrorMessage("최대 가격은 최저 가격보다 커야 합니다.");
							return;
						}
					}
					
					if (!productNameField.getText().equals(""))
						productName = " and p_name like '%" + productNameField.getText() + "%'";
					else
						productName = " and p_name like '%'";
					
					if (!minimumPriceField.getText().equals(""))
						minPrice = " and p_price >= " + minimumPriceField.getText();
					
					if (!maximumPriceField.getText().equals(""))
						maxPrice = " and p_price <=" + maximumPriceField.getText();
					
					search(productName, minPrice, maxPrice);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		purchaseListBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new PurchaseListFrame(id);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		eventBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new AttendanceFrame(id);
			}
		});
	}
	
	public void categoryRefresh() throws Exception {
		for (int i = 0; i < categoryLabel.length; i++) {
			categoryLabel[i].setForeground(Color.BLACK);
		}
		categoryLabel[categoryCnt].setForeground(Color.RED);
		
		try {
			model.setNumRows(0);
		} catch (Exception e) {}
		
		
		ResultSet rs = DBInterface.stmt.executeQuery("select count(*) from product where c_no='" + (categoryCnt + 1) + "'");
		if (rs.next()) {
			int productCnt = rs.getInt(1);
			int row = (productCnt - 1) / 3 + 1;
			picturePanel = new JPanel(new GridLayout(row, 3));
			
			rs = DBInterface.stmt.executeQuery("select * from product where c_no='" + (categoryCnt + 1) + "'");
			productPanel = new JPanel[productCnt];
			
			String[] record = new String[6];
			int cnt = 0;
			while(rs.next()) {
				productPanel[cnt] = new JPanel(new BorderLayout());
				
				Image img = new ImageIcon("datafile/이미지/" + rs.getString(3) + ".jpg").getImage().getScaledInstance(180, 130, Image.SCALE_DEFAULT);
				JLabel imgLabel = new JLabel(new ImageIcon(img));
				int p_no = rs.getInt(1);
				imgLabel.setToolTipText(rs.getString(6));
				imgLabel.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if (e.getClickCount() == 2) {
							new PurchaseFrame(id, p_no);
						}
					}
				});
				productPanel[cnt].add(imgLabel, BorderLayout.CENTER);
				productPanel[cnt].add(new JLabel(rs.getString(3), JLabel.CENTER), BorderLayout.SOUTH);
				productPanel[cnt].setBorder(new LineBorder(Color.BLACK));
				picturePanel.add(productPanel[cnt]);
				
				// 테이블 설정
				record[0] = rs.getString(1);
				record[1] = categoryLabel[categoryCnt].getText();
				record[2] = rs.getString(3);
				record[3] = new DecimalFormat("###,###").getInstance().format(rs.getInt(4)).toString();
				record[4] = rs.getString(5);
				record[5] = rs.getString(6);
				
				model.addRow(record);
				cnt++;
			}
		}
		pictureScroll = new JScrollPane(picturePanel);
		
		centerPanel.removeAll();
		centerPanel.add(pictureScroll);
		centerPanel.add(tableScroll);
		centerPanel.revalidate();
	}
	
	public void search(String productName, String minPrice, String maxPrice) throws Exception {
		
		try {
			model.setNumRows(0);
		} catch (Exception e) {}
		
		ResultSet rs = DBInterface.stmt.executeQuery("select count(*) from product where c_no='" + (categoryCnt + 1) + "' " + productName + minPrice + maxPrice);
		
		if (rs.next()) {
			int productCnt = rs.getInt(1);
			int row = (productCnt - 1) / 3 + 1;
			picturePanel = new JPanel(new GridLayout(row, 3));
			
			rs = DBInterface.stmt.executeQuery("select * from product where c_no='" + (categoryCnt + 1) + "' " + productName + minPrice + maxPrice);
			productPanel = new JPanel[productCnt];
			
			String[] record = new String[6];
			int cnt = 0;
			while(rs.next()) {
				productPanel[cnt] = new JPanel(new BorderLayout());
				
				Image img = new ImageIcon("datafile/이미지/" + rs.getString(3) + ".jpg").getImage().getScaledInstance(180, 130, Image.SCALE_DEFAULT);
				JLabel imgLabel = new JLabel(new ImageIcon(img));
				imgLabel.setToolTipText(rs.getString(6));
				imgLabel.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if (e.getClickCount() == 2) {
							//구매 폼
						}
					}
				});
				productPanel[cnt].add(imgLabel, BorderLayout.CENTER);
				productPanel[cnt].add(new JLabel(rs.getString(3), JLabel.CENTER), BorderLayout.SOUTH);
				productPanel[cnt].setBorder(new LineBorder(Color.BLACK));
				picturePanel.add(productPanel[cnt]);
				
				// 테이블 설정
				record[0] = rs.getString(1);
				record[1] = categoryLabel[categoryCnt].getText();
				record[2] = rs.getString(3);
				record[3] = new DecimalFormat("###,###").getInstance().format(rs.getInt(4)).toString();
				record[4] = rs.getString(5);
				record[5] = rs.getString(6);
				
				model.addRow(record);
				cnt++;
			}
			if (productCnt == 0) {
				OptionPane.showInfoMessage("검색 결과가 없습니다.");
			}
		}
		pictureScroll = new JScrollPane(picturePanel);
		
		centerPanel.removeAll();
		centerPanel.add(pictureScroll);
		centerPanel.add(tableScroll);
		centerPanel.revalidate();
	}
}
