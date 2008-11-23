package mikera.util;

public class Bits {
	public static int roundUpToPowerOfTwo(int n) {
		n = n - 1;
		n = fillBitsRight(n);
		n = n + 1;
		return n;
	}
	
	public static long roundUpToPowerOfTwo(long n) {
		n = n - 1;
		n = fillBitsRight(n);
		n = n + 1;
		return n;
	}
	
	public static int roundDownToPowerOfTwo(int n) {
		return n & (~(fillBitsRight(n)>>1));	}
	
	public static long roundDownToPowerOfTwo(long n) {
		return n & (~(fillBitsRight(n)>>1));
	}
	
	public static long fillBitsRight(long n) {
		n = n | (n >> 1);
		n = n | (n >> 2);
		n = n | (n >> 4);
		n = n | (n >> 8);
		n = n | (n >> 16);
		n = n | (n >> 32);
		return n;
	}
	
	public static int fillBitsRight(int n) {
		n = n | (n >> 1);
		n = n | (n >> 2);
		n = n | (n >> 4);
		n = n | (n >> 8);
		n = n | (n >> 16);
		return n;
	}
}
