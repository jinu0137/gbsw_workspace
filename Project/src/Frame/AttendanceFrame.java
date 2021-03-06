package Frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import DBInterface.DBInterface;
import OptionPane.OptionPane;

public class AttendanceFrame extends JFrame {
	private int u_no;
	private String u_id;
	private JLabel title = new JLabel("");
	private JPanel centerPanel = new JPanel(new BorderLayout());
	private JPanel headPanel = new JPanel(new GridLayout(1, 7, 0, 0));
	private String[] header = {"일", "월", "화", "수", "목", "금", "토"};
	private JPanel gridPanel = new JPanel(new GridLayout(5, 7, 0, 0));
	private dayPanel[] dp;
	private JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	private JButton btn = new JButton("쿠폰 받기");
	
	public AttendanceFrame(String u_id) {
		this.u_id = u_id;
		u_noSearch();
		
		setTitle("출석");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		formDesign();
		eventHandler();
		setSize(400, 400);
		setVisible(true);
	}
	
	public void formDesign() {
		for (int i = 0; i < header.length; i++) {
			JLabel h = new JLabel(header[i], JLabel.CENTER);
			if (i == 0)
				h.setForeground(Color.RED);
			if (i == 6)
				h.setForeground(Color.BLUE);
			
			headPanel.add(h);
		}
		
		add(title, BorderLayout.NORTH);
		title.setFont(new Font(null, Font.BOLD, 25));
		
		add(centerPanel, BorderLayout.CENTER);
		centerPanel.add(headPanel, BorderLayout.NORTH);
		headPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		
		calendarSetting();
		centerPanel.add(gridPanel, BorderLayout.CENTER);
		
		add(southPanel, BorderLayout.SOUTH);
		southPanel.add(btn);
	}
	
	public void eventHandler() {
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Calendar date = Calendar.getInstance();
					int year = date.get(Calendar.YEAR);					// 연도	
					int month = date.get(Calendar.MONTH) + 1;			// 월
				
					ResultSet rs = DBInterface.stmt.executeQuery("select count(*) from attendance where u_no='" + u_no + "' and year(a_date)='" + year + "' month(a_date)='" + month + "'");
					
					if (rs.next()) {
						int aCnt = rs.getInt(1);
						
						rs = DBInterface.stmt.executeQuery("select * from coupon where u_no='" + u_id + "' and c_date='" + year + "-" + String.format("%02d", month) + "'");
						
						if (rs.next()) {
							if (aCnt >= 5 && rs.getInt(4) == 1 && rs.getInt(5) == 1) {
								OptionPane.showErrorMessage("쿠폰을 이미 받았습니다");
							} else if (aCnt >= 3 && rs.getInt(4) == 1) {
								OptionPane.showErrorMessage("쿠폰을 이미 받았습니다");
							} else {
								if (aCnt >= 5) {
									DBInterface.stmt.execute("INSERT INTO `2021지방_1`.`coupon` (`u_no`, `c_date`, `c_10percent`, `c_30percent`) VALUES ('" + u_no + "', '" + year + "-" + String.format("%02d", month) + "', '1', '1')");
								} else if (aCnt >= 3) {
									DBInterface.stmt.execute("INSERT INTO `2021지방_1`.`coupon` (`u_no`, `c_date`, `c_10percent`, `c_30percent`) VALUES ('" + u_no + "', '" + year + "-" + String.format("%02d", month) + "', '1', '0')");
								}
								
								new CouponFrame(u_no, aCnt);
							}
						} 
					}
				} catch (Exception e1) {}
			}
		});
	}
	
	public void calendarSetting() {
		Calendar date = Calendar.getInstance();
		
		int year = date.get(Calendar.YEAR);					// 연도
		int month = date.get(Calendar.MONTH) + 1;			// 월
		date.set(year, month - 1, 1);						// 해당 연도와 월의 첫째 날로 설정
		
		int week = date.get(Calendar.DAY_OF_WEEK);			// 월의 시작 요일
		int lastDay = date.getActualMaximum(Calendar.DATE);	// 월의 마지막 날짜
		
		for (int i = 0; i < week - 1; i++) {
			gridPanel.add(new JLabel());
		}
		
		dp = new dayPanel[lastDay];
		for (int i = 1; i <= lastDay; i++) {
			dp[i - 1] = new dayPanel(i);
			gridPanel.add(dp[i  - 1]);
		}
		
		title.setText(year + "년 " + month + "월");
	}
	
	class dayPanel extends JPanel {
		private JLabel label = new JLabel("", JLabel.CENTER);
		private int day;
		private boolean attendance = false;
		
		private Calendar date = Calendar.getInstance();
		private int year = date.get(Calendar.YEAR);					// 연도
		private int month = date.get(Calendar.MONTH) + 1;			// 월
		
		public dayPanel(int day) {
			this.day = day;
			date.set(year, month - 1, 1);							// 해당 연도와 월의 첫째 날로 설정
			
			setLayout(new BorderLayout());
			
			label.setText(day + "");
			add(label);
			setBorder(BorderFactory.createLineBorder(Color.BLACK));
			
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					Date date = new Date();
					
					// 오늘 날짜인지 확인 : attendance t f 여부 확인하고 출석시키기
					if (Integer.parseInt(label.getText()) == date.getDate()) {
						try {
							if (attendance) {
								OptionPane.showErrorMessage("이미 출석체크를 했습니다.");
							} else {
								DBInterface.stmt.execute("INSERT INTO `2021지방_1`.`attendance` (`u_no`, `a_date`) VALUES ('" + u_no + "', curdate())");
								repaint();
							}
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					} else 
						OptionPane.showErrorMessage("출석체크가 불가능한 날짜입니다.");
				}
			});
		}
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			try {
				// 오늘 날짜일 경우 빨간색
				ResultSet rs = DBInterface.stmt.executeQuery("SELECT *, day(a_date) FROM 2021지방_1.attendance as a join user as u on a.u_no = u.u_no where u_id='" + u_id + "' and year(a.a_date)=" + year + " and month(a.a_date)=" + month + " and day(a.a_date)=" + day);
					
				if (rs.next()) {
					if (rs.getInt(12) == day) {
						g.drawOval(5, 2, getWidth() - 10, getHeight() - 4);
						attendance = true;
					}
				} else if ((day == new Date().getDate()) && !attendance) {
					g.setColor(Color.red);
					g.drawOval(5, 2, getWidth() - 10, getHeight() - 4);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void u_noSearch() {
		try {
			ResultSet rs = DBInterface.stmt.executeQuery("select * from user where u_id='" + u_id + "'");
			if (rs.next()) {
				u_no = rs.getInt(1);
			}
		} catch (Exception e) {
			
		}
	}
}
