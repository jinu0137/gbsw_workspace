package MainClass;

import DBInterface.DBInterface;
import Frame.LoginFrame;
import OptionPane.OptionPane;

public class MainClass {
	public static void main(String[] args) throws Exception {
		DBInterface.init();
		new LoginFrame();
		new OptionPane();
	}
}
