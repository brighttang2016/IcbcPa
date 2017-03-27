package com.icbc.nt.test;

import com.icbc.nt.util.TransactionMapData;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TransactionMapData tmd1 = new TransactionMapData();
		tmd1.put("name", "tang");
		TransactionMapData tmd2 = (TransactionMapData) tmd1.clone();
		System.out.println(tmd1.equals(tmd2));
		System.out.println(tmd2.get("name"));
	}

}
