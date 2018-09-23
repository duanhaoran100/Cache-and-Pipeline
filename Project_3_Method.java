
public class Project_3_Method 
{
	Decode method = new Decode();
	int[] Main_Mem = new int[1024];
	int[] Regs = new int[32];
	int header_Line = 72;	
	int IncrPC = 0x7A000;//starting address.
	String result;
	
	int IDEX_Write_Instruction, IDEX_Read_Instruction, EXMEM_Write_Instruction, EXMEM_Read_Instruction, 
	    MEMWB_Write_Instruction, MEMWB_Read_Instruction;
	
	int IFID_Write_Instruction, IFID_Write_IncrPC;//IFID_Write register variables.
	int IFID_Read_Instruction, IFID_Read_IncrPC;//IFID_Read register variables.
	
	//IDEX_Write register control signals.
	int IDEX_Write_ALUSrc,IDEX_Write_ALUOp, IDEX_Write_MemRead, 
	    IDEX_Write_MemWrite, IDEX_Write_RegWrite;
	char IDEX_Write_RegDst = 48, IDEX_Write_MemToReg = 48;
	//IDEX_Write register variables.
	int IDEX_Write_IncrPC,IDEX_Write_ReadReg1Value,IDEX_Write_ReadReg2Value, 
        IDEX_Write_WriteReg_20_16, IDEX_Write_WriteReg_15_11, IDEX_Write_Function;
	short IDEX_Write_SEOffset;
	//IDEX_Read register control signals.
	int IDEX_Read_ALUSrc,IDEX_Read_ALUOp, IDEX_Read_MemRead, 
	    IDEX_Read_MemWrite, IDEX_Read_RegWrite;
	char IDEX_Read_RegDst = 48, IDEX_Read_MemToReg = 48;
	//IDEX_Read register variables.
	int IDEX_Read_IncrPC,IDEX_Read_ReadReg1Value,IDEX_Read_ReadReg2Value, 
	    IDEX_Read_WriteReg_20_16, IDEX_Read_WriteReg_15_11, IDEX_Read_Function;
	short IDEX_Read_SEOffset;
	
	//EXMEM_Write register control signals.
	int EXMEM_Write_MemRead, EXMEM_Write_MemWrite, EXMEM_Write_RegWrite;
	char EXMEM_Write_MemToReg = 48;
	//EXMEM_Write register variables.
	int EXMEM_Write_ALUResult, EXMEM_Write_SWValue, EXMEM_Write_WriteRegNum;	
	//EXMEM_Read register control signals.
	int EXMEM_Read_MemRead, EXMEM_Read_MemWrite, EXMEM_Read_RegWrite;
	char EXMEM_Read_MemToReg = 48;
	//EXMEM_Read register variables.
	int EXMEM_Read_ALUResult, EXMEM_Read_SWValue, EXMEM_Read_WriteRegNum;
	
	//MEMWB_Write register control signals.	
	int MEMWB_Write_RegWrite;
	char MEMWB_Write_MemToReg = 48;
	//MEMWB_Write register variables.
	int MEMWB_Write_LWValue, MEMWB_Write_ALUResult, MEMWB_Write_WriteRegNum;
	//MEMWB_Read register control signals.	
	int MEMWB_Read_RegWrite;
	char MEMWB_Read_MemToReg = 48;
	//MEMWB_Read register variables.
	int MEMWB_Read_LWValue, MEMWB_Read_ALUResult, MEMWB_Read_WriteRegNum;


	public void Initialize_Main_Mem()
	{
		for(int i = 0; i < Main_Mem.length; i++)
			Main_Mem[i] = i % 256;
	}
	
	public void Initialize_Reg()
	{
		for(int i = 1; i < Regs.length; i++)
			Regs[i] = 0x100 + i;
		Regs[0] = 0;
	}
	
	public void IF_Stage(int instruction)
	{		
	    //take 1 instruction from instruction array.
		int IF_Current_Instruction = instruction;
		IncrPC += 4;//address add by 4.

		//write Instruction and IncrPC into IF/ID Register.
		IFID_Write_Instruction = IF_Current_Instruction;
		IFID_Write_IncrPC = IncrPC;
	}
	
