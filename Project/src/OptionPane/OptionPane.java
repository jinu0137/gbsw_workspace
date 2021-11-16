package OptionPane;

import javax.swing.JOptionPane;

public class OptionPane extends JOptionPane {
	public static void showInfoMessage(String str) {
		JOptionPane.showMessageDialog(null, str, "정보", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static void showErrorMessage(String str) {
		JOptionPane.showMessageDialog(null, str, "경고", JOptionPane.ERROR_MESSAGE);
	}
}
