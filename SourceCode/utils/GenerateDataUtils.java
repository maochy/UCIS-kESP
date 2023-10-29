package utils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import model.BaseStation;
import model.ConstNum;
import model.EdgeUser;
import model.Location;

public class GenerateDataUtils {
	
	public static EdgeUser[] generateSubUser(BaseStation[] subBaseStations, ArrayList<EdgeUser> UserList) throws IOException {
		ArrayList<Integer> tmp = new ArrayList<>();
		for (int i = 0; i < UserList.size(); i++) {
			for (int j = 0; j < subBaseStations.length; j++) {
				if (Location.getDistance(subBaseStations[j].getLocation(), UserList.get(i).getLocation()) < subBaseStations[j].getRadius()) {
					tmp.add(i);
					break;
				}
			}
		}
		
		EdgeUser[] subUserList = new EdgeUser[ConstNum.nUser];
		int i = 0;
		if(tmp.size() <= ConstNum.nUser) {
			for (; i < tmp.size(); i++) {
				subUserList[i] = UserList.get(tmp.get(i));
			}
			
			while (i < ConstNum.nUser) {
				int rand = (int)(Math.random() * UserList.size());
				if(!tmp.contains(rand)) {
					subUserList[i] = UserList.get(rand);
					i++;
					tmp.add(rand);
				}
			}
		} else {
			while (i < ConstNum.nUser) {
				int rand = (int)(Math.random() * tmp.size());
				subUserList[i] = UserList.get(tmp.get(rand));
				tmp.remove(rand);
				i++;
			}
		}
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data/Metropolitan/TmpUserData.csv")));
		bw.append("Latitude,Longitude");
		bw.newLine();
		for (int j = 0; j < subUserList.length; j++) {
			bw.append(subUserList[j].getLocation().getLat() + "," + subUserList[j].getLocation().getLng());
			bw.newLine();
		}
		bw.close();
		
		return subUserList;
	}

	public static BaseStation[] generateSubBaseStation_nearBS(ArrayList<BaseStation> BSList)  throws IOException {
		ArrayList<Integer>[] BSGraph = readBSGraph();
		ArrayList<Integer> BSIndexs = new ArrayList<>();
		ArrayList<Integer> BSCandidate = new ArrayList<>();
		int rand = (int)(Math.random() * ConstNum.nBaseStation);
		BSIndexs.add(rand);
		BSCandidate.addAll(BSGraph[rand]);
		
		while (true) {
			if (BSIndexs.size() + BSCandidate.size() <= ConstNum.nBaseStation) {
				BSIndexs.addAll(BSCandidate);
				ArrayList<Integer> tmp = new ArrayList<>();
				tmp.addAll(BSCandidate);
				BSCandidate.clear();
				for (int i = 0; i < tmp.size(); i++) {
					for (Integer index : BSGraph[tmp.get(i)]) {
						if (!BSCandidate.contains(index) && !BSIndexs.contains(index)) {
							BSCandidate.add(index);
						}
					}
				}
			} else {
				while(BSIndexs.size() < ConstNum.nBaseStation) {
					rand =  (int) (Math.random() * BSCandidate.size());
					BSIndexs.add(BSCandidate.get(rand));
					BSCandidate.remove(rand);
				}
				break;
			}
		}
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data/Metropolitan/TmpBaseStationData.csv")));
		bw.append("Radius,Latitude,Longitude");
		bw.newLine();
		for (int j = 0; j < BSIndexs.size(); j++) {
			bw.append(BSList.get(BSIndexs.get(j)).getRadius() + "," + BSList.get(BSIndexs.get(j)).getLocation().getLat() + ","
					+ BSList.get(BSIndexs.get(j)).getLocation().getLng());
			bw.newLine();
		}
		bw.close();
				
		BaseStation[] subBSList = new BaseStation[ConstNum.nBaseStation];
		for (int i = 0; i < subBSList.length; i++) {
			subBSList[i] = BSList.get(BSIndexs.get(i));
		}
		return subBSList;
	}
	
