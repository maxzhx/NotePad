import java.awt.BorderLayout;
import java.awt.CheckboxMenuItem;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.undo.UndoManager;

public class NotePad extends JPanel{
	JFrame mainFrame;
	MyMenuBar myMenuBar=new MyMenuBar();
	MyPopupMenu myPopupMenu=new MyPopupMenu();
	UndoManager undoManager;
	JTextArea textArea=new JTextArea();
	JScrollPane textScrollPane=new JScrollPane(textArea);
	Scanner loadScanner;
	JFontChooser fontChooser=new JFontChooser();
	JFileChooser fileChooser;
	File textFile;
	static String lastpath = "";
	boolean saveFlag=false;//判断是否需要储存
	String currentPath;
	Finder finder=new Finder();
	MyThread myThread=new MyThread();
	NotePad(JFrame frame){
//		textArea.setLineWrap(true);
		mainFrame=frame;
		mainFrame.setJMenuBar(myMenuBar);
		setLayout(new BorderLayout());
		textArea.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				saveFlag=true;
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				saveFlag=true;
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				saveFlag=true;
			}
		});
		textArea.setComponentPopupMenu(myPopupMenu);
		undoManager=new UndoManager();
		textArea.getDocument().addUndoableEditListener(new UndoableEditListener() {
			
			@Override
			public void undoableEditHappened(UndoableEditEvent e) {
				// TODO Auto-generated method stub
				undoManager.addEdit(e.getEdit());
			}
		});
