package edu.neu.ccs.cs6650.Client;

public class RequestResult {
  private Long startTime;
  private double latency;

  public RequestResult(Long startTime, double latency) {
    this.startTime = startTime;
    this.latency = latency;
  }

  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  public double getLatency() {
    return latency;
  }

  public void setLatency(double latency) {
    this.latency = latency;
  }

  @Override
  public String toString() {
    return startTime + ", " + latency + " \n";
  }
}