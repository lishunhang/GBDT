package com.GBDT.CART;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.GBDT.CART.TreeNodeBean;

public class treeMainFunction {

	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		int a = (int)Math.pow(2, 3);
        System.out.println(a);
	}
	
	
	
	/**
	 * { {"column1",{1,2,3,4,5,6...........}},
	 *   {"column2",{1,2,3,4,5,6...........}},
	 *   {"column3",{1,2,3,4,5,6...........}},
	 *   {"column4",{1,2,3,4,5,6...........}},
	 *   {"column5",{1,2,3,4,5,6...........}},
	 *   {"lable",{1,2,3,4,5,6...........}},
	 * }
	 * @param list_next
	 * @param TARGET_column
	 * @param DecisionTree
	 * @param xh
	 * @return
	 */
	public static List<TreeNodeBean> count_tree(Map<String,double[]> Training_data ,int level,String pid)
	{
		List<TreeNodeBean> DecisionTree =new ArrayList<TreeNodeBean>();
		//装载初始化数据
		LinkedList<TreeNodeBean> list_next =new LinkedList<TreeNodeBean>();
		TreeNodeBean start=new TreeNodeBean();
		start.setId(java.util.UUID.randomUUID().toString());
		start.setPid(pid);
		start.setTraining_list(Training_data);
		start.setLevel(1);
		start.setIsleaf(false);
		list_next.addFirst(start);
		
		DecisionTree =CART_tree(DecisionTree,list_next ,level);
		
		return DecisionTree;
	}
	/**
	 * 生成回归树 主要方法
	 * @param DecisionTree
	 * @param list_next
	 * @param level
	 * @return
	 */
	public  static List<TreeNodeBean> CART_tree(List<TreeNodeBean> DecisionTree,LinkedList<TreeNodeBean> list_next ,int level)
	{
		if(list_next.size()>0 )
		{
			//获取当前执行的树层级
			int nowlevel=list_next.getFirst().getLevel();
			System.out.println("正在生成第"+nowlevel+"层级树！");
			if(nowlevel<=level)//当前层级小于等于 迭代层级 继续迭代
			{
				Map<String,double[]> Training_data=list_next.getFirst().getTraining_list();
				Set<Double> y =new HashSet<Double>();//结果集
				double sum=0;//结果合计
				int count=0;//结果行数
				double average=0;//结果均值
				double[] y_list=Training_data.get("lable");
				
				for(double value:y_list)
				{
					y.add(value);
					sum=com.GBDT.CART.DecimalCalculate.add(sum, value);
				}
				count=y_list.length;
				average=com.GBDT.CART.DecimalCalculate.div(sum, count);
				//结果集纯净 ----直接生成叶子节点
				if(y.size()==1)
				{
					TreeNodeBean insert=list_next.getFirst();
					insert.setAverage(average);
					insert.setGini(0);
					insert.setName(String.valueOf(average));
					insert.setY(y);
					insert.setIsleaf(true);
					DecisionTree.add(insert);
					list_next.removeFirst();
				}
				else//结果集不纯净 --计算出最小的GINI指数作为跟节点
				{
//					遍历数据列
					double min_gini=-1;
					String column="";
					Map<String,Object> min_column = new HashMap<String, Object>();
					Iterator it = Training_data.entrySet().iterator();
					while (it.hasNext()) 
					{
						Map.Entry entry = (Map.Entry) it.next();
						if(!"lable".equals(entry.getKey().toString()))
						{
							double[] x=(double[])entry.getValue();
							Map<String,Object> jg=countGINI(x,y_list);
							if(min_gini==-1)
							{
								min_gini=Double.parseDouble(jg.get("GINI").toString());
								min_column=jg;
								column=entry.getKey().toString();
							}
							else 
							{
								if(Double.parseDouble(jg.get("GINI").toString())<=min_gini)
								{
									min_gini=Double.parseDouble(jg.get("GINI").toString());
									min_column=jg;
									column=entry.getKey().toString();
								}
							}
						}
					}
					//特征值唯一 无法二分
					if("0".equals(min_column.get("type").toString()))
					{
						TreeNodeBean insert=list_next.getFirst();
						insert.setGini(0);
						insert.setName(column);
						insert.setAverage(average);
						insert.setY(y);
						insert.setIsleaf(true);
						DecisionTree.add(insert);
						list_next.removeFirst();
					}
					else 
					{
						//写入树数据
						TreeNodeBean insert=list_next.getFirst();
						insert.setGini(Double.parseDouble(min_column.get("GINI").toString()));
						insert.setName(column);
						insert.setAverage(average);
						insert.setY(y);
						insert.setThreshold(Double.parseDouble(min_column.get("FAZHI").toString()));
						if(nowlevel==level)
						{
							insert.setIsleaf(true);
						}
						else 
						{
							//准备 下层树节点计算数据
							int next_level=list_next.getFirst().getLevel()+1;
							
							Set<Integer> left=(Set<Integer>)min_column.get("LEFT");
				        	Set<Integer> right=(Set<Integer>)min_column.get("RIGHT");
							
							
							TreeNodeBean next_left=new TreeNodeBean();
							
							next_left.setId(java.util.UUID.randomUUID().toString());
							next_left.setPid(insert.getId());
							next_left.setLevel(list_next.getFirst().getLevel()+1);
							
							next_left.setIslift(true);
							
							Map<String,double[]> Training_left= new HashMap<String, double[]>();
							Map<String,double[]> Training_right= new HashMap<String, double[]>();
							Iterator it_next = Training_data.entrySet().iterator();
							while (it_next.hasNext()) 
							{
								Map.Entry entry = (Map.Entry) it_next.next();
								String com=entry.getKey().toString();
								double[] all=(double[])entry.getValue();
								double[] ll= new double[left.size()];
								double[] rr= new double[right.size()];
								int lx=0;
								for(int innn:left)
								{
									ll[lx]=all[innn];
									lx+=1;
								}
								int lr=0;
								for(int innn:right)
								{
									rr[lr]=all[innn];
									lr+=1;
								}
								Training_left.put(com, ll);
								Training_right.put(com, rr);
							}
							
							next_left.setTraining_list(Training_left);
							list_next.addLast(next_left);
							
							
							TreeNodeBean next_right=new TreeNodeBean();
							next_right.setId(java.util.UUID.randomUUID().toString());
							next_right.setPid(insert.getId());
							next_right.setLevel(list_next.getFirst().getLevel()+1);
							next_right.setTraining_list(Training_right);
							next_right.setIslift(false);
							list_next.addLast(next_right);
						}
						DecisionTree.add(insert);
						list_next.removeFirst();
					}
				}
			}
			else 
			{
				list_next.removeFirst();
			}
			//进入递归
			CART_tree(DecisionTree,list_next ,level);
		}
		
		return DecisionTree;
	}
	//判断某列存储的数据是否纯净
	public  static boolean columnIsPure(dataModel[] Training_data,String column)
	{
		Set<Double> lable=new HashSet<Double>();
		for(dataModel data:Training_data)
		{
			if(column.equals(data.getColumn()))
			{
				for(double value:data.getValues())
				{
					lable.add(value);
				}
			}
		}
		if(lable.size()>1)
		{
			return false;
		}
		else 
		{
			return true;
		}
	}
	//计算并获取最小基尼指数
	public  static Map<String,Object> countGINI(double[] x,double[] y)
	{
		Map<String,Object> retv=new HashMap<String, Object>();
		Set<Double> Set_x= new HashSet<Double>();
		for(double value:x)
		{
			Set_x.add(value);
		}
		if(Set_x.size()==1)//特征值唯一 无法二分
		{
			retv.put("GINI", 0);
			retv.put("type", "0");
		}
		else 
		{
			List<Double> items= new ArrayList<Double>();
			for(double value:Set_x)
			{
				items.add(value);
			}
	        Collections.sort(items);  
	        
	        double min_gini=-1;
	        
	      //二分数据
	        for(int i=0;i<items.size()-1;i++)
	        {
	        	double qianzhi=items.get(i);
	        	double houzhi= items.get(i+1);
//	        	二分阀值
	        	double fazhi= com.GBDT.CART.DecimalCalculate.div(com.GBDT.CART.DecimalCalculate.add(qianzhi, houzhi), 2);
	        	Set<Integer> left=new HashSet<Integer>();
	        	Set<Integer> right=new HashSet<Integer>();
	        	for(int j=0;j<x.length;j++)
	        	{
	        		if(x[j]<fazhi)
	        		{
	        			left.add(j);
	        		}
	        		else 
	        		{
	        			right.add(j);
	        		}
	        	}
//	        	开始计算GINI指数
	        	double sum_left=0,sum_right=0;
	        	for(int row:left)
	        	{
	        		sum_left=com.GBDT.CART.DecimalCalculate.add(sum_left, y[row]);
	        	}
	        	for(int row:right)
	        	{
	        		sum_right=com.GBDT.CART.DecimalCalculate.add(sum_right, y[row]);
	        	}
	        	double vag_left=com.GBDT.CART.DecimalCalculate.div(sum_left,left.size());
	        	double vag_right=com.GBDT.CART.DecimalCalculate.div(sum_right,right.size());
	        	double gini_left=0,gini_right=0;
	        	for(int row:left)
	        	{
	        		double t1=com.GBDT.CART.DecimalCalculate.sub(vag_left, y[row]);
	        		double t2=com.GBDT.CART.DecimalCalculate.sub(vag_left, y[row]);
	        		double t3=com.GBDT.CART.DecimalCalculate.mul(t1, t2);
	        		gini_left=com.GBDT.CART.DecimalCalculate.add(gini_left,t3);
	        	}
	        	for(int row:right)
	        	{
	        		double t1=com.GBDT.CART.DecimalCalculate.sub(vag_right, y[row]);
	        		double t2=com.GBDT.CART.DecimalCalculate.sub(vag_right, y[row]);
	        		double t3=com.GBDT.CART.DecimalCalculate.mul(t1, t2);
	        		gini_right=com.GBDT.CART.DecimalCalculate.add(gini_right,t3);
	        	}
	        	
//	        	gini_left=Math.sqrt(com.GBDT.CART.DecimalCalculate.div(gini_left, left.size()));
//	        	gini_right=Math.sqrt(com.GBDT.CART.DecimalCalculate.div(gini_right, right.size()));
//	        	gini_left=Math.sqrt(gini_left);
//	        	gini_right=Math.sqrt(gini_right);
	        	double gini=com.GBDT.CART.DecimalCalculate.add(gini_left,gini_right);
	        	if(min_gini==-1)
	        	{
	        		min_gini=gini;
	        		retv.put("GINI", min_gini);
	        		retv.put("FAZHI", fazhi);
	        		retv.put("LEFT", left);
	        		retv.put("RIGHT", right);
	        		retv.put("VAGLEFT", vag_left);
	        		retv.put("VAGRIGHT", vag_right);
	        		retv.put("type", "1");
	        	}
	        	else 
	        	{
	        		if(gini<=min_gini)
	        		{
	        			min_gini=gini;
		        		retv.put("GINI", min_gini);
		        		retv.put("FAZHI", fazhi);
		        		retv.put("LEFT", left);
		        		retv.put("RIGHT", right);
		        		retv.put("VAGLEFT", vag_left);
		        		retv.put("VAGRIGHT", vag_right);
		        		retv.put("type", "1");
	        		}
	        	}
	        }
		}
		return retv;
	}
	
	
}
