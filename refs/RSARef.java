package Crypto;
import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
 
public class RSARef
{
    private BigInteger p;
    private BigInteger q;
    private BigInteger N;
    private BigInteger phi;
    private BigInteger e;
    private BigInteger d;
    private int        bitlength = 32;
    private Random     r;
    private static int blockSize;
    public RSARef()
    {
        r = new Random();
        p = BigInteger.probablePrime(bitlength, r);
        q = BigInteger.probablePrime(bitlength, r);
        N = p.multiply(q);
        phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        e = BigInteger.probablePrime(bitlength / 2, r);
        while (phi.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phi) < 0)
        {
            e.add(BigInteger.ONE);
        }
        d = e.modInverse(phi);
        blockSize=(2*bitlength)/8+1;
    }
 
    public RSARef(BigInteger e, BigInteger d, BigInteger N)
    {
        this.e = e;
        this.d = d;
        this.N = N;
    }
 
    //@SuppressWarnings("deprecation")
    public static void main(String[] args) throws IOException
    {
    	test3();
        
    }
    public static void test3() throws IOException{
    	long startTime = System.currentTimeMillis();
    	System.out.println("Test3");
    	
    	String s="a";
    	for(int i=0;i<100;i++){
    		s="12345"+i;
    		test2(s);
    	}
    	long endTime   = System.currentTimeMillis();
    	long totalTime = endTime - startTime;
    	System.out.println("Processing Time: "+totalTime+" ms");
    }
    public static void test2(String teststring) throws IOException{
    	RSARef rsa = new RSARef();
       
        // encrypt
        byte[] encrypted = rsa.encrypt(teststring.getBytes());
       
      //Check get FirstByte
        encrypted=setFixedBlock(encrypted,blockSize);
        // decrypt
        byte[] decrypted = rsa.decrypt(encrypted);
        String decryptedString=new String(decrypted);
        //if(!teststring.equals(decryptedString)) System.out.println(false);
        System.out.println(encrypted.length+"    "+teststring.equals(decryptedString));
//        if(encrypted.length !=64 || !teststring.equals(decryptedString)) {
//        	System.out.println("+"); return false;
//        }
      
        
        
        
    }
    public static void test() throws IOException{
    	RSARef rsa = new RSARef();
        DataInputStream in = new DataInputStream(System.in);
        String teststring;
        System.out.println("Enter the plain text:");
        teststring = in.readLine();
        System.out.println("Encrypting String: " + teststring);
        System.out.println("String in Bytes: "
                + bytesToString(teststring.getBytes()));
        // encrypt
        byte[] encrypted = rsa.encrypt(teststring.getBytes());
        System.out.println("Encrypting Bytes: " + bytesToString(encrypted));
        System.out.println("Encrypted String: " + new String(encrypted));
        System.out.println("Encrypted Bytes Length: " + encrypted.length);
      //Check get FirstByte
        byte fbyte=encrypted[0];
        byte[] encrypted2=null;
        encrypted=trimByte(encrypted);
        // decrypt
        byte[] decrypted = rsa.decrypt(encrypted);
        System.out.println("Decrypting Bytes: " + bytesToString(decrypted));
        System.out.println("Decrypted String: " + new String(decrypted));
        

        System.out.println("Bit length of Encrypted "+encrypted.length);
        System.out.println("Bit length of Decrypted "+decrypted.length);
        System.out.println("Bit length of N "+rsa.N.bitLength());
    }
    public static byte[] skipByte(byte[]original, int eliminate){
    	int len=original.length-eliminate;
    	byte[] res=new byte[len];
    	res=Arrays.copyOfRange( original, eliminate, original.length);
    	return res;
    }
    public static byte[] skipByte2(byte[]original, int eliminate){
    	byte[] res=new byte[original.length-eliminate];
    	for(int i=eliminate;i<original.length;i++){
    		res[i-eliminate]=original[i];
    		
    	}
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
    public byte[] encrypt(byte[] message)
    {
        return (new BigInteger(message)).modPow(e, N).toByteArray();
    }
 
    // Decrypt message
    public byte[] decrypt(byte[] message)
    {
        return (new BigInteger(message)).modPow(d, N).toByteArray();
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
    public static byte[] setFixedBlock(byte[] original, int byteSize){
    	original=trimByte(original);//remove the case that original's length is greater than byteSize;
    	if(original.length<byteSize) return appendByte(new byte[byteSize-original.length],original);
    	return original;
    }
}