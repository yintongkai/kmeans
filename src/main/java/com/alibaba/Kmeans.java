package com.alibaba;

import java.text.DecimalFormat;
import java.util.*;

/**
 * @program: kmeans
 * @description: none
 * @author: tongkai yin
 * @create: 2019/12/30 21:11
 * 聚类算法，开始聚类
 */
public class Kmeans {
    private static DecimalFormat decimalFormat = new DecimalFormat("#####.00");//对数据格式化处理
    public KmeansData data = null;

    @Override
    public String toString() {
        return "Kmeans{" +
                "data=" + data +
                '}';
    }

    public Kmeans(double[][] da) {
        data = new KmeansData(da, da.length, da[0].length);
    }
    /*
     * double[][] 元素全置
     * @param    [matrix, highDim, lowDim]
     * @return   void
     */
    private static void setDouble2Zero(double[][] matrix,int highDim,int lowDim) {
        for (int i = 0; i <highDim ; i++) {
            for (int j = 0; j <lowDim ; j++) {
                matrix[i][j] = 0;
            }
        }
    }

    /*
     * @param    [dests, sources, highDim, lowDim]
     * @return   void
     * 拷贝源二维矩阵元素到目标二维矩阵。 foreach (dests[highDim][lowDim] = sources[highDim][lowDim]);
     */
    private static void copyCenters(double[][] dests, double[][] sources, int highDim, int lowDim) {
        for (int i = 0; i <highDim ; i++) {
            for (int j = 0; j <lowDim ; j++) {
                dests[i][j] = sources[i][j];
            }
        }
    }
    /*
     * @param    [k, kmeansData]
     * @return   void
     * 更新聚类中心坐标,实现思路为：先求簇中心的和，然后求取均值。
     *
     */
    private static void updateCenters(int k, KmeansData kmeansData) {
        double[][] centers = kmeansData.centers;
        setDouble2Zero(centers,k,kmeansData.dim);
        int[] labels = kmeansData.labels;
        int[] centerCounts = kmeansData.centerCounts;
        for (int i = 0; i <kmeansData.dim ; i++) {
            for (int j = 0; j <kmeansData.length ; j++) {
                //System.out.println("centers[labels["+j+"]]["+i+"]"+centers[labels[j]][i]);
                centers[labels[j]][i] += kmeansData.data[j][i];
                //System.out.println("centers[labels["+j+"]]["+i+"]"+centers[labels[j]][i]);
            }
        }

        for (int i = 0; i < k; i++) {
            for (int j = 0; j < kmeansData.dim; j++) {
                //System.out.println("centers["+i+"]"+centerCounts[i]);
                centers[i][j] = centers[i][j] / centerCounts[i];
                centers[i][j] = Double.valueOf(decimalFormat.format(centers[i][j]));
                //System.out.println("centers["+i+"]"+"["+j+"]"+centers[i][j]);
            }
        }
    }
    /*
     * @param    [pa, pb, dim]
     * @return   double
     * 计算两点欧氏距离
     */
    public static double dist(double[] pa, double[] pb, int dim) {
        double rv = 0;
        for (int i = 0; i < dim; i++) {
            double temp = pa[i] - pb[i];
            temp = temp * temp;
            rv = rv + temp;
        }
        return Math.sqrt(rv);
    }
    /*
     * @param    [data]
     * @return   void
     * 归一化处理
     */
    private static void normalize(KmeansData data) {
        //1.首先计算各个列的最大最小值，存入map中
        Map<Integer, Double[]> minAndMax = new HashMap<Integer, Double[]>();
        for (int i = 0; i <data.dim ; i++) {
            Double[] nums = new Double[2];
            double max = data.data[0][i];
            double min = data.data[data.length - 1][i];
            for (int j = 0; j <data.length ; j++) {
                if (data.data[j][i]>max) {
                    max = data.data[j][i];
                }
                if (data.data[j][i] < min) {
                    min = data.data[j][i];
                }
            }
            nums[0] = min;
            nums[1] = max;
            //System.out.println("min:"+min+" max:"+max);
            minAndMax.put(i, nums);
        }
        //更新矩阵的值
        for (int i = 0; i <data.length ; i++) {
            for (int j = 0; j <data.dim ; j++) {
                double minValue = minAndMax.get(j)[0];
                double maxValue = minAndMax.get(j)[1];
                data.data[i][j] = (data.data[i][j] - minValue) / (maxValue - minValue);
                data.data[i][j] = Double.valueOf(decimalFormat.format(data.data[i][j]));
                //System.out.println("data["+i+"]["+j +"]="+data.data[i][j]);
            }
        }
    }

