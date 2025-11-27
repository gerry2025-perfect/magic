package com.iwhalecloud.bss.magic.script.functions;

import com.iwhalecloud.bss.magic.script.annotation.Comment;
import com.iwhalecloud.bss.magic.script.annotation.Function;

import java.lang.reflect.Array;

public class ArrayFunctions {

	@Function
	@Comment("Create an int array")
	public int[] new_int_array(@Comment(name = "size", value = "Array size") int size) {
		return new int[size];
	}

	@Function
	@Comment("Create a short array")
	public short[] new_short_array(@Comment(name = "size", value = "Array size") int size) {
		return new short[size];
	}

	@Function
	@Comment("Create a double array")
	public double[] new_double_array(@Comment(name = "size", value = "Array size") int size) {
		return new double[size];
	}

	@Function
	@Comment("Create a float array")
	public float[] new_float_array(@Comment(name = "size", value = "Array size") int size) {
		return new float[size];
	}

	@Function
	@Comment("Create a byte array")
	public byte[] new_byte_array(@Comment(name = "size", value = "Array size") int size) {
		return new byte[size];
	}

	@Function
	@Comment("Create a char array")
	public char[] new_char_array(@Comment(name = "size", value = "Array size") int size) {
		return new char[size];
	}

	@Function
	@Comment("Create a boolean array")
	public boolean[] new_boolean_array(@Comment(name = "size", value = "Array size") int size) {
		return new boolean[size];
	}

	@Function
	@Comment("Create a long array")
	public long[] new_long_array(@Comment(name = "size", value = "Array size") int size) {
		return new long[size];
	}

	@Function
	@Comment("Create an object array")
	public Object[] new_array(@Comment(name = "size", value = "Array size") int size) {
		return new Object[size];
	}

	@Function
	@Comment("Create an object array")
	public <T> T[] new_array(@Comment(name = "componentType", value = "Array type") Class<T> componentType,
							 @Comment(name = "size", value = "Array size") int size) {
		return (T[]) Array.newInstance(componentType, size);
	}

	@Function
	@Comment("Create a String array")
	public String[] new_array(@Comment(name = "values", value = "String") String... array) {
		return array;
	}

	@Function
	@Comment("Create an int array")
	public int[] new_array(@Comment(name = "size", value = "Array size") int... array) {
		return array;
	}

	@Function
	@Comment("Create a short array")
	public short[] new_array(@Comment(name = "size", value = "Array size") short... array) {
		return array;
	}

	@Function
	@Comment("Create a double array")
	public double[] new_array(@Comment(name = "values", value = "double value") double... array) {
		return array;
	}

	@Function
	@Comment("Create a float array")
	public float[] new_array(@Comment(name = "values", value = "float value") float... array) {
		return array;
	}

	@Function
	@Comment("Create a char array")
	public char[] new_array(@Comment(name = "values", value = "char value") char... array) {
		return array;
	}

	@Function
	@Comment("Create a byte array")
	public byte[] new_array(@Comment(name = "values", value = "byte value") byte... array) {
		return array;
	}

	@Function
	@Comment("Create a boolean array")
	public boolean[] new_array(@Comment(name = "values", value = "boolean value") boolean... array) {
		return array;
	}

	@Function
	@Comment("Create a long array")
	public long[] new_array(@Comment(name = "values", value = "long value") long... array) {
		return array;
	}

}