//		System.out.print(textArea.getSelectedText());
		mainFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				closeApp();
			}
		});
		add(textScrollPane,BorderLayout.CENTER);
		createText();
		myThread.start();
	}
	
	//右键菜单
	class MyPopupMenu extends JPopupMenu{
		JMenuItem undoPopupItem=new JMenuItem("Undo");
		JMenuItem redoPopupItem=new JMenuItem("Redo");
		JMenuItem cutPopupItem=new JMenuItem("Cut");
		JMenuItem copyPopupItem=new JMenuItem("Copy");
		JMenuItem pastePopupItem=new JMenuItem("Paste");
		JMenuItem deletePopupItem=new JMenuItem("Delete");
		JMenuItem selectAllPopupItem=new JMenuItem("Select All");
		JMenuItem findPopupItem=new JMenuItem("Find");
		MyPopupMenu(){
			undoPopupItem.addActionListener(popupMenuItemListener);
			redoPopupItem.addActionListener(popupMenuItemListener);
			cutPopupItem.addActionListener(popupMenuItemListener);
			copyPopupItem.addActionListener(popupMenuItemListener);
			pastePopupItem.addActionListener(popupMenuItemListener);
			deletePopupItem.addActionListener(popupMenuItemListener);
			selectAllPopupItem.addActionListener(popupMenuItemListener);
			findPopupItem.addActionListener(popupMenuItemListener);
			add(undoPopupItem);
			add(redoPopupItem);
			addSeparator();
			add(cutPopupItem);
			add(copyPopupItem);
			add(pastePopupItem);
			add(deletePopupItem);
			add(selectAllPopupItem);
			addSeparator();
			add(findPopupItem);
		}
		
		ActionListener popupMenuItemListener=new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (e.getSource()==undoPopupItem) {
					if (undoManager.canUndo()) {
						undoManager.undo();
					}
				}
				else if (e.getSource()==redoPopupItem) {
					if (undoManager.canRedo()) {
						undoManager.redo();
					}
				}
				else if (e.getSource()==cutPopupItem) {
					textArea.cut();
				}
				else if (e.getSource()==copyPopupItem) {
					textArea.copy();
				}
				else if (e.getSource()==pastePopupItem) {
					textArea.paste();
				}
				else if (e.getSource()==deletePopupItem) {
					textArea.replaceRange("",textArea.getSelectionStart(),textArea.getSelectionEnd());
				}
				else if (e.getSource()==selectAllPopupItem) {
//					textArea.select(0, textArea.getText().length());
					textArea.selectAll();
				}
				else if (e.getSource()==findPopupItem) {
					finder.find();
				}
			}
		};
	}
	
	//顶部菜单栏
	class MyMenuBar extends JMenuBar{
		JMenu fileMenu=new JMenu("File(F)");
		JMenuItem newMenuItem=new JMenuItem("New");
		JMenuItem openMenuItem=new JMenuItem("Open File");
		JMenuItem saveMenuItem=new JMenuItem("Save");
		JMenuItem saveAsMenuItem=new JMenuItem("Save As");
		JMenuItem exitMenuItem=new JMenuItem("Exit");
		JMenu editMenu=new JMenu("Edit(E)");
		JMenuItem undoMenuItem=new JMenuItem("Undo");
		JMenuItem redoMenuItem=new JMenuItem("Redo");
		JMenuItem cutMenuItem=new JMenuItem("Cut");
		JMenuItem copyMenuItem=new JMenuItem("Copy");
		JMenuItem pasteMenuItem=new JMenuItem("Paste");
		JMenuItem deleteMenuItem=new JMenuItem("Delete");
		JMenuItem selectAllMenuItem=new JMenuItem("Select All");
		JMenuItem findMenuItem=new JMenuItem("Find");
		JMenuItem timeMenuItem=new JMenuItem("Time/Date");
		JMenu toolMenu=new JMenu("Tool(O)");
//		JCheckboxMenuItem autoChangeMenuItem=new JCheckBoxMenuItem("Auto Change");
		JCheckBoxMenuItem autoChangeItem=new JCheckBoxMenuItem("Auto Change");
		JMenuItem fontMenuItem=new JMenuItem("Font");
		JMenu helpMenu=new JMenu("Help(H)");
		JMenuItem helpMenuItem=new JMenuItem("Help");
		JMenuItem aboutMenuItem=new JMenuItem("About");
		MyMenuBar(){
			add(fileMenu);
			fileMenu.setMnemonic(KeyEvent.VK_F);
			newMenuItem.addActionListener(menuItemListener);
			openMenuItem.addActionListener(menuItemListener);
			saveMenuItem.addActionListener(menuItemListener);
			saveAsMenuItem.addActionListener(menuItemListener);
			exitMenuItem.addActionListener(menuItemListener);
			newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
			openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
			saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
			fileMenu.add(newMenuItem);
			fileMenu.add(openMenuItem);
			fileMenu.add(saveMenuItem);
			fileMenu.add(saveAsMenuItem);
			fileMenu.add(exitMenuItem);
			add(editMenu);
			editMenu.setMnemonic(KeyEvent.VK_E);
			undoMenuItem.addActionListener(menuItemListener);
			redoMenuItem.addActionListener(menuItemListener);
			cutMenuItem.addActionListener(menuItemListener);
			copyMenuItem.addActionListener(menuItemListener);
			pasteMenuItem.addActionListener(menuItemListener);
			deleteMenuItem.addActionListener(menuItemListener);
			selectAllMenuItem.addActionListener(menuItemListener);
			findMenuItem.addActionListener(menuItemListener);
			timeMenuItem.addActionListener(menuItemListener);
			undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
			redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK));
			cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
			copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
			pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
			deleteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK));
			selectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
			findMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
			editMenu.add(undoMenuItem);
			editMenu.add(redoMenuItem);
			editMenu.addSeparator();
			editMenu.add(cutMenuItem);
			editMenu.add(copyMenuItem);
			editMenu.add(pasteMenuItem);
			editMenu.add(deleteMenuItem);
			editMenu.add(selectAllMenuItem);
			editMenu.addSeparator();
			editMenu.add(findMenuItem);
			editMenu.add(timeMenuItem);
			add(toolMenu);
			toolMenu.setMnemonic(KeyEvent.VK_O);
			autoChangeItem.addActionListener(menuItemListener);
