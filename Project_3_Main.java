
public class Project_3_Main 
{
	public static void main(String[] args)
	{
		Project_3_Method x = new Project_3_Method();
		Decode y = new Decode();
		int[] Instruction = {0xA1020000,0x810AFFFC,0x00831820,0x01263820,0x01224820,0x81180000,0x81510010,
				0x00624022,0x00000000,0x00000000,0x00000000,0x00000000};	
		
		x.Initialize_Main_Mem();
		x.Initialize_Reg();
		for(int i = 0; i < Instruction.length;i++)
		{
			x.IF_Stage(Instruction[i]);
			x.ID_Stage();
			x.EX_Stage();
			x.MEM_Stage();
			x.WB_Stage();
			x.Print_out_Everthing(i);
			x.Copy_Write_to_Read();
		}

	}

}
