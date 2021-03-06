package Frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import DBInterface.DBInterface;
import OptionPane.OptionPane;

public class PurchaseFrame extends JFrame implements Runnable {
	private int p_no;
	private String u_id;
	private JPanel p = new JPanel(null);
	private JLabel img = new JLabel();
	private JTextField pName = new JTextField();
	private JTextField pPrice = new JTextField();
	private JCheckBox check1 = new JCheckBox("10% 할인 쿠폰 적용");
	private JCheckBox check2 = new JCheckBox("30% 할인 쿠폰 적용");
	private JTextField pStock = new JTextField();
	private JTextArea pExplanation = new JTextArea();
	private JButton submitBtn = new JButton("구매하기");
	private JButton cancelBtn = new JButton("취소하기");
	private JLabel label1 = new JLabel("제품명");
	private JLabel label2 = new JLabel("가격");
	private JLabel label3 = new JLabel("수량");
	private JLabel label4 = new JLabel("상품설명");
	private JLabel label5 = new JLabel("같은 카테고리 목록");
	private JPanel listPanel = new JPanel();
	private JScrollPane listScroll = new JScrollPane(listPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	private JPanel[] list;
	
	private Thread thread = new Thread(PurchaseFrame.this);
	private int listCnt;
	
	public PurchaseFrame(String u_id, int p_no) {
		setTitle("구매");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.u_id = u_id;
		this.p_no = p_no;
		formDesign();
		eventHandler();
		setSize(600, 500);
		setVisible(true);
		
		thread.start();
	}
	
	public void formDesign() {
		add(p);
		p.add(img);
		img.setBounds(5, 5, 180, 180);
		
		p.add(label1);
		label1.setBounds(200, 15, 50, 20);
		p.add(pName);
		pName.setBounds(250, 10, 220, 30);
		
		p.add(label2);
		label2.setBounds(200, 65, 50, 20);
		p.add(pPrice);
		pPrice.setBounds(250, 60, 220, 30);
		
		p.add(check1);	p.add(check2);
		check1.setBounds(200, 110, 140, 20);
		check2.setBounds(340, 110, 140, 20);
		
		p.add(label3);
		label3.setBounds(200, 160, 50, 20);
		p.add(pStock);
		pStock.setBounds(250, 155, 220, 30);
		
		p.add(label4);
		label4.setBounds(5, 220, 50, 20);
		p.add(pExplanation);
		pExplanation.setBounds(5, 240, 300, 100);
		pExplanation.setLineWrap(true);
		pExplanation.setBorder(BorderFactory.createLineBorder(Color.black));
		
		p.add(submitBtn);	p.add(cancelBtn);
		submitBtn.setBounds(350, 250, 85, 25);
		cancelBtn.setBounds(440, 250, 85, 25);
		
		p.add(label5);
		label5.setBounds(5, 350, 150, 20);
		
		p.add(listScroll);
		listScroll.setBounds(5, 370, 575, 85);
		
		findProduct(p_no);
	}
	
	public void eventHandler() {
		submitBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (pStock.getText().equals("")) return;
				
				if ((pStock.getText().matches("[+-]?\\\\d*(\\\\.\\\\d+)?")) || (Integer.parseInt(pStock.getText()) < 1)) {
					OptionPane.showErrorMessage("1개 이상의 수량을 입력하세요.");
					return;
				}
				
				if (check1.isSelected() && check2.isSelected()) {
					OptionPane.showErrorMessage("할인 쿠폰은 중복 사용이 불가능합니다.");
					return;
				}
				
				try {
					ResultSet rs = DBInterface.stmt.executeQuery("select * from user where u_id='"+ u_id + "'");
					if (rs.next()) {
						int u_no = rs.getInt(1);
						
						if (check1.isSelected() && rs.getInt(7) == 0) {
							OptionPane.showErrorMessage("해당 쿠폰이 없습니다.");
							return;
						}
						
						if (check2.isSelected() && rs.getInt(8) == 0) {
							OptionPane.showErrorMessage("해당 쿠폰이 없습니다.");
							return;
						}
						
						ResultSet rs2 = DBInterface.stmt.executeQuery("select * from product where p_no='" + p_no + "'");
						
						if(rs2.next()) {
							int p_stock = rs2.getInt(5);
							
							if (Integer.parseInt(pStock.getText()) > p_stock) {
								OptionPane.showErrorMessage("재고가 부족합니다.");
								return;
							}
						}
						
						ResultSet rs3 = DBInterface.stmt.executeQuery("select * from purchase where p_no='" + p_no + "' and u_no='" + u_no + "' and pu_date=curdate()");
						
						if (rs3.next()) {
							OptionPane.showErrorMessage("동일한 상품을 이미 구매하였습니다.");
							return;
						}
						
						if (! pStock.getText().matches("[+-]?\\\\\\\\d*(\\\\\\\\.\\\\\\\\d+)?")) {
							ResultSet rs4 = DBInterface.stmt.executeQuery("select *  from product where p_no='" + p_no + "'");
							if (rs4.next()) {
								int h = 0;
								int coupon = 0;
								
								if (check1.isSelected()) {
									h = 1;
									coupon = 1;
								} else if (check2.isSelected()) {
									h = 3;
									coupon = 1;
								}
								
								int price = (int) (rs4.getInt(4) * Integer.parseInt(pStock.getText()) - (rs4.getInt(4) * h * 0.1));
								
								int yn = JOptionPane.showConfirmDialog(null, "총 가격이 " + new DecimalFormat("###,###").getInstance().format(price) + "원 입니다.\n결제하시겠습니까?", "결제", JOptionPane.YES_NO_OPTION);
								
								if (yn == JOptionPane.YES_OPTION) {
									DBInterface.stmt.execute("INSERT INTO `2021지방_1`.`purchase` "
											+ "(`p_no`, `pu_price`, `pu_count`, `coupon`, `u_no`, `pu_date`) "
											+ "VALUES ('" + p_no + "', '" + price + "', '" + pStock.getText() + "', '" + coupon + "', '" + u_no + "', curdate())");
									
									OptionPane.showInfoMessage("결제가 완료되었습니다.");
									dispose();
								}
							}
							
						}
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				thread.stop();
				dispose();
			}
		});
	}
	