	public void ID_Stage()
	{
		//read from IF/ID Register.
		int ID_Current_Instruction = IFID_Read_Instruction;
		int ID_IncrPC = IFID_Read_IncrPC;	

		//decode the instruction
		int Op_Code = ((1<<6) - 1 )& ID_Current_Instruction>>>26;//zero out all other bits and get the first 6 bits
		int rs = ((1<<5) - 1 )& ID_Current_Instruction>>>21;
		int rt = ((1<<5) - 1 )& ID_Current_Instruction>>>16;
		int rd = ((1<<5) - 1 )& ID_Current_Instruction>>>11;
		int funct = ((1<<6) - 1 )& ID_Current_Instruction;
		short offset = (short) ((short) ((1<<16) - 1)& ID_Current_Instruction);
		
		//get control signal from instruction.
		char ID_RegDst;
		int ID_ALUSrc;
		int ID_ALUOp;
		int ID_MemRead;
		int ID_MemWrite;
		char ID_MemToReg;
		int ID_RegWrite;
		
		//R type control signal.
		if(ID_Current_Instruction == 0)
		{
			ID_RegDst = 48;
			ID_ALUSrc = 0;
			ID_ALUOp = 0;
			ID_MemRead = 0;
			ID_MemWrite = 0;
			ID_MemToReg = 48;
			ID_RegWrite = 0;
		}
		else if(Op_Code == 0)
		{
			ID_RegDst = 49;
			ID_ALUSrc = 0;
			ID_ALUOp = 10;
			ID_MemRead = 0;
			ID_MemWrite = 0;
			ID_MemToReg = 48;
			ID_RegWrite = 1;
		}
		//lb type control signal.
		else if (Op_Code == 0x20)
		{
			ID_RegDst = 48;
			ID_ALUSrc = 1;
			ID_ALUOp = 0;
			ID_MemRead = 1;
			ID_MemWrite = 0;
			ID_MemToReg = 49;
			ID_RegWrite = 1;
		}
		//sb control signal.
		else
		{
			ID_RegDst = 120;
			ID_ALUSrc = 1;
			ID_ALUOp = 0;
			ID_MemRead = 0;
			ID_MemWrite = 1;
			ID_MemToReg = 120;
			ID_RegWrite = 0;
		}
		
		//write value into ID/EX Register.
		IDEX_Write_RegDst = ID_RegDst;
		IDEX_Write_ALUSrc = ID_ALUSrc;
		IDEX_Write_ALUOp = ID_ALUOp;
		IDEX_Write_MemRead = ID_MemRead;
		IDEX_Write_MemWrite = ID_MemWrite;
		IDEX_Write_MemToReg = ID_MemToReg;
		IDEX_Write_RegWrite = ID_RegWrite;
		IDEX_Write_IncrPC = ID_IncrPC;
		IDEX_Write_ReadReg1Value = Regs[rs];
		IDEX_Write_ReadReg2Value = Regs[rt];
		IDEX_Write_SEOffset = offset;
		IDEX_Write_WriteReg_20_16 = rt;
		IDEX_Write_WriteReg_15_11 = rd;
		IDEX_Write_Function = funct;
		
		//put MIPS instruction next stage so that heading can print out Source Instruction.
		IDEX_Write_Instruction = IFID_Read_Instruction;
	}
	
	public void EX_Stage()
	{
		//read from ID/EX Register.
		//signals.
		int EX_RegDst = IDEX_Read_RegDst;
		int EX_ALUSrc = IDEX_Read_ALUSrc;
		int EX_ALUOp = IDEX_Read_ALUOp;
		int EX_MemRead = IDEX_Read_MemRead;
		int EX_MemWrite = IDEX_Read_MemWrite;
		char EX_MemToReg = IDEX_Read_MemToReg;
		int EX_RegWrite = IDEX_Read_RegWrite;
		//variables.
		int EX_Offset = IDEX_Read_SEOffset;
		int EX_Offset_6bits = (IDEX_Read_SEOffset <<26) >>> 26;
		
		int EX_ALUResult;
		int EX_WriteRegNum;
		//add function
		if(EX_ALUOp == 10 && EX_ALUSrc == 0 && EX_Offset_6bits == 0x20)
			EX_ALUResult = IDEX_Read_ReadReg1Value + IDEX_Read_ReadReg2Value;
		//sub function
		else if(EX_ALUOp == 10 && EX_ALUSrc == 0 && EX_Offset_6bits == 0x22)
			EX_ALUResult = IDEX_Read_ReadReg1Value - IDEX_Read_ReadReg2Value;
		//lb and sb function
		else
			EX_ALUResult = IDEX_Read_ReadReg1Value + IDEX_Read_SEOffset;
			
		//add,sub,lw function
		if(EX_RegDst - '0' == 1)
			EX_WriteRegNum = IDEX_Read_WriteReg_15_11;
		//sb function
		else
			EX_WriteRegNum = IDEX_Read_WriteReg_20_16;
		int EX_SWValue = IDEX_Read_ReadReg2Value;
	
		//write value into EX/MEM Register.
		//signals.
		EXMEM_Write_MemRead = EX_MemRead;
		EXMEM_Write_MemWrite = EX_MemWrite;
		EXMEM_Write_MemToReg = EX_MemToReg;
		EXMEM_Write_RegWrite = EX_RegWrite;
		//variables.
		EXMEM_Write_ALUResult = EX_ALUResult;		
		EXMEM_Write_SWValue = EX_SWValue;
		EXMEM_Write_WriteRegNum = EX_WriteRegNum;
		
		//put MIPS instruction next stage so that heading can print out Source Instruction.
		EXMEM_Write_Instruction = IDEX_Read_Instruction;
	}
	
