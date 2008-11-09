package org.delysid.freedots;

import java.util.ArrayList;
import java.util.List;
import org.delysid.musicxml.Measure;
import org.delysid.musicxml.MusicXML;
import org.delysid.musicxml.Part;

public class Transcriber {
  MusicXML score;
  Options options;
  String textStore;

  public Transcriber(MusicXML score, Options options) {
    this.score = score;
    this.options = options;
    clear();
    if (score != null) { transcribe(); }
  }
  private void clear() {
    textStore = "";
  }
  void transcribe() {
    for (Part part:score.parts()) {
      for (System system:getSystems(part)) {
        for (Measure measure:system.measures()) {
        }
      }
    }
  }
  public String toString() {
    return textStore;
  }
  class System {
    int staffCount;
    List<Measure> measures = new ArrayList<Measure>();
    public System(Measure firstMeasure) {
      this.staffCount = firstMeasure.getStaffCount();
      add(firstMeasure);
    }
    public void add(Measure measure) { measures.add(measure); }
    public List<Measure> measures() { return measures; }
    public int getStaffCount() { return staffCount; }
  }
  List<System> getSystems(Part part) {
    List<System> systems = new ArrayList<System>();
    System currentSystem = null;
    for (Measure measure:part.measures()) {
      if (currentSystem == null) {
        systems.add(currentSystem = new System(measure));
      } else {
        if (measure.getStaffCount() == currentSystem.getStaffCount()) {
          currentSystem.add(measure);
        } else {
          systems.add(currentSystem = new System(measure));
        }
      }
    }
    return systems;
  }
}
