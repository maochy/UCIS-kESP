package utils;

import java.util.ArrayList;

import model.*;

public class AlgorithmUtils {
	
	    
    @SuppressWarnings("unchecked")
	public static ArrayList<Integer>[] calculateAccessMatrix(BaseStation[] BSList, EdgeUser[] UserList) {
		ArrayList<Integer>[] accessibility = new ArrayList[BSList.length];
		for (int i = 0; i < BSList.length; i++) {
			ArrayList<Integer> nearUserKey = new ArrayList<Integer>();
			for (int j = 0; j < UserList.length; j++) {
				if (Location.getDistance(BSList[i].getLocation(), UserList[j].getLocation()) < BSList[i].getRadius()) {
					nearUserKey.add(j);
				}
			}
			accessibility[i] = nearUserKey;
		}
		return accessibility;
	}

	public static int[][] calculateRobustMatrix(ArrayList<Integer>[] accessibility) {
		int BSNum = accessibility.length;
		int[][] robustness = new int[BSNum][BSNum];
		for (int i = 0; i < BSNum; i++) {
			for (int j = i; j < BSNum; j++) {
				int count = 0;
				if (i != j) {
					for (Integer id : accessibility[i]) {
						if (accessibility[j].contains(id)) {
							count++;
						}
					}
					robustness[i][j] = count;
					robustness[j][i] = count;
				}else {
					robustness[i][j] = accessibility[i].size();
				}
			}
		}
		return robustness;
	}
}