	public static BaseStation[] generateSubBaseStation_RandAndOverlap(ArrayList<BaseStation> BSList)  throws IOException {
		ArrayList<Integer>[] BSGraph = readBSGraph();
		ArrayList<Integer> BSIndexs = new ArrayList<>();
		ArrayList<Integer> BSCandidate = new ArrayList<>();
		int rand = (int)(Math.random() * ConstNum.nBaseStation);
		BSIndexs.add(rand);
		BSCandidate.addAll(BSGraph[rand]);
		
		while (BSIndexs.size() < ConstNum.nBaseStation) {
			rand = (int)(Math.random() * BSCandidate.size());
			BSIndexs.add(BSCandidate.get(rand));
			for (Integer candiIndex : BSGraph[BSCandidate.get(rand)]) {
				if(!BSCandidate.contains(candiIndex) && !BSIndexs.contains(candiIndex)) {
					BSCandidate.add(candiIndex);
				}
			}
			BSCandidate.remove(rand);
		}
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data/Metropolitan/TmpBaseStationData.csv")));
		bw.append("Radius,Latitude,Longitude");
		bw.newLine();
		for (int j = 0; j < BSIndexs.size(); j++) {
			bw.append(BSList.get(BSIndexs.get(j)).getRadius() + "," + BSList.get(BSIndexs.get(j)).getLocation().getLat() + ","
					+ BSList.get(BSIndexs.get(j)).getLocation().getLng());
			bw.newLine();
		}
		bw.close();		
		
		BaseStation[] subBSList = new BaseStation[ConstNum.nBaseStation];
		for (int i = 0; i < subBSList.length; i++) {
			subBSList[i] = BSList.get(BSIndexs.get(i));
		}
		return subBSList;
	}
	
	@SuppressWarnings("unchecked")
	public static void calculateAndOutputBSGraph() throws IOException {
		String BSDataFilePath = "data/Metropolitan/melb_metro_station.csv";
		ArrayList<BaseStation> BSList = new ArrayList<>();
		BSList = readBSData(BSDataFilePath);
		ArrayList<Integer>[] BSGraph = new ArrayList[BSList.size()];
		BSGraph = generateBSGraph(BSList);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data/Metropolitan/BSGraphData.csv")));
		for (int i = 0; i < BSGraph.length; i++) {
			bw.append(i + "");
			for (int index : BSGraph[i]) {
				bw.append("," + index);
				bw.flush();
			}
			bw.newLine();
		}
		bw.close();
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Integer>[] readBSGraph() {
		ArrayList<Integer>[] BSGraph = new ArrayList[1464];
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("data/Metropolitan/BSGraphData.csv")));
			String line = "";
			int i = 0;
			while ((line = reader.readLine()) != null) {
				String[] items = line.split(",");
				BSGraph[i] = new ArrayList<>();
				for (int j = 1; j < items.length; j++) {
					BSGraph[i].add(Integer.valueOf(items[j]));
				}
				i++;
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return BSGraph;
	}
	
	public static ArrayList<BaseStation> readBSData(String path) {
		ArrayList<BaseStation> stations = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
			String line = reader.readLine();
			while ((line = reader.readLine()) != null) {
				String[] items = line.split(",");
				BaseStation bs = new BaseStation();
				bs.setID(stations.size());
				bs.setRadius(Double.parseDouble(items[0]));
				bs.getLocation().setLat(Double.parseDouble(items[1]));
				bs.getLocation().setLng(Double.parseDouble(items[2]));
				stations.add(bs);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stations;
	}

	/**
	 * 根据基站之间的通信关系，利用BSList的基站数据构建基站通信网
	 * @param BSList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<Integer>[] generateBSGraph(ArrayList<BaseStation> BSList) {
		ArrayList<Integer>[] BSGraph = new ArrayList[BSList.size()];
		for (int i = 0; i < BSList.size(); i++) {
			ArrayList<Integer> nearBSKey = new ArrayList<Integer>();
			for (int j = 0; j < BSList.size(); j++) {
				if (Location.getDistance(BSList.get(i).getLocation(), BSList.get(j).getLocation()) < (BSList.get(i).getRadius() + BSList.get(j).getRadius())
						&& i != j) {
					nearBSKey.add(j);
				}
			}
			BSGraph[i] = nearBSKey;
		}
		return BSGraph;
	}
}
