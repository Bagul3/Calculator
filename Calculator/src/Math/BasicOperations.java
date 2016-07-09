package Math;

public class BasicOperations {	
	
	private Memory memory = new Memory();
	
	public float addFunction(float number, float additionNumber){
		return (number + additionNumber);
	}
	
	public float subtractionFunction(float number, float subtractionNumber){
		return (number - subtractionNumber);
	}
	
	public float mutlplicationFunction(float number, float muplicationNumber){
		return (number * muplicationNumber);
	}
	
	public float divisionFunction(float number, float divisionNumber){
		return (number / divisionNumber);
	}
	
	public float mplusFunction(float number){
		memory.setMemory(memory.getMemory() + number);
		return memory.getMemory();
	}
	
	public float mminiusFunction(float number){
		memory.setMemory(memory.getMemory() - number);
		return memory.getMemory();
	}
	
	public float mclearFunction() {
		memory.setMemory(0);
		return memory.getMemory();
	}
	
	public float msetFunction(float number){
		memory.setMemory(number);
		return memory.getMemory();
	}
}
