package com.alibaba.optimization;

import java.util.Arrays;

/**
 * @program: kmeans
 * @description: none
 * @author: tongkai yin
 * @create: 2019/12/31 11:13
 */
public class normallizeTest {
    public double minMaxNormalize(double a,double min,double max) {
        return a=(a-min)/(max-min);
    }

    public double[] zeroMeanNormalize(double[] aa) {
        double sum = 0;
        int count = 0;
        for (double a: aa) {
            sum += a;
            count++;
        }
        double avg = sum / count;
        double sum2 = 0;
        for (double a: aa) {
            sum2=(a-avg)*(a-avg)+sum2;
        }
        double sqrt = Math.sqrt(sum2 / count);
        //System.out.println("avg:"+avg);
        //System.out.println("标准差："+sqrt);
        double zero[]=new double[count];
        for (int i = 0; i <count ; i++) {
            zero[i] = (aa[i] - avg) / sqrt;
        }
        return zero;
    }
    public static void main(String[] args) {
        normallizeTest normallizeTest = new normallizeTest();
        double aa[]={2, 45, 67,78,34,89};
        double max = aa[0];
        double min = aa[0];
        for (double a:
             aa) {
            if (a > max) {
                max = a;
            }
            if (a < min) {
                min=a;
            }
        }
        for (double a:
                aa) {
            System.out.println(normallizeTest.minMaxNormalize(a,min,max));
        }

        //System.out.println(max);
        //System.out.println(min);
        for (double a:normallizeTest.zeroMeanNormalize(aa)) {
            System.out.println(a);
        }
        String[] str = new String[3];
        Arrays.fill(str,"a");
        System.out.println(Arrays.toString(str));
    }
}