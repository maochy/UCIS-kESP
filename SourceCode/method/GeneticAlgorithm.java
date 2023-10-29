package method;

import java.util.ArrayList;
import java.util.HashSet;

public class GeneticAlgorithm {

	private int populationSize;
	private int chromosomeLength;
	private double mutationRate;
	private Integer[][] population;
	private ArrayList<Integer[]> offsprings;
	private int k;
	private double objRate;
	private ArrayList<Integer>[] accessibility;
	private int[][] robustness;
	private int[] oneBSIndex;
	private int[][] twoBSIndex;

	private int maxRobustness;
	private int maxCoverage;

	private double BestFitness;

	private Integer[] BestSolution;

	public GeneticAlgorithm(int populationSize, int chromosomeLength, double mutationRate, int k,
			ArrayList<Integer>[] accessibility, int[][] robustness, double objRate, int[] oneBSIndex,
			int[][] twoBSIndex, int maxRobustness, int maxCoverage) {
		this.populationSize = populationSize;
		this.chromosomeLength = chromosomeLength;
		this.mutationRate = mutationRate;
		population = new Integer[populationSize][chromosomeLength];
		for (int i = 0; i < populationSize; i++) {
			for (int j = 0; j < chromosomeLength; j++) {
				population[i][j] = 0;
			}
		}
		offsprings = new ArrayList<>();
		this.k = k;
		this.accessibility = accessibility;
		this.robustness = robustness;
		this.objRate = objRate;
		this.oneBSIndex = oneBSIndex;
		this.twoBSIndex = twoBSIndex;
		this.BestFitness = 0;
		this.maxRobustness = maxRobustness;
		this.maxCoverage = maxCoverage;
	}

	private void initializePopulation() {

		for (int i = 0; i < populationSize; i++) {
			int tmp = 0;
			while (tmp < k) {
				int index = (int) (Math.random() * chromosomeLength);
				if (population[i][index] < 2) {
					population[i][index]++;
					tmp++;
				}
			}
		}
	}

	public double calculateFitness(Integer[] individual) {
		// Implement fitness calculation
		
		HashSet<Integer> users = new HashSet<>();
		for (int i = 0; i < individual.length; i++) {
			if (individual[i] == 0) {
				continue;
			} else if (individual[i] == 1) {
				users.addAll(accessibility[oneBSIndex[i]]);
			} else if (individual[i] == 2) {
				users.addAll(accessibility[twoBSIndex[i][0]]);
				users.addAll(accessibility[twoBSIndex[i][1]]);
			}
		}
		
		int coverage = users.size();

		// Robustness
		int robust = 0;
		ArrayList<Integer> tmp = new ArrayList<>();
		for (int i = 0; i < individual.length; i++) {
			if (individual[i] == 0) {
				continue;
			} else if (individual[i] == 1) {
				tmp.add(oneBSIndex[i]);
			} else if (individual[i] == 2) {
				tmp.add(twoBSIndex[i][0]);
				tmp.add(twoBSIndex[i][1]);
			}
		}

		for (int i = 0; i < tmp.size(); i++) {
			for (int j = i + 1; j < tmp.size(); j++) {
				robust += robustness[tmp.get(i)][tmp.get(j)];
			}
		}

		return objRate * coverage / maxCoverage + (1 - objRate) * robust / maxRobustness;
	}

	@SuppressWarnings("unlikely-arg-type")
	private void selection() {
		ArrayList<Integer> tournament;
		int index = 0;
		double selectRate = 0.5;

		ArrayList<Integer> tmpList = new ArrayList<>();
		ArrayList<Double> tmpFitnessList = new ArrayList<>();
		for (int i = 0; i < offsprings.size(); i++) {
			double tmpF = calculateFitness(offsprings.get(i));
			int j = 0;
			for (; j < tmpList.size(); j++) {
				if (tmpF > tmpFitnessList.get(j)) {
					break;
				}
			}
			tmpList.add(j, i);
			tmpFitnessList.add(j, tmpF);
		}

		double maxFitnessByOffsprings = calculateFitness(offsprings.get(tmpList.get(0)));
		if (this.BestFitness < maxFitnessByOffsprings) {
			setBestFitness(maxFitnessByOffsprings);
			Integer[] solution = new Integer[chromosomeLength];
			for (int j = 0; j < chromosomeLength; j++) {
				solution[j] = offsprings.get(tmpList.get(0))[j];
			}
			setBestSolution(solution);
		}

		for (; index < populationSize * selectRate; index++) {
			int offSIndex = tmpList.get(index);
			for (int i = 0; i < offsprings.get(offSIndex).length; i++) {
				population[index][i] = offsprings.get(offSIndex)[i];
			}
		}

		for (int i = 0; i < populationSize * selectRate; i++) {
			offsprings.remove(tmpList.get(i));
		}

		while (index < populationSize) {
			tournament = new ArrayList<>();
			while (tournament.size() < 5) {
				int tmp = (int) (Math.random() * offsprings.size());
				if (!tournament.contains(tmp)) {
					tournament.add(tmp);
				}
			}
			int bestIndex = 0;
			double bestFitness = calculateFitness(offsprings.get(bestIndex));
			for (int i = 1; i < tournament.size(); i++) {
				double tmpFitness = calculateFitness(offsprings.get(tournament.get(i)));
				if (bestFitness < tmpFitness) {
					bestIndex = i;
					bestFitness = tmpFitness;
					if (this.BestFitness < bestFitness) {
						setBestFitness(bestFitness);
						Integer[] solution = new Integer[chromosomeLength];
						for (int j = 0; j < chromosomeLength; j++) {
							solution[j] = offsprings.get(bestIndex)[j];
						}
						setBestSolution(solution);
					}
				}
			}
			for (int i = 0; i < chromosomeLength; i++) {
				population[index][i] = offsprings.get(bestIndex)[i];
			}
			offsprings.remove(bestIndex);
			index++;
		}
	}

