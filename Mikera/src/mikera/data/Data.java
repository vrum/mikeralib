package mikera.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

import mikera.net.BufferCache;
import mikera.net.Util;
import mikera.util.Maths;
import mikera.util.TextUtils;
import mikera.util.emptyobjects.NullArrays;

/**
 * Class representing a chunk of data as a mutable
 * variable length block of bytes
 * 
 * Note: big-endian format used for numbers, this allows for 
 * data comparison to be equivalent to numerical comparison
 * 
 * @author Mike
 *
 */
public final class Data extends AbstractList<Byte> implements List<Byte>, Cloneable, Serializable, Comparable<Data>, Externalizable {
	private static final long serialVersionUID = 293989965333996558L;
	private static final int DEFAULT_DATA_INCREMENT=50;
	
	private byte[] data=NullArrays.NULL_BYTES;
	private int count=0;
	
	public Data() {
	}
	
	/**
	 * Creates a Data object with the given initial capacity
	 * @param length
	 */
	public Data(int length) {
		data=new byte[length];
	}
	
	public Data(Data d) {
		this(d.size());
		d.copyTo(0, this, 0, d.size());
	}
	
	public Data(ByteBuffer bb) {
		appendByteBuffer(bb);
	}
	
	private Data(byte[] bytes) {
		data=bytes;
		count=bytes.length;
	}
	
	private class DataIterator implements Iterator<Byte> {
		private int pos=0;
		
		public boolean hasNext() {
			return pos<data.length;
		}