	public void MEM_Stage()
	{
		//read from EX/MEM Register.
		//signals.
		int MEM_MemRead = EXMEM_Read_MemRead;
		int MEM_MemWrite = EXMEM_Read_MemWrite;
		char MEM_MemToReg = EXMEM_Read_MemToReg;
		int MEM_RegWrite = EXMEM_Read_RegWrite;	
		//variables.
		int MEM_ALUResult = EXMEM_Read_ALUResult;
		int MEM_WriteRegNum = EXMEM_Read_WriteRegNum;
		int MEM_SWValue = EXMEM_Read_SWValue;
		int MEM_LWValue;
		//R type instruction
		if(MEM_MemRead == 0 && MEM_MemWrite == 0 && MEM_RegWrite == 1)
			MEM_LWValue = Main_Mem[MEM_ALUResult];
		//load instruction
		else if(MEM_MemRead == 1 && MEM_MemWrite == 0 && MEM_RegWrite == 1)
			MEM_LWValue = Main_Mem[MEM_ALUResult];
		//sw instruction
		else if(MEM_MemRead == 0 && MEM_MemWrite == 1 && MEM_RegWrite == 0)	
		{
			MEM_LWValue = Main_Mem[MEM_ALUResult];
			Main_Mem[MEM_ALUResult] = Regs[MEM_WriteRegNum];
		}
		//nop instruction
		else
			MEM_LWValue = 0;
			
		MEM_WriteRegNum = EXMEM_Read_WriteRegNum;


		//write value into MEM/WB Register.
		//signals.
		MEMWB_Write_MemToReg = MEM_MemToReg;
		MEMWB_Write_RegWrite = MEM_RegWrite;
		//variables.
		MEMWB_Write_LWValue = MEM_LWValue;
		MEMWB_Write_ALUResult = MEM_ALUResult;
		MEMWB_Write_WriteRegNum = MEM_WriteRegNum;	
		
		//put MIPS instruction next stage so that heading can print out Source Instruction.
		MEMWB_Write_Instruction = EXMEM_Read_Instruction;
	}

	public void WB_Stage()
	{
		int WB_MemToReg = MEMWB_Read_MemToReg;
		int WB_RegWrite = MEMWB_Read_RegWrite;
		
		int WB_LWValue = MEMWB_Read_LWValue;
		int WB_ALUResult = MEMWB_Read_ALUResult;
		int WB_WriteRegNum = MEMWB_Read_WriteRegNum;
		
		result = method.result(MEMWB_Read_Instruction, Regs, Main_Mem);
		//R type, put ALU result to register.
		if(WB_MemToReg - '0' == 0 && WB_RegWrite == 1)
			Regs[WB_WriteRegNum] = WB_ALUResult;
		
		//load instruction
		else if(WB_MemToReg - '0' == 1 && WB_RegWrite == 1)
			Regs[WB_WriteRegNum] = WB_LWValue;	
		

	}
	
