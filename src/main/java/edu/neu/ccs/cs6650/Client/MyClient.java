package edu.neu.ccs.cs6650.Client;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import edu.neu.ccs.cs6650.Helpers.StatCalc;
import edu.neu.ccs.cs6650.Helpers.MyWriter;

/**
 * Created by Atharva Jakkanwar on 29-Sep-17.
 */
/*

 */
public class MyClient {

  private static AtomicInteger successCounter;
  private static AtomicInteger requestCounter;
  private static CopyOnWriteArrayList<RequestResult> results;
  private static CopyOnWriteArrayList<Thread> threads;

  private Client client;
  private WebTarget target;

  public MyClient(String endpoint) {
    this.client = ClientBuilder.newClient();
    this.target = client.target(endpoint);
    results = new CopyOnWriteArrayList<RequestResult>();
    successCounter = new AtomicInteger(0);
    requestCounter = new AtomicInteger(0);
    //    /*Threads: initlialise*/
//    threads = new CopyOnWriteArrayList<Thread>();
  }

  public CopyOnWriteArrayList<RequestResult> getResults() {
    return results;
  }

  public synchronized void incrementSuccess() {
    successCounter.addAndGet(1);
  }

  public synchronized void incrementRequest() {
    requestCounter.addAndGet(1);
  }

  public void doTask(int numIterations) {
    for (int i = 0; i < numIterations; i++) {

      incrementRequest();
      long startGet = System.currentTimeMillis();
      String responseGet = target.request(MediaType.TEXT_PLAIN).get().readEntity(String.class);
      long latencyGet = System.currentTimeMillis()-startGet;
      results.add(new RequestResult(System.currentTimeMillis(), latencyGet));
      if((responseGet.equals("Valar Morghulis"))){
        incrementSuccess();
      }

      // uncomment to test for POST requests

//      incrementRequest();
//      long startPost = System.currentTimeMillis();
//      String responsePost = target.request().post(Entity.text("abcd")).readEntity(String.class);
//      long latencyPost = System.currentTimeMillis()-startPost;
//      results.add(new RequestResult(System.currentTimeMillis(), latencyPost));
//      if((responsePost.equals("4"))){
//        incrementSuccess();
//      }

    }
  }

  public static void main(String[] args) {
    //inputs
    int numThreads = Integer.parseInt(args[0]);
    final int numIterations = Integer.parseInt(args[1]);
    final String ip = args[2];
    final String port = args[3];
    String endpoint = "http://" + ip + ":" + port + "/assignment1/webapi/myresource";

    //start time
    System.out.println("Client Starting time: " + System.currentTimeMillis());
    final MyClient myClient = new MyClient(endpoint);
    /*Threadpool: Initialize*/
    final ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);

    long start = System.currentTimeMillis();

    for (int i = 0; i < numThreads; i++) {
      threadPool.submit(new Runnable() {
        @Override
        public void run() {
         myClient.doTask(numIterations);
        }
      });
    }

    /*Threadpool: Shutdown*/
    threadPool.shutdown();
    System.out.println("All threads running..." + System.currentTimeMillis());

    /*Threadpool: Wait for Threads to finish*/
    try {
      threadPool.awaitTermination(1, TimeUnit.DAYS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("All threads complete: " + System.currentTimeMillis());

    long wall = System.currentTimeMillis()-start;
    System.out.println("Number of request sent: " + requestCounter.get());
    System.out.println("Number of successful requests: " + successCounter.get());
    System.out.println("Test wall time: " + wall);


    //Post Processing
    StatCalc calc = new StatCalc(results);
    System.out.println();
    System.out.println("Average Latency: " + calc.average());
    System.out.println("Median Latency: " + calc.median());
    System.out.println("99th Percentile: " +calc.percentile(99));
    System.out.println("95th Percentile: " +calc.percentile(95));

    //CSV for plot
    MyWriter writer = new MyWriter("post100_100.csv");
    writer.write(results);
  }
}