		public Byte next() {
			return data[pos++];
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	public Iterator<Byte> iterator() {
		return new DataIterator();
	}
	
	/**
	 * Creates data object by bulk reading from a (flipped) bytebuffer
	 * 
	 * @param bb
	 * @return
	 */
	public static Data create(ByteBuffer bb) {
		int remaining=bb.remaining();
		Data nd=new Data(remaining);
		bb.get(nd.data, 0, remaining);
		nd.count=remaining;
		return nd;
	}
	
	public static Data create(byte[] bytes) {
		Data nd=new Data(bytes.length);
		nd.append(bytes, 0, bytes.length);
		return nd;
	}
	
	public static Data wrap(byte[] bytes) {
		return new Data(bytes);
	}
	
	public byte getByte(int pos) {
		if ((pos<0)||(pos>=count)) throw new IndexOutOfBoundsException();
		return data[pos];
	}
	
	public boolean getBoolean(int pos) {
		if ((pos<0)||(pos>=count)) throw new IndexOutOfBoundsException();
		return data[pos]!=0;
	}
	
	public int getInt(int pos) {
		return getVarInt(pos);		
	}
	
	public int getFullInt(int pos) {
		if ((pos<0)||((pos+3)>=count)) throw new IndexOutOfBoundsException();
		return ((int)data[pos+3]&255)
	      |(((int)data[pos+2]&255)<<8)		
	      |(((int)data[pos+1]&255)<<16)		
	      |(((int)data[pos]&255)<<24);		
	}
	
	public char getChar(int pos) {
		if ((pos<0)||((pos+1)>=count)) throw new IndexOutOfBoundsException();
		int res= ((data[pos+1])&(255))
	      |(((data[pos])&(255))<<8);
		return (char)res;
	}
	
	public short getShort(int pos) {
		if ((pos<0)||((pos+1)>=count)) throw new IndexOutOfBoundsException();
		int res= ((data[pos+1])&(255))
	      |(((data[pos])&(255))<<8);
		return (short)res;
	}
	
	public float getFloat(int pos) {
		return Float.intBitsToFloat(getFullInt(pos));
	}
	
	public long getLong(int pos) {
		return getVarLong(pos);
	}
	
	public long getFullLong(int pos) {
		long lv=((long)getFullInt(pos+4))&0xFFFFFFFFl;
		lv^=((long)getFullInt(pos))<<32;
		return lv;
	}
	
	public double getDouble(int pos) {
		return Double.longBitsToDouble(getFullLong(pos));
	}

	public Byte get(int pos) {
		return getByte(pos);
	}
	
	public int appendByte(byte b) {
		put(count,b);
		return 1;
	}
	
	public int appendBoolean(boolean b) {
		put(count,(byte)(b?1:0));
		return 1;
	}
	
	public int appendInt(int v) {
		return appendVarInt(v);
	}
	
	public int appendFullInt(int v) {
		int pos=count;
		ensureCapacity(pos+4);
		data[pos+3]=(byte)(v);
		data[pos+2]=(byte)(v>>>8);
		data[pos+1]=(byte)(v>>>16);
		data[pos]=(byte)(v>>>24);
		count+=4;
		
		return 4;
	}
	
	public int appendByteBuffer(ByteBuffer bb) {
		int pos=count;
		int rem=bb.remaining();
		ensureCapacity(pos+rem);
		bb.get(data, pos, rem);
		count+=rem;
		
		return rem;
	}
	
	public int appendChar(char v) {
		int pos=count;
		ensureCapacity(pos+2);
		data[pos+1]=(byte)(v);
		data[pos]=(byte)(v>>>8);
		count+=2;
		
		return 2;
	}
	
	public int appendShort(short v) {
		int pos=count;
		ensureCapacity(pos+2);
		data[pos+1]=(byte)(v);
		data[pos]=(byte)(v>>>8);
		count+=2;
		
		return 2;
	}
	
	public int appendFloat(float v) {
		return appendFullInt(Float.floatToIntBits(v));
	}
	
	public int appendDouble(double v) {
		return appendFullLong(Double.doubleToLongBits(v));
	}
	
	public int appendLong(long lv) {
		return appendVarLong(lv);
	}
	
	public int appendFullLong(long lv) {
		int sizeResult=0;
		sizeResult+=appendFullInt((int)(lv>>32));
		sizeResult+=appendFullInt((int)(lv));
		return sizeResult;
	}
	
	public int appendString(String s) {
		int size=0;
		int len=s.length();
		size+=appendInt(len);
		for (int i=0; i<len; i++) {
			size+=appendChar(s.charAt(i));
		}
		return size;
	}
	
	public String getString(int pos) {
		int len=getInt(pos);
		pos+=sizeOfInt(len);
		
		char[] cs=new char[len];
		for (int i=0; i<len; i++) {
			char c=getChar(pos);
			pos+=sizeOfChar(c);
			cs[i]=c;
		}
		return new String(cs);	
	}
	

	public int append(Data d) {
		int size=d.size();
		put(count,d,0,size);
		return size;
	}
	
	public int append(byte[] bs, int offset, int len) {
		put(count,bs,offset,len);
		return len;
	}
	
	public void put(int pos, byte b) {
		if (pos+1>count) {
			ensureCapacity(pos+1);
			count=pos+1;
		}		
		data[pos]=b;
	}
	
	public Byte set(int pos, Byte b) {
		Byte result=get(pos);
		put(pos,b);
		return result;
	}
	
	public void put(int pos, byte[] bs, int offset, int len) {
		if (pos+len>count) {
			ensureCapacity(pos+len);
			count=pos+len;
		}
		System.arraycopy(bs, offset, data, pos, len);
	}
	
	public void put(int pos, Data d, int offset, int len) {
		if (pos+len>count) {
			ensureCapacity(pos+len);
			count=pos+len;
		}
		System.arraycopy(d.data, offset, data, pos, len);
	}
	
	public void copyTo(int pos, byte[] dest, int destoffset, int len) {
		System.arraycopy(data, pos, dest, destoffset, len);	
	}
	
	public void copyTo(int pos, Data dest, int destoffset, int len) {
		dest.put(destoffset,data,pos,len);	
	}
	
	public Data subset(int start, int end) {
		if ((start<0)||(end>count)) throw new IllegalArgumentException();
		int len=end-start;
		Data d=new Data(len);
		copyTo(start,d,0,len);
		return d;
	}
	
	public int size() {
		return count;
	}
	
	public void clear() {
		count=0;
		data=NullArrays.NULL_BYTES;
	}
	
	byte[] getInternalData() {
		return data;
	}
	
	public void ensureCapacity(int len) {
		int dlen=data.length;
		
		// extend data array if too small
		if (dlen<len) {
			int nlen=Maths.max(len,dlen*2,dlen+DEFAULT_DATA_INCREMENT);
			byte[] ndata=new byte[nlen];
			System.arraycopy(data, 0, ndata,0, count);
			data=ndata;
		}
	}
	
	public int currentCapacity() {
		return data.length;
	}
	
	public void writeToByteBuffer(ByteBuffer bb) {
		bb.put(data,0,count);
	}
	
	public ByteBuffer toFlippedByteBuffer() {
		ByteBuffer bb=BufferCache.instance().getBuffer(count);
		bb.put(data,0,count);
		bb.flip();
		return bb;
	}
	
	public ByteBuffer toWrapByteBuffer() {
		ByteBuffer bb=ByteBuffer.wrap(data);
		bb.limit(count);
		return bb;
	}
	
	public ByteBuffer wrapAndClear() {
		ByteBuffer bb=ByteBuffer.wrap(data);
		bb.limit(count);
		clear();
		return bb;
	}
	
	public byte[] toNewByteArray() {
		byte[] bs=new byte[count];
		System.arraycopy(data, 0, bs,0, count);
		return bs;
	}
	
	public int hashCode() {
		int result=0;
		for(int i=0; i<count; i++) {
			result^=data[i];
			result=Integer.rotateRight(result, 1);
		}
		return result;
	}
	
	@Override
	public Data clone() {
		Data nd=new Data(count);
		copyTo(0,nd,0,count);
		return nd;
	}
	
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if (o instanceof List<?>) {
			return equals((List<Byte>) o);
		}
		return super.equals(o);
	}
	
