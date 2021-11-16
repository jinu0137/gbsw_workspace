package Frame;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import DBInterface.DBInterface;
import OptionPane.OptionPane;

public class SignupFrame extends JFrame {
	private String[] labelStr = {"이름", "아이디", "비밀번호", "비밀번호체크", "전화번호", "생년월일"};
	private JTextField[] field = new JTextField[6];
	private JButton checkBtn = new JButton("중복확인");
	private JButton submitBtn = new JButton("회원가입");
	private JButton cancelBtn = new JButton("취소");
	private JPanel leftPanel = new JPanel(new GridLayout(6, 1, 0, 20));
	private JPanel centerPanel = new JPanel(new GridLayout(6, 1, 0, 20));
	private JPanel rightPanel = new JPanel(new GridLayout(6, 1, 0, 10));
	private JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	private Boolean check = false;
	
	public SignupFrame() {
		setTitle("회원가입");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		formDesign();
		eventHandler();
		setSize(400, 400);
		setVisible(true);
	}
	
	public void formDesign() {
		add(leftPanel, BorderLayout.WEST);
		leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 40, 20));
		for (int i = 0; i < labelStr.length; i++) {
			leftPanel.add(new JLabel(labelStr[i] + ":"));
		}
		
		add(centerPanel, BorderLayout.CENTER);
		centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 5, 40, 5));
		for (int i = 0; i < labelStr.length; i++) {
			field[i] = new JTextField();
			centerPanel.add(field[i]);
		}
		
		add(rightPanel, BorderLayout.EAST);
		rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 40, 20));
		rightPanel.add(new JPanel());
		rightPanel.add(checkBtn);
		
		add(southPanel, BorderLayout.SOUTH);
		southPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 20));
		southPanel.add(submitBtn);
		southPanel.add(cancelBtn);
	}
	
	public void eventHandler() {
		checkBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (field[1].getText().equals("")) {
					OptionPane.showErrorMessage("아이디를 입력하세요.");
				} else {
					try {
						ResultSet rs = DBInterface.stmt.executeQuery("select * from user where u_id='" + field[1].getText() + "'");
						
						if (rs.next()) {
							OptionPane.showErrorMessage("이미 존재하는 아이디입니다.");
							field[1].setText("");
							field[1].requestFocus();
						} else {
							OptionPane.showInfoMessage("사용가능 한 아이디입니다.");
							check = true;
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		
		field[1].addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				check = false;
			}
		});
		
		field[4].addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.paramString().indexOf("Backspace") != -1) return;
				
				if (!(e.getKeyChar() >= '0' && e.getKeyChar() <= '9')) {
					OptionPane.showErrorMessage("문자는 입력이 불가합니다.");
					e.consume();
					field[4].requestFocus();
				}
				
				int length = field[4].getText().length();
				
				if (length >= 13) {
					e.consume();
				} else {
					if (length < 3) {} 
					else if (length == 3) {
						field[4].setText(field[4].getText().toString() + "-");
					}
					else if (length == 8) {
						field[4].setText(field[4].getText().toString() + "-");
					}
				}
			}
		});
		
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		submitBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!check) {
					OptionPane.showErrorMessage("아이디 중복확인을 해주세요.");					
				} else if (!field[2].getText().equals(field[3].getText())) {
					OptionPane.showErrorMessage("비밀번호가 일치하지 않습니다.");
				} else {
					
				}
			}
		});
	}
}
