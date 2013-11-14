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
package com.oculusinfo.ml.search;

import java.util.Random;

import com.oculusinfo.ml.search.stochastic.SimulatedAnnealing;

public class TestAnnealer {

	public static class TestSolution implements Solution {
		public double vertex[] = new double[3];
		
		public TestSolution(double vals[]) {
			vertex = vals.clone();
		}
		public TestSolution(double a, double b, double c) {
			vertex[0] = a;
			vertex[1] = b;
			vertex[2] = c;
 		}
		
		@Override
		public Solution neighbor(double temp) {
			Random rnd = new Random();
			double tmp[] = vertex.clone();
			
			tmp[0] = tmp[0] + Math.pow(-1, rnd.nextInt(2)) * rnd.nextDouble() * temp;
			tmp[1] = tmp[1] + Math.pow(-1, rnd.nextInt(2)) * rnd.nextDouble() * temp;
			
			return new TestSolution(tmp);
		}
		
		@Override
		public String toString() {
			return "a: " + vertex[0] + ", b: " + + vertex[1] + ", c: " + + vertex[2];
		}
		
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SimulatedAnnealing annealer = new SimulatedAnnealing(new ObjectiveFunction() {

//			static final double A=2;
//			static final double B=3;
			
			@Override
			public double score(Solution solution) {
				double error;
				TestSolution s = (TestSolution)solution;
				error = Math.pow(s.vertex[0], 2) + Math.pow(s.vertex[1], 2);
//				error=Math.pow(A-Math.sin(s.vertex[0])*Math.exp(s.vertex[1])*s.vertex[2], 2);
//				error+=Math.pow(B-Math.exp(s.vertex[0])*Math.sin(s.vertex[1]), 2);
				System.out.println(error);
				return error;
			}
			
		});
		
		try {
			Random rnd = new Random();
			annealer.setInitialSolution(new TestSolution(rnd.nextDouble(), rnd.nextDouble(), rnd.nextDouble()));
			annealer.setInitialTemperature(20);
			annealer.setCoolingRate(0.001);
			annealer.setMaxIterations(10000);
			
			Solution best = annealer.search(true);
			System.out.println(best);
			
			// print out "distance" to solution to build a bit of confidence in the result
			System.out.println("Distance " + annealer.getObjectiveFunction().score(best));
			
		} catch (SearchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
