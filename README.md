# Ensemble Clustering Library

Ensemble Clustering is a flexible multi-threaded data clustering software library for rapidly constructing tailored clustering solutions that leverage the different semantic aspects of heterogeneous data. Data clustering groups data together based on their similarity and has many applications such as organizing data to aid analysts or facilitating searching for similar data entities.  For example, twitter users could be clustered into groups based on the similarity of their names or locations.  

## Designed for Heterogeneous Data ##
General clustering approaches do not distinguish or take advantage of the different semantic aspects of heterogeneous data and treat them all as the same such as a "bag of words" or a vector of numbers losing important information that can help improve clustering results and group data in ways that are more meaningful to analysts.  A challenge is building clustering solutions that can scale to the massive amounts of data available and its heterogeneity.  

The Ensemble Clustering library is designed to handle multiple entity features with different semantics and scale with the growing size of today's data. Each semantic class of features contains valuable information that describe an entity. Each is associated with a data type to represent the features and semantically meaningful method for comparing the feature to others of the same semantic class. Combining them together into an ensemble leverages each of these characteristics when determining similarity of entities and clustering them.  Each can be weighted according to its value in distinguishing one entity from another to tailor how entities are clustered. 

### Scale Out, Extend ###
Ensemble Clustering provides a high-level Java API that can be integrated into existing software.  The library has been used to cluster entities involving a variety of properties such as names, locations, and classes, as well as events involving descriptions, actors, and dates.  The library provides a parallelized version that takes advantage of multiple cores on a single machine, and a distributed version that can scale across a cluster of machines.

