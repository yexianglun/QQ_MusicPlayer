package com.anjoyo.netmusic;

import java.io.StringReader;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class DownMusicSax extends DefaultHandler {
	public void Sax(String resultVal, MusicSax musicSax) {

		SAXParserFactory saxFactory = SAXParserFactory.newInstance();

		try {

			// ����XMLReader��ȡ����xml�ļ�������
			XMLReader reader = saxFactory.newSAXParser().getXMLReader();
			// ע�������¼�������������xml�ļ��������Ľ�����ʽ��
			reader.setContentHandler(musicSax);

			// �����ʽת��
			String strUTF = new String(resultVal.getBytes("gb2312"));
			// ��ʼ����xml��ʽ�ļ�
			reader.parse(new InputSource(new StringReader(strUTF)));

		} catch (Exception e) {
			System.out.println("sax �쳣");
			e.printStackTrace();
		}

	}
}
