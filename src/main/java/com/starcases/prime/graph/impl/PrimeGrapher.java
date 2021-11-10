package com.starcases.prime.graph.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.gephi.appearance.api.AppearanceController;
import org.gephi.appearance.api.AppearanceModel;
import org.gephi.appearance.api.Function;
import org.gephi.appearance.plugin.RankingLabelSizeTransformer;
import org.gephi.appearance.plugin.RankingNodeSizeTransformer;
import org.gephi.graph.api.Column;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.Node;
import org.gephi.layout.plugin.AutoLayout;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.layout.plugin.forceAtlas.ForceAtlasLayout;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.GraphDistance;
import org.openide.util.Lookup;

import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.extern.java.Log;
import picocli.CommandLine.Command;

// 
// start 1
//       2
//       3 <-  2 + 1
//       5 <-  3 + 2
//       7 <-  5 + 2
//       11 <- 7+3+1; 5+3+2+1
@Log
public class PrimeGrapher 
{
	private static Comparator<Node> nodeComparator = (Node o1, Node o2) -> (Integer.decode((String)o1.getId())).compareTo( Integer.decode((String)o2.getId()));

	private ProjectController pc;

	private Workspace workspace; 
	private PrimeSourceIntfc ps;
	private GraphModel primeModel;
	private DirectedGraph primeGraph; 
	private AppearanceController appearanceController; 
    private AppearanceModel appearanceModel; 
	
	public PrimeGrapher(PrimeSourceIntfc ps, int maxCount)
	{
		this.ps = ps;
		this.ps.init();
		
		pc = org.openide.util.Lookup.getDefault().lookup(ProjectController.class);
		pc.newProject();
		workspace = pc.getCurrentWorkspace();
		primeModel = org.openide.util.Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace);
	
