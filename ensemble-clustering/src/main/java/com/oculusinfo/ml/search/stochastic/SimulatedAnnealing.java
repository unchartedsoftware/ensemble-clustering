/**
 * Copyright (c) 2013 Oculus Info Inc.
 * http://www.oculusinfo.com/
 *
 * Released under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.oculusinfo.ml.search.stochastic;

import java.util.Random;

import com.oculusinfo.ml.search.ObjectiveFunction;
import com.oculusinfo.ml.search.SearchException;
import com.oculusinfo.ml.search.Solution;

/***
 * Simulated Annealing for stochastically searching for optimum solution given an objective function.
 * 
 * A global optimum is not guaranteed and the solution may converge on a local optimum, however the stochastic nature
 * can avoid becoming "stuck" in a local optimum by accepting inferior solutions with a transition probability.
 * 
 * @author slangevin
 *
 */
public class SimulatedAnnealing {
	// initial temperature
	protected double initTemp = 20;
	
	// current temperature
	protected double curTemp = 20;
	
	// minimum temperature
	protected double minTemp = 1e-10;
	
	// change in temperature at every run
	protected double coolingRate = 0.005;
	
	// maximum search iterations to perform
	protected int maxIterations = 10000;
	
	// score of best solution found
	protected double score = 0;
	
	// best solution found
	protected Solution solution;
	
	// Objective function used to calculate score of a solution
	protected ObjectiveFunction objfunc;
	
	// Random number generated for accepting new solutions
	protected Random rnd = new Random();
	
	public SimulatedAnnealing(ObjectiveFunction func) {
		setObjectiveFunction(func);
	}
	
	private boolean acceptNewSolution(double newScore, double curScore) {
		double delta = curScore - newScore;
		
		// new solution is better, accept it
		if (newScore < curScore) return true;
		
		// solution is inferior, randomly determine whether to move to the new solution
		double p = Math.exp(delta / curTemp);
		return ( rnd.nextDouble() < p );
	}
	
	private void coolTemperature(int step) {
		// Alternate: reduce temperature using a linear schedule  
//		curTemp -= curTemp * coolingRate;
		
		// Annealing scheme cools temperature as a function of time and cooling rate
		curTemp = initTemp * Math.exp((-1.0) * coolingRate * step);
	}
	
	public Solution search() throws SearchException {
		return search(false);
	}
	
	public Solution search(boolean outputProgress) throws SearchException {
		if (solution == null) throw new SearchException("You must specifiy an initial solution.");
		
		// start with the intial solution
		Solution curSolution = solution;
		
		// calculate the initial solution score and init our best score 
		score = objfunc.score(curSolution);
		
		if (outputProgress) printSolution("Initial Solution", solution, score);
		
		// set the current temperature to the initial temperature
		curTemp = initTemp;
		
		// set the current score to the initial solution score
		double curScore = score;
		int i = 0;
		
		// iteratively search for solution 
		while (i < maxIterations && curTemp > minTemp) {
			// find a neighbor of the  current solution
			Solution newSolution = curSolution.neighbor(curTemp);
			
			// calculate the score of the new solution
			double newScore = objfunc.score(newSolution);
	
			// move to the new solution if it is accepted
			if ( acceptNewSolution(newScore, curScore) ) {
				curSolution = newSolution;
				curScore = newScore;
				if (outputProgress) printSolution("Accepting New Current Solution", curSolution, curScore);
			}
			
			// if the current solution is better then the best, switch to it
			if (curScore < score) {
				score = curScore;
				solution = curSolution; 
				if (outputProgress) printSolution("* New Best Solution Found * ", solution, score);
			}
			
			// reduce the temperature
			coolTemperature(i);
			
			// move to next iteration
			i++;
		}
		return solution;
	}
	
	private void printSolution(String msg, Solution sln, double s) {
		System.out.println("--------------------");
		System.out.println(msg);
		System.out.println("Score: " + s);
		System.out.println(sln.toString());
		System.out.println("--------------------");
	}
	
	public ObjectiveFunction getObjectiveFunction() {
		return this.objfunc;
	}
	
	public void setObjectiveFunction(ObjectiveFunction func) {
		this.objfunc = func;
	}
	
	public void setInitialSolution(Solution initial) {
		this.solution = initial;
	}
	
	public void setCoolingRate(double rate) {
		this.coolingRate = rate;
	}
	
	public void setMaxIterations(int max) {
		this.maxIterations = max;
	}
	
	public void setInitialTemperature(double temp) {
		this.initTemp = temp;
	}
	
	public void setMinTemperature(double min) {
		this.minTemp = min;
	}
}
