import pandas as pd
from sklearn.cluster import KMeans
from sklearn.mixture import GaussianMixture


def main():
    # Read the user data
    serverBudget = 100
    userDataPath = '../data/UserData.csv'
    data = pd.read_csv(userDataPath)
    # Gaussian Mixture Cluster
    clusterNum = int(serverBudget * 2)
    gmm = GaussianMixture(n_components=clusterNum, n_init=5).fit(data)
    labels = gmm.predict(data)
    # Output the center position of each cluster generated by gmm
    labelsDF = pd.DataFrame(labels, columns=['Cluster'])
    df = pd.concat([data, labelsDF], axis=1)
    clusterOutputPath = '../data/cluster_gmm_user.csv'
    df.to_csv(clusterOutputPath, sep=',', index=True, header=True)
    # Output the two position generated by kmeans on each cluster
    ClusterInfo = pd.DataFrame(columns=['Lat1', 'Lng1', 'Lat2', 'Lng2'])
    for j in range(0, clusterNum):
        X = df[df['Cluster'] == j].drop(['Cluster'], axis=1)
        if len(X) == 1:
            new_row = pd.Series({
                'Lat1': X.iloc[0, 0],
                'Lng1': X.iloc[0, 1],
                'Lat2': X.iloc[0, 0],
                'Lng2': X.iloc[0, 1]},
                index=ClusterInfo.columns)
        else:
            kmeans = KMeans(n_clusters=2, n_init=5).fit(X)
            new_row = pd.Series({
                'Lat1': kmeans.cluster_centers_[0, 0],
                'Lng1': kmeans.cluster_centers_[0, 1],
                'Lat2': kmeans.cluster_centers_[1, 0],
                'Lng2': kmeans.cluster_centers_[1, 1]}
                , index=ClusterInfo.columns)
        ClusterInfo = pd.concat([ClusterInfo, pd.DataFrame([new_row])], ignore_index=True)
    ClusterInfo.to_csv('../data/cluster_Info.csv', sep=',', index=True, header=True)

if __name__ == "__main__":
    main()
