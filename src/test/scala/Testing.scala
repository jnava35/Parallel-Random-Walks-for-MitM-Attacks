import NetGraphAlgebraDefs.{NetGraph, NodeObject}
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{DoubleType, StringType, StructType}
import scala.util.Random
import org.scalatest.funsuite.AnyFunSuite
import spark.implicits._

class Testing extends AnyFunSuite {
  val spark: SparkSession = SparkSession.builder()
    .appName("NetGraphAttackSimulationTest")
    .master("local[2]")
    .getOrCreate()

  val perturbedGraph: NetGraph = createPerturbedGraph(spark) // Replace with your test graph data

  test("generateRandomWalks should return the expected number of random walks") {
    val numRandomWalks = 10
    val maxSteps = 100
    val randomWalkResults = NetGraphAttackSimulation.generateRandomWalks(perturbedGraph, numRandomWalks, maxSteps, spark)
    val count = randomWalkResults.count()
    assert(count == numRandomWalks, s"Expected $numRandomWalks random walks, but got $count")
  }

  test("calculateAttacks should return the expected statistics") {
    val numRandomWalks = 20
    val maxSteps = 200
    val randomWalkResults = NetGraphAttackSimulation.generateRandomWalks(perturbedGraph, numRandomWalks, maxSteps, spark)
    val (successfulAttacks, failedAttacks) = NetGraphAttackSimulation.calculateAttacks(randomWalkResults)
    assert(successfulAttacks >= 0, "Successful attacks should be non-negative")
    assert(failedAttacks >= 0, "Failed attacks should be non-negative")
  }

  // Category: calculateAttacks
  test("calculateAttacks should return the expected statistics") {
    val numRandomWalks = 30
    val maxSteps = 300
    val randomWalkResults = NetGraphAttackSimulation.generateRandomWalks(perturbedGraph, numRandomWalks, maxSteps, spark)
    val (successfulAttacks, failedAttacks) = NetGraphAttackSimulation.calculateAttacks(randomWalkResults)
    assert(successfulAttacks >= 0, "Successful attacks should be non-negative")
    assert(failedAttacks >= 0, "Failed attacks should be non-negative")
  }

  // Category: calculateAttacks
  test("calculateAttacks should return the expected statistics") {
    val numRandomWalks = 40
    val maxSteps = 400
    val randomWalkResults = NetGraphAttackSimulation.generateRandomWalks(perturbedGraph, numRandomWalks, maxSteps, spark)
    val (successfulAttacks, failedAttacks) = NetGraphAttackSimulation.calculateAttacks(randomWalkResults)
    assert(successfulAttacks >= 0, "Successful attacks should be non-negative")
    assert(failedAttacks >= 0, "Failed attacks should be non-negative")
  }

  // Category: calculateAttacks
  test("calculateAttacks should return the expected statistics") {
    val numRandomWalks = 50
    val maxSteps = 500
    val randomWalkResults = NetGraphAttackSimulation.generateRandomWalks(perturbedGraph, numRandomWalks, maxSteps, spark)
    val (successfulAttacks, failedAttacks) = NetGraphAttackSimulation.calculateAttacks(randomWalkResults)
    assert(successfulAttacks >= 0, "Successful attacks should be non-negative")
    assert(failedAttacks >= 0, "Failed attacks should be non-negative")
  }

  test("simRank should return a similarity score between 0 and 1") {
    val originalNode = perturbedGraph.sm.nodes().head
    val perturbedNode = perturbedGraph.sm.nodes().last
    val similarityScore = NetGraphAttackSimulation.simRank(originalNode, perturbedNode)
    assert(similarityScore >= 0.0 && similarityScore <= 1.0, s"Similarity score $similarityScore is out of range")
  }
  
  test("saveAttackStats create a CSV file") {
    val outputPath = "test_output"
    val precision = 0.8
    val recall = 0.6
    NetGraphAttackSimulation.saveAttackStats(precision, recall, outputPath, spark)
    val fileExists = new java.io.File(outputPath).isFile
    assert(fileExists, s"CSV file not found at $outputPath")
  }

}
