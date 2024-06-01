# UCIS-*k*ESP

Chengying Mao and Haiquan Hu, "Large-scale *k* Edge Server Placement Based on User Clustering and Intelligent Search", in Proc .of 2023 IEEE International Conference on Parallel & Distributed Processing with Applications, Big Data & Cloud Computing, Sustainable Computing & Communications, Social Computing & Networking (ISPA/BDCloud/SocialCom/SustainCom), Wuhan, China, December 2023, pp. 997-1004.

This is the source code and results of UCIS-*k*ESP algorithm.

Dataset Source (EUA Datasets): <https://github.com/PhuLai/eua-dataset>

File Directory:

./Results
- Comparison on Different Numbers of Base Stations.xlsx
- Comparison on Different Server Budgets.xlsx
- Comparison on Different Numbers of Users.xlsx
- Impact Analysis of Cluster Number.xlsx

./SourceCode
- GenerateMobileNetwork_main.java
- UCIS_kESP_main.java
- UserClusterByGMM.py
- data/Metropolitan
  - melb_metro_station.csv
  - melb_metro_user.csv
- method
  - GeneticAlgorithm.java
- model
  - BaseStation.java
  - EdgeUser.java
  - Location.java
  - ConstNUm.java
- utils
  - AlgorithmUtils.java
  - GeneralUtils.java
  - GenerateDataUtils.java

# Erratum
This note aims to correct an error in Table 1. In the final version of this article published, in Table 1, the parameter *k* of Group 1 and Group 2 were erroneously written as 120. The correct *k* is 100. The other elements of the table remain the same.
