package com.tmcl.siem.util;

import java.security.Key;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


public class SiEMUtil {

	
	private static final String passPhrase = "fjL&829t6BH3";
    private static byte[] salt = new byte[]{-78, 18, -43, -78, 68, 33, -61, -61};
	
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\\\|;:\\'\\\",<.>/?\"";
	
	public static String generateRandomToken() {

		return RandomStringUtils.randomAlphabetic(20).toLowerCase();

	}
	
	public static String generateRandomPassword() {
		return RandomStringUtils.random(8,CHARACTERS);
	}
	

	public static List<String> privileges(){
		List<String> privileges = Lists.newArrayList();
		privileges.add("SEARCH");
		privileges.add("GET");
		privileges.add("READ");
		privileges.add("WRITE");
		return privileges;
	}

	public static String bulidIndexName(String token) {
		return "compaylog"+token;
	}

	public static List<String> culsterRoles() {
		List<String> clusterRoles = Lists.newArrayList();
		clusterRoles.add("CLUSTER_COMPOSITE_OPS_RO");
		return clusterRoles;
	}
	
	public static List<Map<String, Object>> getFieldsForKibanaIndex(){
		List<Map<String, Object>> dataList = Lists.newLinkedList();
		dataList.add(prepareKibanaField("message", "string"));
		dataList.add(prepareKibanaField("@timestamp", "date"));
		dataList.add(prepareKibanaField("message.keyword", "string"));
		dataList.add(prepareKibanaField("_source", "_source"));
		dataList.add(prepareKibanaField("_id", "string"));
		dataList.add(prepareKibanaField("_type", "string"));
		dataList.add(prepareKibanaField("_index", "string"));
		dataList.add(prepareKibanaField("_score", "number"));
		return dataList;
		
	}
	
	private static Map<String, Object> prepareKibanaField(String fieldName,String type){
		
		Map<String, Object> dataMap = Maps.newLinkedHashMap();
		dataMap.put("name", fieldName);
		dataMap.put("type", type);
		dataMap.put("count", 0);
		if(fieldName.equalsIgnoreCase("message")) {
			dataMap.put("scripted", false);
			dataMap.put("indexed", true);
			dataMap.put("analyzed", true);
			dataMap.put("doc_values", false);
			dataMap.put("searchable", true);
			dataMap.put("aggregatable", false);
		}
		if(fieldName.equalsIgnoreCase("@timestamp")) {
			dataMap.put("scripted", false);
			dataMap.put("indexed", true);
			dataMap.put("analyzed", false);
			dataMap.put("doc_values", true);
			dataMap.put("searchable", true);
			dataMap.put("aggregatable", true);
		}
		if(fieldName.equalsIgnoreCase("message.keyword")) {
			dataMap.put("scripted", false);
			dataMap.put("indexed", true);
			dataMap.put("analyzed", false);
			dataMap.put("doc_values", true);
			dataMap.put("searchable", true);
			dataMap.put("aggregatable", true);
		}
		if(fieldName.equalsIgnoreCase("_source")) {
			dataMap.put("scripted", false);
			dataMap.put("indexed", false);
			dataMap.put("analyzed", false);
			dataMap.put("doc_values", false);
			dataMap.put("searchable", false);
			dataMap.put("aggregatable", false);
		}
		if(fieldName.equalsIgnoreCase("_id")) {
			dataMap.put("scripted", false);
			dataMap.put("indexed", false);
			dataMap.put("analyzed", false);
			dataMap.put("doc_values", false);
			dataMap.put("searchable", false);
			dataMap.put("aggregatable", false);
		}
		if(fieldName.equalsIgnoreCase("_type")) {
			dataMap.put("scripted", false);
			dataMap.put("indexed", false);
			dataMap.put("analyzed", false);
			dataMap.put("doc_values", false);
			dataMap.put("searchable", true);
			dataMap.put("aggregatable", true);
		}
		if(fieldName.equalsIgnoreCase("_type")) {
			dataMap.put("scripted", false);
			dataMap.put("indexed", false);
			dataMap.put("analyzed", false);
			dataMap.put("doc_values", false);
			dataMap.put("searchable", true);
			dataMap.put("aggregatable", true);
		}
		if(fieldName.equalsIgnoreCase("_index")) {
			dataMap.put("scripted", false);
			dataMap.put("indexed", false);
			dataMap.put("analyzed", false);
			dataMap.put("doc_values", false);
			dataMap.put("searchable", false);
			dataMap.put("aggregatable", false);
		}
		if(fieldName.equalsIgnoreCase("_score")) {
			dataMap.put("scripted", false);
			dataMap.put("indexed", false);
			dataMap.put("analyzed", false);
			dataMap.put("doc_values", false);
			dataMap.put("searchable", false);
			dataMap.put("aggregatable", false);
		}
		
		return dataMap;
	}
	


