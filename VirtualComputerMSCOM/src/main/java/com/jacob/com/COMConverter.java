package com.jacob.com;

public class COMConverter {
	public static long GetVariantAddress(Variant variant) {
		return variant.m_pVariant;
	}

	public static void SetVariantAddress(Variant variant, long addr) {
		variant.m_pVariant = addr;
	}
}