	private void crossover() {
		offsprings.clear();
		for (int i = 0; i < populationSize; i += 2) {
			int tmp1 = (int) (Math.random() * chromosomeLength);
			int tmp2 = (int) (Math.random() * chromosomeLength);
			while (tmp1 == tmp2) {
				tmp2 = (int) (Math.random() * chromosomeLength);
			}
			int pos1 = tmp1 < tmp2 ? tmp1 : tmp2;
			int pos2 = tmp1 > tmp2 ? tmp1 : tmp2;

			int[] crossPart1 = new int[pos2 - pos1 + 1];
			int kNum1 = 0;
			for (int j = pos1; j <= pos2; j++) {
				crossPart1[j - pos1] = population[i][j];
				kNum1 += population[i][j];
			}

			int[] crossPart2 = new int[pos2 - pos1 + 1];
			int kNum2 = 0;
			for (int j = pos1; j <= pos2; j++) {
				crossPart2[j - pos1] = population[i + 1][j];
				kNum2 += population[i + 1][j];
			}

			Integer[] offSpring1 = new Integer[chromosomeLength];
			Integer[] offSpring2 = new Integer[chromosomeLength];

			for (int j = 0; j < chromosomeLength; j++) {
				offSpring1[j] = population[i][j];
				offSpring2[j] = population[i + 1][j];
			}

			Integer[] offSpring3 = new Integer[chromosomeLength];
			Integer[] offSpring4 = new Integer[chromosomeLength];

			for (int j = 0; j < pos1; j++) {
				offSpring3[j] = population[i][j];
				offSpring4[j] = population[i + 1][j];
			}

			for (int j = pos1; j <= pos2; j++) {
				offSpring3[j] = crossPart2[j - pos1];
				offSpring4[j] = crossPart1[j - pos1];
			}

			for (int j = pos2; j < chromosomeLength; j++) {
				offSpring3[j] = population[i][j];
				offSpring4[j] = population[i + 1][j];
			}

			int tmp = kNum1 - kNum2;
			if (kNum1 > kNum2) {
				int count = 0;
				int pos = -1;
				while (count < tmp) {
					pos = (int) (Math.random() * chromosomeLength);
					if (offSpring3[pos] < 2) {
						offSpring3[pos]++;
						count++;
					}
				}

				count = 0;
				while (count < tmp) {
					pos = (int) (Math.random() * chromosomeLength);
					if (offSpring4[pos] > 0) {
						offSpring4[pos]--;
						count++;
					}
				}

			} else if (kNum1 < kNum2) {
				int count = 0;
				int pos = -1;
				while (count < tmp) {
					pos = (int) (Math.random() * chromosomeLength);
					if (offSpring3[pos] > 0) {
						offSpring3[pos]--;
						count++;
					}
				}

				count = 0;
				while (count < tmp) {
					pos = (int) (Math.random() * chromosomeLength);
					if (offSpring4[pos] < 2) {
						offSpring4[pos]++;
						count++;
					}
				}
			}

			offsprings.add(offSpring1);
			offsprings.add(offSpring2);
			offsprings.add(offSpring3);
			offsprings.add(offSpring4);
		}
	}

	private void mutation() {
		for (int i = 0; i < populationSize; i++) {
			if (Math.random() < mutationRate) {
				int pos1 = (int) (Math.random() * chromosomeLength);
				int pos2 = (int) (Math.random() * chromosomeLength);
				while (population[i][pos1] == population[i][pos2]) {
					pos2 = (int) (Math.random() * chromosomeLength);
				}
				int tmp = population[i][pos1];
				population[i][pos1] = population[i][pos2];
				population[i][pos2] = tmp;
			}
		}
	}

	public void run(int maxGenerations) {
		initializePopulation();
		for (int i = 0; i < maxGenerations; i++) {
			System.out.println("µÚ"+(i+1)+"µü´ú");
			crossover();
			mutation();
			selection();
		}
	}

	public Integer[] getBestSolution() {
		return BestSolution;
	}

	public void setBestSolution(Integer[] bestSolution) {
		BestSolution = bestSolution;
	}

	public double getBestFitness() {
		return BestFitness;
	}

	public void setBestFitness(double bestFitness) {
		BestFitness = bestFitness;
	}

	public ArrayList<Integer>[] getAccessibility() {
		return accessibility;
	}

	public int[][] getRobustness() {
		return robustness;
	}
}