	public void Print_out_Everthing(int i)
	{
		for(int k = 0; k < header_Line; k++)
			System.out.print("=");
		//Heading
		System.out.printf("\n%40s%1d\n", "Clock Cycle ", i+1);
		
		for(int k = 0; k < header_Line; k++)
			System.out.print("=");
		
		//IF/ID write print out.
		System.out.println("");
		System.out.println("IF/ID Write Stage: " + method.decode(IFID_Write_Instruction));
		for(int k = 0; k < header_Line; k++)
			System.out.print("-");
		System.out.println("\nInstruction = 0x" + String.format("%08X", IFID_Write_Instruction) + "    "
				+ "IncrPC = " + String.format("%05X", IFID_Write_IncrPC));
		
		//IF/ID read print out.
		System.out.println("");
		System.out.println("IF/ID Read Stage: " + method.decode(IFID_Read_Instruction));
		for(int k = 0; k < header_Line; k++)
			System.out.print("-");
		System.out.println("\nInstruction = 0x" + String.format("%08X", IFID_Read_Instruction) + "    "
				+ "IncrPC = " + String.format("%05X", IFID_Read_IncrPC));
		
		//ID/EX write print out.
		System.out.println("");
		System.out.println("ID/EX Write Stage: " + method.decode(IDEX_Write_Instruction));
		for(int k = 0; k < header_Line; k++)
			System.out.print("-");
		System.out.println("\nControl: RegDst = " + IDEX_Write_RegDst + ",   ALUSrc = " + IDEX_Write_ALUSrc + 
				",   ALUOp = " + IDEX_Write_ALUOp + ",   MemRead = " + IDEX_Write_MemRead);
		System.out.println("         MemWrite = " + IDEX_Write_MemWrite + ", MemToReg = " + IDEX_Write_MemToReg + 
				", RegWrite = " + IDEX_Write_RegWrite);
		
		System.out.println("Value: IncrPC = " + String.format("%05X", IDEX_Write_IncrPC) + ",      ReadReg1Value = " 
		+ String.format("%03X", IDEX_Write_ReadReg1Value) + ", ReadReg2Value = " + String.format("%03X", IDEX_Write_ReadReg2Value));
		System.out.println("       SEOffset = " + String.format("%08X", (int) IDEX_Write_SEOffset) + ", WriteReg_20_16 = " + IDEX_Write_WriteReg_20_16 + 
				",  WriteReg_15_11 = " + IDEX_Write_WriteReg_15_11);	
		System.out.println("       Function = " + String.format("%02X", IDEX_Write_Function));	
		
		//ID/EX read print out.
		System.out.println("");
		System.out.println("ID/EX Read Stage: " + method.decode(IDEX_Read_Instruction));
		for(int k = 0; k < header_Line; k++)
			System.out.print("-");
		System.out.println("\nControl: RegDst = " + IDEX_Read_RegDst + ",   ALUSrc = " + IDEX_Read_ALUSrc + 
				", ALUOp = " + IDEX_Read_ALUOp + ",    MemRead = " + IDEX_Read_MemRead);
		System.out.println("         MemWrite = " + IDEX_Read_MemWrite + ", MemToReg = " + IDEX_Read_MemToReg + 
				", RegWrite = " + IDEX_Read_RegWrite);
		
		System.out.println("Value: IncrPC = " + String.format("%05X", IDEX_Read_IncrPC) + ",       ReadReg1Value = " 
		+ String.format("%03X", IDEX_Read_ReadReg1Value) + ", ReadReg2Value = " + String.format("%03X", IDEX_Read_ReadReg2Value));
		System.out.println("       SEOffset = " + String.format("%08X", (int) IDEX_Read_SEOffset) + ",  WriteReg_20_16 = " + IDEX_Read_WriteReg_20_16 + 
				",  WriteReg_15_11 = " + IDEX_Read_WriteReg_15_11);	
		System.out.println("       Function = " + String.format("%02X", IDEX_Read_Function));	
		
		//EX/MEM write print out.
		System.out.println("");
		System.out.println("EX/MEM Write Stage: " + method.decode(EXMEM_Write_Instruction));
		for(int k = 0; k < header_Line; k++)
			System.out.print("-");
		System.out.println("\nControl: MemRead = " + EXMEM_Write_MemRead + ", MemWrite = " + EXMEM_Write_MemWrite + 
				", MemToReg = " + EXMEM_Write_MemToReg + ", RegWrite = " + EXMEM_Write_RegWrite); 
		
		System.out.println("Value: ALUResult = " + String.format("%03X", EXMEM_Write_ALUResult) + ", SWValue = " + String.format("%03X", EXMEM_Write_SWValue) + 
				", WriteRegNum = " + EXMEM_Write_WriteRegNum);
		
		//EX/MEM Read print out.
		System.out.println("");
		System.out.println("EX/MEM Read Stage: " + method.decode(EXMEM_Read_Instruction));
		for(int k = 0; k < header_Line; k++)
			System.out.print("-");
		System.out.println("\nControl: MemRead = " + EXMEM_Read_MemRead + ", MemWrite = " + EXMEM_Read_MemWrite + 
				", MemToReg = " + EXMEM_Read_MemToReg + ", RegWrite = " + EXMEM_Read_RegWrite); 

		System.out.println("Value: ALUResult = " + String.format("%03X", EXMEM_Read_ALUResult) + ", SWValue = " + String.format("%03X", EXMEM_Read_SWValue) + 
				", WriteRegNum = " + EXMEM_Read_WriteRegNum);
		
		//MEM/WB write print out.
		System.out.println("");
		System.out.println("MEM/WB Write Stage: " + method.decode(MEMWB_Write_Instruction));
		for(int k = 0; k < header_Line; k++)
			System.out.print("-");
		System.out.println("\nControl: MemToReg = " + MEMWB_Write_MemToReg + ", RegWrite = " + MEMWB_Write_RegWrite);

		System.out.println("Value: ALUResult = " + String.format("%03X", MEMWB_Write_ALUResult) + ", LWValue = " + String.format("%02X", MEMWB_Write_LWValue) + 
				", WriteRegNum = " + MEMWB_Write_WriteRegNum);
		
		//MEM/WB read print out.
		System.out.println("");
		System.out.println("MEM/WB Read Stage: " + method.decode(MEMWB_Read_Instruction));
		for(int k = 0; k < header_Line; k++)
			System.out.print("-");
		System.out.println("\nControl: MemToReg = " + MEMWB_Read_MemToReg + ", RegWrite = " + MEMWB_Read_RegWrite);

		System.out.println("Value: ALUResult = " + String.format("%03X", MEMWB_Read_ALUResult) + ", LWValue = " + String.format("%02X", MEMWB_Read_LWValue) + 
				", WriteRegNum = " + MEMWB_Read_WriteRegNum);
		
		for(int k = 0; k < header_Line; k++)
			System.out.print("-");
		System.out.println("");
		System.out.println(result);
		
		for(int k = 0; k < header_Line; k++)
			System.out.print("=");
		System.out.printf("\n%40s\n", "Registers ");
		for(int k = 0; k < header_Line; k++)
			System.out.print("-");
		System.out.print("\n");
		for(int k = 0; k < Regs.length; k++)
		{
			if(k % 7 == 0 && k != 0)
				System.out.print("\n");
			System.out.print("$" + String.format("%02d", k) + " = " + String.format("%03X", Regs[k]) + " ");
		}
		System.out.print("\n");

	}
	
