package com.mingrisoft;

public class crc16 {
	public static int crcCalc_Table(int[] arr_buff,int length) { 
	int len = length;  
	
	  //Ԥ�� 1 �� 16 λ�ļĴ���Ϊʮ������FFFF, �ƴ˼Ĵ���Ϊ CRC�Ĵ�����  
	  int crc = 0xFFFF;  
	  int i, j;  
	  for (i = 0; i < len; i++) {  
	      //�ѵ�һ�� 8 λ���������� �� 16 λ�� CRC�Ĵ����ĵ� 8 λ�����, �ѽ������ CRC�Ĵ���  
	  crc = ((crc & 0xFF00) | (crc & 0x00FF) ^ (arr_buff[i] & 0xFF));  
	  for (j = 0; j < 8; j++) {  
	      //�� CRC �Ĵ�������������һλ( ����λ)�� 0 ����λ, ��������ƺ���Ƴ�λ  
	  if ((crc & 0x0001) > 0) {  
	      //����Ƴ�λΪ 1, CRC�Ĵ��������ʽA001�������  
	      crc = crc >>> 1;  
	      crc = crc ^ 0xA001;  
	  } else  
	      //����Ƴ�λΪ 0,�ٴ�����һλ  
	              crc = crc >>> 1;  
	      }  
	  }  
	  return crc;  

    } 

}
