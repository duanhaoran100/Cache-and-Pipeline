import java.util.Scanner;

public class Project_2_Method 
{
	private int[] Memory = new int[0x800];
	private int value = 0;//main memory value.
	private int slot = 16;//total slot number.
	private int block_size = 16;
	private int offset_bit = (int) (Math.log(block_size)/Math.log(2));
	private int Slot_bit = (int) (Math.log(slot)/Math.log(2));
	private int[][] Cache = new int[slot][19];
	private int[] Dirty_Bit = new int[slot];
	private int[] Cache_Address = new int[slot];//store address of each slot.
	private int Slot_Number;
	private int Tag_Number;
	private int Offset_Number;
	private String command;
	private int User_Input_Address;
	private int User_Input_Value;
	
	//put values in memory.
	public void Initialize_Memory()
	{
		for(int i = 0; i < Memory.length; i++)
		{
			if(value == 0xFF + 1)
				value = 0;
			Memory[i] = value;
			value++;
		}
	}
	
	//put slot number in the first column
	//other columns are all 0.
	public void Initialize_Cache()
	{
		for(int i = 0; i < Cache.length; i++)
		{
			for(int j = 0; j < Cache[0].length;j++)
			{
				if(j == 0)
					Cache[i][j] = i;
				else
				Cache[i][j] = 0;
			}		
		}
	}

	//menu of inputs
	public void Menu()
	{
		Scanner String_input = new Scanner(System.in);
		Scanner Integer_input = new Scanner(System.in);
		System.out.println("(R)ead,(W)rite, or (D)isplay Cache?");
		command = String_input.nextLine();	

		//E will exit the program
		while(!command.equals("E")) 
		{
			//"R"-read
			if(command.equals("R"))
		    {
		    	System.out.print("Please enter the adress:");
		    	User_Input_Address = Integer_input.nextInt(16);
		    	Read();
		    }
			//"W"-Write
			else if (command.equals("W"))
			{
		    	System.out.print("Please enter the adress:");
		    	User_Input_Address = Integer_input.nextInt(16);
		    	System.out.print("Please enter the Value:");
		    	User_Input_Value = Integer_input.nextInt(16);
		    	Write();
				System.out.println("Value " + String.format("%03X", User_Input_Value) + " has been written to address " + 
	                       String.format("%03X", User_Input_Address));	
			}
			//"D"-Display
			else if (command.equals("D"))
				display();
			
			System.out.println("\n(R)ead,(W)rite, or (D)isplay Cache?");
			command = String_input.nextLine();
		}
	
	}
	
	public void display()
	{
		for(int k = 0; k < 67; k++)
			System.out.print("=");
		
		System.out.printf("\n%6s%6s%4s%25s\n","Slot", "Valid", "Tag", "Data");
		
		for(int k = 0; k < 67; k++)
			System.out.print("=");
		
		System.out.println("");
		for(int i = 0; i < Cache.length; i++)
		{
			for(int j = 0; j < Cache[0].length;j++)
			{
				if(j < 3)
					System.out.printf("%5s", String.format("%01X", Cache[i][j]));

				else
				{
					if(j == 3)
						System.out.print("   ");
					if(Cache[i][1] == 0)
						System.out.printf("%3s", Cache[i][j]);
					else
						System.out.printf("%3s", String.format("%02X", Cache[i][j]));
				}
					
			}
			System.out.println("");
		}
		
		for(int k = 0; k < 67; k++)
			System.out.print("=");
	}
	