	public void Copy_Write_to_Read()
	{
		//copy the value and put into IF/ID read side.
		IFID_Read_Instruction = IFID_Write_Instruction;
		IFID_Read_IncrPC = IFID_Write_IncrPC;
		
		//copy the value and put into ID/EX read side.
		IDEX_Read_RegDst = IDEX_Write_RegDst;
		IDEX_Read_ALUSrc = IDEX_Write_ALUSrc;
		IDEX_Read_ALUOp = IDEX_Write_ALUOp;
		IDEX_Read_MemRead = IDEX_Write_MemRead;
		IDEX_Read_MemWrite = IDEX_Write_MemWrite;
		IDEX_Read_MemToReg = IDEX_Write_MemToReg;
		IDEX_Read_RegWrite = IDEX_Write_RegWrite;
		IDEX_Read_IncrPC = IDEX_Write_IncrPC;
		IDEX_Read_ReadReg1Value = IDEX_Write_ReadReg1Value;
		IDEX_Read_ReadReg2Value = IDEX_Write_ReadReg2Value;
		IDEX_Read_SEOffset = IDEX_Write_SEOffset;
		IDEX_Read_WriteReg_20_16 = IDEX_Write_WriteReg_20_16;
		IDEX_Read_WriteReg_15_11 = IDEX_Write_WriteReg_15_11;
		IDEX_Read_Function = IDEX_Write_Function;
		
		IDEX_Read_Instruction = IDEX_Write_Instruction;
		
		//copy the value and put into EX/MEM read side.
		EXMEM_Read_MemRead = EXMEM_Write_MemRead;
		EXMEM_Read_MemWrite = EXMEM_Write_MemWrite;
		EXMEM_Read_MemToReg = EXMEM_Write_MemToReg;
		EXMEM_Read_RegWrite = EXMEM_Write_RegWrite;
		EXMEM_Read_ALUResult = EXMEM_Write_ALUResult;		
		EXMEM_Read_SWValue = EXMEM_Write_SWValue;
		EXMEM_Read_WriteRegNum = EXMEM_Write_WriteRegNum;
		
		EXMEM_Read_Instruction = EXMEM_Write_Instruction;
		
		//copy the value and put into MEM/WB read side.
		MEMWB_Read_MemToReg = MEMWB_Write_MemToReg;
		MEMWB_Read_RegWrite = MEMWB_Write_RegWrite;
		MEMWB_Read_LWValue = MEMWB_Write_LWValue;
		MEMWB_Read_ALUResult = MEMWB_Write_ALUResult;
		MEMWB_Read_WriteRegNum = MEMWB_Write_WriteRegNum;	
		
		MEMWB_Read_Instruction = MEMWB_Write_Instruction;
	}

}
