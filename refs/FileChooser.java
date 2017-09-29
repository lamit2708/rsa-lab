import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class FileChooser {

	protected static final int FILE_OPEN = 1;
	protected static final int FILE_SAVE = 2;
	private JFrame frame;
	private JTextArea taFile;
	private boolean changed;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FileChooser window = new FileChooser();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public FileChooser() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 650, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(200, 150);
		JMenuBar menuBar = new JMenuBar();
		menuBar.setFont(new Font("Segoe UI", Font.PLAIN, 25));
		frame.setJMenuBar(menuBar);
		frame.setVisible(true);
		JMenu mnFile = new JMenu("File");
		mnFile.setFont(new Font("Segoe UI", Font.PLAIN, 25));
		menuBar.add(mnFile);
		
		JMenuItem mNew = new JMenuItem("New");
		mNew.setFont(new Font("Segoe UI", Font.PLAIN, 25));
		mNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//JOptionPane.showMessageDialog(null, "New is updating");
				if(changed){
					int result = JOptionPane.showConfirmDialog(null, "Do you want to save this file");
					if(result == JOptionPane.YES_OPTION){
						operateFile("Save a file", FILE_SAVE, 1);
					}
					
				}
				taFile.setText("");
				taFile.requestFocus();
				frame.setTitle("");
				changed=false;
			}
		});
		mnFile.add(mNew);
		
		JMenuItem mOpen = new JMenuItem("Open");
		mOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(changed){
					int result = JOptionPane.showConfirmDialog(null, "Do you want to save this file");
					if (result == JOptionPane.YES_OPTION){
						operateFile("Save a file", FILE_SAVE, 1);
					}
				}
				operateFile("Open a file", FILE_OPEN, 2);
				changed=false;
			}
		});
		mOpen.setFont(new Font("Segoe UI", Font.PLAIN, 25));
		mnFile.add(mOpen);
		
		JMenuItem mSave = new JMenuItem("Save");
		mSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				operateFile("Save a file", FILE_SAVE,1);
			}
		});
		mSave.setFont(new Font("Segoe UI", Font.PLAIN, 25));
		mnFile.add(mSave);
		
		JMenuItem mExit = new JMenuItem("Exit");
		mExit.setFont(new Font("Segoe UI", Font.PLAIN, 25));
		mExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
			
		});
		mnFile.add(mExit);
		
		JMenu mnEdit = new JMenu("Edit");
		mnEdit.setFont(new Font("Segoe UI", Font.PLAIN, 25));
		menuBar.add(mnEdit);
		
		taFile = new JTextArea();
		taFile.setFont(new Font("Monospaced", Font.PLAIN, 18));
		taFile.setWrapStyleWord(true);
		taFile.setLineWrap(true);
		frame.getContentPane().add(taFile, BorderLayout.CENTER);
	}
	private void taFileKeyTyped(KeyEvent evt){
		changed = true;
	}
	
	protected void operateFile(String title, int type, int order) {
		// TODO Auto-generated method stub
		JFileChooser chooser=new JFileChooser();
		int choose = -1;
		chooser.setDialogTitle(title);
		switch(type){
			case FILE_OPEN:
				choose = chooser.showOpenDialog(null);
				break;
			case FILE_SAVE:
				choose = chooser.showSaveDialog(null);
				break;
				
		}
		if(choose == JFileChooser.APPROVE_OPTION){
			File file = chooser.getSelectedFile();
			switch(type){
			case FILE_OPEN:
				JOptionPane.showMessageDialog(null, "read a file");
				readFile(file);
				break;
			case FILE_SAVE:
				if(order ==2){
					//JOptionPane.showMessageDialog(null, "Write is updating");
					int result=JOptionPane.showConfirmDialog(null, "Do you want to save this file?");
					if(result == JOptionPane.YES_OPTION){
						writeFile(file);
						//this.setTitle(file.getName());
					}
				}else{
					writeFile(file);
					
				}
				break;
				
			}
		}
		
	}
	private void readFile(File file) {
		
		try {
			taFile.setText("");
			FileReader fr;
			fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null){
				taFile.append(line +"\n");
			}
			br.close();
			fr.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "Error to open file:");
		}
	}
	private void writeFile(File file) {
		try{
			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			String data = taFile.getText();
			bw.write(data);
			bw.close();
			fw.close();
			changed =false;
			readFile(file);
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Error to save file:");
		}
		
	}

	

}
