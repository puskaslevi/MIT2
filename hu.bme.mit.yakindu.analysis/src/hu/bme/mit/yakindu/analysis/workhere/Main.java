package hu.bme.mit.yakindu.analysis.workhere;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.stext.stext.EventDefinition;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

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
	
	public static void generatePrint(List<String> list) {
		System.out.println("\tpublic static void print(IExampleStatemachine s) {");
		for (int i = 0; i < list.size(); i++) {
			System.out.println("\t\tSystem.out.println(s.getSCInterface().get" + list.get(i).substring(0, 1).toUpperCase() + list.get(i).substring(1) + "());");
		}
		System.out.println("\t}");
	}
	
	public static void generateCode(List<String> events, List<String> variables) {
		System.out.println("package hu.bme.mit.yakindu.analysis.workhere;\n");
		System.out.println("import java.io.IOException;\nimport java.util.Scanner;\nimport hu.bme.mit.yakindu.analysis.RuntimeService;");
		System.out.println("import hu.bme.mit.yakindu.analysis.TimerService;\nimport hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;\nimport hu.bme.mit.yakindu.analysis.example.IExampleStatemachine;\n");
		System.out.println("public class RunStatechart {\n");
		System.out.println("\tpublic static void main(String[] args) throws IOException {");
		System.out.println("\t\tExampleStatemachine s = new ExampleStatemachine();\n\t\ts.setTimer(new TimerService());\n\t\tRuntimeService.getInstance().registerStatemachine(s, 200);");
		System.out.println("\t\ts.init();\n\t\ts.enter();\n\t\ts.runCycle();\n");
		System.out.println("\t\tScanner scanner = new Scanner(System.in);");
		System.out.println("\t\twhile(true) {\n\t\t\tswitch(scanner.nextLine()) {");
		for (int i = 0; i < events.size(); i++) {
			System.out.println("\t\t\t\tcase " + "\"" + events.get(i) + "\": s.raise" + events.get(i).substring(0, 1).toUpperCase() + events.get(i).substring(1) + "(); break;");
		}
		System.out.println("\t\t\t\tcase \"exit\": System.exit(0); break;\n\t\t\t}");
		System.out.println("\t\t\ts.runCycle();\n\t\t\tprint(s);\n\t\t}\n\t}\n\t");
		generatePrint(variables);
		System.out.println("}");

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
		
		List<String> variableNames = new ArrayList<String>();
		List<String> eventNames = new ArrayList<String>();
		
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
			
			}else if (content instanceof EventDefinition) {
				EventDefinition event = (EventDefinition) content;
				System.out.println(event.getName());
				eventNames.add(event.getName());
			}else if (content instanceof VariableDefinition) {
				VariableDefinition variable = (VariableDefinition) content;
				System.out.println(variable.getName());
				variableNames.add(variable.getName());
			}
		}
		
		//generatePrint(variableNames);
		
		generateCode(eventNames, variableNames);
		
		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		manager.saveFile("model_output/graph.gml", content);
	}
}