//			autoChangeMenuItem.addActionListener(menuItemListener);
			fontMenuItem.addActionListener(menuItemListener);
			toolMenu.add(autoChangeItem);
			toolMenu.add(fontMenuItem);
			add(helpMenu);
			helpMenu.setMnemonic(KeyEvent.VK_H);
			helpMenuItem.addActionListener(menuItemListener);
			aboutMenuItem.addActionListener(menuItemListener);
			helpMenu.add(helpMenuItem);
			helpMenu.add(aboutMenuItem);
		}
		//处理菜单栏的事件
		ActionListener menuItemListener=new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (e.getSource()==newMenuItem) {
					newFile();
				}
				else if(e.getSource()==openMenuItem){
					openFile();
				}
				else if (e.getSource()==saveMenuItem) {
					if (currentPath=="New file") {
						saveFileAs();
					}
					else {
						saveFile();					
					}
				}
				else if (e.getSource()==saveAsMenuItem) {
					saveFileAs();
				}
				else if (e.getSource()==exitMenuItem) {
					closeApp();
				}
				else if (e.getSource()==undoMenuItem) {
					if (undoManager.canUndo()) {
						undoManager.undo();
					}
				}
				else if (e.getSource()==redoMenuItem) {
					if (undoManager.canRedo()) {
						undoManager.redo();
					}
				}
				else if (e.getSource()==cutMenuItem) {
					textArea.cut();
				}
				else if (e.getSource()==copyMenuItem) {
					textArea.copy();
				}
				else if (e.getSource()==pasteMenuItem) {
					textArea.paste();
				}
				else if (e.getSource()==deleteMenuItem) {
					textArea.replaceRange("",textArea.getSelectionStart(),textArea.getSelectionEnd());
				}
				else if (e.getSource()==selectAllMenuItem) {
//					textArea.select(0, textArea.getText().length());
					textArea.selectAll();
				}
				else if (e.getSource()==findMenuItem) {
					finder.find();
				}
				else if (e.getSource()==timeMenuItem) {
					SimpleDateFormat sm=new SimpleDateFormat("yyyy/M/d HH:mm:ss");
					textArea.append(sm.format(new Date()));
				}
				else if(e.getSource()==autoChangeItem){
					textArea.setLineWrap(autoChangeItem.isSelected());
				}
				else if(e.getSource()==fontMenuItem){
//					new JFileChooser().showDialog(null, null);
					LHFont lhFont=fontChooser.showDialog(mainFrame, null);
					if (lhFont!=null) {						
						textArea.setFont(lhFont.getFont());
						textArea.setForeground(lhFont.getColor());
					}
				}
				else if (e.getSource()==helpMenuItem) {
					
				}
				else if (e.getSource()==aboutMenuItem) {
					JOptionPane.showMessageDialog(mainFrame, "郑慧翔\n24320102202668\n" +
							"本软件只作沟通交流之用,请于下载24小时内删除.\n" +
							"如喜欢此软件,请购买正版");
				}
			}
		};
	}
	
	//新文件操作
	void newFile(){
		if (saveFlag) {
			int resultFlag=JOptionPane.showConfirmDialog(mainFrame, "File unsaved!\n" +
					"Would you like to save this file first?");
			if (resultFlag==JOptionPane.CANCEL_OPTION) {
			}
			else if (resultFlag==JOptionPane.CLOSED_OPTION) {
			}
			else if (resultFlag==JOptionPane.NO_OPTION) {
				createText();
			}
			else if(resultFlag==JOptionPane.OK_OPTION){
				if (currentPath=="New file") {
					saveFileAs();
				}
				else {					
					saveFile();
				}
				createText();
			}			
		}
		else {			
			createText();
		}
	}
	//创建文本
	void createText(){
		textArea.setText("");
		saveFlag=false;
		mainFrame.setTitle("New file - NotePad");
		currentPath="New file";
		undoManager=new UndoManager();
	}
	
	//另存为操作
	void openFile(){
		if (saveFlag) {
			int resultFlag=JOptionPane.showConfirmDialog(mainFrame, "File unsaved!\n" +
					"Would you like to save this file first?");
			if (resultFlag==JOptionPane.CANCEL_OPTION) {
			}
			else if (resultFlag==JOptionPane.CLOSED_OPTION) {
			}
			else if (resultFlag==JOptionPane.NO_OPTION) {
				loadFile();
			}
			else if(resultFlag==JOptionPane.OK_OPTION){
				if (currentPath=="New file") {
					saveFileAs();
				}
				else {
					saveFile();
				}
				loadFile();
			}			
		}
		else {			
			loadFile();
		}
	}
	//载入文本文件的操作
	void loadFile(){
		try {
			fileChooser=new JFileChooser(lastpath);
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.addChoosableFileFilter(new TextFileFilter());
			int openFileFlag=fileChooser.showOpenDialog(mainFrame);
			textFile=fileChooser.getSelectedFile();
			if (textFile!=null) {
				if (textFile.exists()) {
					lastpath=textFile.getPath();
					currentPath=lastpath;
					mainFrame.setTitle(textFile.getPath()+" - NotePad");
					textArea.setText("");
					loadScanner=new Scanner(textFile);
					String tempString="";
					if (loadScanner.hasNext()) {
						tempString+=loadScanner.next();
					}
					while (loadScanner.hasNext()) {
						tempString+='\n'+loadScanner.next();
					}
					textArea.setText(tempString);
					saveFlag=false;
					undoManager=new UndoManager();
				}
				else JOptionPane.showMessageDialog(null, "File not find .");
			}
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	void saveFileAs(){
		try {
			fileChooser=new JFileChooser(lastpath);
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.addChoosableFileFilter(new TextFileFilter());
			int saveFileFlag=fileChooser.showSaveDialog(mainFrame);
			if (saveFileFlag==JFileChooser.APPROVE_OPTION) {
				String path=fileChooser.getSelectedFile().getAbsolutePath();
				if (!path.endsWith(".txt")) {
					path+=".txt";
				}
				Formatter formatter=new Formatter(new File(path));
//				String s[]=textArea.getText().split("\n");
				String temps=textArea.getText().replace("\n", "\r\n");
				formatter.format(temps);
				formatter.close();
				lastpath=path;
				currentPath=path;
				mainFrame.setTitle(path+" - NotePad");
				saveFlag=false;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	void saveFile(){
		Formatter formatter;
		try {
			formatter = new Formatter(new File(currentPath));
			String temps=textArea.getText().replace("\n", "\r\n");
			formatter.format(temps);
			formatter.close();
			saveFlag=false;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	void closeApp(){
		if (saveFlag) {
			int resultFlag=JOptionPane.showConfirmDialog(mainFrame, "File unsaved!\n" +
					"Would you like to save this file first?");
			if (resultFlag==JOptionPane.CANCEL_OPTION) {
			}
			else if (resultFlag==JOptionPane.CLOSED_OPTION) {
			}
			else if (resultFlag==JOptionPane.NO_OPTION) {
//			System.out.print("no");
				System.exit(0);
			}
			else if(resultFlag==JOptionPane.OK_OPTION){			
				if (currentPath=="New file") {
					saveFileAs();
				}
				else {					
					saveFile();
				}
				System.exit(0);
			}			
		}
		else {			
			System.exit(0);
		}
	}
	
	class Finder {
		int endp=0;
		String strAll="";
		JDialog findDialog=new JDialog(mainFrame,"Find");
		Container searchContainer=new Container();
		JLabel searchLabel=new JLabel("Find:");
		JTextField searchTextField=new JTextField(20);
		JButton searchButton=new JButton("Find next");
		Container replaceContainer=new Container();
		JLabel replaceLabel=new JLabel("Replace:");
		JTextField replaceTextField=new JTextField(20);
		JButton replaceButton=new JButton("Replace");
		boolean selectFlag=false;
		int selectedBegin=0;
		int selectedEnd=0;

		public Finder() {
			// TODO Auto-generated constructor stub
			searchButton.addActionListener(buttonListener);
			replaceButton.addActionListener(buttonListener);
			searchContainer.setLayout(new FlowLayout());
			searchContainer.add(searchLabel);
			searchContainer.add(searchTextField);
			searchContainer.add(searchButton);
			replaceContainer.setLayout(new FlowLayout());
			replaceContainer.add(replaceLabel);
			replaceContainer.add(replaceTextField);
			replaceContainer.add(replaceButton);
			findDialog.setLayout(new BorderLayout());
			findDialog.add(searchContainer,BorderLayout.CENTER);
			findDialog.add(replaceContainer,BorderLayout.SOUTH);
			findDialog.pack();
			findDialog.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					// TODO Auto-generated method stub
					if (selectFlag) {
						textArea.select(selectedBegin, selectedEnd);
					}
					findDialog.setVisible(false);
				}
			});
		}
		
		void find(){
			endp=0;
			findDialog.setVisible(true);
			if (textArea.getSelectedText()==null) {
				strAll=textArea.getText();
				selectedBegin=0;
				selectedEnd=0;
				selectFlag=false;
			}
			else {
				strAll=textArea.getSelectedText();
				selectedBegin=textArea.getSelectionStart();
				selectedEnd=textArea.getSelectionEnd();
				selectFlag=true;
			}
		}
		ActionListener buttonListener=new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (e.getSource()==searchButton) {
					if(!searchTextField.getText().equals(""))
					{
						String strRest=strAll.substring(endp);
						int n=strRest.indexOf(searchTextField.getText());
						if (n!=-1) {
							textArea.select(n+endp+selectedBegin, n+endp+selectedBegin+searchTextField.getText().length());
							endp=n+endp+searchTextField.getText().length();
//							System.out.print(endp+" ");
						}
						else {
							endp=0;
							JOptionPane.showMessageDialog(findDialog, "The End!");
						}
					}
				}
				else {
					if (textArea.getSelectionEnd()!=textArea.getSelectionStart()) {
						textArea.replaceRange(replaceTextField.getText(), textArea.getSelectionStart(), textArea.getSelectionEnd());
						if (selectFlag) {
							strAll=textArea.getText().substring(selectedBegin,selectedEnd);
						}
						else {
							strAll=textArea.getText();
						}
					}
				}
			}
		};
	}

	
	//监控线程
	class MyThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				if (!undoManager.canRedo()) {
					myMenuBar.redoMenuItem.setEnabled(false);
					myPopupMenu.redoPopupItem.setEnabled(false);
				}
				else {
					myMenuBar.redoMenuItem.setEnabled(true);
					myPopupMenu.redoPopupItem.setEnabled(true);
				}
				if (!undoManager.canUndo()) {
					myMenuBar.undoMenuItem.setEnabled(false);
					myPopupMenu.undoPopupItem.setEnabled(false);
				}
				else {
					myMenuBar.undoMenuItem.setEnabled(true);
					myPopupMenu.undoPopupItem.setEnabled(true);
				}
				if (textArea.getSelectionStart()==textArea.getSelectionEnd()) {
					myMenuBar.cutMenuItem.setEnabled(false);
					myMenuBar.copyMenuItem.setEnabled(false);
					myMenuBar.deleteMenuItem.setEnabled(false);
					myPopupMenu.cutPopupItem.setEnabled(false);
					myPopupMenu.copyPopupItem.setEnabled(false);
					myPopupMenu.deletePopupItem.setEnabled(false);
				}
				else {
					myMenuBar.cutMenuItem.setEnabled(true);
					myMenuBar.copyMenuItem.setEnabled(true);
					myMenuBar.deleteMenuItem.setEnabled(true);
					myPopupMenu.cutPopupItem.setEnabled(true);
					myPopupMenu.copyPopupItem.setEnabled(true);
					myPopupMenu.deletePopupItem.setEnabled(true);
				}
			}
		}
	}
	
	//只选择文本文件
	class TextFileFilter extends FileFilter{
		@Override
		public boolean accept(File file) {
			// TODO Auto-generated method stub
			 if (file.isDirectory()) return true;
			 String name=file.getName().toLowerCase();
			 if (name.endsWith(".txt")) {
				return true;
			}
			 return false;
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return "文本文件 (*.txt)";
		}
	}
}