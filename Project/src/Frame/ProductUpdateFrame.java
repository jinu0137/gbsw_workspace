package Frame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import DBInterface.DBInterface;
import OptionPane.OptionPane;

public class ProductUpdateFrame extends JFrame {
	private int p_no;
	
	private JLabel img = new JLabel();
	private JButton imgBtn  = new JButton("사진 넣기");
	private JTextField name = new JTextField();
	private JComboBox<String> categoryCombo = new JComboBox<String>();
	private JTextField categoryText = new JTextField();
	private JTextField price = new JTextField();
	private JTextField stock = new JTextField();
	private JTextField explanation = new JTextField();
	private JButton updateBtn = new JButton("수정");
	private JButton cancelBtn = new JButton("취소");
	
	private JPanel imgPanel = new JPanel(new BorderLayout());
	private JPanel textPanel = new JPanel(new GridBagLayout());
	private GridBagConstraints gbc = new GridBagConstraints();
	private JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	
	private JFileChooser fc = new JFileChooser();
	private String filePath;
	private String selectedFile;
	
	public ProductUpdateFrame(int p_no) {
		this.p_no = p_no;
		setTitle("상품수정");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		formDesign();
		eventHandler();
		setCombo();
		setData();
		setSize(400, 500);
		setVisible(true);
	}
	
	public void formDesign() {
		setLayout(new GridLayout(2, 1));
		add(imgPanel);
		imgPanel.add(img);
		imgPanel.add(imgBtn, BorderLayout.SOUTH);
		
		add(textPanel);
		gbc.weightx=1.0;
		gbc.weighty=1.0;
		
		gbc.fill = GridBagConstraints.BOTH;
		
		add(textPanel);
		layout(new JLabel("상품명:"), 0, 0, 1, 1);
		layout(name, 1, 0, 1, 1);
		
		layout(new JLabel("카테고리:"), 0, 1, 1, 1);
		layout(categoryCombo, 1, 1, 1, 1);
		JPanel visiblePanel = new JPanel(new BorderLayout());
		layout(visiblePanel, 2, 1, 1, 1);
		visiblePanel.add(categoryText);
		
		layout(new JLabel("가격:"), 0, 2, 1, 1);
		layout(price, 1, 2, 1, 1);
		
		layout(new JLabel("재고:"), 0, 3, 1, 1);
		layout(stock, 1, 3, 1, 1);
		
		layout(new JLabel("설명:"), 0, 4, 1, 1);
		layout(explanation, 1, 4, 2, 1);
		
		layout(btnPanel, 0, 5, 3, 1);
		btnPanel.add(updateBtn);
		btnPanel.add(cancelBtn);
	}
	
	public void eventHandler() {
		categoryCombo.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				categoryText.setVisible(false);
				if (categoryCombo.getSelectedItem().toString().equals("기타")) {
					categoryText.setVisible(true);
				}
				revalidate();
			}
		});
		
		imgBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int sv = fc.showOpenDialog(null);
				
				if (sv == 0) {
					try {
						selectedFile = fc.getSelectedFile().getPath();
						img.setIcon(new ImageIcon(selectedFile));
						
						
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		updateBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateProduct();
			}
		});
		
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}
	
	public void layout(Component obj, int x, int y, int width, int height) {
		gbc.gridx = x;					// 시작위치 x
		gbc.gridy = y;					// 시작위치 y
		gbc.gridwidth = width;			// 컨테이너 너비
		gbc.gridheight = height;		// 컨테이너 높이
		textPanel.add(obj, gbc);
	}
	
	public void setCombo() {
		try {
			ResultSet rs = DBInterface.stmt.executeQuery("select * from category");
			
			while(rs.next()) {
				categoryCombo.addItem(rs.getString(2));
			}
			
			categoryCombo.addItem("기타");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setData() {
		try {
			ResultSet rs = DBInterface.stmt.executeQuery("SELECT * FROM product where p_no='"+ p_no +"'");
			
			if (rs.next()) {
				Image image = new ImageIcon("datafile/이미지/" + rs.getString(3) + ".jpg").getImage();
				img.setIcon(new ImageIcon(image.getScaledInstance(400, 300, Image.SCALE_DEFAULT)));
				categoryCombo.setSelectedIndex(rs.getInt(2) - 1);
				name.setText(rs.getString(3));
				price.setText(rs.getString(4));
				stock.setText(rs.getString(5));
				explanation.setText(rs.getString(6));
				
				filePath = "datafile/이미지/" + rs.getString(3) + ".jpg";
				selectedFile = filePath;
				
				name.setEnabled(false);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateProduct() {
		try {
			// 입력값 체크
			if (name.getText().equals("") ||
					price.getText().equals("") ||
					stock.getText().equals("") ||
					explanation.getText().equals(""))
				OptionPane.showErrorMessage("빈칸이 있습니다.");
			
			if (categoryCombo.getSelectedItem().toString().equals("기타")) {
				for (int i = 0; i < categoryCombo.getItemCount(); i++) {
					if (categoryCombo.getItemAt(i).equals(categoryText.getText())) {
						OptionPane.showErrorMessage("이미 있는 카테고리입니다.");
						break;
					}						
				}
			}
			
			if (categoryCombo.getSelectedItem().toString().equals("기타") && categoryText.getText().equals(""))
				OptionPane.showErrorMessage("카테고리를 입력해 주세요.");
			
			for (int i = 0; i < price.getText().length(); i++) {
				if (! Character.isDigit(price.getText().charAt(i))) {
					OptionPane.showErrorMessage("숫자로 입력하세요.");
					break;
				}
			}
			
			for (int i = 0; i < stock.getText().length(); i++) {
				if (! Character.isDigit(stock.getText().charAt(i))) {
					OptionPane.showErrorMessage("숫자로 입력하세요.");
					break;
				}
			}
			
			ResultSet rs = DBInterface.stmt.executeQuery("select * from product where p_no='" + p_no + "'");
			
			if(rs.next()) {
				if (selectedFile.equals(filePath) && 
						rs.getInt(2) == categoryCombo.getSelectedIndex() + 1 &&
						rs.getString(4).equals(price.getText()) &&
						rs.getString(5).equals(stock.getText()) && 
						rs.getString(6).equals(explanation.getText())) {
					OptionPane.showErrorMessage("수정한 내용이 없습니다.");
					return;
				}
			}
			
			// 이미지 저장
			File saveImg = new File(selectedFile);
			
			if (!saveImg.exists()) {
				saveImg.createNewFile();
				BufferedImage bi = ImageIO.read(saveImg);
				ImageIO.write(bi, "jpg", saveImg);
			}
			
			if (categoryCombo.getSelectedIndex() == categoryCombo.getItemCount() - 1) {
				DBInterface.stmt.execute("INSERT INTO `2021지방_1`.`category` (`c_name`) VALUES ('" + categoryText.getText() + "')");
			}
			
			DBInterface.stmt.execute("UPDATE `2021지방_1`.`product` SET `c_no` = '" + (categoryCombo.getSelectedIndex() + 1) + "', `p_price` = '" + price.getText() + "', `p_stock` = '" + stock.getText() + "', `p_explanation` = '" + explanation.getText() + "' WHERE (`p_no` = '" + p_no + "')");
			
			OptionPane.showInfoMessage("상품정보가 수정되었습니다.");
			dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