	public static String getCurrentLoginedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication!=null) {
			return authentication.getName();
		}
		return StringUtils.EMPTY;
	}
	
	
    public static String encryptString(String eSigRecord)  {
        String EncText = "";
        byte[] keyArray = new byte[24];
        byte[] toEncryptArray = null;
        try {
            toEncryptArray = eSigRecord.getBytes("UTF-8");
            MessageDigest m = MessageDigest.getInstance("MD5");
            byte[] temporaryKey = m.digest(passPhrase.getBytes("UTF-8"));
            if (temporaryKey.length < 24) {
                int index = 0;
                for (int i = temporaryKey.length; i < 24; ++i) {
                    keyArray[i] = temporaryKey[index];
                }
            }
            Cipher c = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            c.init(1, (Key)new SecretKeySpec(keyArray, "DESede"), new IvParameterSpec(salt));
            byte[] encrypted = c.doFinal(toEncryptArray);
            EncText = org.apache.commons.codec.binary.Base64.encodeBase64String((byte[])encrypted);
        }
        catch (Exception localException) {
            
        }
        return EncText;
    }

    public static String deCryptString(String encString){
        String rawText = "";
        byte[] keyArray = new byte[24];
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            byte[] temporaryKey = m.digest(passPhrase.getBytes("UTF-8"));
            if (temporaryKey.length < 24) {
                int index = 0;
                for (int i = temporaryKey.length; i < 24; ++i) {
                    keyArray[i] = temporaryKey[index];
                }
            }
            Cipher c = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            c.init(2, (Key)new SecretKeySpec(keyArray, "DESede"), new IvParameterSpec(salt));
            byte[] decrypted = c.doFinal(org.apache.commons.codec.binary.Base64.decodeBase64((String)encString));
            rawText = new String(decrypted, "UTF-8");
        }
        catch (Exception localException) {
            
        }
        return rawText;
    }
    
    public static String formatFileSize(long size) {
        String hrSize = null;
        double b = size;
        double k = size/1024.0;
        double m = ((size/1024.0)/1024.0);
        double g = (((size/1024.0)/1024.0)/1024.0);
        double t = ((((size/1024.0)/1024.0)/1024.0)/1024.0);

        DecimalFormat dec1 = new DecimalFormat("0.00");
        DecimalFormat dec2 = new DecimalFormat("0");
        if (t>1) {
            hrSize = isDouble(t) ? dec1.format(t).concat(" TB") : dec2.format(t).concat(" TB");
        } else if (g>1) {
            hrSize = isDouble(g) ? dec1.format(g).concat(" GB") : dec2.format(g).concat(" GB");
        } else if (m>1) {
            hrSize = isDouble(m) ? dec1.format(m).concat(" MB") : dec2.format(m).concat(" MB");
        } else if (k>1) {
            hrSize = isDouble(k) ? dec1.format(k).concat(" KB") : dec2.format(k).concat(" KB");
        } else {
            hrSize = isDouble(b) ? dec1.format(b).concat(" B") : dec2.format(b).concat(" B");
        }
        return hrSize;
    }
    
    private static boolean isDouble(double value) {
        if (value % 1 == 0) {
          
            return false;
        } else {
           
            return true;
        }
    }

}
