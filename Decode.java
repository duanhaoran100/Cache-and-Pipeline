
public class Decode 
{
	//using first 6 bits to decide which method to use
	public String decode(int Instru)
	{
		if(Instru == 0)
			return "No Instruction";
		int Op_Code = ((1<<6) - 1 )& Instru>>>26;//zero out all other bits and get the first 6 bits
		
		if(Op_Code == 0)//add, sub, and, or,slt
			return R_Format(Instru);
		else //lb and sb
			return I_Format(Instru, Op_Code);
	}

	public String R_Format(int Instru)
	{
		//decode the instruction
		int rs = ((1<<5) - 1 )& Instru>>>21;
		int rt = ((1<<5) - 1 )& Instru>>>16;
		int rd = ((1<<5) - 1 )& Instru>>>11;
		int funct = ((1<<6) - 1 )& Instru;
		
		//print out
		if(funct == 0x20)
			return "(add, $" + rd + ", $" + rs + ", $" + rt + ")";	
		else
			return "(sub, $" + rd + ", $" + rs + ", $" + rt + ")";	
	}
	
	public String I_Format(int Instru, int Op_Code)
	{
		int rs = ((1<<5) - 1 )& Instru>>>21;
		int rt = ((1<<5) - 1 )& Instru>>>16;
		short offset = (short) ((short) ((1<<16) - 1)& Instru);
		
		if(Op_Code == 0x20)
			return "(lb, $" + rt + ", " + offset + "($" + rs + "))";	
		else
			return "(sb, $" + rt + ", " + offset + "($" + rs + "))";	

	}
	
	public String result(int Instru, int Regs[], int Main_Mem[])
	{
		//decode the instruction
		int Op_Code = ((1<<6) - 1 )& Instru>>>26;
		int rs = ((1<<5) - 1 )& Instru>>>21;
		int rt = ((1<<5) - 1 )& Instru>>>16;
		int rd = ((1<<5) - 1 )& Instru>>>11;
		int funct = ((1<<6) - 1 )& Instru;
		short offset = (short) (((1<<16) - 1)& Instru);
		
		//print out add and sub result
		if(Instru == 0)
			return "Instruction Result: No Instruction";
		if(Op_Code == 0 && funct == 0x20)
			return "Instruction Result: $" + rd + " is set to a new value of " + String.format("%03X", Regs[rs] + Regs[rt]);
		else if(Op_Code == 0 && funct == 0x22)
			return "Instruction Result: $" + rd + " is set to a new value of " + String.format("%03X", Regs[rs] - Regs[rt]);
		//print out lb result
		else if (Op_Code == 0x20)
			return "Instruction Result: $" + rt + " is set to a new value of " + 
			String.format("%02X", Main_Mem[Regs[rs] + offset]) + " from Memory address \n                    of " 
			+ String.format("%03X", Regs[rs] + offset);
		else
			return "Instruction Result: Store Byte has No register written";
	}

}
