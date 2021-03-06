package Frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MonthChooseFrame extends JFrame {
	private JPanel monthPanel = new JPanel(new GridLayout(3, 4));
	private MonthCircle[] month = new MonthCircle[12];
	private JButton btn = new JButton("확인");
	
	public MonthChooseFrame() {
		setTitle("월 선택");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		formDesign();
		eventHandler();
		setSize(300, 300);
		setVisible(true);
	}
	
	public void formDesign() {
		add(monthPanel, BorderLayout.CENTER);
		monthPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 0));
		for (int i = 0; i < 12; i++) {
			month[i] = new MonthCircle(i + 1);
			monthPanel.add(month[i]);
		}
		JPanel pp = new JPanel(new FlowLayout(FlowLayout.CENTER));
		pp.add(btn);
		
		add(pp, BorderLayout.SOUTH);
	}
	
	public void eventHandler() {
		
	}
	
	public class MonthCircle extends JPanel {
		private int month;
		
		public MonthCircle(int month) {
			this.month = month;
			setLayout(new BorderLayout());
		}
		
		@Override
		public void paint(Graphics g) {
			// TODO Auto-generated method stub
			super.paint(g);
			g.setColor(Color.YELLOW);
			g.fillOval(0, 0, 50, 50);
			g.setColor(Color.BLACK);
			g.drawOval(0, 0, 50, 50);
			g.setFont(new Font(null, Font.BOLD, 12));
			g.drawString(month + "월", 15, 30);
		}
	}
}
