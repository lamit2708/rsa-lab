package Crypto;
/**
 ** Java Program to Implement Miller Rabin Primality Test Algorithm
 **/
 
import java.util.Random;
import java.math.BigInteger;
 
/** Class MillerRabin **/
public class MillerRabinLong
{
    /** Function to check if prime or not **/
    public boolean isPrime(long n, int k)
    {
    	/*
    	 * Input #1: n > 3, an odd integer to be tested for primality; 
    	 * Input #2: k, a parameter that determines the accuracy of the test
    	 * Output: composite if n is composite, otherwise probably prime
    	 */
        /** base case **/
        if (n == 0 || n == 1)
            return false;
        /** base case - 2 is prime **/
        if (n == 2)
            return true;
        /** an even number other than 2 is composite **/
        if (n % 2 == 0)
            return false;
        //write n − 1 as (2^r)*q with q odd by factoring powers of 2 from n − 1
        long q = n - 1;
        while (q % 2 == 0)
            q /= 2;
        //Select a random integer a, 1< a <n–1
        Random rand = new Random();
        for (int i = 0; i < k; i++)
        {
            long r = Math.abs(rand.nextLong());            
            long a = r % (n - 1) + 1, temp = q; //a =1 to n-1
            long mod = modPow(a, temp, n);
            while (temp != n - 1 && mod != 1 && mod != n - 1)
            {
                mod = mulMod(mod, mod, n);
                temp *= 2;
            }
            if (mod != n - 1 && temp % 2 == 0)
                return false;
        }
        return true;        
    }
    public boolean isPrimeX(long n, int k){
    	//https://en.wikipedia.org/wiki/Miller%E2%80%93Rabin_primality_test
    	Random rand = new Random();
    	 /** base case **/
        if (n == 0 || n == 1)
            return false;
        /** base case - 2 is prime **/
        if (n == 2 || n == 3)
            return true;
        /** an even number other than 2 is composite **/
        if (n % 2 == 0)
            return false;
        //write n − 1 as (2^r)*d with d odd by factoring powers of 2 from n − 1
        long d = n - 1;
        long r=0;
        while (d % 2 == 0){
            d /= 2;
            r++;
        }
        //WitnessLoop: repeat k times:
        for(int i=0;i<k; i++){
        
	        //pick a random integer a in the range [2, n − 2]
	        long a = Math.abs(rand.nextLong());            
	        a = a % (n - 3) + 2; 
	        //x ← a^d mod n
	        long x = modPow(a, d, n);
	        if(x == 1 || x == n-1) continue; //continue WitnessLoop
	        //repeat r − 1 times:
	        int j;
	        for(j=0;j<r; j++){
	        	//x ← x^2 mod n
	        	x=mulMod(x,x,n); 
	        	if(x ==1 ) return false; //if x = 1 then return composite
	        	else if(x == (n-1)) break; //if x = n − 1 then continue WitnessLoop
	        }
	        if( x != (n-1)) return false; //composite
	    }
    	return true;
    }
    /** Function to calculate (a ^ b) % c **/
    public long modPow(long a, long b, long c)
    {
        long res = 1;
        for (int i = 0; i < b; i++)
        {
            res *= a;
            res %= c; 
        }
        return res % c;
    }
    /** Function to calculate (a * b) % c **/
    public long mulMod(long a, long b, long mod) 
    {
        return BigInteger.valueOf(a).multiply(BigInteger.valueOf(b)).mod(BigInteger.valueOf(mod)).longValue();
    }
    /** Main function **/
    public static void main (String[] args) 
    {
        test();
    	
 
    }
    public static void test(){
    	
        MillerRabinLong mr = new MillerRabinLong();
        /** Accept number **/
        
       
        String[] primes = {"22", "3613", "7297",
				"226673591177742970257407", "2932031007403" };
		String[] nonPrimes = { "3341", "2932021007403",
				"226673591177742970257405" };
		long[] primesLong = {   3, 7, 11, 13, 17, 19, 23, 29,31, 3613, 7297, 3341};//,				226673591177742970257407, 2932031007403 };
		long[] nonPrimesLong = { 3341};//, 2932021007403,	226673591177742970257405 }
		int k = 40;
		
		for (long p : primesLong)
			assert  mr.isPrimeX(p, k): p+ " Khong phai so nguyen to";
		for (long n : nonPrimesLong)
			assert  !mr.isPrime(n, k): "Not composite";
    }
    /** Function to check if prime or not **/
	public boolean isPrimeLongByMillerRabin(long n, int k){
    	//https://en.wikipedia.org/wiki/Miller%E2%80%93Rabin_primality_test
    	Random rand = new Random();
    	 /** base case **/
        if (n == 0 || n == 1) return false;
        /** base case - 2 is prime **/
        if (n == 2 || n == 3) return true;
        /** an even number other than 2 is composite **/
        if (n % 2 == 0)
            return false;
        //write n − 1 as (2^r)*d with d odd by factoring powers of 2 from n − 1
        long d = n - 1;
        long r=0;
        while (d % 2 == 0){
            d /= 2;
            r++;
        }
        for(int i=0;i<k; i++){
	        //pick a random integer a in the range [2, n − 2]
	        long a = Math.abs(rand.nextLong());            
	        a = a % (n - 3) + 2; 
	        long x = modPow(a, d, n);
	        if(x == 1 || x == n-1) continue; //continue WitnessLoop
	        int j;
	        for(j=0;j<r; j++){
	        	x=mulMod(x,x,n); 
	        	if(x ==1 ) return false; //if x = 1 then return composite
	        	if(x == (n-1)) break; //if x = n − 1 then continue WitnessLoop
	        }
	        if( x != (n-1)) return false; //composite
	    }
    	return true;
    }
    
}