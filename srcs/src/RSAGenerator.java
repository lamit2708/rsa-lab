import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.awt.event.ActionEvent;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class RSAGenerator {
	protected static final int STATUS_HAVE_KEY = 1;
	protected static final int STATUS_NONE_KEY = 0;
	private int status=STATUS_NONE_KEY;
	protected static final int FILE_OPEN = 1;
	protected static final int FILE_SAVE = 2;
	private JFrame frmRsaKeyGenerator;
	private JTextField fBitLength;
	private JLabel lblProgress;
	private JScrollPane scrollPane;
	private JTextArea displayProgress;
	private JButton btnSaveKeyPairs;
	private int bitLength;
	private static final String DEFAULT_BIT_LENGTH="1024";
	private RSA rsa;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		//Form chính: tạo khóa, sử dụng khóa
		//Form tạo khóa: một màn hình. Thể hiện các bước chạy step by step. show ra p, q, e, d, N, phi
		//=>Save, chọn file và save khóa
		//Form sử dụng khóa: chọn khóa, chọn file, tiến hành mã hóa, save file
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RSAGenerator window = new RSAGenerator();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public RSAGenerator() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		//Font myFont = new Font("Serif", Font.ITALIC | Font.BOLD, 12);
	    //Font newFont = myFont.deriveFont(50F);
		Font baseFont = new Font("Serif",  Font.BOLD, 12);
		Font myFont = baseFont.deriveFont(18F);
		frmRsaKeyGenerator = new JFrame();
		frmRsaKeyGenerator.setTitle("RSA Key Generator");
		frmRsaKeyGenerator.setBounds(100, 100, 890, 540);
		frmRsaKeyGenerator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SpringLayout springLayout = new SpringLayout();
		frmRsaKeyGenerator.getContentPane().setLayout(springLayout);
		frmRsaKeyGenerator.setVisible(true);
		JLabel lblBitsLength = new JLabel("Bits Length:");
		lblBitsLength.setFont(myFont);
		springLayout.putConstraint(SpringLayout.NORTH, lblBitsLength, 40, SpringLayout.NORTH, frmRsaKeyGenerator.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, lblBitsLength, 10, SpringLayout.WEST, frmRsaKeyGenerator.getContentPane());
		frmRsaKeyGenerator.getContentPane().add(lblBitsLength);
		
		fBitLength = new JTextField();
		springLayout.putConstraint(SpringLayout.WEST, fBitLength, 120, SpringLayout.WEST, frmRsaKeyGenerator.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, fBitLength, 227, SpringLayout.WEST, frmRsaKeyGenerator.getContentPane());
		fBitLength.setText(DEFAULT_BIT_LENGTH);
		fBitLength.setFont(myFont);
		springLayout.putConstraint(SpringLayout.NORTH, fBitLength, 0, SpringLayout.NORTH, lblBitsLength);
		
		frmRsaKeyGenerator.getContentPane().add(fBitLength);
		fBitLength.setColumns(10);
		fBitLength.setHorizontalAlignment(SwingConstants.RIGHT);
		lblProgress = new JLabel("Display");
		lblProgress.setFont(myFont);
		springLayout.putConstraint(SpringLayout.NORTH, lblProgress, 19, SpringLayout.SOUTH, lblBitsLength);
		springLayout.putConstraint(SpringLayout.WEST, lblProgress, 10, SpringLayout.WEST, frmRsaKeyGenerator.getContentPane());
		frmRsaKeyGenerator.getContentPane().add(lblProgress);
		
		JPanel panelProgress = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, panelProgress, 0, SpringLayout.NORTH, lblProgress);
		springLayout.putConstraint(SpringLayout.WEST, panelProgress, 0, SpringLayout.EAST, frmRsaKeyGenerator.getContentPane());

		frmRsaKeyGenerator.getContentPane().add(panelProgress);
		
		displayProgress = new JTextArea(15, 50);
		displayProgress.setEditable ( false ); // set textArea non-editable
		displayProgress.setLineWrap(true);
		displayProgress.setFont(displayProgress.getFont().deriveFont(18f));
		displayProgress.setBorder(BorderFactory.createCompoundBorder(
				displayProgress.getBorder(), 
		        BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		scrollPane = new JScrollPane(displayProgress);
		scrollPane.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
		panelProgress.add(scrollPane);
		
		JButton btnGenKeyPairs = new JButton("Generate Key Pairs");
		springLayout.putConstraint(SpringLayout.NORTH, btnGenKeyPairs, 0, SpringLayout.NORTH, fBitLength);
		springLayout.putConstraint(SpringLayout.WEST, btnGenKeyPairs, 30, SpringLayout.EAST, fBitLength);
		btnGenKeyPairs.setFont(myFont);
		springLayout.putConstraint(SpringLayout.WEST, panelProgress, 100, SpringLayout.WEST, frmRsaKeyGenerator.getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, panelProgress, 10, SpringLayout.SOUTH, fBitLength);
		btnGenKeyPairs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				genKeyPair();
			}
		});
		frmRsaKeyGenerator.getContentPane().add(btnGenKeyPairs);
		
		btnSaveKeyPairs = new JButton("Save Key Pairs");
		springLayout.putConstraint(SpringLayout.NORTH, btnSaveKeyPairs, 0, SpringLayout.NORTH, fBitLength);
		springLayout.putConstraint(SpringLayout.WEST, btnSaveKeyPairs, 30, SpringLayout.EAST, btnGenKeyPairs);
		//springLayout.putConstraint(SpringLayout.EAST, btnSaveKeyPairs, -317, SpringLayout.EAST, frmRsaKeyGenerator.getContentPane());
		btnSaveKeyPairs.setFont(myFont);
		btnSaveKeyPairs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveKeyPair();
			}
		});
		frmRsaKeyGenerator.getContentPane().add(btnSaveKeyPairs);
		
		JMenuBar menuBar = new JMenuBar();
		UIManager.put("Menu.font", myFont);
		UIManager.put("MenuItem.font", myFont); 
		springLayout.putConstraint(SpringLayout.WEST, menuBar, 10, SpringLayout.WEST, frmRsaKeyGenerator.getContentPane());
		frmRsaKeyGenerator.getContentPane().add(menuBar);
		
		JMenu mnMenu = new JMenu("Menu");
		menuBar.add(mnMenu);
		
		JMenuItem mntmRsaKeyGenerator = new JMenuItem("RSA Key Generator");
		mntmRsaKeyGenerator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "RSA Generator have already opened!");
			}
		});
		mnMenu.add(mntmRsaKeyGenerator);
		
		JMenuItem mntmRsaCryptor = new JMenuItem("RSA Cryptor");
		mntmRsaCryptor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmRsaKeyGenerator.dispose();
				new RSACryptor();
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
	}
	public void genKeyPair(){
		new RSAGenKeyPair().start();
		
		//BigInteger p=PrimeNumber.genPrime(bitLength,certainty);
		//displayP.setText(p.toString());
	}
	public void saveKeyPair(){
		if(status ==STATUS_HAVE_KEY){
			String keypair=rsa.getKeyPair();
			operateFile("Save a file", FILE_SAVE, keypair);
		}else{
			JOptionPane.showMessageDialog(null, "Please generate a RSA key pair!");
		}
	}
	
	protected void operateFile(String title, int type, String data) {
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
				//JOptionPane.showMessageDialog(null, "read a file");
				data = readFile(file);
				break;
			case FILE_SAVE:
				writeFile(file,data);
				//JOptionPane.showMessageDialog(null, "Write is updating");
				//int result=JOptionPane.showConfirmDialog(null, "Do you want to save this file?");
				//if(result == JOptionPane.YES_OPTION){
					//writeFile(file,data);
					//this.setTitle(file.getName());
				//}
				
				break;
				
			}
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
	class RSAGenKeyPair extends Thread{
		public void run(){
			bitLength=Integer.parseInt(fBitLength.getText());
			rsa=new RSA(bitLength);
			rsa.setLogger(displayProgress);
			rsa.generateKeyPair();
			status=STATUS_HAVE_KEY;
		}
	}
}
