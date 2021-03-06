package Frame;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import DBInterface.DBInterface;
import OptionPane.OptionPane;

public class LoginFrame extends JFrame {
	private JPanel p = new JPanel(new BorderLayout());
	private JPanel midPanel = new JPanel(new BorderLayout());
	private JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 20));
	private JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 20));
	private JPanel rightPanel = new JPanel(new BorderLayout());
	private JLabel title = new JLabel("기능마켓", JLabel.HORIZONTAL);
	private JTextField idField = new JTextField();
	private JTextField pwField = new JTextField();
	private JButton loginBtn = new JButton("로그인");
	private JLabel signupLabel = new JLabel("회원가입", JLabel.RIGHT);
	
	public LoginFrame() {
		setTitle("로그인");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		formDesign();
		eventHandler();
		setSize(380, 190);
		setVisible(true);
	}
	
	public void formDesign() {
		add(p);
		p.add(title, BorderLayout.NORTH);
		p.setBorder(BorderFactory.createEmptyBorder(10, 40, 5, 5));
		title.setFont(new Font("휴먼둥근헤드라인", Font.PLAIN, 25));
		
		p.add(midPanel);
		
		midPanel.add(leftPanel, BorderLayout.WEST);		
		leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 20));
		leftPanel.add(new JLabel("아이디", JLabel.RIGHT));
		leftPanel.add(new JLabel("비밀번호", JLabel.RIGHT));
		midPanel.add(centerPanel, BorderLayout.CENTER);
		centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 20));
		centerPanel.add(idField);
		centerPanel.add(pwField);
		midPanel.add(rightPanel, BorderLayout.EAST);
		rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		rightPanel.add(loginBtn);
		
		p.add(signupLabel, BorderLayout.SOUTH);
	}
	
	public void eventHandler() {
		loginBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (idField.getText().equals("") || pwField.getText().equals(""))
					OptionPane.showErrorMessage("빈칸이 존재합니다.");
				else {
					try {
						ResultSet rs = DBInterface.stmt.executeQuery("select u_id, u_pw, u_name from user "
								+ "where u_id='" + idField.getText() + "' and u_pw='" + pwField.getText() + "'");
						
						if (rs.next()) {
							OptionPane.showInfoMessage(rs.getString(3) + "님 환영합니다.");
							new ProductListFrame(idField.getText(), rs.getString(3));
							dispose();
						} else if (idField.getText().equals("admin") && pwField.getText().equals("1234")) {
							OptionPane.showInfoMessage("관리자로 로그인 되었습니다.");
							new ProductManagementFrame();
							dispose();
						} else {
							OptionPane.showErrorMessage("회원정보가 일치하지 않습니다.");
						}
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		
		signupLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				new SignupFrame();
			}
		});
	}
}
