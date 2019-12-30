package com.alibaba.optimization;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @program: kmeans
 * @description: none
 * @author: tongkai yin
 * @create: 2019/12/30 22:57
 */
public class Kmeans {
    private DecimalFormat df = new DecimalFormat("#####.00");
    public KmeansData data = null;
    // feature,样本名称和索引映射
    private Map<String, Integer> identifier = new HashMap<String, Integer>();
    private Map<Integer, String> iden0 = new HashMap<Integer, String>();
    private ClusterModel model = new ClusterModel();

    /**
     * 文件到矩阵的映射
     * @param path
     * @return
     * @throws Exception
     */
    public double[][] fileToMatrix(String path) throws Exception {
        List<String> contents = new ArrayList<String>();
        model.identifier = identifier;
        model.iden0 = iden0;

        FileInputStream file = null;
        InputStreamReader inputFileReader = null;
        BufferedReader reader = null;
        String str = null;
        int rows = 0;
        int dim = 0;

        try {
            file = new FileInputStream(path);
            inputFileReader = new InputStreamReader(file, "utf-8");
            reader = new BufferedReader(inputFileReader);
            // 一次读入一行，直到读入null为文件结束
            while ((str = reader.readLine()) != null) {
                contents.add(str);
                ++rows;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }

        String[] strs = contents.get(0).split(":");
        dim = strs[0].split(" ").length;

        double[][] da = new double[rows][dim];

        for (int j = 0; j < contents.size(); j++) {
            strs = contents.get(j).split(":");
            identifier.put(strs[1], j);
            iden0.put(j, strs[1]);
            String[] feature = strs[0].split(" ");
            for (int i = 0; i < dim; i++) {
                da[j][i] = Double.parseDouble(feature[i]);
            }
        }
        return da;
    }

    /**
     * 清零操作
     * @param matrix
     * @param highDim
     * @param lowDim
     */
    private void setDouble2Zero(double[][] matrix, int highDim, int lowDim) {
        for (int i = 0; i < highDim; i++) {
            for (int j = 0; j < lowDim; j++) {
                matrix[i][j] = 0;
            }
        }
    }

    /**
     * 聚类中心拷贝
     * @param dests
     * @param sources
     * @param highDim
     * @param lowDim
     */
    private void copyCenters(double[][] dests, double[][] sources, int highDim, int lowDim) {
        for (int i = 0; i < highDim; i++) {
            for (int j = 0; j < lowDim; j++) {
                dests[i][j] = sources[i][j];
            }
        }
    }

    /**
     * 更新聚类中心
     * @param k
     * @param data
     */
    private void updateCenters(int k, KmeansData data) {
        double[][] centers = data.centers;
        setDouble2Zero(centers, k, data.dim);
        int[] labels = model.labels;
        int[] centerCounts = model.centerCounts;
        for (int i = 0; i < data.dim; i++) {
            for (int j = 0; j < data.length; j++) {
                centers[labels[j]][i] += data.data[j][i];
            }
        }
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < data.dim; j++) {
                centers[i][j] = centers[i][j] / centerCounts[i];
            }
        }
    }

    /**
     * 计算欧氏距离
     * @param pa
     * @param pb
     * @param dim
     * @return
     */
    public double dist(double[] pa, double[] pb, int dim) {
        double rv = 0;
        for (int i = 0; i < dim; i++) {
            double temp = pa[i] - pb[i];
            temp = temp * temp;
            rv += temp;
        }
        return Math.sqrt(rv);
    }

    /**
     * 样本训练,需要人为设定k值(聚类中心数目)
     * @param k
     * @param
     * @return
     * @throws Exception
     */
    public ClusterModel train(String path, int k) throws Exception {
        double[][] matrix = fileToMatrix(path);
        data = new KmeansData(matrix, matrix.length, matrix[0].length);
        return train(k, new KmeansParam());
    }

    /**
     * 样本训练(系统默认最优聚类中心数目)
     * @param
     * @return
     * @throws Exception
     */
    public ClusterModel train(String path) throws Exception {
        double[][] matrix = fileToMatrix(path);
        data = new KmeansData(matrix, matrix.length, matrix[0].length);
        return train(new KmeansParam());
    }

    private ClusterModel train(KmeansParam param) {
        int k = KmeansParam.K;
        // 首先进行数据归一化处理
        normalize(data);
        // 计算第一个样本和后面的所有样本的欧氏距离，存入list中然后计算均值，作为聚类中心选取的依据
        List<Double> dists = new ArrayList<Double>();
        for (int i = 1; i < data.length; i++) {
            dists.add(dist(data.data[0], data.data[i], data.dim));
        }
        param.min_euclideanDistance = Double.valueOf(df.format((Collections.max(dists) + Collections.min(dists)) / 2));
        double euclideanDistance = param.min_euclideanDistance > 0 ? param.min_euclideanDistance
                : KmeansParam.MIN_EuclideanDistance;

        int centerIndexes[] = new int[k];// 收集聚类中心索引的数组
        int countCenter = 0;// 动态表示中心的数目
        int count = 0;// 计数器
        centerIndexes[0] = 0;
        countCenter++;
        for (int i = 1; i < data.length; i++) {
            for (int j = 0; j < countCenter; j++) {
                if (dist(data.data[i], data.data[centerIndexes[j]], data.dim) > euclideanDistance) {
                    count++;
                }
            }
            if (count == countCenter) {
                centerIndexes[countCenter++] = i;
            }
            count = 0;
        }

        double[][] centers = new double[countCenter][data.dim]; // 聚类中心
        data.centers = centers;
        int[] centerCounts = new int[countCenter]; // 聚类中心的样本个数
        model.centerCounts = centerCounts;
        Arrays.fill(centerCounts, 0);
        int[] labels = new int[data.length]; // 样本的类别
        model.labels = labels;
        double[][] oldCenters = new double[countCenter][data.dim]; // 存储旧的聚类中心

        // 给聚类中心赋值
        for (int i = 0; i < countCenter; i++) {
            int m = centerIndexes[i];
            for (int j = 0; j < data.dim; j++) {
                centers[i][j] = data.data[m][j];
            }
        }

        // 给最初始的聚类中心赋值
        model.originalCenters = new double[countCenter][data.dim];
        for (int i = 0; i < countCenter; i++) {
            for (int j = 0; j < data.dim; j++) {
                model.originalCenters[i][j] = centers[i][j];
            }
        }

        //初始聚类
        for (int i = 0; i < data.length; i++) {
            double minDist = dist(data.data[i], centers[0], data.dim);
            int label = 0;
            for (int j = 1; j < countCenter; j++) {
                double tempDist = dist(data.data[i], centers[j], data.dim);
                if (tempDist < minDist) {
                    minDist = tempDist;
                    label = j;
                }
            }
            labels[i] = label;
            centerCounts[label]++;
        }
        updateCenters(countCenter, data);
        copyCenters(oldCenters, centers, countCenter, data.dim);

        // 迭代预处理
        int maxAttempts = param.attempts > 0 ? param.attempts : KmeansParam.MAX_ATTEMPTS;
        int attempts = 1;
        double criteria = param.criteria > 0 ? param.criteria : KmeansParam.MIN_CRITERIA;
        double criteriaBreakCondition = 0;
        boolean[] flags = new boolean[k]; // 用来表示聚类中心是否发生变化

        // 迭代
        iterate: while (attempts < maxAttempts) { // 迭代次数不超过最大值，最大中心改变量不超过阈值
            for (int i = 0; i < countCenter; i++) { //  初始化中心点"是否被修改过"标记
                flags[i] = false;
            }
            for (int i = 0; i < data.length; i++) {
                double minDist = dist(data.data[i], centers[0], data.dim);
                int label = 0;
                for (int j = 1; j < countCenter; j++) {
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
            updateCenters(countCenter, data);
            attempts++;

            // 计算被修改过的中心点最大修改量是否超过阈值
            double maxDist = 0;
            for (int i = 0; i < countCenter; i++) {
                if (flags[i]) {
                    double tempDist = dist(centers[i], oldCenters[i], data.dim);
                    if (maxDist < tempDist) {
                        maxDist = tempDist;
                    }
                    for (int j = 0; j < data.dim; j++) { // 更新oldCenter
                        oldCenters[i][j] = centers[i][j];
                        oldCenters[i][j] = Double.valueOf(df.format(oldCenters[i][j]));
                    }
                }
            }
            if (maxDist < criteria) {
                criteriaBreakCondition = maxDist;
                break iterate;
            }
        }
        // 把结果存储到ClusterModel中
        ClusterModel rvInfo = outputClusterInfo(criteriaBreakCondition, countCenter, attempts, param, centerCounts);
        return rvInfo;
    }

    private ClusterModel train(int k, KmeansParam param) {
        // 首先进行数据归一化处理
        normalize(data);

        List<Double> dists = new ArrayList<Double>();
        for (int i = 1; i < data.length; i++) {
            dists.add(dist(data.data[0], data.data[i], data.dim));
        }

        param.min_euclideanDistance = Double.valueOf(df.format((Collections.max(dists) + Collections.min(dists)) / 2));
        double euclideanDistance = param.min_euclideanDistance > 0 ? param.min_euclideanDistance
                : KmeansParam.MIN_EuclideanDistance;


        double[][] centers = new double[k][data.dim];
        data.centers = centers;
        int[] centerCounts = new int[k];
        model.centerCounts = centerCounts;
        Arrays.fill(centerCounts, 0);
        int[] labels = new int[data.length];
        model.labels = labels;
        double[][] oldCenters = new double[k][data.dim];


        int centerIndexes[] = new int[k];
        int countCenter = 0;
        int count = 0;
        centerIndexes[0] = 0;
        countCenter++;
        for (int i = 1; i < data.length; i++) {
            for (int j = 0; j < countCenter; j++) {
                if (dist(data.data[i], data.data[centerIndexes[j]], data.dim) > euclideanDistance) {
                    count++;
                }
            }
            if (count == countCenter) {
                centerIndexes[countCenter++] = i;
            }
            count = 0;

            if (countCenter == k) {
                break;
            }

            if (countCenter < k && i == data.length - 1) {
                k = countCenter;
                break;
            }
        }

        for (int i = 0; i < k; i++) {
            int m = centerIndexes[i];
            for (int j = 0; j < data.dim; j++) {
                centers[i][j] = data.data[m][j];
            }
        }


        model.originalCenters = new double[k][data.dim];
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < data.dim; j++) {
                model.originalCenters[i][j] = centers[i][j];
            }
        }


        for (int i = 0; i < data.length; i++) {
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
        updateCenters(k, data);
        copyCenters(oldCenters, centers, k, data.dim);

        int maxAttempts = param.attempts > 0 ? param.attempts : KmeansParam.MAX_ATTEMPTS;
        int attempts = 1;
        double criteria = param.criteria > 0 ? param.criteria : KmeansParam.MIN_CRITERIA;
        double criteriaBreakCondition = 0;
        boolean[] flags = new boolean[k];

        iterate: while (attempts < maxAttempts) {
            for (int i = 0; i < k; i++) {
                flags[i] = false;
            }
            for (int i = 0; i < data.length; i++) {
                double minDist = dist(data.data[i], centers[0], data.dim);
                int label = 0;
                for (int j = 1; j < k; j++) {
                    double tempDist = dist(data.data[i], centers[j], data.dim);
                    if (tempDist < minDist) {
                        minDist = tempDist;
                        label = j;
                    }
                }
                if (label != labels[i]) {
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

            double maxDist = 0;
            for (int i = 0; i < k; i++) {
                if (flags[i]) {
                    double tempDist = dist(centers[i], oldCenters[i], data.dim);
                    if (maxDist < tempDist) {
                        maxDist = tempDist;
                    }
                    for (int j = 0; j < data.dim; j++) { // 锟斤拷锟斤拷oldCenter
                        oldCenters[i][j] = centers[i][j];
                        oldCenters[i][j] = Double.valueOf(df.format(oldCenters[i][j]));
                    }
                }
            }
            if (maxDist < criteria) {
                criteriaBreakCondition = maxDist;
                break iterate;
            }
        }

        ClusterModel rvInfo = outputClusterInfo(criteriaBreakCondition, k, attempts, param, centerCounts);
        return rvInfo;
    }

    /**
     * 把聚类结果存储到Model中
     * @param criteriaBreakCondition
     * @param k
     * @param attempts
     * @param param
     * @param centerCounts
     * @return
     */
    private ClusterModel outputClusterInfo(double criteriaBreakCondition, int k, int attempts, KmeansParam param, int[] centerCounts) {
        model.data = data;
        model.k = k;
        int perm[] = new int[data.length];
        model.perm = perm;
        int start[] = new int[k];
        model.start = start;
        group_class(perm, start, k, data);
        return model;
    }

    /**
     * 把聚类样本按所属类别连续存储
     * @param perm
     * @param start
     * @param k
     * @param data
     */
    private void group_class(int perm[], int start[], int k, KmeansData data) {

        start[0] = 0;
        for (int i = 1; i < k; i++) {
            start[i] = start[i - 1] + model.centerCounts[i - 1];
        }

        for (int i = 0; i < data.length; i++) {
            perm[start[model.labels[i]]++] = i;
        }

        start[0] = 0;
        for (int i = 1; i < k; i++) {
            start[i] = start[i - 1] + model.centerCounts[i - 1];
        }
    }

    /**
     * 数据归一化处理
     * @param data
     * @author TongXueQiang
     */
    private void normalize(KmeansData data) {
        Map<Integer, Double[]> minAndMax = new HashMap<Integer, Double[]>();
        for (int i = 0; i < data.dim; i++) {
            Double[] nums = new Double[2];
            double max = data.data[0][i];
            double min = data.data[data.length - 1][i];
            for (int j = 0; j < data.length; j++) {
                if (data.data[j][i] > max) {
                    max = data.data[j][i];
                }
                if (data.data[j][i] < min) {
                    min = data.data[j][i];
                }
            }
            nums[0] = min;
            nums[1] = max;
            minAndMax.put(i, nums);
        }
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.dim; j++) {
                double minValue = minAndMax.get(j)[0];
                double maxValue = minAndMax.get(j)[1];
                data.data[i][j] = (data.data[i][j] - minValue) / (maxValue - minValue);
                data.data[i][j] = Double.valueOf(df.format(data.data[i][j]));
            }
        }
    }
}