	public boolean equals(List<Byte> l) {
		int size=size();
		if (size!=l.size()) return false;
		for (int i=0; i<size; i++) {
			if (data[i]!=l.get(i)) return false;
		}
		return true;
	}
	
	public int compareTo(Data d) {
		int n=Maths.min(size(), d.size());
		for (int i=0; i<n; i++) {
			int bd=getByte(i)-d.getByte(i);
			if (bd!=0) return bd;
		}
		if (size()<d.size()) return -1;
		if (size()>d.size()) return 1;
		return 0;
	}
	
	public String toString() {
		StringBuilder sb=new StringBuilder();
		
		int s=size();
		for (int i=0; i<s; i++) {
			if (i>0) {
				if ((i&15)==0) {
					sb.append('\n');
				} else {
					sb.append(' ');					
				}
			}
			int b=data[i];
			sb.append(TextUtils.toHexChar(b>>4));
			sb.append(TextUtils.toHexChar(b));
		}
		
		return sb.toString();
	}

	public void readExternal(ObjectInput oi) throws IOException,
			ClassNotFoundException {
		int len=oi.readInt();
		data=new byte[len];
		int res=oi.read(data, 0, len);
		if (res!=len) throw new IOException("Error: "+res+" bytes read out of length "+len+" expected");
		count=len;
	}

	public void writeExternal(ObjectOutput oo) throws IOException {
		oo.writeInt(count);
		oo.write(data, 0, count);
	}

	public static int sizeOfBoolean(boolean b) {
		return 1;
	}
	
	public static int sizeOfByte(byte b) {
		return 1;
	}
	
	public static int sizeOfShort(short b) {
		return 2;
	}
	
	public static int sizeOfChar(char b) {
		return 2;
	}
	
	public static int sizeOfInt(int b) {
		return sizeOfVarInt(b);
	}
	
	public static int sizeOfFullInt(int b) {
		return 4;
	}
	
	public static int sizeOfFullLong(long b) {
		return 8;
	}
	
	public static int sizeOfFloat(float b) {
		return 4;
	}
	
