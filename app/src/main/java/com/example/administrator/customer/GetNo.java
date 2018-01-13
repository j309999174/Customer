package com.example.administrator.customer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static org.w3c.dom.Node.ATTRIBUTE_NODE;
import static org.w3c.dom.Node.CDATA_SECTION_NODE;
import static org.w3c.dom.Node.COMMENT_NODE;
import static org.w3c.dom.Node.DOCUMENT_TYPE_NODE;
import static org.w3c.dom.Node.ELEMENT_NODE;
import static org.w3c.dom.Node.ENTITY_NODE;
import static org.w3c.dom.Node.ENTITY_REFERENCE_NODE;
import static org.w3c.dom.Node.NOTATION_NODE;
import static org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE;
import static org.w3c.dom.Node.TEXT_NODE;

public class GetNo {
    public String[] getNo(String url) throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();


        DocumentBuilder builder = null;
        try {
            builder = builderFactory.newDocumentBuilder();  // Create the parser
        } catch(ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document xmlDoc = null;
        String htmlContent = HtmlService.getHtml(url);
        System.out.println(htmlContent);
        try {
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(htmlContent));
            xmlDoc = builder.parse(is);

        } catch(SAXException e) {
            e.printStackTrace();

        } catch(IOException e) {
            e.printStackTrace();
        }


        return listNodes1(xmlDoc.getDocumentElement());
    }

    static String[] listNodes1(Node node) {
        String[] nos=new String[5];
        NodeList list = node.getChildNodes();//no的子集
        int i;
        for (i=0;i<list.getLength();i++) {
            NodeList list1 = list.item(i).getChildNodes();//notitle的子集
            Node node1 = list1.item(0);//notitle的属性，是子集第一项
            String nodeName = list.item(i).getNodeName();//判断名字是不是notitle
            if (nodeName.equals("notitle")){
                nos[0]=((Text) node1).getWholeText();
                //System.out.println(" Content is: " + ((Text) node1).getWholeText());
            }
            if (nodeName.equals("nocontent")){
                nos[1]=((Text) node1).getWholeText();
                //System.out.println(" Content is: " + ((Text) node1).getWholeText());
            }
            if (nodeName.equals("nodate")){
                nos[2]=((Text) node1).getWholeText();
                //System.out.println(" Content is: " + ((Text) node1).getWholeText());
            }
            if (nodeName.equals("notime")){
                nos[3]=((Text) node1).getWholeText();
                //System.out.println(" Content is: " + ((Text) node1).getWholeText());
            }
            if (nodeName.equals("nolink")){
                nos[4]=((Text) node1).getWholeText();
                //System.out.println(" Content is: " + ((Text) node1).getWholeText());
            }
        }
        return nos;
    }
    static void listNodes(Node node, String indent) {
        String nodeName = node.getNodeName();
        System.out.println(indent+" Node: " + nodeName);
        short type = node.getNodeType();
        System.out.println(indent+" Node Type: " + nodeType(type));
        if(type == TEXT_NODE){
            System.out.println(indent+" Content is: "+((Text)node).getWholeText());
        }

        NodeList list = node.getChildNodes();
        if(list.getLength() > 0) {
            System.out.println(indent+" Child Nodes of "+nodeName+" are:");
            for(int i = 0 ; i<list.getLength() ; i++) {
                listNodes(list.item(i),indent+"  ");
            }
        }
    }

    static String nodeType(short type) {
        switch(type) {
            case ELEMENT_NODE:                return "Element";
            case DOCUMENT_TYPE_NODE:          return "Document type";
            case ENTITY_NODE:                 return "Entity";
            case ENTITY_REFERENCE_NODE:       return "Entity reference";
            case NOTATION_NODE:               return "Notation";
            case TEXT_NODE:                   return "Text";
            case COMMENT_NODE:                return "Comment";
            case CDATA_SECTION_NODE:          return "CDATA Section";
            case ATTRIBUTE_NODE:              return "Attribute";
            case PROCESSING_INSTRUCTION_NODE: return "Attribute";
        }
        return "Unidentified";
    }

    static String xmlString ="<?xml version=\"1.0\"?>" +
            "  <!DOCTYPE address" +
            "  [" +
            "     <!ELEMENT address (buildingnumber, street, city, state, zip)>" +
            "     <!ATTLIST address xmlns CDATA #IMPLIED>" +
            "     <!ELEMENT buildingnumber (#PCDATA)>" +
            "     <!ELEMENT street (#PCDATA)>" +
            "     <!ELEMENT city (#PCDATA)>" +
            "     <!ELEMENT state (#PCDATA)>" +
            "     <!ELEMENT zip (#PCDATA)>" +
            "  ]>" +
            "" +
            "  <address>" +
            "    <buildingnumber> 29 </buildingnumber>" +
            "    <street> South Street</street>" +
            "    <city>Vancouver</city>" +
            "" +
            "    <state>BC</state>" +
            "    <zip>V6V 4U7</zip>" +
            "  </address>";
}