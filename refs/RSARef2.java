package Crypto;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
 
public class RSARef2
{
    private BigInteger p;
    private BigInteger q;
    private BigInteger N;
    private BigInteger phi;
    private BigInteger e;
    private BigInteger d;
    private static int bitlength = 32;
    private Random     r;
    private static int blockSize;
    private static int chunkSize;
    public RSARef2()
    {
        r = new Random();
        p = new BigInteger("4107176093");
        q = new BigInteger("3448577593");
        N = p.multiply(q);
        phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        //e = new BigInteger("50627");
        e = new BigInteger("46199"); //58441, 53899
        chunkSize=(2*bitlength)/8;
        blockSize=chunkSize+1;
//        while (phi.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phi) < 0)
//        {
//            e.add(BigInteger.ONE);
//        }
        d = e.modInverse(phi);

        	
    }
 
    public RSARef2(BigInteger e, BigInteger d, BigInteger N)
    {
        this.e = e;
        this.d = d;
        this.N = N;
    }
 
    //@SuppressWarnings("deprecation")
    public static void main(String[] args) throws Exception
    {
        
    	
        
    }
    public void driverTest2() throws Exception{
    	RSARef2 rSARef2=new RSARef2();
        //rsa.driverTest1();
    	rSARef2.encryptFile("plaintext.txt","encrypted.txt");
    	rSARef2.decryptFile("encrypted.txt","decrypted.txt");
    }
    public void driverTest1() throws Exception{
    	
		byte[] block= "he video".getBytes(); //he video//100
		byte[] eblock=encryptBlock(block,blockSize);
		byte[] dblock=decryptBlock(eblock,blockSize);
		//Plaintext
		System.out.println("_________________");
		System.out.println("Block Byte  "+bytesToString(block));
		System.out.println("Block String  "+new String(block));
		System.out.println("Block Byte Hex  "+bytesToHexString(block));
		//Encrypted
	    System.out.println("EBlock Byte  "+bytesToString(eblock));
	    System.out.println("EBlock String  "+new String(eblock));
	    System.out.println("EBlock Byte Hex  "+bytesToHexString(eblock));
	    
	  //Encrypted
	    System.out.println("DBlock Byte  "+bytesToString(dblock));
	    System.out.println("DBlock String  "+new String(dblock));
	    System.out.println("DBlock Byte Hex  "+bytesToHexString(dblock));
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
			int count=1;
			System.out.println("EBlock String: ");
			while(i != -1){
				byte[] eblock=encryptBlock(block,blockSize);
			  //Plaintext
				//Encrypted
			    System.out.print(new String(eblock));
			    count++;
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
			System.out.println("\nDBlock String: ");
			while(i != -1){
				dblock=decryptBlock(block,blockSize);
				fos.write(dblock,0,dblock.length);
				i=fis.read(block);
			    System.out.print(new String(dblock));
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
}