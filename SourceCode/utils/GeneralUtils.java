package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import model.BaseStation;
import model.ConstNum;
import model.EdgeUser;

public class GeneralUtils {

	/***
	 * read the user data {latitude, longitude}
	 * @param path
	 * @return users by ArrayList
	 */
	public static ArrayList<EdgeUser> readUserData(String path) {
		ArrayList<EdgeUser> users = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
			String line = reader.readLine();
			while ((line = reader.readLine()) != null) {
				String[] items = line.split(",");
				EdgeUser u = new EdgeUser();
				u.getLocation().setLat(Double.parseDouble(items[0]));
				u.getLocation().setLng(Double.parseDouble(items[1]));
				users.add(u);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return users;
	}

	/**
	 * An random and non-repeat sample to UserList (its number is ConstNum.nUser)
	 * and convert ArrayList<> to Array[] structure
	 * @param users ArrayList<>
	 * @return users EdgeUser[]
	 */
	public static EdgeUser[] generateUserList(ArrayList<EdgeUser> users) {
		EdgeUser[] UserList = new EdgeUser[ConstNum.nUser];
		boolean[] flag = new boolean[ConstNum.nUser];
		for (int i = 0; i < ConstNum.nUser; i++) {
			int rand = (int) (Math.random() * users.size());
			if (flag[i]) {
				i--;
				continue;
			} else {
				UserList[i] = users.get(rand);
				flag[i] = true;
			}
		}
		return UserList;
	}

	/**
	 * read metropolitan base station data {id, latitude, longitude, radius}
	 * @param path String
	 * @return base stations ArrayList<>
	 */
	public static ArrayList<BaseStation> readBaseStationData_Metro(String path) {
		ArrayList<BaseStation> stations = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
			String line = reader.readLine();
			while ((line = reader.readLine()) != null) {
				String[] items = line.split(",");
				BaseStation bs = new BaseStation();
				bs.setID(stations.size());
				bs.getLocation().setLat(Double.parseDouble(items[1]));
				bs.getLocation().setLng(Double.parseDouble(items[2]));
				bs.setRadius(Double.parseDouble(items[0]));
				stations.add(bs);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stations;
	}
	
	/**
	 * An random and non-repeat sample to BSList (its number is ConstNum.nBaseStation)
	 * and convert ArrayList<> to Array[] structure
	 * @param users ArrayList<>
	 * @return users Array[]
	 */
	public static BaseStation[] generateBSList(ArrayList<BaseStation> baseStations) {
		BaseStation[] BSList = new BaseStation[ConstNum.nBaseStation];
		boolean[] flag = new boolean[ConstNum.nBaseStation];
		for (int i = 0; i < ConstNum.nBaseStation; i++) {
			int rand = (int) (Math.random() * baseStations.size());
			if (flag[i]) {
				i--;
				continue;
			} else {
				BSList[i] = baseStations.get(rand);
				flag[i] = true;
			}
		}
		return BSList;
	}
	
	/**
	 * reconstructed the base station data into a HashMap structure with cluster ID as the key
	 * @param BSList ArrayList<>
	 * @return BSList HashMap<>
	 */
	public static HashMap<Integer, ArrayList<BaseStation>> reshuffleBaseStationByClusterID(ArrayList<BaseStation> BSList) {
		HashMap<Integer, ArrayList<BaseStation>> clusters = new HashMap<>();
		for (BaseStation bs : BSList) {
			if (clusters.containsKey(bs.getClusterID())) {
				clusters.get(bs.getClusterID()).add(bs);
			} else {
				ArrayList<BaseStation> tmp = new ArrayList<BaseStation>();
				tmp.add(bs);
				clusters.put(bs.getClusterID(), tmp);
			}
		}

		return clusters;
	}
	
}


