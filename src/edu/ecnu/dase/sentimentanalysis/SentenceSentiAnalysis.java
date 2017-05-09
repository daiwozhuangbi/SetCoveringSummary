package edu.ecnu.dase.sentimentanalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
 
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class SentenceSentiAnalysis {
	public static final int GET_POLARITY = 7;
	public static final int GET_WORD = 1;
	public static final int GET_WEIGHT = 6;
	
	/**
	 * 判断句子的情感倾向
	 * @param sentence 待判断情感倾向的句子
	 * @return -1 负面 1 正面 0 中性
	 * @throws Exception 异常
 	 * @throws IOException IO异常
	 */
	public int judgeSentence(String sentence) throws Exception, IOException{
		List<String> wordList = new ArrayList<String>();
		wordList = cutSenToList(sentence);
		Map<String,Double> wordweightmap = new HashMap<String,Double>();
		wordweightmap = countPolarityWeight(wordList);
		Map<String,Integer> unnumbermap = new HashMap<String,Integer>();	
		unnumbermap = countUnInItsSentiWord(wordList,wordweightmap);
		Map<String,Float>degreemap = new HashMap<String,Float>();
		degreemap = countDegreeAndItsSentiWord(wordList,wordweightmap);

		
		Map<String,Float> resultmap = new HashMap<String,Float>();
		for(String word : wordweightmap.keySet()){
			for(String un : unnumbermap.keySet()){
				for(String degree : degreemap.keySet()){
					if(word.equals(un)&&word.equals(degree)&&un.equals(degree)){
						resultmap.put(word, (float) (wordweightmap.get(word)*Math.pow(-1, unnumbermap.get(un))*degreemap.get(degree)));
					}
			    }
		    }
		}
		
		Float senweight = 0f;
		for(String word :resultmap.keySet()){
			senweight += resultmap.get(word);
		}
		
		if(senweight>0)
			return 1;
		else if(senweight<0)
			return -1;
		else 
			return 0;
		
	}
	/**
	 * 将句子分词，存入List
	 * @param sentence 待分词的句子
	 * @return wordList 分词结果
	 */
	public List<String> cutSenToList(String sentence){
		List<String> wordList = new ArrayList<String>();
		Result result = ToAnalysis.parse(sentence);
		for (Term term : result) {
		  String item = term.getName().trim();
		  wordList.add(item);
		}
		return wordList;
	}
	
	/**
	 * 判断是否为情感词
	 * @param word 待判断的词语
	 * @param wordweightmap key 情感词 value 情感权重
	 * @return true false
	 */
	public boolean isSentiSen(String word,Map<String,Double>wordweightmap){
		for(String words : wordweightmap.keySet())
			if(word.equals(words))
				return true;				
		return false;		
	}
	
	/**
	 * 获取情感词以及权重
	 * @param wordList 句子的分词结果
	 * @return wordweightmap key 情感词 value 情感权重
	 * @throws IOException 抛出IO异常
	 * @throws FileNotFoundException 文件不存在时抛出异常
	 */
	@SuppressWarnings("resource")
	public Map<String,Double> countPolarityWeight(List<String> wordList) throws IOException, FileNotFoundException{
		HSSFWorkbook wb;
		wb = new HSSFWorkbook(new FileInputStream("dic/polarity.xls"));
    	String wordtemp = null;
		double polaritytemp = 0;
		double weighttemp = 0;
		Row row = null;
		Map<String,Double> wordweightmap = new HashMap<String,Double>();
		for (String term : wordList) {						
			for(int i = 1;i<=wb.getSheetAt(0).getLastRowNum();i++){

//				int s = wb.getSheetAt(0).getLastRowNum();
				row = wb.getSheetAt(0).getRow(i);
//				row.getCell(GET_WORD-1).setCellType(Cell.CELL_TYPE_STRING);
				
				Cell c = row.getCell(GET_WORD-1);
				wordtemp = c.getRichStringCellValue().getString();
				
			    c = row.getCell(GET_POLARITY-1);
				polaritytemp = c.getNumericCellValue();
				
				c = row.getCell(GET_WEIGHT-1);
				weighttemp = c.getNumericCellValue();
				
				if(wordtemp.equals(term)&&polaritytemp==1){
					wordweightmap.put(term,weighttemp);
				}					 
				else if(wordtemp.equals(term)&&polaritytemp==2){
					wordweightmap.put(term,-weighttemp);
				}
		    }
		}
		return wordweightmap;		
	}
	
	/**
	 * 计算情感词所带否定词个数
	 * @param wordList 句子的分词结果
	 * @param wordweightmap key 情感词 value 情感权重
	 * @return unnumbermap key 情感词 value 否定词个数
	 * @throws Exception 抛出异常
	 * @throws FileNotFoundException 文件不存在时抛出异常
	 */
	@SuppressWarnings("resource")
	public Map<String,Integer> countUnInItsSentiWord(List<String> wordList,Map<String,Double>wordweightmap) throws Exception, FileNotFoundException{
		File file = new File("dic/un.txt");
		
		String lineText = null;
		Map<String,Integer>unnumbermap = new HashMap<String,Integer>();	
		int count = 0;	
		for(String word : wordList){	
			if(word.equals("，")){
				count = 0;
				continue;
			}				
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
			while((lineText = bufferedReader.readLine())!= null){
				String reg = "[^\u4e00-\u9fa5]";
				lineText = lineText.replaceAll(reg, "");
//				System.out.println(word.indexOf(lineText)!=-1);
//				System.out.println(!isSentiSen(word, wordweightmap));				
				if(word.indexOf(lineText)!=-1&&!isSentiSen(word, wordweightmap)){
					count++;					
				}
				else if(isSentiSen(word, wordweightmap)){
					unnumbermap.put(word, count);
				    count = 0;
				    break;
				}

			}
		}
		return unnumbermap;		
	}
	/**
	 * 计算情感词所带程度副词权重
	 * @param wordList 句子的分词结果
	 * @param wordweightmap key 情感词 value 情感权重
	 * @return degreemap key 情感词  value 程度权重 
	 * @throws Exception 抛出异常
	 */
	@SuppressWarnings("resource")
	public Map<String,Float> countDegreeAndItsSentiWord(List<String> wordList,Map<String,Double>wordweightmap) throws Exception{
		File file = new File("dic/degree.txt");
		Map<String,Float>degreemap = new HashMap<String,Float>();
		String lineText = null;
		Float degree = 0f;
		for(String word : wordList){
			if(word.equals("，")){
				degree = 0f;
				continue;
			}		
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
			while((lineText = bufferedReader.readLine())!= null){
				String[] worddegree = lineText.split("\\s+");
				if(worddegree[0].equals(word)&&!isSentiSen(worddegree[0], wordweightmap)){
					degree += Float.parseFloat(worddegree[1]);
				}
				else if(isSentiSen(word, wordweightmap)){
					if(degree==0f)
						degree=1f;
					degreemap.put(word, degree);
					degree = 0f;
					break;
				}
					
			}
		}		
		return degreemap;
	}
	
}