	public static int sizeOfDouble(double b) {
		return 8;
	}
	
	public static int sizeOfLong(long b) {
		return sizeOfVarLong(b);
	}
	
	public static int sizeOfString(String s) {
		int stringLength=s.length();
		
		int size=Data.sizeOfInt(stringLength);
		
		for (int i=0; i<stringLength; i++) {
			size+=Data.sizeOfChar(s.charAt(i));
		}
		
		return size;
	}
	
	/*****************************************************
	 * Variable length integer handling
	 * 
	 * Inspired by Google's protocol buffers
	 * 
	 * Byte format:
	 *   most significant bit = {1: more bytes coming / 0: last byte of varint }
	 *   lowest seven bits    = 7 bits of encoded integer (little-endian sequencing)
	 */
	
	public int appendVarInt(final int i) {
		int enc=Util.zigzagEncodeInt(i);
		int size=sizeOfEncodedVarInt(enc);
		int pos=count;
		ensureCapacity(pos+size);
		
		while ((enc&(~0x7F))!=0) {
			data[pos++]=(byte)( enc |0x80);
			enc>>>=7;
		}		
		data[pos++]=(byte)( (enc&0x7F));
		
		count+=size;
		return size;
	}
	
	public int appendVarLong(final long i) {
		long enc=Util.zigzagEncodeLong(i);
		int size=sizeOfEncodedVarLong(enc);
		int pos=count;
		ensureCapacity(pos+size);
		
		while ((enc&(~0x7FL))!=0) {
			data[pos++]=(byte)( enc |0x80);
			enc>>>=7;
		}		
		data[pos++]=(byte)( (enc&0x7F));
		
		count+=size;
		return size;
	}
	
	public int getVarInt(int pos) {
		int enc=0;
		byte b=data[pos++];
		int shift=0;
		while ((b&0x80)!=0) {
			enc|=((int)(b&0x7F))<<shift;
			b=data[pos++];
			shift+=7;
		}
		enc|=((int)b)<<shift;
		return Util.zigzagDecodeInt(enc);
	}
	
	public long getVarLong(int pos) {
		long enc=0;
		byte b=data[pos++];
		int shift=0;
		while ((b&0x80)!=0) {
			enc|=((long)(b&0x7F))<<shift;
			b=data[pos++];
			shift+=7;
		}
		enc|=((long)b)<<shift;
		return Util.zigzagDecodeLong(enc);
	}
	
	
	public static int sizeOfVarInt(int a) {
		int enc=Util.zigzagEncodeInt(a);
		return sizeOfEncodedVarInt(enc);
	}
	
	private static int sizeOfEncodedVarInt(int enc) {
	    if ((enc & (0xffffffff <<  7)) == 0) return 1;
	    if ((enc & (0xffffffff << 14)) == 0) return 2;
	    if ((enc & (0xffffffff << 21)) == 0) return 3;
	    if ((enc & (0xffffffff << 28)) == 0) return 4;
	    return 5;
	}
	
	public static int sizeOfVarLong(long a) {
		long enc=Util.zigzagEncodeLong(a);
		return sizeOfEncodedVarLong(enc);
	}
	
	private static int sizeOfEncodedVarLong(long enc) {
	    if ((enc & (0xffffffffffffffffL <<  7)) == 0) return 1;
	    if ((enc & (0xffffffffffffffffL << 14)) == 0) return 2;
	    if ((enc & (0xffffffffffffffffL << 21)) == 0) return 3;
	    if ((enc & (0xffffffffffffffffL << 28)) == 0) return 4;
	    if ((enc & (0xffffffffffffffffL << 35)) == 0) return 5;
	    if ((enc & (0xffffffffffffffffL << 42)) == 0) return 6;
	    if ((enc & (0xffffffffffffffffL << 49)) == 0) return 7;
	    if ((enc & (0xffffffffffffffffL << 56)) == 0) return 8;
	    if ((enc & (0xffffffffffffffffL << 63)) == 0) return 9;
	    return 10;
	
	}
}
