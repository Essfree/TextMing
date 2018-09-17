package march;

import java.io.IOException;
import java.util.Map;

import march.Tfidf;

//三月改程序系列
public class Main {
	
	public static void main(String[] args) throws IOException {
//		文本分词	 放在sample里	
		SplitText st = new SplitText();
		String dataDir = "E:\\MUC\\三月\\3.23-3.29\\NLPdata";
//		String dataDir = "E:\\MUC\\三月\\3000\\data";
//
		st.split(dataDir);
//		
		String splitDir = "E:\\MUC\\三月\\3.23-3.29\\Sample";
		int classNum = 6;
		String desDir = "E:\\MUC\\三月\\3.23-3.29\\SampleResult\\Result.txt";
		//计算tfidf
		
//		String splitDir = "E:\\MUC\\1.28\\Sample\\OriginalSample";

		Tfidf tI = new Tfidf();
		Map<String, Map<String, Double>> tfidfMap = tI.CalTfIdf(splitDir);
		
		
//		String desDir = "E:\\MUC\\1.28\\Result\\Result.txt";

//		两种方法，在获取初始中心点的不同，分别是kmeans++和lda模型
		new Kmeans(tfidfMap,classNum,desDir);
		Analysis as = new Analysis();
		
		String result = "E:\\MUC\\三月\\3.23-3.29\\SampleResult\\nlp-lda-example.txt";

		as.analyData(desDir,classNum,result);
		
	}

}
