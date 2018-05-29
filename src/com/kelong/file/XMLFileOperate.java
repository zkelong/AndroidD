package com.kelong.file;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.util.Xml;

public class XMLFileOperate {

	public static int PARSE_TYPE_DOM = 1;
	public static int PARSE_TYPE_SAX = 2;
	public static int PARSE_TYPE_PULL = 3;

	/**
	 * 读取 assets 下的guide配置 xml 文件
	 * 
	 * @return
	 */
	public static List<String> getGuideFiles(Context context, int type) {
		if (type == PARSE_TYPE_DOM) {
			try {
				return dom2xml(context.getResources().getAssets()
						.open("appConfig.xml"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (type == PARSE_TYPE_SAX) {
			try {
				return sax2xml(context.getResources().getAssets()
						.open("appConfig.xml"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				return pull2xml(context.getResources().getAssets().open("appConfig.xml"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/////////// dom解析 ///////////
	public static List<String> dom2xml(InputStream is) {
		List<String> list = null;
		try {
			// 一系列的初始化
			list = new ArrayList<>();
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			// 获得Document对象
			Document document = builder.parse(is);
			// 获得student的List
			NodeList studentList = document.getElementsByTagName("guide");
			// 遍历student标签
			for (int i = 0; i < studentList.getLength(); i++) {
				// 获得student标签
				Node node_student = studentList.item(i);
				// 获得student标签里面的标签
				NodeList childNodes = node_student.getChildNodes();
				for (int j = 0; j < childNodes.getLength(); j++) {
					Node childNode = childNodes.item(j);
					if ("item".equals(childNode.getNodeName())) {
						list.add(childNode.getTextContent());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/////////// sax 解析 ///////////
	public static List<String> sax2xml(InputStream is) throws Exception {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		// 初始化Sax解析器
		SAXParser sp = spf.newSAXParser();
		// 新建解析处理器
		MyHandler handler = MyHandler.getInstance();
		// 将解析交给处理器
		sp.parse(is, handler);
		// 返回List
		return handler.getList();
	}

	public static class MyHandler extends DefaultHandler {

		private List<String> list;
		// 用于存储读取的临时变量
		private String tempString;
		private static final MyHandler instance = new MyHandler();
	
		public static MyHandler getInstance() {
			return instance;
		}
		
		/**
		 * 解析到文档开始调用，一般做初始化操作
		 *
		 * @throws SAXException
		 */
		@Override
		public void startDocument() throws SAXException {
			list = new ArrayList<>();
			super.startDocument();
		}

		/**
		 * 解析到文档末尾调用，一般做回收操作
		 *
		 * @throws SAXException
		 */
		@Override
		public void endDocument() throws SAXException {
			super.endDocument();
		}

		/**
		 * 每读到一个元素就调用该方法
		 *
		 * @param uri
		 * @param localName
		 * @param qName
		 * @param attributes
		 * @throws SAXException
		 */
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			super.startElement(uri, localName, qName, attributes);
		}

		/**
		 * 读到元素的结尾调用
		 *
		 * @param uri
		 * @param localName
		 * @param qName
		 * @throws SAXException
		 */
		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if("item".equals(qName)) {
				list.add(tempString);
			}
			super.endElement(uri, localName, qName);
		}

		/**
		 * 读到属性内容调用
		 *
		 * @param ch
		 * @param start
		 * @param length
		 * @throws SAXException
		 */
		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			tempString = new String(ch, start, length);
			super.characters(ch, start, length);
		}

		/**
		 * 获取该List
		 *
		 * @return
		 */
		public List<String> getList() {
			return list;
		}
	}

	/////////// pull 解析 ///////////
	public static List<String> pull2xml(InputStream is) throws Exception {
	    List<String> rList = new ArrayList<String>();
	    //创建xmlPull解析器
	    XmlPullParser parser = Xml.newPullParser();
	    ///初始化xmlPull解析器
	    parser.setInput(is, "utf-8");
	    //读取文件的类型
	    int type = parser.getEventType();
	    //无限判断文件类型进行读取
	    while (type != XmlPullParser.END_DOCUMENT) {
	        switch (type) {
	            //开始标签
	            case XmlPullParser.START_TAG:
	            	if ("item".equals(parser.getName())) {
	                    rList.add(parser.nextText());
	                }
	                break;
	            //结束标签
	            case XmlPullParser.END_TAG:	                
	                break;
	        }
	        //继续往下读取标签类型
	        type = parser.next();
	    }
	    return rList;
	}
}
