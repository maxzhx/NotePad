import java.awt.BorderLayout;

import javax.swing.JFrame;


public class NotePadTest extends JFrame{
	
	NotePadTest(){
		setLayout(new BorderLayout());
		setBounds(100, 100, 500, 500);
		setVisible(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		NotePad notePad=new NotePad(this);
		add(notePad,BorderLayout.CENTER);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new  NotePadTest();
	}

}