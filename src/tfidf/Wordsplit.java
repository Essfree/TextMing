package tfidf;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;



import java.util.HashSet;
import java.util.Set;

import tfidf.MyFileReader;

import com.sun.jna.Library;
import com.sun.jna.Native;


public class Wordsplit {
// 定义接口CLibrary，继承自com.sun.jna.Library
	public interface CLibrary extends Library {
	// 定义并初始化接口的静态变量
		CLibrary Instance = (CLibrary) Native.loadLibrary("E:\\ICTCLAS2016\\20140928\\lib\\win64\\NLPIR", CLibrary.class);

		public int NLPIR_Init(String sDataPath, int encoding,String sLicenceCode);
		
		public String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);
		
		public String NLPIR_GetKeyWords(String sLine, int nMaxKeyLimit,boolean bWeightOut);
		public String NLPIR_GetFileKeyWords(String sLine, int nMaxKeyLimit,boolean bWeightOut);
		public int NLPIR_AddUserWord(String sWord);//
		public int NLPIR_DelUsrWord(String sWord);//
		public String NLPIR_GetLastErrorMsg();
		public void NLPIR_Exit();
	}
	public static String transString(String aidString, String ori_encoding,String new_encoding) {
		try {
			return new String(aidString.getBytes(ori_encoding), new_encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static final String stopWordTable = "E:\\MUC\\10\\stopwords.txt";

/**
 * 分词
 * @param srcPath
 * @return	String[]nativeBayesArray
 * @throws Exception
 */
	public String[] Split(String srcPath)throws Exception {
		//分词
		String argu = "E:\\ICTCLAS2016\\20140928";

		@SuppressWarnings("unused")
		String system_charset = "UTF-8";
		int charset_type = 1;

		int init_flag = CLibrary.Instance.NLPIR_Init(argu, charset_type, "0");
		String nativeBytes = null;
		String nativeByte = null;
		ArrayList<String> name = new ArrayList<String>();
		ArrayList<String> classify = new ArrayList<String>();
		if (0 == init_flag) {
			nativeBytes = CLibrary.Instance.NLPIR_GetLastErrorMsg();
			System.err.println("初始化失败！fail reason is "+nativeBytes);
			return null;
		}
	
			//分词
			String sinputt= MyFileReader.read(srcPath);
			nativeBytes = CLibrary.Instance.NLPIR_ParagraphProcess(sinputt,0);
			CLibrary.Instance.NLPIR_Exit();
			nativeBytes = nativeBytes.replace("　 　 ", "");
			String[] nativeBytesArray=nativeBytes.split(" ");
			return nativeBytesArray;
	}
/**
 * 去停用词
 * @param nativeBytesArray
 * @return	nativeBytesArray
 * @throws IOException
 */
	public String[] Stwp(String[] nativeBytesArray) throws IOException{
			//读取停用词表    
			BufferedReader StopWordFileBr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(stopWordTable))));       
			//用来存放停用词的集合 
			Set stopWordSet = new HashSet<String>();    
		    String stopWord;
			//初如化停用词集    String stopWord = null;
		    for(;(stopWord = StopWordFileBr.readLine()) != null;){     
				stopWordSet.add(stopWord);   
			}

			for(int i = 0;i<nativeBytesArray.length;i++){      
				if(stopWordSet.contains(nativeBytesArray[i])){       
					nativeBytesArray[i] = "";     
				}     
			}
			return nativeBytesArray;
	}
	
}