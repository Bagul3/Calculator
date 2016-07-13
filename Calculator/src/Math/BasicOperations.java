package Math;

public class BasicOperations {	
	
	private Memory memory = new Memory();
	
	public String addFunction(float number, String additionNumber){
		return String.valueOf(Float.valueOf((number + Float.valueOf(additionNumber))));
	}
	
	public String subtractionFunction(float number, String subtractionNumber){
		return String.valueOf(Float.valueOf((number - Float.valueOf(subtractionNumber))));
	}
	
	public String mutlplicationFunction(float number, String muplicationNumber){
		return String.valueOf(Float.valueOf((number * Float.valueOf(muplicationNumber))));
	}
	
	public String divisionFunction(float number, String divisionNumber){
		return String.valueOf(Float.valueOf((number / Float.valueOf(divisionNumber))));
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