		primeGraph = primeModel.getDirectedGraph();
		this.populateData(maxCount);
		
	}
	
	public void setNodeLocations()
	{
		log.info("setNodeLocations() - start");
		primeGraph
		.getNodes()				
		.toCollection()
		.stream()
		.sorted(nodeComparator)
		.forEach(n -> 
						{
							float inDegree = primeModel.getDirectedGraph().getInDegree(n);
							float outDegree = primeModel.getDirectedGraph().getOutDegree(n);
							Integer id = Integer.decode((String)n.getId());
							
							float x = 80 - (float)Math.sin(11 * id *127);
							float y = (inDegree * 11 + (float)Math.sin(id)) + (outDegree * 11 + (float)Math.cos(id)) ;
							float z = 0;
							n.setX(x);
							n.setY(y);
							n.setZ(z);
							log.info(String.format("Prime %s  x[%f] y[%f] z[%f]  in-degree[%f] out-degree[%f]", n.getLabel(), x,y,z, inDegree, outDegree ));
						
						});		
		log.info("setNodeLocations() - exit");
}
	
	
	@Command
	public void logGraphStructure()
	{
		System.out.println("log structure");
			primeGraph
				.getNodes()				
				.toCollection()
				.stream()
				.sorted(nodeComparator)
				.forEach(n -> 
							System.out.println(String.format("Prime %s: created-from:[count(%d), %s] creates-primes:[count(%d), %s]", 
						n.getLabel(), 
						primeModel.getDirectedGraph().getInDegree(n),
						primeModel.getDirectedGraph().getPredecessors(n).toCollection().stream().map(nn -> nn.getLabel()).collect(Collectors.joining(",")),
						primeModel.getDirectedGraph().getOutDegree(n),
						primeModel.getDirectedGraph().getSuccessors(n).toCollection().stream().map(nn -> nn.getLabel()).collect(Collectors.joining(",")))));		
	}
	
	public void logNodeStructure()
	{
		try
		{
			int i = 0;
			while(true) 
			{ 
				PrimeRefIntfc ref = ps.getPrimeRef(i++);
				System.out.println(String.format("Prime %d bases %s", ref.getPrime(), ref.getIdxPrimes()));
			}
		}
		catch(Exception e)
		{}
	}
	
	final BiFunction<Integer, ArrayList<Integer>, Consumer<Integer>> reducer = (m, a)-> idx -> 
	{
		if (idx < m)
		{
			while (m > a.size())
				a.add(0);
			
			a.set(idx, a.get(idx)+1);
		}
		else 
		{ 
			this.primeReduction(idx, this.reducer.apply(m, a)); 
		}
	};
	
	 
	public void logReduced(int maxReduce)
	{
		int i = 0;
		while(true) 
		{ 
			PrimeRefIntfc pr;				
			try
			{
				ArrayList<Integer> ret = new ArrayList<>();
				pr = ps.getPrimeRef(i);
				primeReduction(i++, reducer.apply(maxReduce, ret));
				int [] tmpI = {0};			
				System.out.println(String.format("Prime [%d] %s", pr.getPrime(), 
						ret.stream().map(idx -> String.format("base-%d-count:[%d]", ps.getPrime(tmpI[0]++), idx)).collect(Collectors.joining(", "))));
			}
			catch(Exception e)
			{
				break;
			}				
		}	
	}
	
	private void primeReduction(Integer idx, Consumer<Integer> reducer)
	{	
		ps.getPrimeRef(idx)
		.getPrimeBaseIdxs()
		.forEach(
				bs -> 
					bs
					.stream()
					.boxed()
					.forEach(reducer));
	}
	
	private void populateData(int maxCount)
	{
		// Start setting up the actual graph/data generations
		PrimeNodeGenerator primeNodeGenerator = new PrimeNodeGenerator(ps, primeModel, primeGraph);
		primeNodeGenerator.begin();
		
		while (primeNodeGenerator.nextEvents());		
		
		primeNodeGenerator.end();		
	}
	
	public void viewDefault()
	{
        GraphDistance distance = new GraphDistance();
        distance.setDirected(true);
        distance.execute(primeModel);
        
		log.info("Enter viewDefault()");
		AutoLayout autoLayout = new AutoLayout(1, TimeUnit.MINUTES);
        autoLayout.setGraphModel(primeModel);
        YifanHuLayout firstLayout = new YifanHuLayout(null, new StepDisplacement(1f));
        ForceAtlasLayout secondLayout = new ForceAtlasLayout(null);
        AutoLayout.DynamicProperty adjustBySizeProperty = AutoLayout.createDynamicProperty("forceAtlas.adjustSizes.name", Boolean.TRUE, 0.1f);//True after 10% of layout time
        AutoLayout.DynamicProperty repulsionProperty = AutoLayout.createDynamicProperty("forceAtlas.repulsionStrength.name", 500., 0f);//500 for the complete period
        autoLayout.addLayout(firstLayout, 0.5f);
        autoLayout.addLayout(secondLayout, 0.5f, new AutoLayout.DynamicProperty[]{adjustBySizeProperty, repulsionProperty});
        autoLayout.execute();
    	appearanceController = Lookup.getDefault().lookup(AppearanceController.class);
        appearanceModel = appearanceController.getModel();
       
        //Rank size by centrality
       // Column centralityColumn = primeModel.getNodeTable().getColumn(GraphDistance.BETWEENNESS);
      //  Function centralityRanking = appearanceModel.getNodeFunction(primeGraph, centralityColumn, RankingNodeSizeTransformer.class);
      //  RankingNodeSizeTransformer centralityTransformer = (RankingNodeSizeTransformer) centralityRanking.getTransformer();
      //  centralityTransformer.setMinSize(3);
      //  centralityTransformer.setMaxSize(10);
      //  appearanceController.transform(centralityRanking);

        //Rank label size - set a multiplier size
//        Function centralityRanking2 = appearanceModel.getNodeFunction(primeGraph, centralityColumn, RankingLabelSizeTransformer.class);
 //       RankingLabelSizeTransformer labelSizeTransformer = (RankingLabelSizeTransformer) centralityRanking2.getTransformer();
   //     labelSizeTransformer.setMinSize(1);
     //   labelSizeTransformer.setMaxSize(3);
   //     appearanceController.transform(centralityRanking2);
        
        GraphView gv = primeGraph.getView();
        primeModel.setVisibleView(gv);
        
        try
		{
			do
			{
				System.out.println("press key to exit");
			} while (System.in.read() != -1);
		}
		catch(Exception e)
		{}	        
        log.info("Exit viewDefault()");
	}
}
