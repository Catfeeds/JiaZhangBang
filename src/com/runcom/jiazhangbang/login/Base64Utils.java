package com.runcom.jiazhangbang.login;

import android.util.Base64;

/**
 * ʹ��Base64������ͻ�ȡ��������
 */
public class Base64Utils
{

	/**
	 * Base64����
	 * 
	 * @param Ҫ������ַ���
	 * @return ������ɵ��ַ�����
	 */
	public static String getEncodeStr(String str )
	{
		byte byteArr[] = Base64.encode(str.getBytes() ,Base64.DEFAULT);
		return new String(byteArr);
	}

	/**
	 * Base64����
	 * 
	 * @param ��������ַ���
	 * @return ������ɣ����ԭ���ַ���
	 */
	public static String getDecodeStr(String encodeStr )
	{
		byte byteArr[] = Base64.decode(encodeStr ,Base64.DEFAULT);
		return new String(byteArr);
	}

}
