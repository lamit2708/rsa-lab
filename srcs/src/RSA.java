import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import javax.swing.JTextArea;
 
public class RSA
{
    private BigInteger p;
    private BigInteger q;
    private BigInteger N;
    private BigInteger phi;
    private BigInteger e;
    private BigInteger d;
    private static int bitlength;
    //private Random     r;
    private static int certainty=10;
    private static int blockSize;
    private static int chunkSize;
	private static JTextArea logger;
	private static final int LOG_SYTEM=1;
	private static final int LOG_GRAPHIC=2;
	private static int logType = LOG_SYTEM;
    public RSA(int len)
    {
    	
    	bitlength=len;

    }
    
 
    public RSA(int len, BigInteger e, BigInteger d, BigInteger N)
    {
    	bitlength=len;
        this.e = e;
        this.d = d;
        this.N = N;
        chunkSize=(2*bitlength)/8;
        blockSize=chunkSize+1;
    }
    
    public void generateKeyPair(){
    	PrimeNumber.setLogger(logger);
    	logProgress("Generate the RSA Key Pairs (Use the Miller Rabin algorithm to find the primes):");
        p = PrimeNumber.genPrime(bitlength, certainty);
        logProgress("\nNumber P:\t"+p.toString()+"\n");
        q = PrimeNumber.genPrime(bitlength, certainty);
        logProgress("\nNumber Q:\t"+q.toString()+"\n");
        N = p.multiply(q);
        logProgress("\nFinding Public Key (E,N)\n");
        phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        e = PrimeNumber.genPrime(bitlength/2, certainty);
        logProgress("\nNumber E:\t"+e.toString()+"\n");
        logProgress("\nNumber N:\t"+N.toString()+"\n");
        chunkSize=(2*bitlength)/8;
        blockSize=chunkSize+1;
        d = e.modInverse(phi);
        logProgress("\nFinding Private Key (D,N)\n");
        logProgress("\nNumber D:\t"+d+"\n");
        logProgress("\nNumber N:\t"+N.toString()+"\n");
        logProgress("\nComplete!\n");
    }
    public static void logProgress(String signal){
    	if(logType==LOG_GRAPHIC){
    		logger.append(signal);
    	}else{
    		System.out.print(signal);
    	}
    }
    //@SuppressWarnings("deprecation")
    public static void main(String[] args) throws Exception
    {
        driverTest2(512);
        
    }
    public static void driverTest2(int len) throws Exception{
    	RSA rsa=new RSA(len);
    	rsa.generateKeyPair();
        //rsa.driverTest1();
    	rsa.encryptFile("plaintext.txt","encrypted.txt");
    	rsa.decryptFile("encrypted.txt","decrypted.txt");
    }
    
 
    private static String bytesToString(byte[] encrypted)
    {
        String test = "";
        for (byte b : encrypted)
        {
            test += "."+Byte.toString(b);
        }
        return test;
    }
 
    // Encrypt message
    public byte[] encryptBlock(byte[] message,int byteSize)
    {
    	BigInteger msg=new BigInteger(message);
    	BigInteger block=new BigInteger(message);
    	BigInteger encrypted=block.modPow(e, N);
    	byte[] res=encrypted.toByteArray();
    	res=setFixedBlock(res, byteSize);
        return res;
    }
 
    // Decrypt message
    public byte[] decryptBlock(byte[] message, int byteSize) throws Exception
    {
//    	if(message.length!=byteSize) {
//    		throw new Exception("Block Size Exception! MessageLength: "+message.length+";ByteSize:"+byteSize);
//    	}
    	BigInteger block=(new BigInteger(message));

    	BigInteger decrypted=block.modPow(d, N);
    	byte[] res=decrypted.toByteArray();
        return res;
    }
    public void encryptFile(String InputFile, String OutputFile) throws Exception{
    	FileInputStream fis = null;
    	FileOutputStream fos = null;
    	try {
			fis = new FileInputStream(InputFile);
			fos = new FileOutputStream(OutputFile);
			byte[] block= new byte[chunkSize];
			int i=fis.read(block);
			while(i != -1){
				byte[] eblock=encryptBlock(block,blockSize);
			    
				fos.write(eblock,0,eblock.length);
				i=fis.read(block);
				if(i<chunkSize && i>0)	 //End Block
					block=Arrays.copyOf(block, i);
				

			}
		} catch (FileNotFoundException e) {
			System.out.println("Can not open file: "+InputFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				fos.flush();
				fos.close();
				fis.close();
			} catch (IOException e) {
				
			}
		}
    	
    }
    public void decryptFile(String InputFile, String OutputFile){
    	FileInputStream fis = null;
    	FileOutputStream fos = null;
    	try {
			fis = new FileInputStream(InputFile);
			fos = new FileOutputStream(OutputFile);
			byte[] block= new byte[blockSize];
			int i=fis.read(block);
			byte[] dblock;
			while(i != -1){
				dblock=decryptBlock(block,blockSize);
				fos.write(dblock,0,dblock.length);
				i=fis.read(block);
			}
		} catch (FileNotFoundException e) {
			System.out.println("Can not open file: "+InputFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				fos.flush();
				fos.close();
				fis.close();
			} catch (IOException e) {
				
			}
		}
    	
    }
    public static String bytesToHexString(byte[] bytes){
    	StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return (sb.toString());
    }
    public static byte[] trimByte(byte[]original){
    	int j=0;
    	for(;j<original.length;j++){
    		if(original[j]!=0) break;
    	}
    	int eliminate=j;
    	if(eliminate ==0) return original;
    	byte[] res=new byte[original.length-eliminate];
    	res=Arrays.copyOfRange( original, eliminate, original.length);
    	return res;
    }
    public static byte[] appendByte(byte[] input1, byte[] input2){
    	int len1=input1.length;
    	int len2=input2.length;
    	int len=len1+len2;
    	byte[] res=new byte[len1+len2];
    	for(int i=0;i<len1;i++){
    		res[i]=input1[i];
    	}
    	for(int j=len1;j<len;j++){
    		res[j]=input2[j-len1];
    	}
    	return res;
    }
    public static byte[] setFixedBlock(byte[] original, int byteSize){
    	original=trimByte(original);//remove the case that original's length is greater than byteSize;
    	if(original.length<byteSize) return appendByte(new byte[byteSize-original.length],original);
    	return original;
    }


	public void setLogger(JTextArea displayProgress) {
		// TODO Auto-generated method stub
		displayProgress.setText("");
		this.logger=displayProgress;
		logType=LOG_GRAPHIC;
	}


	public String getKeyPair() {
		// TODO Auto-generated method stub
		String keyPair ="---- BEGIN RSA KEY PAIR ----";
		keyPair+="\r\nL: "+bitlength;
		keyPair+="\r\nE: "+e;
		keyPair+="\r\nD: "+d;
		keyPair+="\r\nN: "+N;
		keyPair +="\r\n---- BEGIN RSA KEY PAIR ----";
		return keyPair;
	}
	
    
	  
}