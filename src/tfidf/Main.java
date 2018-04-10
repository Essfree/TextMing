package tfidf;

import java.util.HashMap;
import java.util.List;

public class Main {

//	分词
//	用TF-IDF提取关键词(TF、IDF、TF-IDF、选取10个关键词)合并成一个集合,计算每篇对于这几个词的词频
//	生成各自的词频向量
//	先用两个句子试一下:"我喜欢看电视,不喜欢看电影"\"我不喜欢看电视,也不喜欢看电影"
	public static final String stopWordTable = "E:\\MUC\\10\\stopwords.txt";
	
	public static void main(String[] args) throws Exception {
		//分词、去停用词
		Wordsplit ws = new Wordsplit();
		String[] testA =ws.Stwp( ws.Split("E:\\MUC\\10\\test\\A.txt"));
		String[] testB =ws.Stwp( ws.Split("E:\\MUC\\10\\test\\B.txt"));
		
		Tfidf t = new Tfidf();
		//AB共有的，计算总文档
		List file = t.readDirs("E:\\MUC\\10\\SogouC.reduced\\Reduced");
		List all = t.readFile(file);

		//对A：
		//得到A的tf
		List tfa = t.Tf(testA);
		System.out.println("tfa "+tfa);
		//得到A的idf
		List idfa = t.Idf(tfa, all);
		HashMap<String, Double> tfidfa = t.tf_idf(tfa, idfa);
		System.out.println("tfidfa "+tfidfa);
		//对B：
		//得到B的tf
		List tfb = t.Tf(testB);
		System.out.println("tfb "+tfb);
		//得到B的idf
		List idfb = t.Idf(tfb, all);
		HashMap<String, Double> tfidfb = t.tf_idf(tfb, idfb);
		System.out.println("tfidfb "+tfidfb);
		//计算cos
//		CosineSimilarAlgorithmt cs = new CosineSimilarAlgorithmt();
//		cs.cosine(tfidfa, tfidfb);
	}

}
