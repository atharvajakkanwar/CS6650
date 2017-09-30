package edu.neu.ccs.cs6650.Helpers;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import edu.neu.ccs.cs6650.Client.RequestResult;

public class StatCalc {

  private CopyOnWriteArrayList<RequestResult> results;
  private List<Double> data;

  public StatCalc(CopyOnWriteArrayList<RequestResult> results) {
    this.results = new CopyOnWriteArrayList<RequestResult>();
    this.data = results
        .stream()
        .map(RequestResult::getLatency)
        .collect(Collectors.toList());
  }

  public double average() {
    return data.stream().mapToDouble(a -> a).average().getAsDouble();
  }

  public Double median() {
    List<Double> res = data.stream().sorted().collect(Collectors.toList());
    return res.get(res.size() / 2);
  }

  public Double percentile(int num) {
    List<Double> res = data.stream().sorted().collect(Collectors.toList());
    return res.get((int)((res.size()/100)*(100-num)));
  }

}