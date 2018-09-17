package march;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class Lemma {

	// 词干化
	public String stemmed(String inputStr) {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		Annotation document = new Annotation(inputStr);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		String outputStr = "";
		for (CoreMap sentence : sentences) {
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				String lema = token.get(LemmaAnnotation.class);
				outputStr += lema+" ";
			}

		}
		return outputStr;
	}
	public static void split(String srcPath) throws IOException {
		File fileDir = new File(srcPath);
		//查找路径，如果路径出错的话输出文件不存在
		if(!fileDir.exists()){  
            System.out.println("File not exist:" + srcPath);  
            return;  
        }
		String subStrPath = srcPath.substring(srcPath.lastIndexOf('\\'));
//		System.out.println(subStrPath);
		//新建目标文件Sample,与测试文件夹test在同一目录下
		String dirTarget = srcPath + "\\..\\NLPdata"+subStrPath;
		File dirFile = new File(dirTarget);
		if(!dirFile.exists()){
			dirFile.mkdirs();
		}
		File[] srcFiles = fileDir.listFiles();
//		略迷
//		System.out.println(srcPath.length());
		//母文件夹中一共有多少个子文件和第一层子文件夹
		String[] stemFileNames = new String[srcFiles.length]; 
		for(int i = 0;i<srcFiles.length;i++){
			//得到标准的绝对路径
			String fileFullName = srcFiles[i].getCanonicalPath();
			//得到文件名
			String fileShortName = srcFiles[i].getName();
			StringBuffer sb = new StringBuffer();
			sb.append(dirTarget+"\\"+fileShortName);
			if(!new File(fileFullName).isDirectory()){
				nlp(fileFullName,sb.toString());
				stemFileNames[i] = sb.toString();
			}else{
				System.out.println("this is a file "+fileFullName);
				split(fileFullName);
			}
		}
	}
	private static void nlp(String srcDir, String targetDir) {
		Lemma lemma=new Lemma();
		try{
            BufferedReader br = new BufferedReader(new FileReader(new File(srcDir)));//构造一个BufferedReader类来读取文件
            String temp =br.readLine();
			StringBuffer sb = null;
			String target = targetDir.substring(targetDir.lastIndexOf("\\")+1,targetDir.lastIndexOf("."));
//        	System.out.println(target);
        	if(target.equals("Metallic_contents")){
        		target = target.toLowerCase();
        	}
        	target = targetDir.substring(0, targetDir.lastIndexOf("\\")+1)+target;
//        	System.out.println(target);
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(target+".txt",true),"UTF-8");
			BufferedWriter bw= new BufferedWriter(out);
			sb = new StringBuffer("");
			while(temp!=null){
				
            	
            	
            	
				
				String output=lemma.stemmed(temp)+"\n";
				
				sb.append(output);
				
				
				temp = br.readLine();
				
            }
			bw.write(sb+"");
			bw.close();
			out.close();
            br.close();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
	}
	public static void main(String[]args) throws IOException{
		
//		Lemma lemma=new Lemma();
		String dataDir = "E:\\MUC\\三月\\3.23-3.29\\data";
		split(dataDir);
////		String reslDir = "E:\\MUC\\三月\\3.23-3.29\\NLPdata";
//		
//		String input="jack had been to china there months ago. he likes china very much,and he is falling love with this country";
//		String output=lemma.stemmed(input);
//		System.out.print("原句    ：");
//		System.out.println(input);
//		System.out.print("词干化：");
//		System.out.println(output);
				
		
	}

}