package com.alibaba;

/**
 * @program: kmeans
 * @description: none
 * @author: tongkai yin
 * @create: 2019/12/30 21:08
 */
public class KmeansResult {
    public int attempts;//退出迭代时的尝试次数
    public double criteriaBreakCondition;// 退出迭代时的最大距离（小于阈值）
    public int k;//聚类数
    public int perm[];//归类后连续存放的原始数据索引
    public int start[];//每个类在原数数据中的起始位置
}