package march;

import java.io.IOException;
import java.util.Map;

import march.Tfidf;

//三月改程序系列
public class Main {
	
	public static void main(String[] args) throws IOException {
//		文本分词	 放在sample里	
//		SplitText st = new SplitText();
////		String dataDir = "E:\\MUC\\三月\\3.23-3.29\\data";
//		String dataDir = "E:\\MUC\\三月\\3000\\data";
//
//		st.split(dataDir);
//		
		//计算tfidf
		String splitDir = "E:\\MUC\\三月\\3000\\Sample";
//		String splitDir = "E:\\MUC\\1.28\\Sample\\OriginalSample";
		Tfidf tI = new Tfidf();
		Map<String, Map<String, Double>> tfidfMap = tI.CalTfIdf(splitDir);
		int classNum = 6;
		String desDir = "E:\\MUC\\三月\\3000\\SampleResult\\Result.txt";
//		String desDir = "E:\\MUC\\1.28\\Result\\Result.txt";
		
//		String RocDir = "E:\\MUC\\1.28\\Result\\Roc.txt";
		new KmeansPlus(tfidfMap,classNum,desDir);
		Analysis as = new Analysis();
		as.analyData(desDir,classNum);
	}

}
