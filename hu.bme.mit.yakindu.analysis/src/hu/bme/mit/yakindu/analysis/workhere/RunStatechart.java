package hu.bme.mit.yakindu.analysis.workhere;

import java.io.IOException;
import java.util.Scanner;
import hu.bme.mit.yakindu.analysis.RuntimeService;
import hu.bme.mit.yakindu.analysis.TimerService;
import hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;
import hu.bme.mit.yakindu.analysis.example.IExampleStatemachine;

public class RunStatechart {

	public static void main(String[] args) throws IOException {
		ExampleStatemachine s = new ExampleStatemachine();
		s.setTimer(new TimerService());
		RuntimeService.getInstance().registerStatemachine(s, 200);
		s.init();
		s.enter();
		s.runCycle();

		Scanner scanner = new Scanner(System.in);
		while(true) {
			switch(scanner.nextLine()) {
				case "start": s.raiseStart(); break;
				case "white": s.raiseWhite(); break;
				case "gray": s.raiseGray(); break;
				case "blue": s.raiseBlue(); break;
				case "exit": System.exit(0); break;
			}
			s.runCycle();
			print(s);
		}
	}
	
	public static void print(IExampleStatemachine s) {
		System.out.println(s.getSCInterface().getWhiteTime());
		System.out.println(s.getSCInterface().getGrayTime());
		System.out.println(s.getSCInterface().getBlueTime());
	}
}
