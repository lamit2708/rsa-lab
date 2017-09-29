/**
 ** Java Program to Implement Miller Rabin Primality Test Algorithm
 **/
 
import java.util.Random;
import java.util.Scanner;
import javax.swing.JTextArea;
import java.math.BigInteger;
import java.security.SecureRandom;
 
/** Class MillerRabin **/
public class PrimeNumber
{
	private static final BigInteger ZERO=BigInteger.ZERO;
	private static final BigInteger ONE=BigInteger.ONE;
	private static final BigInteger TWO=new BigInteger("2");
	private static final BigInteger THREE=new BigInteger("3");
	private static JTextArea logger;
	private static final int LOG_SYTEM=1;
	private static final int LOG_GRAPHIC=2;
	private static int logType = LOG_SYTEM;
	
	public static boolean isPrimeBigByMillerRabin(BigInteger n, int k){
    	//https://en.wikipedia.org/wiki/Miller%E2%80%93Rabin_primality_test
        if (ZERO.equals(n) || ONE.equals(n)) return false;
        if (TWO.equals(n) || THREE.equals(n)) return true;
        /** an even number other than 2 is composite **/
        if (n.mod(TWO).equals(ZERO)) return false;
        BigInteger d =n.subtract(ONE);  //write n − 1 as (2^r)*d
        int r=0;
        while (d.mod(TWO).equals(ZERO)){
            d=d.divide(TWO);
            r++;
        }
        
        for(int i=0; i<k; i++){
	        //pick a random integer a in the range [2, n − 2]
	        BigInteger a=randomRangeBig(TWO, n.subtract(TWO));
	        BigInteger x = modPowBig(a, d, n);
	        if(x.equals(ONE) || x.equals(n.subtract(ONE))) continue; //continue WitnessLoop
	        int j;
	        for(j=0;j<r; j++){
	        	x=x.multiply(x).mod(n); 
	        	if(x.equals(ONE)) return false; //if x = 1 then return composite
	        	if(x.equals(n.subtract(ONE))) break; //if x = n − 1 then continue WitnessLoop
	        }
	        if( !x.equals(n.subtract(ONE))) return false; //composite
	    }
    	return true;
    }
	public static BigInteger randomRangeBig(BigInteger bottom, BigInteger top) {
		//Random rnd = new Random();
		try {
			Random rnd = SecureRandom.getInstance("SHA1PRNG");
			BigInteger res;
			do {
				res = new BigInteger(top.bitLength(), rnd);
			} while (res.compareTo(bottom) < 0 || res.compareTo(top) > 0);
			return res;
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return null;
	}
	

    /** Greatest Common Divisor **/
    public BigInteger gcd(BigInteger a, BigInteger b){ 
		  a=a.abs();
		  b = b.abs();
		  while(!a.equals(ZERO) && b.equals(ZERO)) // until either one of them is 0
		  {
		     BigInteger c = new BigInteger(b.toString());
		     b = a.mod(b);
		     a = c;
		  }
		  return a.add(b); // either one is 0, so return the non-zero value
	 
    }
    public static BigInteger modPowBig(BigInteger base, BigInteger exponent, final BigInteger modulo) {
        // plan: exploit the binary representation of the exponent, see for example http://en.wikipedia.org/w/index.php?title=Modular_exponentiation&oldid=517653653#Right-to-left_binary_method
        BigInteger result = BigInteger.ONE;
        while (exponent.compareTo(BigInteger.ZERO) > 0) {
            if (exponent.testBit(0)) // then exponent is odd
                result = (result.multiply(base)).mod(modulo);
            exponent = exponent.shiftRight(1);
            base = (base.multiply(base)).mod(modulo);
        }
        //return result.mod(modulo);
        return result;
    }
    public static void main(String[] args) throws Exception
    {
    	driverGenPrime();
        
    }
    public static void driverGenPrime(){ //Just for test
    	long startTime = System.currentTimeMillis();
    	Scanner in = new Scanner(System.in);
    	System.out.printf("Enter the number of bits: ");
    	int bitLength=in.nextInt();
    	System.out.printf("Enter the certainty number: ");
    	int certainty=in.nextInt();
    	System.out.println("Finding a "+bitLength+"-bit prime with the certanty of "+certainty);
    	BigInteger prime=genPrime(bitLength, certainty);
    	System.out.println("\nWe have the prime: ");
    	System.out.println(prime.toString());
    	System.out.println("Length: " +prime.bitLength()+" bits");
    	long endTime   = System.currentTimeMillis();
    	long totalTime = endTime - startTime;
    	System.out.println("Processing Time: "+totalTime+" ms");
    	int certaintyCheck=100;
    	System.out.println("Use the library BigInteger.isProbablePrime, recheck with the certainty of "+certaintyCheck+": "+prime.isProbablePrime(certainty));
    	
    }
    public static BigInteger genPrime(int bitLength,int k){
    	BigInteger n=genFixOddBigInteger(bitLength);
    	int loop = 1;
    	int loop100=0;
    	
    	logProgress("\nFinding 00"+loop+"\t.");
    	while(!isPrimeBigByMillerRabin(n,k)){
    		loop++;
    		n=n.add(TWO);
    		logProgress(".");
    		if(loop%100==0) {
    			loop100++;
    			logProgress("\nFinding "+loop+"\t.");
    		}
    	}
    	logProgress("Success!\n");
    	return n;
    }
    public static void logProgress(String signal){
    	if(logType==LOG_GRAPHIC){
    		logger.append(signal);
    	}else{
    		System.out.print(signal);
    	}
    }
    
    public static BigInteger genFixOddBigInteger(int bitLength){
    	Random rnd = new Random();
    	BigInteger b ;
    	int bitMissing=0;
    	do{
    		b = new BigInteger(bitLength,rnd);
    		bitMissing=bitLength - b.bitLength();
    	}while(bitMissing>(bitLength/2));
    	if(bitMissing>0) 
    		b=b.shiftLeft(bitMissing); //Fix Length
    	if(b.mod(TWO).equals(ZERO)) 
    		b=b.add(ONE); //ODD
    	return b;
    }
public static void driverTestIsPrimeBigByMillerRabin(){
    	
        PrimeNumber papp = new PrimeNumber();
        /** Accept number **/
        
       
        String[] primes = {"3","31","97", "3613", "7297", "226673591177742970257407", "2932031007403" };
		String[] nonPrimes = { "91","3341", "2932021007403", "226673591177742970257405" };
		
		int k = 40;
		for (String p : primes)
			assert  isPrimeBigByMillerRabin(new BigInteger(p), k): p+ " Khong phai so nguyen to";
		for (String n : nonPrimes)
			assert  isPrimeBigByMillerRabin(new BigInteger(n), k): "Not composite";
    }
public static void setLogger(JTextArea console) {
	logger=console;
	logType=LOG_GRAPHIC;
}
  
    
    
}