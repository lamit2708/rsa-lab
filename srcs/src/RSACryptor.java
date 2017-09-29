import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigInteger;
import java.awt.event.ActionEvent;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class RSACryptor {

	private JFrame frmRsaCryptor;
	private JTextField txtRsaKeyPair;
	protected static final int FILE_OPEN = 1;
	protected static final int FILE_SAVE = 2;
	private JTextArea displayProgress;
	private RSA rsa;
	private JTextField txtInput;
	private JTextField txtOutput;
	private String outputFilePath;
	private String inputFilePath;
	private File outputFile;
	private File inputFile;
	private JButton btnLoadInputFile;
	private JButton btnLoadOutputFile;
	protected static final int STATUS_NONE_KEY = 0;
	protected static final int STATUS_HAVE_KEY = 4;//Binary 100
	protected static final int STATUS_FILE_ONE = 5;//Binary 101
	protected static final int STATUS_FILE_TWO = 6;//Binary 110
	protected static final int STATUS_OK = 7; //Binary 111
	private int status=STATUS_NONE_KEY;
	private JButton btnEncrypt;
	private JButton btnDecrypt;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RSACryptor window = new RSACryptor();
					window.frmRsaCryptor.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public RSACryptor() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		Font baseFont = new Font("Serif",  Font.BOLD, 12);
		Font myFont = baseFont.deriveFont(18F);
		UIManager.put("Menu.font", myFont);
		UIManager.put("MenuItem.font", myFont);
		frmRsaCryptor = new JFrame();
		frmRsaCryptor.setTitle("RSA Cryptor");
		frmRsaCryptor.setBounds(100, 100, 890, 600);
		frmRsaCryptor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmRsaCryptor.setVisible(true);
		JMenuBar menuBar = new JMenuBar();
		frmRsaCryptor.setJMenuBar(menuBar);
		
		JMenu mnMenu = new JMenu("Menu");
		 
		menuBar.add(mnMenu);
		
		JMenuItem mntmRsaKeyGenerator = new JMenuItem("RSA Key Generator");
		mntmRsaKeyGenerator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmRsaCryptor.dispose();
				new RSAGenerator();
			}
		});
		mnMenu.add(mntmRsaKeyGenerator);
		
		JMenuItem mntmRsaCryptor = new JMenuItem("RSA Cryptor");
		mntmRsaCryptor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "RSA Cryptor have already opened!");
			}
		});
		mnMenu.add(mntmRsaCryptor);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnMenu.add(mntmExit);
		
		SpringLayout springLayout = new SpringLayout();
		frmRsaCryptor.getContentPane().setLayout(springLayout);
		
		JButton btnLoadRsaKey = new JButton("Load Keys");
		btnLoadRsaKey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//String keyPair=loadKeyPair();
				loadKeyPairFile();
				//displayInput.setText("abc");
				
			}
		});
		btnLoadRsaKey.setFont(myFont);
		springLayout.putConstraint(SpringLayout.NORTH, btnLoadRsaKey, 10, SpringLayout.NORTH, frmRsaCryptor.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, btnLoadRsaKey, -10, SpringLayout.EAST, frmRsaCryptor.getContentPane());
		frmRsaCryptor.getContentPane().add(btnLoadRsaKey);
		
		
		
		JLabel lblRsaKeyPair = new JLabel("RSA Key Pair");
		lblRsaKeyPair.setFont(myFont);
		//springLayout.putConstraint(SpringLayout.WEST, tRsaKeyPair, 10, SpringLayout.EAST, lblRsaKeyPair);
		springLayout.putConstraint(SpringLayout.NORTH, lblRsaKeyPair, 10, SpringLayout.NORTH, frmRsaCryptor.getContentPane());
		//springLayout.putConstraint(SpringLayout.EAST, lblRsaKeyPair, -585, SpringLayout.EAST, frmRsaCryptor.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, lblRsaKeyPair, 20, SpringLayout.WEST, frmRsaCryptor.getContentPane());
		frmRsaCryptor.getContentPane().add(lblRsaKeyPair);
		
		txtRsaKeyPair = new JTextField();
		txtRsaKeyPair.setEnabled(false);
		txtRsaKeyPair.setFont(myFont);
		springLayout.putConstraint(SpringLayout.WEST, txtRsaKeyPair, 150, SpringLayout.WEST, frmRsaCryptor.getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, txtRsaKeyPair, 10, SpringLayout.NORTH, frmRsaCryptor.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, txtRsaKeyPair, -150, SpringLayout.EAST, frmRsaCryptor.getContentPane());
		frmRsaCryptor.getContentPane().add(txtRsaKeyPair);
		//txtRsaKeyPair.setColumns(36);
		
		JLabel lblPlaintext = new JLabel("Input File");
		lblPlaintext.setFont(myFont);
		springLayout.putConstraint(SpringLayout.NORTH, lblPlaintext, 32, SpringLayout.SOUTH, lblRsaKeyPair);
		springLayout.putConstraint(SpringLayout.WEST, lblPlaintext, 0, SpringLayout.WEST, lblRsaKeyPair);
		frmRsaCryptor.getContentPane().add(lblPlaintext);
		
		btnLoadInputFile = new JButton("Select File");
		btnLoadInputFile.setEnabled(false);
		btnLoadInputFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectInputFile();
				//txtRsaKeyPair.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, btnLoadInputFile, -4, SpringLayout.NORTH, lblPlaintext);
		springLayout.putConstraint(SpringLayout.EAST, btnLoadInputFile, 0, SpringLayout.EAST, btnLoadRsaKey);
		btnLoadInputFile.setFont(myFont);
		frmRsaCryptor.getContentPane().add(btnLoadInputFile);
		
		JPanel panelInput = new JPanel();
		//springLayout.putConstraint(SpringLayout.SOUTH, panelInput, -269, SpringLayout.SOUTH, frmRsaCryptor.getContentPane());
		
		springLayout.putConstraint(SpringLayout.WEST, panelInput, 0, SpringLayout.WEST, txtRsaKeyPair);
		springLayout.putConstraint(SpringLayout.EAST, panelInput, 0, SpringLayout.EAST, txtRsaKeyPair);
		frmRsaCryptor.getContentPane().add(panelInput);
		
		displayProgress = new JTextArea(8,36);
		displayProgress.setFont(myFont);
		
		displayProgress.setEditable ( false ); // set textArea non-editable
		displayProgress.setLineWrap(true);
		displayProgress.setFont(displayProgress.getFont().deriveFont(18f));
		displayProgress.setBorder(BorderFactory.createCompoundBorder(
				displayProgress.getBorder(), 
		        BorderFactory.createEmptyBorder(5, 0, 5, 0)));
		JScrollPane scrollPane = new JScrollPane(displayProgress);
		scrollPane.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
		panelInput.add(scrollPane,BorderLayout.CENTER);
		//springLayout.putConstraint(SpringLayout.EAST, displayInput, 0, SpringLayout.EAST, panelInput);
		//springLayout.putConstraint(SpringLayout.WEST, displayInput, 0, SpringLayout.WEST, panelInput);
		
		JLabel lblOutput = new JLabel("Progress");
		lblOutput.setFont(myFont);
		springLayout.putConstraint(SpringLayout.NORTH, lblOutput, 158, SpringLayout.SOUTH, lblPlaintext);
		springLayout.putConstraint(SpringLayout.WEST, lblOutput, 0, SpringLayout.WEST, lblRsaKeyPair);
		frmRsaCryptor.getContentPane().add(lblOutput);
		
		btnEncrypt = new JButton("Encrypt");
		btnEncrypt.setEnabled(false);
		btnEncrypt.setFont(myFont);
		springLayout.putConstraint(SpringLayout.NORTH, panelInput, -229, SpringLayout.NORTH, btnEncrypt);
		springLayout.putConstraint(SpringLayout.SOUTH, panelInput, -26, SpringLayout.NORTH, btnEncrypt);
		btnEncrypt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					rsa.encryptFile(inputFilePath,outputFilePath);
					String outputContent=readFile(outputFile);
					displayProgress.append("\r\n---------------------\r\nEncrypting is complete!");
					displayProgress.append("\r\n---------------------\r\nResult:\r\n"+outputContent);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		springLayout.putConstraint(SpringLayout.WEST, btnEncrypt, 148, SpringLayout.WEST, frmRsaCryptor.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, btnEncrypt, -43, SpringLayout.SOUTH, frmRsaCryptor.getContentPane());
		frmRsaCryptor.getContentPane().add(btnEncrypt);
		
		btnDecrypt = new JButton("Decrypt");
		btnDecrypt.setEnabled(false);
		btnEncrypt.setEnabled(false);
		btnDecrypt.setFont(myFont);
		btnDecrypt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try {
					rsa.decryptFile(inputFilePath,outputFilePath);
					String outputContent=readFile(outputFile);
					displayProgress.append("\r\n---------------------\r\nDecrypting is complete!");
					displayProgress.append("\r\n---------------------\r\nResult:\r\n"+outputContent);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, btnDecrypt, 0, SpringLayout.NORTH, btnEncrypt);
		springLayout.putConstraint(SpringLayout.WEST, btnDecrypt, 107, SpringLayout.EAST, btnEncrypt);
		frmRsaCryptor.getContentPane().add(btnDecrypt);
		
		txtInput = new JTextField();
		txtInput.setEnabled(false);
		txtInput.setFont(myFont);
		springLayout.putConstraint(SpringLayout.NORTH, txtInput, 26, SpringLayout.SOUTH, txtRsaKeyPair);
		springLayout.putConstraint(SpringLayout.WEST, txtInput, 0, SpringLayout.WEST, txtRsaKeyPair);
		springLayout.putConstraint(SpringLayout.EAST, txtInput, 0, SpringLayout.EAST, txtRsaKeyPair);
		frmRsaCryptor.getContentPane().add(txtInput);
		txtInput.setColumns(10);
		
		JLabel lblOutputFile = new JLabel("Output File");
		lblOutputFile.setFont(myFont);
		springLayout.putConstraint(SpringLayout.NORTH, lblOutputFile, 38, SpringLayout.SOUTH, lblPlaintext);
		springLayout.putConstraint(SpringLayout.WEST, lblOutputFile, 0, SpringLayout.WEST, lblRsaKeyPair);
		frmRsaCryptor.getContentPane().add(lblOutputFile);
		
		txtOutput = new JTextField();
		txtOutput.setEnabled(false);
		txtOutput.setFont(myFont);
		springLayout.putConstraint(SpringLayout.NORTH, txtOutput, 33, SpringLayout.SOUTH, txtInput);
		springLayout.putConstraint(SpringLayout.WEST, txtOutput, 39, SpringLayout.EAST, lblOutputFile);
		springLayout.putConstraint(SpringLayout.EAST, txtOutput, 0, SpringLayout.EAST, txtRsaKeyPair);
		frmRsaCryptor.getContentPane().add(txtOutput);
		txtOutput.setColumns(10);
		
		btnLoadOutputFile = new JButton("Select File");
		btnLoadOutputFile.setEnabled(false);
		btnLoadOutputFile.setFont(myFont);
		btnLoadOutputFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectOutputFile();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, btnLoadOutputFile, 0, SpringLayout.NORTH, lblOutputFile);
		springLayout.putConstraint(SpringLayout.EAST, btnLoadOutputFile, 0, SpringLayout.EAST, btnLoadRsaKey);
		frmRsaCryptor.getContentPane().add(btnLoadOutputFile);
		
		JButton btnClear = new JButton("Clear");
		btnClear.setFont(myFont);
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayProgress.setText("");
			}
		});
		springLayout.putConstraint(SpringLayout.WEST, btnClear, 105, SpringLayout.EAST, btnDecrypt);
		springLayout.putConstraint(SpringLayout.SOUTH, btnClear, 0, SpringLayout.SOUTH, btnEncrypt);
		frmRsaCryptor.getContentPane().add(btnClear);
		
		
	}
	protected void parseKeyPair(File file) {
		// TODO Auto-generated method stub
		try {
			String res="";
			FileReader fr;
			int l=0;
			BigInteger e = null, d = null, n = null;
			fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			String preLine="";
			while ((line = br.readLine()) != null){
				preLine=line.substring(0, 1);
				
				switch(preLine){
				case "L":
					String strLen=line.substring(3,line.length());
					l=Integer.parseInt(strLen);
					break;
				case "E":
					e= new BigInteger(line.substring(3,line.length()));
					break;
				case "D":
					d= new BigInteger(line.substring(3,line.length()));
					break;
				case "N":
					n= new BigInteger(line.substring(3,line.length()));
					break;
				}
				
			}
			if(l ==0 || e == null || d==null ||n==null) {
				JOptionPane.showMessageDialog(null, "ERROR:Loading RSA key");
				return;
			}
			rsa = new RSA(l,e,d,n);
			displayProgress.setText("Load Key Pair is Success!\r\n"+rsa.getKeyPair());
			br.close();
			fr.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "Error to open file:");
		}
		
		
	}

	protected void loadKeyPairFile() {

		JFileChooser chooser=new JFileChooser();
		int choose = -1;
		chooser.setDialogTitle("Open a file");
		choose = chooser.showOpenDialog(null);
			
		if(choose == JFileChooser.APPROVE_OPTION){
			File file = chooser.getSelectedFile();
			parseKeyPair(file);
			txtRsaKeyPair.setText(chooser.getSelectedFile().getAbsolutePath());
			btnLoadInputFile.setEnabled(true);
			btnLoadOutputFile.setEnabled(true);
			status=STATUS_HAVE_KEY;
		}
		
		
		
	}
	private String readFile(File file) {
		
		try {
			String res="";
			FileReader fr;
			fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null){
				res +=line +"\n";
			}
			br.close();
			fr.close();
			return res;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "Error to open file:");
		}
		return null;
	}
	private void writeFile(File file, String data) {
		try{
			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(data);
			bw.close();
			fw.close();
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Error to save file:");
		}
		
	}
	protected void selectInputFile() {
		// TODO Auto-generated method stub
		JFileChooser chooser=new JFileChooser();
		int choose = -1;
		chooser.setDialogTitle("Open a file");
		choose = chooser.showOpenDialog(null);
		if(choose == JFileChooser.APPROVE_OPTION){
			inputFile = chooser.getSelectedFile();
			inputFilePath=inputFile.getAbsolutePath();
			txtInput.setText(inputFile.getAbsolutePath());
			String inputContent=readFile(inputFile);
			displayProgress.append("\r\n---------------------\r\nLoad the input file!");
			displayProgress.append("\r\n---------------------\r\n"+inputContent);
			status =status|STATUS_FILE_ONE;
		}
		if(status ==STATUS_OK) {
			btnEncrypt.setEnabled(true);
			btnDecrypt.setEnabled(true);
		}
		
	}
	protected void selectOutputFile() {
		// TODO Auto-generated method stub
		JFileChooser chooser=new JFileChooser();
		int choose = -1;
		chooser.setDialogTitle("Select a output file");
		choose = chooser.showSaveDialog(null);
				
		if(choose == JFileChooser.APPROVE_OPTION){
			outputFile = chooser.getSelectedFile();
			//writeFile(file,"");
			outputFilePath=outputFile.getAbsolutePath();
			txtOutput.setText(outputFilePath);	
			status =status|STATUS_FILE_TWO;
		}	
		if(status ==STATUS_OK) {
			btnEncrypt.setEnabled(true);
			btnDecrypt.setEnabled(true);
		}
	}
}
