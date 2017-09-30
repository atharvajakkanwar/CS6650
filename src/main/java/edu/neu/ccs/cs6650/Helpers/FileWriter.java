package edu.neu.ccs.cs6650.Helpers;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.neu.ccs.cs6650.Client.RequestResult;

/**
 * Created by Atharva Jakkanwar on 30-Sep-17.
 */
public class FileWriter {

  private String name;

  public FileWriter(String name) {
    this.name = name;
  }

  public boolean write(CopyOnWriteArrayList<RequestResult> results) {
    try {
      java.io.FileWriter writer = new java.io.FileWriter(name);
      for(RequestResult r: results) {
        writer.write(r.getStartTime().toString()+","+Double.toString(r.getLatency()));
        writer.write("\n");
      }
      writer.close();
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

}
