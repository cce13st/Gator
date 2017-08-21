package presto.android.gui.clients;

import com.google.common.collect.Sets;
import presto.android.Configs;
import presto.android.Debug;
import presto.android.Logger;
import presto.android.gui.GUIAnalysisClient;
import presto.android.gui.GUIAnalysisOutput;
import presto.android.gui.clients.energy.EnergyAnalyzer;
import presto.android.gui.clients.energy.EnergyUtils;
import presto.android.gui.clients.energy.Pair;
import presto.android.gui.clients.energy.VarUtil;
import presto.android.gui.graph.*;
import presto.android.gui.wtg.EventHandler;
import presto.android.gui.wtg.StackOperation;
import presto.android.gui.wtg.WTGAnalysisOutput;
import presto.android.gui.wtg.WTGBuilder;
import presto.android.gui.wtg.ds.WTG;
import presto.android.gui.wtg.ds.WTGEdge;
import presto.android.gui.wtg.ds.WTGNode;
import presto.android.gui.wtg.flowgraph.NLauncherNode;
import soot.SootMethod;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zero on 10/21/15.
 */
public class WTGDemoClient implements GUIAnalysisClient {
  @Override
  public void run(GUIAnalysisOutput output){
    VarUtil.v().guiOutput = output;
    WTGBuilder wtgBuilder = new WTGBuilder();
    wtgBuilder.build(output);
    WTGAnalysisOutput wtgAO = new WTGAnalysisOutput(output, wtgBuilder);
    WTG wtg = wtgAO.getWTG();

    Collection<WTGEdge> edges = wtg.getEdges();
    Collection<WTGNode> nodes = wtg.getNodes();


    Logger.verb("DEMO", "Application: " + Configs.benchmarkName);
    Logger.verb("DEMO", "Launcher Node: " + wtg.getLauncherNode());

    for (WTGNode n : nodes){
      Logger.verb("DEMO", "Current Node: " + n.getWindow().toString());
      Logger.verb("DEMO", "Number of in edges: "
              + Integer.toString(n.getInEdges().size()));
      Logger.verb("DEMO", "Number of out edges: "
              + Integer.toString(n.getOutEdges().size()) + "\n");
    }

    for (WTGEdge e : edges){
      Logger.verb("DEMO", "Current Edge ID: " + e.hashCode());
      Logger.verb("DEMO", "Source Window: "
              + e.getSourceNode().getWindow().toString());
      Logger.verb("DEMO", "Target Window: "
              + e.getTargetNode().getWindow().toString());
      Logger.verb("DEMO", "EventType: " + e.getEventType().toString());
      Logger.verb("DEMO", "Event Callbacks: ");
      for (SootMethod m : e.getEventHandlers()) {
        Logger.verb("DEMO", "\t"+ m.toString());
      }
      Logger.verb("DEMO", "Lifecycle Callbacks: ");
      for (EventHandler eh : e.getCallbacks()) {
        Logger.verb("DEMO", "\t"+ eh.getEventHandler().toString());
      }
      Logger.verb("DEMO", "Stack Operations: ");
      for (StackOperation s : e.getStackOps()){
        if (s.isPushOp())
          Logger.verb("DEMO", "PUSH " + s.getWindow().toString());
        else
          Logger.verb("DEMO", "POP " + s.getWindow().toString());
      }
    }
  }
}
