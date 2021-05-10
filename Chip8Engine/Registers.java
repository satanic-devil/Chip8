package Chip8Engine;

public class Registers{
	private byte[] generalPurposeRegisters = new byte[ 16 ];
	private short indexRegister ;
	private byte delayTimer, soundTimer;
	
	public void setIndexRegister(short i){
		indexRegister = i;
		
	}

	public short getIndexRegister(){ return indexRegister; }

	public byte getRegisterValueAt( int x){ return generalPurposeRegisters[ x ]; }

	public void setRegisterValueAt(int x, byte value){ 
		generalPurposeRegisters[ x ] = value;
	}
	

	public byte getDelayTimer(){
		return delayTimer;
	}

	public void setDelayTimer( byte value){
		delayTimer = value;
	}
		
	public void setSoundTimer( byte value){
		soundTimer = value;
	}

	public byte getSoundTimer(){
		return soundTimer;
	}

	public Registers(){
			
	}
}