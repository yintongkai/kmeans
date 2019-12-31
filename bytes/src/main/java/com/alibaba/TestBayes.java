package com.alibaba;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * @program: kmeans
 * @description: none
 * @author: tongkai yin
 * @create: 2019/12/31 14:16
 */
public class TestBayes {
    /**
     * 读取测试元组
     *
     * @return 一条测试元组
     * @throws IOException
     */
    public ArrayList<String> readTestData() throws IOException {
        ArrayList<String> candAttr = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String str = "";
        while (!(str = reader.readLine()).equals("")) {
            StringTokenizer tokenizer = new StringTokenizer(str);
            while (tokenizer.hasMoreTokens()) {
                candAttr.add(tokenizer.nextToken());
            }
        }
        return candAttr;
    }

    /**
     * 读取训练元组
     *
     * @return 训练元组集合
     * @throws IOException
     */
    public ArrayList<ArrayList<String>> readData() throws IOException {
        String path = "E:\\demo\\kmeans\\bytes\\src\\main\\resources\\test.txt";
        FileInputStream file = new FileInputStream(path);
        InputStreamReader inputFileReader = new InputStreamReader(file, "utf-8");
        BufferedReader reader = new BufferedReader(inputFileReader);

        ArrayList<ArrayList<String>> datas = new ArrayList<ArrayList<String>>();
        //BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String str = "";
        while ((str = reader.readLine()) != null) {
            StringTokenizer tokenizer = new StringTokenizer(str," ");
            ArrayList<String> s = new ArrayList<String>();
            while (tokenizer.hasMoreTokens()) {
                s.add(tokenizer.nextToken());
            }
            datas.add(s);
        }
        return datas;
    }

    public static void main(String[] args) {
        TestBayes tb = new TestBayes();
        ArrayList<ArrayList<String>> datas = null;
        ArrayList<String> testT = null;
        Bayes bayes = new Bayes();
        try {
            System.out.println("请输入训练数据");
            datas = tb.readData();
            while (true) {
                System.out.println("请输入测试元组");
                testT = tb.readTestData();
                String c = bayes.predictClass(datas, testT);
                System.out.println("The class is: " + c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}