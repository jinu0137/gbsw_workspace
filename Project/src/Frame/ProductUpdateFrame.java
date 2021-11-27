package Frame;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class ProductUpdateFrame extends JFrame {
	private JLabel img = new JLabel();
	private JButton picBtn = new JButton("사진 넣기");
	private JTextField name = new JTextField();
	private JComboBox<String> category = new JComboBox<String>();
	private JTextField price = new JTextField();
	private JTextField stock = new JTextField();
	private JTextField explanation = new JTextField();
	private JButton updateBtn = new JButton("수정");
	private JButton cancelBtn = new JButton("수정");
	
	public ProductUpdateFrame() {
		setTitle("상품수정");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		formDesign();
		eventHandler();
		setSize(400, 300);
		setVisible(true);
	}
	
	public void formDesign() {}
	
	public void eventHandler() {}
}
