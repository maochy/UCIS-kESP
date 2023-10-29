import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import model.*;
import utils.AlgorithmUtils;
import utils.GenerateDataUtils;
import utils.GeneralUtils;

public class GenerateMobileNetwork_main {

	public static void main(String[] arg) throws IOException {

		// read the metropolitan data set includes base station and user
		String metroBaseStationFile = "data/Metropolitan/melb_metro_station.csv";
		String metroUserFile = "data/Metropolitan/melb_metro_user.csv";
		ArrayList<BaseStation> metroBaseStations = new ArrayList<>();
		metroBaseStations = GeneralUtils.readBaseStationData_Metro(metroBaseStationFile);
		ArrayList<EdgeUser> metroUsers = new ArrayList<>();
		metroUsers = GeneralUtils.readUserData(metroUserFile);

		ConstNum.nBaseStation = 600;
		ConstNum.nServer = 100;
		ConstNum.nUser = 6000;

		// generate the base station data
		BaseStation[] subBaseStations = GeneralUtils.generateBSList(metroBaseStations);
		// output the base station data
		BufferedWriter bw_bs = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data/BaseStationData.csv")));
		bw_bs.append("Radius,Latitude,Longitude");
		bw_bs.newLine();
		for (int j = 0; j < subBaseStations.length; j++) {
			bw_bs.append(subBaseStations[j].getRadius() + "," + subBaseStations[j].getLocation().getLat() + ","
					+ subBaseStations[j].getLocation().getLng());
			bw_bs.newLine();
		}
		bw_bs.close();

		// generate the user data
		EdgeUser[] subUsers = GenerateDataUtils.generateSubUser(subBaseStations, metroUsers);
		// output the user data
		BufferedWriter bw_us = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data/UserData.csv")));
		bw_us.append("Latitude,Longitude");
		bw_us.newLine();
		for (int j = 0; j < subUsers.length; j++) {
			bw_us.append(subUsers[j].getLocation().getLat() + "," + subUsers[j].getLocation().getLng());
			bw_us.newLine();
		}
		bw_us.close();

		// calculate and output the accessMatrix
		ArrayList<Integer>[] accessMatrix = AlgorithmUtils.calculateAccessMatrix(subBaseStations, subUsers);
		BufferedWriter bw_access = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data/AccessMatrix.csv")));
		for (int j = 0; j < accessMatrix.length; j++) {
			for (int k = 0; k < accessMatrix[j].size(); k++) {
				bw_access.append(accessMatrix[j].get(k) + ",");
			}
			bw_access.newLine();
		}
		bw_access.close();


		// calculate and output the robustMatrix
		int[][] robustMatrix = AlgorithmUtils.calculateRobustMatrix(accessMatrix);
		BufferedWriter bw_robust = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data/RobustMatrix.csv")));
		for (int j = 0; j < robustMatrix.length; j++) {
			for (int k = 0; k < robustMatrix[j].length; k++) {
				bw_robust.append(robustMatrix[j][k] + ",");
			}
			bw_robust.newLine();
		}
		bw_robust.close();
	}
}
