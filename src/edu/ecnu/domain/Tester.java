package edu.ecnu.domain;

import java.io.IOException;

import edu.ecnu.dase.summarization.GetSummarization;

public class Tester {


	public static void main(String[] args) throws Exception {		
		String inputfilePath="/Users/iris/Documents/训练预测数据（分开）/1.餐饮管理/餐饮管理.xls";
		String outfilePath="/study/eclipse/iris-output/Summary/";
		GetSummarization gets=new GetSummarization();
		try {
        gets.getSummarization(inputfilePath,outfilePath,1,4);//自定义生成篇数
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
	}

}
