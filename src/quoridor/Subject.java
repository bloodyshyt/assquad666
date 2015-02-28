package quoridor;

public class Subject implements Comparable<Subject>{
	
	float[] weights;
	int fitnessFunction;
	
	public Subject(float[] weights) {
		this.weights = weights;
		fitnessFunction  = 0;
	}
	
	public Subject clone() {
		return new Subject(weights);
	}
	
	@Override
	public int compareTo(Subject o) {
		if(this.fitnessFunction > o.fitnessFunction) return 1;
		if(this.fitnessFunction < o.fitnessFunction) return -1;
		return 0;
	}

}
