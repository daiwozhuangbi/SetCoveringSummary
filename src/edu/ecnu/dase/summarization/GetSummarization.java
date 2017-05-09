package edu.ecnu.dase.summarization;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import edu.ecnu.dase.getkeywords.CountKeyWords;
import edu.ecnu.dase.getkeywords.GetUserKeyWords;
import edu.ecnu.dase.util.LinearProgramming;

/** 
 * 自动生成文本文摘
 * @author Iris
 */
public class GetSummarization {
	/** 每次取GET_NUM_DOCS篇文献 **/
	public static final int GET_NUM_DOCS = 10;
	/** 自定义关键词的个数 一篇文本5个关键词 **/
	public static final int KeyWordCount=2;
 
	/**
	 * 获得文本摘要（自定义生成篇数）<br>
	 * 1.计算关键词
	 * 2.获得用户自定义关键词
	 * 3.获得最小覆盖关键词的句子集合
	 * @param filePath 语料库的存放路径
	 * @param outfilePath 输出文摘存放路径
	 * @param n 生成文摘的篇数
	 * @throws Exception 关闭流出现异常
	 */
	public void getSummarization(String filePath,String outfilePath,int n,int kmtopic) throws Exception {
		for(int i=0;i<n;i++){		 

			 int[] rowofdocs = new int[GET_NUM_DOCS];
//			 RandomNumofRows randomnumofrows=new RandomNumofRows();
//			 rowofdocs=randomnumofrows.getRow0fContent(filePath,GET_NUM_DOCS);
			 for(int j = 0;j<GET_NUM_DOCS;j++)
				 rowofdocs[j]=j+1;
			 
			 DocCluster doccluster = new DocCluster();
			 doccluster.docCluster(filePath, rowofdocs);
			 Map<Integer, List<Integer>> clusterresult = new TreeMap<Integer, List<Integer>>();
			 clusterresult=doccluster.getClusterResult(rowofdocs,kmtopic);
//				List<Integer> r1 = new ArrayList<Integer>();
//			    r1.add(1);
//				r1.add(8);
//				r1.add(5);
//				clusterresult.put(1,r1);
//				List<Integer> r2 = new ArrayList<Integer>();
//				r2.add(4);
//				r2.add(7);
//				clusterresult.put(2,r2);
//				List<Integer> r3 = new ArrayList<Integer>();		
//				r3.add(9);
//				r3.add(10);
//				clusterresult.put(3,r3);
//				List<Integer> r4 = new ArrayList<Integer>();
//				r4.add(2);
//				r4.add(3);	
//				r4.add(6);
//				clusterresult.put(4,r4);
			 //对一类文档生成摘要组成一段
			 Set<Integer>topics = clusterresult.keySet();
			 for(Integer topic:topics){
				 if(clusterresult.get(topic).size()==0)
					 continue;
				 
				 int[] sameclusterrows = new int[clusterresult.get(topic).size()];
			      for(int l=0;l<clusterresult.get(topic).size();l++)
			        {			    	 
			    	  sameclusterrows[l] = clusterresult.get(topic).get(l);
			        }
		      
			      //keyword个数
			     int keywordnumber = KeyWordCount*(clusterresult.get(topic).size());
				//求keywords
			     CountKeyWords countkeyword=new CountKeyWords();
			     List<String> keywords=new ArrayList<String>();	 
			     keywords=countkeyword.countKeyWords(filePath, sameclusterrows,keywordnumber);
				 
				 //对用户自定义的keyword列分词,结果存入userKeywords，去重
				 List<String> userKeywords = new ArrayList<String>();
				 GetUserKeyWords usk= new GetUserKeyWords();
				 userKeywords=usk.cutKeywords(filePath, sameclusterrows);
				 
		   	     //求sentence (过滤掉长度超过50的句子)
				 CountParaForLP c = new CountParaForLP();
				 List<String> allsentence = new ArrayList<String>();		
				 allsentence=c.getSentence(filePath,sameclusterrows); 
 
				 
//				 /* sentiment analysis */
//				 List<String> positivesentence = new ArrayList<String>();
//				 positivesentence = c.ridnegativeSentence(allsentence);
				 
				 //求Yij Zij
				 int[][] Y;
				 Y=c.countYij(keywords, allsentence);
				 
				 int[][] Z;
				 Z=c.countZij(userKeywords, allsentence);
				 
				 //minimize ∑Xi 
				 /** 存储每个句子的得分  **/
				 Map<Integer, Double> resultoflpmap=new TreeMap<Integer, Double>();
				 LinearProgramming linearprogramsolver = new LinearProgramming();
				 resultoflpmap=linearprogramsolver.GLPK(Y,Z,allsentence,keywordnumber);
				 
				 //输出最小覆盖的句子集合	 
				 /** 存储最小句子集合 **/
				 List<String> minscover=new ArrayList<String>();
				 MinSentenceCover msc=new MinSentenceCover();
			     minscover=msc.minSCover(allsentence, resultoflpmap);
				 
				 //存储到i.txt文档中
			     String temp=i+".txt";
			     File filename = new File(outfilePath+"/"+temp);
				 BufferedWriter bw = null;
					try {
						bw = new BufferedWriter(new FileWriter(filename, true), 100);
						bw.write("    ");
						for (String str : minscover) {
							if(str.matches("[0-9、.]+.*"))
								str = str.substring(2);
							bw.write(str+"。");	
						}
						bw.newLine(); 
					} catch (IOException e) {
						System.out.println("写入文件出错，输出文件路径不正确");
					} finally {
						if (bw != null) {
							bw.flush();
							bw.close();
						}
					}
		}
		}
 }
}
	