Ensemble Clustering is under ongoing development and is freely available for download under The [MIT License](http://www.opensource.org/licenses/MIT) open source licensing. Unlike GNU General Public License (GPL), MIT freely permits distribution of derivative work under proprietary license, without requiring the release of source code.

##Interested in Learning More?##

* [Features](#features): See our features list for a high level summary of the capabilities of the Ensemble Clustering Library
* [Quick Start](#quick-start): To quickly get started with creating your own custom clustering solutions see the quick start guide. It is recommended to read through both examples in the quick start guide as concepts are similar in both the multi-threaded and distributed computing libraries.
* [API Documentation](#docs): Refer to the API documentation to find out more 

## <a name="features"></a>Features

* Cluster your data using a range of semantic features that each can be weighted individually to tailor how to cluster your heterogenous data. The following feature types are supported:
	* Strings
 	* Geospatial
 	* Bag of Words
  	* Numeric Vectors
  	* Semantic Classes
  	* Temporal Date Ranges

* Use of cluster prototypes to speed up clustering operations
	* Each cluster is represented with a centroid value that summarizes the features of its members significantly reducing computation time

* Multi-threaded library to speed up clustering operations on a single machine
* Distributed computing library to scale to big data across a cluster of machines
* Implementations of K-Means, DP-Means and a fast single-pass threshold clustering algorithm

## <a name="quick-start"></a>Quick Start


### Prerequistives

This projects builds with [Java JDK](http://www.java.com/) version 1.7+ and [Apache Maven](http://maven.apache.org/) version 3.1.0 (other 3.x versions may work). Ensure maven is configured properly on the system on which you are building Ensemble Clustering.

### Installation

To install Ensemble Clustering you need to download the source code from GitHub.  This can be done either by [downloading a zip file from GitHub](https://github.com/oculusinfo/ensemble-clustering/archive/master.zip) or by installing the free open source [git version control system](http://git-scm.com/) and opening a command prompt to the directory you wish to download the source code and typing the following command:

    git clone git://github.com/oculusinfo/ensemble-clustering.git

After the source code has been downloaded (or unpacked if a zip was used) open a command prompt and execute the following in the `ensemble-clustering` root directory:

    mvn install

This will compile and install the Ensemble Clustering libraries in your local maven repository.


### Multi-threaded Ensemble Clustering Example

This example will walk through using the Ensemble Clustering multi-threaded library to cluster an example dataset where each data entity is a user name and location involving latitude and longitude.  The result will be data grouped by their similar names and locations.  The full source code can be found at:

    ensemble-clustering/ensemble-clustering/src/test/java/com/oculusinfo/ml/unsupervised/TestNameLocationCluster.java

To link the Ensemble Clustering multi-threaded library in your Java maven project add the following dependency to your `pom.xml`:

    <dependency>
      <groupId>com.oculusinfo</groupId>
      <artifactId>ensemble-clustering</artifactId>
      <version>0.1.0-SNAPSHOT</version>
    </dependency>

**Create a DataSet**

The first step is to create a DataSet which represents the data you wish to cluster:  

    DataSet ds = new DataSet();

Next, we need to populate the `DataSet` with instances that represent each data entity in the data.  Each `Instance` is associated with a set of `Features` that are typed data attributes for the entity. In this example we will randomly create some instances that have a string representing a name and geo spatial feature that represents the latitude and longitude location associated with the instance.

The following code generates the sample `DataSet`:

    String[] tokens = {"alpha", "bravo", "charlie", "delta", "echo", "foxtrot", "romeo", "sierra", "tango", "whiskey"};
		
    Random rnd = new Random();
    for (int i=0; i < 100000; i++) {
		// create a new data instance
		Instance inst = new Instance();
			
		// add name feature to the instance
		StringFeature name = new StringFeature("name");
		name.setValue( tokens[rnd.nextInt(tokens.length)] + " " + tokens[rnd.nextInt(tokens.length)]);
		inst.addFeature(name);
			
		// add geo spatial feature to the instance
		GeoSpatialFeature geo = new GeoSpatialFeature("location");
		geo.setLatitude(rnd.nextDouble() * 180 - 90);
		geo.setLongitude(rnd.nextDouble() * 360 - 180);
		inst.addFeature(geo);
		
		// add the instance to the dataset
		ds.add(inst);
	}

Now we have a `DataSet` populated with 100,000 instances that have a `StringFeature` with the name "name" and a `GeoSpatialFeature` with the name "location".  Note each feature must be given a unique name so the clusterer can associate an applicable distance function and centroid that define how similarity is computed and clusters are represented.  

**Create a Clusterer**

With the dataset created, we can create a clusterer that will perform the actual data clustering.  There are several choices of clusterer in the Ensemble Clustering library.  For this example we will use the well-known [k-means clusterer](http://en.wikipedia.org/wiki/K-means_clustering), which attempts to find a clustering of data into a pre-defined number of clusters.  Each entity is assigned to the cluster that has a mean value most similar to the entity.  In this example we will choose to cluster the data into 4 clusters:

    // create a k-means clusterer with k=4, 5 max iterations
    KMeans clusterer = new KMeans(4, 5, false);

Once the clusterer is created, we need to register the distance functions to use for each feature and the method of computing the centroid value for that feature that each cluster will use to represent it's "mean".  Each feature is given a weight that the clusterer uses to compute a weighted distance score when computing the distance of each instance in the data set to each clusters centroid.  The cluster with the smallest distance is the cluster the instance is assigned to and the cluster centroid is updated to reflect it's new contents.  In this example we will associate the "name" feature with a [Levenshtein Distance](http://en.wikipedia.org/wiki/Levenshtein_distance) function (also known as Edit Distance) for computing the similarity between two strings. To represent the median value for the "name" feature of all instances in each cluster we will use a StringMedianCentroid that uses a fast approximation method for computing the string that is median.  For the "location" feature we will use a [Haversine Distance](http://en.wikipedia.org/wiki/Haversine_formula) function (also known as great circle distance) for computing the similarity between latitude/longitude coordinates.  To represent the mean value for the "location" feature of all instances in each cluster we will use a GeoSpatialCentroid that computes the average latitude and longitude.  If we wanted to weight one of the features as being more important we could adjust the weights accordingly, but for this example we will leave both at 1.0:

    // register the name feature distance function and centroid method using a weight of 1.0
	clusterer.registerFeatureType(
		"name",
		StringMedianCentroid.class, 
		new EditDistance(1.0));
	
	// register the location feature distance function and centroid method using a weight of 1.0
    clusterer.registerFeatureType(
		"location", 
		GeoSpatialCentroid.class,
		new HaversineDistance(1.0));

**Cluster the DataSet**

Our clusterer is now configured, we can now cluster the data set, which returns a `ClusterResult` containing the resulting clusters:

    ClusterResult clusters = clusterer.doCluster(ds);

To print the resulting cluster centroid we can use the `Cluster.toString()` method that will return a string representation of the centroid values for each feature in the cluster:

    for (Cluster c : clusters) {
		System.out.println( c.toString() );
	}

The output should look similar to the following showing the four clusters and the associated location and name centroid values:

    "id:0c5fc377-469c-4c3a-954a-34962561931a","location:[5.4638009343415375;163.20379731236523]","name:charlie sierra"
    "id:5f5c8071-3aef-40fd-9c56-dc231df9cd45","location:[13.282395014075899;130.99606647760783]","name:romeo tango"
    "id:07a4bb00-d1d5-4675-9646-97bc83d113e6","location:[29.9204897054355;-148.7042698700601]","name:sierra whiskey"
    "id:b87f6871-257d-4c11-8e4e-70712f9a5c74","location:[4.966681327235642;-24.00041079095416]","name:alpha alpha"

Explore the examples in the `src\main\test` folder to learn more about how to use the library or look through the [API documentation](#docs).

### Distributed Ensemble Clustering Example

This example will walk through using the Ensemble Clustering distributed computing library to cluster an example dataset where each data point represents a location involving x and y coordinates.  The result will be data grouped by their similar x/y locations.  The full source code can be found at:

    ensemble-clustering/ensemble-clustering-spark/src/test/java/com/oculusinfo/ml/spark/unsupervised/TestKMeans.java

The distributed computing library is built upon [Apache Spark](http://spark.apache.org/), a fast general-purpose cluster computing system.  This example will detail using spark in standalone mode, see the spark documentation for configuration of a spark cluster.

To link the Ensemble Clustering distributed computing library in your Java maven project add the following dependency to your `pom.xml`:

    <dependency>
      <groupId>com.oculusinfo</groupId>
      <artifactId>ensemble-clustering-spark</artifactId>
      <version>0.1.0-SNAPSHOT</version>
    </dependency>

**Create Sample Data**

Similar to the multi-threaded library, the first step is to create a `DataSet` which represents the data you wish to cluster.  The library is designed to read data directly from the file system or [Hadoop Distributed File System](http://hadoop.apache.org/) (HDFS).  First we will create a sample data file containing the data to be clustered. For this example we will generate a comma separated file of 1,000,000 lines, where each line represents a data entity with a numeric x and y pair and store it on the local filesystem. 

    PrintWriter writer;
	try {
		writer = new PrintWriter("test.txt", "UTF-8");
		    
		// each class size is equal 
		int classSize = 200000;
			
		double stdDev = 30.0;
			
		// generate 5 classes of data points using a normal distribution with random means and fixed std deviation
		for (int i=0; i < 5; i++) {
			Random rnd = new Random();
		
			double meanX = rnd.nextDouble() * 400.0;
			double meanY = rnd.nextDouble() * 400.0;
				
			// randomly generate a dataset of x, y points
			for (int j = 0; j < classSize; j++) {
				double x = rnd.nextGaussian()*stdDev + meanX;
				double y = rnd.nextGaussian()*stdDev + meanY;
			
				writer.println(x + "," + y);
			}
		}
		writer.close();
	} catch (Exception e) { }

**Create a Spark Context, Instance Parser and Spark DataSet**

Now that the data has been generated and saved in the file named "test.txt", we can create a `DataSet` that contains the data to be clustered.  In the multi-threaded library the DataSet was stored in the memory of a single machine, but in the distributed library the `DataSet` can be stored in memory across a cluster of machines.  Apache Spark stores data in a [Resilient Distributed Dataset](http://spark.apache.org/docs/latest/programming-guide.html#resilient-distributed-datasets-rdds) (RDD).  Under the hood, the distributed clustering library uses an RDD to contain the DataSet using the `SparkDataSet` class.  First you must create a [JavaSparkContext](http://spark.apache.org/docs/latest/api/java/org/apache/spark/api/java/JavaSparkContext.html) that connects to your Spark installation.  In this example we will use a Spark standalone instance by specifying the master url as "local":

    JavaSparkContext sc = new JavaSparkContext("local", "MyExampleAppName");  

With the `JavaSparkContext` created we can now initialize a `SparkDataSet` by passing it the Spark context to use:
    
    SparkDataSet ds = new SparkDataSet(sc);

Next we need to populate the `SparkDataSet` with `Instances` that represent each data entity to be clustered similar to the multi-threaded library. The distributed library will load the data and pass each file line to a `SparkInstanceParser` that converts the raw line into an `Instance` object. You must write your own SparkInstanceParser to define how to parse your data and convert it into relevant `Instances` that contain the `Features` you are clustering on.  Below is an example that reads in the generated "test.txt" file and parses each line into an `Instance` that contains a `NumericVectorFeature` with a name of "point" that represents the numeric x and y values in the data:

    ds.load("test.txt", new SparkInstanceParser() {
		private static final long serialVersionUID = 1L;

		@Override
		public Tuple2<String, Instance> call(String line) throws Exception {
			Instance inst = new Instance();
				
			String tokens[] = line.split(",");
				
			NumericVectorFeature v = new NumericVectorFeature("point");
				
			double x = Double.parseDouble(tokens[0]);
			double y = Double.parseDouble(tokens[1]);
			v.setValue( new double[] { x, y } );
				
			inst.addFeature(v);
				
			return new Tuple2<String, Instance>(inst.getId(), inst);
		}
	});

**Create a Distributed Clusterer**

With the dataset created, we can create a clusterer that will perform the actual data clustering.  Similar with the multi-threaded library there are several choices of clusterer in the distributed library.  For this example we will use k-means and cluster the data into 5 clusters.  We can instruct the clusterer to save the centroid of each cluster and the cluster membership to the local file system or HDFS.  In this example we will save the results to "output/centroids" and "output/clusters".

	// create a k-means clusterer with 5 clusters and 10 max iterations using a stopping criteria of 0.001
	KMeansClusterer clusterer = new KMeansClusterer(5, 10, 0.001, "output/centroids", "output/clusters");

Once the clusterer is created, we can register the distance functions to use for each feature and the method of computing the centroid value for that feature that each cluster will use to represent it's "mean".  Since our Instances contain "point" features that are NumericVectors we will use [Euclidean Distance](http://en.wikipedia.org/wiki/Euclidean_distance) for computing the similarity between two points. To represent the mean value for the "point" feature in each cluster we will use a MeanNumericVectorCentroid that computes the average value for each vector component:
		
	// register the point feature distance function and centroid method using a weight of 1.0
	clusterer.registerFeatureType("point", MeanNumericVectorCentroid.class, new EuclideanDistance(1.0));


**Cluster the DataSet**

Our clusterer is now configured, we can now cluster the data set, which returns a `SparkClusterResult` containing a Spark RDD of the resulting clusters:

    ClusterResult clusters = clusterer.doCluster(ds);

To examine the resulting clusters, the cluster centroids and cluster membership were output to text files and can be read from there. Note that Spark writes it's output in partition files so there may be several "part-xxxxx" files contained within the "cluster" and "centroids" folders.

For example the centroids folder contents contain the following, which shows five cluster centroids and their average points:

	(8755b532-f9f9-4c62-b777-161c5db197f8,"id:c6326ede-242a-405e-827b-1d7027ac5d14","point:[193.19015073636675;96.48693677066142]")
	(dc70d932-e850-4f44-aa15-992c82aa6b96,"id:10cd7373-430e-4cc5-8cb3-df8fbf14c6fd","point:[399.1918756632515;280.5139050430419]")
	(8d54b4bf-14b5-4257-ad56-75d33913fb43,"id:fce7d565-898e-4d80-92d2-d24e563721f0","point:[105.817157183995;140.94178706769415]")
	(496b2b10-3396-4d4e-9e36-ce238cc164a1,"id:a29fd87c-4f1a-4896-a54e-75f0df98206a","point:[9.42711813720453;46.12606351759711]")
	(7c7a273e-24b7-4ff5-9149-ae999217353c,"id:3aef7e08-652d-47d5-811f-7c7e0b232532","point:[195.8785875514361;20.53061201672536]")

Explore the examples in the `src\main\test` folder to learn more about how to use the library or look through the [API documentation](#docs).

## <a name="docs"></a>API Documentation

API Documentation can be found at: [ensemble-clustering docs](http://unchartedsoftware.github.io/ensemble-clustering/javadoc/ensemble-clustering/0.1/index.html) and [ensemble-clustering-spark](http://unchartedsoftware.github.io/ensemble-clustering/javadoc/ensemble-clustering-spark/0.1/index.html)

It is recommended to look through the many examples in the `src/main/test` folder of the `ensemble-clustering` and `ensemble-clustering-spark` folders.
