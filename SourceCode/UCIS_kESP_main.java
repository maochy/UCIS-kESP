import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import method.GeneticAlgorithm;
import model.BaseStation;
import model.EdgeUser;
import model.Location;
import utils.GeneralUtils;

public class UCIS_kESP_main {
	public static int populationSize = 50;
	public static int maxIteration = 200;
	public static double mutationRate = 0.1;
	public static double objRate = 0.7;

	public static int BSNum = 600;
	public static int k = 100;
	public static int maxCoverage = 6000;
	public static int chromosomeLength = k * 2;

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		// read the accessMatrix data
		ArrayList<Integer>[] accessibility = new ArrayList[BSNum];
		BufferedReader reader = new BufferedReader(new FileReader(new File("data/AccessMatrix.csv")));
		String line = "";
		int index = 0;
		while ((line = reader.readLine()) != null) {
			ArrayList<Integer> access = new ArrayList<>();
			if (!line.equals("")) {
				String[] items = line.split(",");
				for (String i : items) {
					access.add(Integer.parseInt(i));
				}
			}
			accessibility[index] = access;
			index++;
		}

		// read the robustMatrix data
		int[][] robustness = new int[BSNum][BSNum];
		reader = new BufferedReader(new FileReader(new File("data/RobustMatrix.csv")));
		line = "";
		index = 0;
		while ((line = reader.readLine()) != null) {
			String[] items = line.split(",");
			for (int j = 0; j < items.length; j++) {
				robustness[index][j] = Integer.parseInt(items[j]);
			}
			index++;
		}

		int maxRobustness = 0;
		for (int i = 0; i < robustness.length; i++) {
			for (int j = i + 1; j < robustness[i].length; j++) {
				maxRobustness += robustness[i][j];
			}
		}

		// match the center position of each cluster into one base station
		outputOneBS();
		int[] oneBSForCluster = new int[chromosomeLength];
		reader = new BufferedReader(new FileReader(new File("data/cluster_oneBS.csv")));
		line = reader.readLine();
		index = 0;
		while ((line = reader.readLine()) != null) {
			String[] items = line.split(",");
			oneBSForCluster[index] = Integer.parseInt(items[0]);
			index++;
		}
		
		// match the two centers of two sub-clusters into the base stations
		outputTwoBS(chromosomeLength);
		int[][] twoBSForCluster = new int[chromosomeLength][2];
		reader = new BufferedReader(new FileReader(new File("data/cluster_twoBS.csv")));
		line = reader.readLine();
		index = 0;
		while ((line = reader.readLine()) != null) {
			String[] items = line.split(",");
			twoBSForCluster[index][0] = Integer.parseInt(items[0]);
			twoBSForCluster[index][1] = Integer.parseInt(items[4]);
			index++;
		}

		// solving by genetic algorithm 
		GeneticAlgorithm GA = new GeneticAlgorithm(populationSize, chromosomeLength, mutationRate, k,
				accessibility, robustness, objRate, oneBSForCluster, twoBSForCluster, maxRobustness, maxCoverage);
		double timeStart = System.currentTimeMillis();
		GA.run(maxIteration);
		double end = System.currentTimeMillis();
		Integer[] individual = GA.getBestSolution();
		
		// calculate the user coverage
		ArrayList<Integer> users = new ArrayList<>();
		for (int i = 0; i < individual.length; i++) {
			if (individual[i] == 0)
				continue;
			else if (individual[i] == 1) {
				for (int j = 0; j < accessibility[oneBSForCluster[i]].size(); j++) {
					if (users.contains(accessibility[oneBSForCluster[i]].get(j)))
						continue;
					users.add(accessibility[oneBSForCluster[i]].get(j));
				}
			} else if (individual[i] == 2) {
				for (int j = 0; j < accessibility[twoBSForCluster[i][0]].size(); j++) {
					if (users.contains(accessibility[twoBSForCluster[i][0]].get(j)))
						continue;
					users.add(accessibility[twoBSForCluster[i][0]].get(j));
				}

				for (int j = 0; j < accessibility[twoBSForCluster[i][1]].size(); j++) {
					if (users.contains(accessibility[twoBSForCluster[i][1]].get(j)))
						continue;
					users.add(accessibility[twoBSForCluster[i][1]].get(j));
				}
			}
		}
		int coverage = users.size();

		// calculate the robustness
		int robust = 0;
		ArrayList<Integer> tmp = new ArrayList<>();
		for (int i = 0; i < individual.length; i++) {
			if (individual[i] == 0) {
				continue;
			} else if (individual[i] == 1) {
				tmp.add(oneBSForCluster[i]);
			} else if (individual[i] == 2) {
				tmp.add(twoBSForCluster[i][0]);
				tmp.add(twoBSForCluster[i][1]);
			}
		}