    public  KmeansResult doKmeans(int k,KmeansParam param) {
        //对数据进行规一化处理，以消除大的数据的影响
        normalize(data);
//  System.out.println("规格化处理后的数据：");
//  for (int i = 0;i < data.length;i++) {
//   for (int j = 0;j < data.dim;j++) {
//    System.out.print(data.data[i][j] + " ");
//   }
//   System.out.println();
//  }


        // 预处理
        double[][] centers = new double[k][data.dim]; // 聚类中心点集
        data.centers = centers;
        int[] centerCounts = new int[k]; // 各聚类的包含点个数
        data.centerCounts = centerCounts;
        Arrays.fill(centerCounts, 0);
        int[] labels = new int[data.length]; // 各个点所属聚类标号
        data.labels = labels;
        double[][] oldCenters = new double[k][data.dim]; // 临时缓存旧的聚类中心坐标

        // 初始化聚类中心（随机或者依序选择data内的k个不重复点）
        if (param.initCenterMethod == KmeansParam.CENTER_RANDOM) { // 随机选取k个初始聚类中心
            Random rn = new Random();
            List<Integer> seeds = new LinkedList<Integer>();
            while (seeds.size() < k) {
                int randomInt = rn.nextInt(data.length);
                //System.out.println("randomInt:"+randomInt);
                if (!seeds.contains(randomInt)) {
                    seeds.add(randomInt);
                }
            }
            //System.out.println(seeds.toString());
            Collections.sort(seeds);
            //System.out.println(seeds.toString());
            for (int i = 0; i < k; i++) {
                int m = seeds.remove(0);
                //System.out.println("选取的值"+m);
                for (int j = 0; j < data.dim; j++) {
                    centers[i][j] = data.data[m][j];
                    //System.out.println("centers[i][j]"+centers[i][j]);
                }
            }
        } else { // 选取前k个点位初始聚类中心
            for (int i = 0; i < k; i++) {
                for (int j = 0; j < data.dim; j++) {
                    centers[i][j] = data.data[i][j];
                }
            }
        }
        //给最初的聚类中心赋值
        data.originalCenters = new double[k][data.dim];
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < data.dim; j++) {
                data.originalCenters[i][j] = centers[i][j];
            }
        }

        // 第一轮迭代
        for (int i = 0; i < data.length; i++) {
            //System.out.println(Arrays.toString(centers[0]));
            double minDist = dist(data.data[i], centers[0], data.dim);
            int label = 0;
            for (int j = 1; j < k; j++) {
                double tempDist = dist(data.data[i], centers[j], data.dim);
                if (tempDist < minDist) {
                    minDist = tempDist;
                    label = j;
                }
            }
            labels[i] = label;
            centerCounts[label]++;
        }
        updateCenters(k, data);//更新簇中心
        copyCenters(oldCenters, centers, k, data.dim);

        // 迭代预处理
        int maxAttempts = param.attempts > 0 ? param.attempts : KmeansParam.MAX_ATTEMPTS;
        int attempts = 1;
        double criteria = param.criteria > 0 ? param.criteria : KmeansParam.MIN_CRITERIA;
        double criteriaBreakCondition = 0;
        boolean[] flags = new boolean[k]; // 标记哪些中心被修改过

        // 迭代
        iterate:
        while (attempts < maxAttempts) { // 迭代次数不超过最大值，最大中心改变量不超过阈值
            for (int i = 0; i < k; i++) { // 初始化中心点“是否被修改过”标记
                flags[i] = false;
            }
            for (int i = 0; i < data.length; i++) { // 遍历data内所有点
                double minDist = dist(data.data[i], centers[0], data.dim);
                int label = 0;
                for (int j = 1; j < k; j++) {
                    double tempDist = dist(data.data[i], centers[j], data.dim);
                    if (tempDist < minDist) {
                        minDist = tempDist;
                        label = j;
                    }
                }
                if (label != labels[i]) { // 如果当前点被聚类到新的类别则做更新
                    int oldLabel = labels[i];
                    labels[i] = label;
                    centerCounts[oldLabel]--;
                    centerCounts[label]++;
                    flags[oldLabel] = true;
                    flags[label] = true;
                }
            }
            updateCenters(k, data);
            attempts++;

            // 计算被修改过的中心点最大修改量是否超过阈值
            double maxDist = 0;
            for (int i = 0; i < k; i++) {
                if (flags[i]) {
                    double tempDist = dist(centers[i], oldCenters[i], data.dim);
                    if (maxDist < tempDist) {
                        maxDist = tempDist;
                    }
                    for (int j = 0; j < data.dim; j++) { // 更新oldCenter
                        oldCenters[i][j] = centers[i][j];
                        oldCenters[i][j] = Double.valueOf(decimalFormat.format(oldCenters[i][j]));
                    }
                }
            }
            if (maxDist < criteria) {
                criteriaBreakCondition = maxDist;
                break iterate;
            }
        }

        // 输出信息，把属于同一类的数据连续存放
        KmeansResult rvInfo = new KmeansResult();
        int perm[] = new int[data.length];
        //System.out.println("perm:"+Arrays.toString(perm));
        rvInfo.perm = perm;
        int start[] = new int[k];
        rvInfo.start = start;
        groupClass(perm,start,k,data);

        rvInfo.attempts = attempts;
        //System.out.println("attempts="+attempts);
        rvInfo.criteriaBreakCondition = criteriaBreakCondition;
        //System.out.println("criteriaBreakCondition"+criteriaBreakCondition);
        if (param.isDisplay) {
            System.out.println("最初的聚类中心：");
            for(int i = 0;i < data.originalCenters.length;i++){
                for(int j = 0;j < data.dim;j++){
                    System.out.print(data.originalCenters[i][j]+" ");
                }
                System.out.print("\t类别："+i+"\t"+"总数："+centerCounts[i]);
                System.out.println();
            }
            System.out.println("\n聚类结果--------------------------->");

            int originalCount = 0;
            for (int i = 0;i < k;i++) {
                int index = data.labels[perm[start[i]]];//所属类别
                int count = data.centerCounts[index];//类别中个体数目
                originalCount += count;
                System.out.println("所属类别：" + index);
                for (int j = start[i];j < originalCount;j++) {
                    for (double num:data.data[perm[j]]) {
                        System.out.print(num+" ");
                    }
                    System.out.println();
                }
            }
        }
        return rvInfo;
    }
    /*
     * @param    [perm, start, k, data]
     * @return   void
     * 原始数据---二维矩阵
     */
    private static void groupClass(int perm[],int start[],int k,KmeansData data){
        start[0] = 0;
        for(int i = 1;i < k;i++){
            start[i] = start[i-1] + data.centerCounts[i-1];
            //System.out.println("start["+i+"]:"+start[i]);
        }

        for(int i = 0;i < data.length;i++){
            perm[start[data.labels[i]]++] = i;
            //System.out.println(Arrays.toString(perm));
        }

        start[0] = 0;
        for(int i = 1;i < k;i++){
            start[i] = start[i-1] + data.centerCounts[i-1];
        }
    }
}