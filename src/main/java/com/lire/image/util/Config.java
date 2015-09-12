package com.lire.image.util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 读取config.xml配置文件
 * Created by Administrator on 2015/9/1.
 */
public class Config {
    static Map<String,String> configMap = null;


    private Config(){

    }

    static class ReadConfig{
        public Map<String,String> read(){
            Map<String,String> config = new HashMap<String,String>();
            try{
                String path = this.getClass().getClassLoader().getResource("/").getPath();
                File f = new File(path+"/config.xml");
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(f);
                NodeList nl = doc.getElementsByTagName("root");
                if (nl != null && nl.getLength() > 0){
                    Node root = nl.item(0);
                    NodeList subList = root.getChildNodes();
                    for (int i = 0; i < subList.getLength(); i++) {
                        Node node = subList.item(i);
                        String name =  node.getNodeName();
                        String value = node.getTextContent();
//                        System.out.println(node.getNodeType());
                        config.put(name,value);
//                        System.out.println("name:"+name);
//                        System.out.println("value:"+value);

                    }
                }

            }catch (ParserConfigurationException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }catch (SAXException e){
                e.printStackTrace();
            }

            return config;
        }
    }
    private  void readConfig() {

    }
    public static  String get(String key){
        if (configMap == null){
            configMap = new ReadConfig().read();
        }
        return configMap.get(key);
    }

    public static void main(String[] args){

        Config.get("aaa");
    }

}