		for (int i = 0; i < tmp.size(); i++) {
			for (int j = i + 1; j < tmp.size(); j++) {
				robust += robustness[tmp.get(i)][tmp.get(j)];
			}
		}

		System.out.println((end - timeStart) / 1000 + "\t" + GA.getBestFitness() + "\t" + 1.0 * coverage / maxCoverage
				+ "\t" + 1.0 * robust / maxRobustness);

	}

	public static void outputTwoBS(int clusterNum) throws IOException {
		String metroBaseStationFile = "data/BaseStationData.csv";
		String ClusterInfoFile = "data/cluster_Info.csv";

		ArrayList<BaseStation> metroBaseStations = new ArrayList<>();
		metroBaseStations = GeneralUtils.readBaseStationData_Metro(metroBaseStationFile);

		HashMap<Integer, Double[]> clusterMap = new HashMap<>();
		BufferedReader reader = new BufferedReader(new FileReader(new File(ClusterInfoFile)));
		String line = reader.readLine();
		while ((line = reader.readLine()) != null) {
			String[] items = line.split(",");
			Double[] tmpLocs = { Double.valueOf(items[1]), Double.valueOf(items[2]), Double.valueOf(items[3]),
					Double.valueOf(items[4]) };
			clusterMap.put(Integer.valueOf(items[0]), tmpLocs);
		}
		reader.close();

		BaseStation[][] twoBS = new BaseStation[clusterNum][2];
		twoBS = getTwoBS(clusterNum, clusterMap, metroBaseStations);

		BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream("data/cluster_twoBS.csv")));
		bw.append("ID,Latitude,Longitude,Radius,ID,Latitude,Longitude,Radius,Cluster");

		for (BaseStation[] bs : twoBS) {
			bw.newLine();
			bw.append(bs[0].getID() + "," + bs[0].getLocation().getLat() + "," + bs[0].getLocation().getLng() + ","
					+ bs[0].getRadius() + "," + bs[1].getID() + "," + bs[1].getLocation().getLat() + ","
					+ bs[1].getLocation().getLng() + "," + bs[1].getRadius() + "," + bs[0].getClusterID());
		}
		bw.close();
	}

	public static int outputOneBS() throws IOException {
		String metroBaseStationFile = "data/BaseStationData.csv";
		String metroUserFile = "data/cluster_gmm_user.csv";
		ArrayList<BaseStation> metroBaseStations = new ArrayList<>();
		metroBaseStations = GeneralUtils.readBaseStationData_Metro(metroBaseStationFile);
		ArrayList<EdgeUser> metroUsers = new ArrayList<>();
		metroUsers = readUserData(metroUserFile);

		HashMap<Integer, ArrayList<EdgeUser>> clusters = new HashMap<>();
		clusters = reshuffleUserByClusterID(metroUsers);

		BaseStation[] centers = new BaseStation[clusters.keySet().size()];
		centers = getOneBS(clusters, metroBaseStations);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data/cluster_oneBS.csv")));
		bw.append("ID,Latitude,Longitude,Radius,Cluster");

		for (BaseStation bs : centers) {
			bw.newLine();
			bw.append(bs.getID() + "," + bs.getLocation().getLat() + "," + bs.getLocation().getLng() + ","
					+ bs.getRadius() + "," + bs.getClusterID());
		}
		bw.close();
		return clusters.keySet().size();
	}

	public static ArrayList<EdgeUser> readUserData(String path) {
		ArrayList<EdgeUser> users = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
			String line = reader.readLine();
			while ((line = reader.readLine()) != null) {
				String[] items = line.split(",");
				EdgeUser u = new EdgeUser();
				u.getLocation().setLat(Double.parseDouble(items[1]));
				u.getLocation().setLng(Double.parseDouble(items[2]));
				u.setClusterID(Integer.parseInt(items[3]));
				users.add(u);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return users;
	}

	public static HashMap<Integer, ArrayList<EdgeUser>> reshuffleUserByClusterID(ArrayList<EdgeUser> UserList) {
		HashMap<Integer, ArrayList<EdgeUser>> clusters = new HashMap<>();
		for (EdgeUser eu : UserList) {
			if (clusters.containsKey(eu.getClusterID())) {
				clusters.get(eu.getClusterID()).add(eu);
			} else {
				ArrayList<EdgeUser> tmp = new ArrayList<EdgeUser>();
				tmp.add(eu);
				clusters.put(eu.getClusterID(), tmp);
			}
		}
		return clusters;
	}

	/**
	 * match the two centers of two sub-clusters into the base stations
	 * 
	 * @param clusterNum
	 * @param clusters
	 * @param robustness
	 * @return
	 */
	private static BaseStation[][] getTwoBS(int clusterNum, HashMap<Integer, Double[]> clusterMap,
			ArrayList<BaseStation> BSList) {
		BaseStation[][] twoBS = new BaseStation[clusterNum][2];
		HashSet<Integer> tmpSet = new HashSet<>();

		for (int i = 0; i < twoBS.length; i++) {
			Double[] LatAndLng = clusterMap.get(i);
			int bsIndex1 = -1;
			int bsIndex2 = -1;
			double minDist1 = Double.MAX_VALUE;
			double minDist2 = Double.MAX_VALUE;
			for (int j = 0; j < BSList.size(); j++) {
				if (tmpSet.contains(j))
					continue;
				double tmp1 = (BSList.get(j).getLocation().getLat() - LatAndLng[0])
						* (BSList.get(j).getLocation().getLat() - LatAndLng[0])
						+ (BSList.get(j).getLocation().getLng() - LatAndLng[1])
								* (BSList.get(j).getLocation().getLng() - LatAndLng[1]);

				double tmp2 = (BSList.get(j).getLocation().getLat() - LatAndLng[2])
						* (BSList.get(j).getLocation().getLat() - LatAndLng[2])
						+ (BSList.get(j).getLocation().getLng() - LatAndLng[3])
								* (BSList.get(j).getLocation().getLng() - LatAndLng[3]);

				if (bsIndex1 == -1 && bsIndex2 == -1) {
					if (tmp1 < tmp2) {
						minDist1 = tmp1;
						bsIndex1 = j;
					} else {
						minDist2 = tmp2;
						bsIndex2 = j;
					}
				} else if (bsIndex1 == -1 && bsIndex2 != -1) {
					minDist1 = tmp1;
					bsIndex1 = j;
				} else if (bsIndex1 != -1 && bsIndex2 == -1) {
					minDist2 = tmp2;
					bsIndex2 = j;
				} else {
					if (minDist1 > tmp1 && minDist2 < tmp2) {
						minDist1 = tmp1;
						bsIndex1 = j;
					} else if (minDist1 < tmp1 && minDist2 > tmp2) {
						minDist2 = tmp2;
						bsIndex2 = j;
					} else if (minDist1 > tmp1 && minDist2 > tmp2) {
						if (tmp1 < tmp2) {
							minDist1 = tmp1;
							bsIndex1 = j;
						} else {
							minDist2 = tmp2;
							bsIndex2 = j;
						}
					}
				}
			}

			BSList.get(bsIndex1).setClusterID(i);
			tmpSet.add(bsIndex1);
			twoBS[i][0] = BSList.get(bsIndex1);

			BSList.get(bsIndex2).setClusterID(i);
			tmpSet.add(bsIndex2);
			twoBS[i][1] = BSList.get(bsIndex2);
		}

		return twoBS;
	}

	/**
	 * match the each center of clusters into one base station
	 * 
	 * @param clusters
	 * @param robustness
	 * @return
	 */
	public static BaseStation[] getOneBS(HashMap<Integer, ArrayList<EdgeUser>> clusters,
			ArrayList<BaseStation> BSList) {
		BaseStation[] oneBS = new BaseStation[clusters.keySet().size()];
		HashSet<Integer> tmpSet = new HashSet<>();
		for (int i = 0; i < oneBS.length; i++) {
			Location loc = new Location(0, 0);
			for (EdgeUser eu : clusters.get(i)) {
				loc.setLat(loc.getLat() + eu.getLocation().getLat());
				loc.setLng(loc.getLng() + eu.getLocation().getLng());
			}
			loc.setLat(loc.getLat() / clusters.get(i).size());
			loc.setLng(loc.getLng() / clusters.get(i).size());

			int index = -1;
			double dist = Double.MAX_VALUE;
			for (int j = 0; j < BSList.size(); j++) {
				double tmp = (BSList.get(j).getLocation().getLat() - loc.getLat())
						* (BSList.get(j).getLocation().getLat() - loc.getLat())
						+ (BSList.get(j).getLocation().getLng() - loc.getLng())
								* (BSList.get(j).getLocation().getLng() - loc.getLng());
				if (dist > tmp && !tmpSet.contains(j)) {
					dist = tmp;
					index = j;
				}
			}
			BSList.get(index).setClusterID(i);
			tmpSet.add(index);
			oneBS[i] = BSList.get(index);
		}

		return oneBS;
	}
}