	public void findProduct(int p_no) {
		try {
			ResultSet rs = DBInterface.stmt.executeQuery("select * from product where p_no='" + p_no + "'");
			
			if (rs.next()) {
				Image imgIcon = new ImageIcon("datafile/이미지/" + rs.getString(3) + ".jpg").getImage().getScaledInstance(180, 180, Image.SCALE_DEFAULT);
				img.setIcon(new ImageIcon(imgIcon));
				
				pName.setText(rs.getString(3));
				pPrice.setText(new DecimalFormat("###,###").getInstance().format(rs.getInt(4)));
				
				pExplanation.setText(rs.getString(6));
				
				int c_no = rs.getInt(2);
				
				rs = DBInterface.stmt.executeQuery("select count(*) from product where c_no='" + c_no + "' and not(p_no='" + p_no + "')");
				rs.next();
				listPanel.setLayout(new GridLayout(1, rs.getInt(1)));
				list = new JPanel[rs.getInt(1)];
				
				rs = DBInterface.stmt.executeQuery("select * from product where c_no='" + c_no + "' and not(p_no='" + p_no + "')");
				
				int cnt = 0;
				while(rs.next()) {
					list[cnt] = new JPanel(new BorderLayout());
					list[cnt].setBorder(BorderFactory.createLineBorder(Color.black));
					list[cnt].setPreferredSize(new Dimension(80, 80));
					JLabel listImgLabel = new JLabel();
					imgIcon = new ImageIcon("datafile/이미지/" + rs.getString(3) + ".jpg").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT);
					listImgLabel.setIcon(new ImageIcon(imgIcon));
					list[cnt].add(listImgLabel, BorderLayout.CENTER);
					list[cnt].add(new JLabel(rs.getString(3), JLabel.CENTER), BorderLayout.SOUTH);
					listPanel.add(list[cnt]);
					listCnt++;
					cnt++;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		pName.setEnabled(false);
		pPrice.setEnabled(false);
		pExplanation.setEnabled(false);
	}

	@Override
	public void run() {
		int cnt = 0;
		int max = listScroll.getHorizontalScrollBar().getMaximum();
		
		while (true) {
			try {
				Thread.sleep(1000);
				cnt += max / listCnt;
				listScroll.getHorizontalScrollBar().setValue(cnt);
				
				if (cnt > 880)
					cnt = 0;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
