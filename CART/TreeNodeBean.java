package com.GBDT.CART;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class TreeNodeBean {
	private String id; // 节点ID
	private String pid; // 节点父ID
	private String name; // 节点名（分裂属性的名称）
	private boolean islift;//二分节点专用  是否左侧节点   true 小于阀值   false 大于阀值
	private double threshold;//二分节点专用  二分阀值
	private double average;//结果均值
	private double gini;//基尼指数
	private Set<Double> y;//该节点下的所有结果集
	private int level;//节点所属层级
	private Map<String,double[]> Training_list;//该节点训练集
	private boolean  isleaf;//是否叶子节点
	
	
	
	public boolean isIsleaf() {
		return isleaf;
	}
	public void setIsleaf(boolean isleaf) {
		this.isleaf = isleaf;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public Set<Double> getY() {
		return y;
	}
	public void setY(Set<Double> y) {
		this.y = y;
	}
	public double getAverage() {
		return average;
	}
	public void setAverage(double average) {
		this.average = average;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isIslift() {
		return islift;
	}
	public void setIslift(boolean islift) {
		this.islift = islift;
	}
	public double getThreshold() {
		return threshold;
	}
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	public double getGini() {
		return gini;
	}
	public void setGini(double gini) {
		this.gini = gini;
	}
	public Map<String, double[]> getTraining_list() {
		return Training_list;
	}
	public void setTraining_list(Map<String, double[]> trainingList) {
		Training_list = trainingList;
	}
	
	
	
	
}
