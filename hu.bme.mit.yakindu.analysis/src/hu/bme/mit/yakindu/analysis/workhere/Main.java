package hu.bme.mit.yakindu.analysis.workhere;

import java.nio.charset.Charset;
import java.util.Random;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Transition;

import hu.bme.mit.model2gml.Model2GML;
import hu.bme.mit.yakindu.analysis.modelmanager.ModelManager;

public class Main {
	@Test
	public void test() {
		main(new String[0]);
	}
	
	public static String proposedStateName(int i) {
		int offset = i;
	    String name = "";
	    while(offset >= 25) {
	    	name = name + (char)65;
	    	offset -= 25;
	    }
	    name = name + (char)(65+offset);
	    return name;
	   
	}
	
	public static void main(String[] args) {
		ModelManager manager = new ModelManager();
		Model2GML model2gml = new Model2GML();
		
		// Loading model
		EObject root = manager.loadModel("model_input/example.sct");
		
		// Reading model
		Statechart s = (Statechart) root;
		TreeIterator<EObject> iterator = s.eAllContents();
		int count = 0;
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			
			if(content instanceof State ) {
				State state = (State) content;
				EList<Transition> transitions = state.getOutgoingTransitions();		
				boolean trap = true;
				int i = 0;
				while (i < transitions.size() && trap) {
					if (transitions.get(i).getTarget().getName() != transitions.get(i).getSource().getName())
						trap = false;
					++i;
				}
				if (state.getName() == "") {
					state.setName(proposedStateName(count));
					System.out.println("State has no name, proposed state name: " + state.getName());
					count++;
					
				}
				
				System.out.println(state.getName());
				if (trap)
					System.out.println(state.getName() + " state is a trap!");

			}else if (content instanceof Transition) {
				Transition trans = (Transition) content;
				System.out.println(trans.getSource().getName() + " -> " + trans.getTarget().getName());
			}
		}
		
		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		manager.saveFile("model_output/graph.gml", content);
	}
}
