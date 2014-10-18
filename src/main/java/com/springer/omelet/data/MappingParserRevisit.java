package com.springer.omelet.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.springer.omelet.common.Utils;

public class MappingParserRevisit implements IDataSource {

	private final DocumentBuilderFactory factory = DocumentBuilderFactory
			.newInstance();
	private DocumentBuilder builder = null;
	private Document document = null;
	private String xmlName;
	private static final String DELIMITTER = ";";
	private static final Logger LOGGER = Logger.getLogger(MappingParserRevisit.class);
	private HashMap<String, IMappingData> bucket = new HashMap<String, IMappingData>();

	public MappingParserRevisit(String xmlName) {
		this.xmlName = xmlName;
		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse("/home/kapil/git/omelet-example-dataset/src/test/resources/Mapping.xml");
		} catch (ParserConfigurationException e) {

		} catch (SAXException e) {

		} catch (IOException e) {

		}

	}


	/***
	 * walk to every element of the Xml from the root Element which updates Map as well for all the values
	 * @param element
	 */
	private void walkInXml(Element element) {
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node n = nodeList.item(i);
			if (n instanceof Element) {
				Element childElement = (Element) n;
				updateBucket(childElement);
				walkInXml(childElement);
			}
		}
	}
	
	/***
	 * Simple Primary data read from the datasource
	 * for obvious reason {@link IMappingData} will have nulls as well
	 */
	public Map<String, IMappingData> getPrimaryData(){
		//get the root Element
		walkInXml(document.getDocumentElement());
		for(String key:bucket.keySet()){
			System.out.println("Key is:"+key+" TestData:"+bucket.get(key).getTestData()+"ClientEnv:"+bucket.get(key).getClientEnvironment());
			System.out.println("Strategy is:"+bucket.get(key).getRunStartegy());
		}
		return bucket;
	}
	/**
	 * update the master bucket with values
	 * @param element
	 */
	private void updateBucket(Element element){
		System.out.println(element.getAttribute("name"));
		bucket.put(element.getAttribute("name"), getImap(element));
		
		
	}

	/**
	 * Get the {@link IMappingData} for any Entry in Datasource
	 * @param element
	 * @return
	 */
	private IMappingData getImap(Element element) {
		return new ImplementIMap.Builder()
				.withRunStartegy(element.getAttribute("runStrategy"))
				.withTestData(element.getAttribute("testData"))
				.withClientEnvironment(
						getList(element.getAttribute("clientEnvironment")))
				.build();

	}
	/**
	 * Helper method to get List from "," seprated strings
	 * @param commaSepratedList
	 * @return
	 */
	private List<String> getList(String commaSepratedList) {

		List<String> returnedList = new ArrayList<String>();
		if (StringUtils.isNotBlank(commaSepratedList)) {
			if (commaSepratedList.contains(DELIMITTER)) {
				String array[] = commaSepratedList.split(";");
				for (int i = 0; i < array.length; i++)
					returnedList.add(array[i]);
			} else {
				returnedList.add(commaSepratedList);
			}
		}
		return returnedList;
	}

	@Override
	public String toString(){
		return "Reading the Xml file with name:"+xmlName;
		
	}
}