	public void Read()
	{
		//take slot number from input address.
		Slot_Number = ((1<<Slot_bit)-1)&(User_Input_Address>>>Slot_bit);
		//take tag number from input address.
		Tag_Number = ((1<<Slot_bit)-1)&(User_Input_Address>>>(Slot_bit + offset_bit));
		//take offset number from input address.
		Offset_Number = ((1<<offset_bit)-1)&(User_Input_Address);
		
		//if valid bit is 0 or valid bit is 0 but tag does not tie, then we need to check if there is a any value changed
		//if there is, copy the value to memory and then bring the new block in.
		if(Cache[Slot_Number][1] == 0 || (Cache[Slot_Number][1] == 1 && Cache[Slot_Number][2] != Tag_Number))
		{
			//if dirty bit is 1, copy the whole block into 
			if(Dirty_Bit[Slot_Number] == 1)
			{
				for(int i = 3; i < 19; i++)
					Memory[Cache_Address[Slot_Number] + i - 3] = Cache[Slot_Number][i];
				//put dirty bit back to 0.
				Dirty_Bit[Slot_Number] = 0;
			}

			//bring the whole block from memory to cache.
			for(int i = 3; i < 19; i++)
				Cache[Slot_Number][i] = Memory[((User_Input_Address>>>Slot_bit)<<Slot_bit) + i - 3];
			Cache[Slot_Number][1] = 1;//set valid bit to 1.
			Cache[Slot_Number][2] = Tag_Number;//set tag number.
			
			System.out.println("Value in " + String.format("%03X", User_Input_Address) + " is " + 
			                   String.format("%02X", Cache[Slot_Number][Offset_Number + 3]) +". (Cache Miss)");
		}
		//Cache hit
		else
			System.out.println("Value in " + String.format("%03X", User_Input_Address) + " is " + 
		                       String.format("%02X", Cache[Slot_Number][Offset_Number + 3]) +". (Cache Hit)");
		//Store the current address of the slot without offset.
		Cache_Address[Slot_Number] = (User_Input_Address>>>Slot_bit)<<Slot_bit;
	}
	
	public void Write()
	{
		//take slot number from input address.
		Slot_Number = ((1<<Slot_bit)-1)&(User_Input_Address>>>Slot_bit);
		//take tag number from input address.
		Tag_Number = ((1<<Slot_bit)-1)&(User_Input_Address>>>(Slot_bit + offset_bit));
		//take offset number from input address.
		Offset_Number = ((1<<offset_bit)-1)&(User_Input_Address);
		
		//if valid bit is 0 or valid bit is 0 but tag does not tie, then we need to check if there is a any value changed
		//if there is, copy the value to memory and then bring the new block in.
		if(Cache[Slot_Number][1] == 0 || (Cache[Slot_Number][1] == 1 && Cache[Slot_Number][2] != Tag_Number))
		{
			//if dirty bit is 1, copy the whole block into 
			if(Dirty_Bit[Slot_Number] == 1)
			{
				for(int i = 3; i < 19; i++)
					Memory[Cache_Address[Slot_Number] + i - 3] = Cache[Slot_Number][i];
				//put dirty bit back to 0.
				Dirty_Bit[Slot_Number] = 0;
			}

			//bring the whole block from memory to cache.
			for(int i = 3; i < 19; i++)
				Cache[Slot_Number][i] = Memory[((User_Input_Address>>>Slot_bit)<<Slot_bit) + i - 3];
			Cache[Slot_Number][1] = 1;//set valid bit to 1.
			Cache[Slot_Number][2] = Tag_Number;//set tag number.

			System.out.println("Current value in " + String.format("%03X", User_Input_Address) + " is " + 
			                   String.format("%02X", Memory[User_Input_Address]) +". (Cache Miss)");
		}
		else			
			System.out.println("Current value in " + String.format("%03X", User_Input_Address) + " is " + 
	                   String.format("%02X", Memory[User_Input_Address]) +". (Cache Hit)");

		//update the value in cache.
		Cache[Slot_Number][Offset_Number + 3] = User_Input_Value;
		//update the address of the slot.		
		Cache_Address[Slot_Number] = (User_Input_Address>>>Slot_bit)<<Slot_bit;	
		//put dirty bit to 1.
		Dirty_Bit[Slot_Number] = 1;
	}


}
