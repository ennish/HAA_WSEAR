package com.hkhs.hmms.haa.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
/**
 * create on 20070709
 * @author nineday
 *
 * to get property from file .properties
 */
public final class Config{

        private static ResourceBundle configResource = null;
        /**
         * initialization
         */
        public static void initConfig(String fileName){
                try{
                        configResource = ResourceBundle.getBundle(fileName);//file name
                }catch(MissingResourceException mre){
                        mre.printStackTrace();
                }
        }

        /**
         *get value
         *@param String key
         *@return String value
         */
        public static String getValue(String key){
                if (configResource == null) 
                	//initConfig();
                	return "";
                try{
                        return new String(
                          (configResource.getString(key))
                          .getBytes("ISO8859_1"),"gb2312");
                }catch(Exception e){
                        return null;
                }
        }

}
