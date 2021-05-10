package Chip8Engine;

import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.applet.*;

public class Display extends Frame{
	Adapter adapter;
	CPU cpu;
	BufferedImage buffer;
	Timer timer;
	KeyAdapter keyAdapter;
	private int screen[][];
	private Registers registers;
	public boolean ready = false;
	private String info = "";
	private boolean colorRed = false;


	
	public Display(Registers registers, CPU cpu){
		super("Chip - 8");
		this.registers = registers;
		keyAdapter = new KeyAdapter(this);
		adapter = new Adapter();
		screen = new int[32][64];
		addWindowListener(adapter);
		addKeyListener( keyAdapter );
		setSize( 1048, 590);
		buffer = new BufferedImage(1048, 590, BufferedImage.TYPE_INT_RGB);
		setVisible( true );
		this.cpu = cpu;
		
	}

	public void increase(){
		cpu.timeToWait++;
		System.out.println(cpu.timeToWait);		
	}

	public void decrease(){
		cpu.timeToWait--;
		System.out.println(cpu.timeToWait);
	}

	public void keyInput(){
		
	}
	public void setJoypad(Joypad joypad){
		keyAdapter.setJoypad( joypad);
	}

	public void setScreenData(int x, int y, int n,int[] data){
		for( int i = 0, k = 0; i<n; i++){
			int b = data[i];
			k = 0;
			
			for( int j = 7; j>0; j--){
				int res = (b >> j) & 0x01;
				registers.setRegisterValueAt(0xf, (byte)( screen[Math.abs( (y+i)%32 )][Math.abs( (x+k)%64)] ^ res ));
				screen[Math.abs( (y+i)%32 )][Math.abs( (x+k)%64 )] ^= res;	
				k++;
			}
		}
		
	}

	public void paint( Graphics gf){
			final int w = 16;
			Graphics g = buffer.getGraphics();
		for( int i = 0; i<32; i++){
			for( int j=0; j<64; j++){
				if( screen[i][j] == 1){
					if( colorRed) g.setColor(Color.red);
					else g.setColor(Color.white);
					
					g.fillRect(j*w,i*w+20,w,w);
				} else {
					g.setColor(Color.black);
					g.fillRect(j*w,i*w+20,w,w);
				}
			}
		}
		gf.drawImage(buffer,0,20,null);
		
			
	}

	public void clearScreen(){
		for( int i=0; i<32; i++){
			for( int j =0; j<64;j++)
				screen[i][j] = 0;
		}
		repaint();
		
	}

	public void changeColor(){
		colorRed = true;
	}

	public void display( String msg){
		info = msg;
		repaint();
	}
}

class Adapter extends WindowAdapter{

	public void windowClosing(WindowEvent we){
		System.exit(0);
	}
}

class KeyAdapter implements  KeyListener{
	private Joypad joypad;
	private Display display;

	KeyAdapter(Display display){
		this.display = display;	
		this.joypad = joypad;
	}

	public void keyPressed( KeyEvent ke){
		if( joypad != null){
			
			switch( ke.getKeyCode() ){
				case KeyEvent.VK_0 : 	joypad.setKeyAt(0);
							break;
				case KeyEvent.VK_W :	display.changeColor();break;
				case KeyEvent.VK_1 : 	joypad.setKeyAt(1);
							break;
				case KeyEvent.VK_2 : 	joypad.setKeyAt(2);
							break;
				case KeyEvent.VK_3 : 	joypad.setKeyAt(3);
							break;
				case KeyEvent.VK_4 : 	joypad.setKeyAt(4);
							break;
				case KeyEvent.VK_5 : 	joypad.setKeyAt(5);
							break;
				case KeyEvent.VK_6 : 	joypad.setKeyAt(6);
							break;
				case KeyEvent.VK_7 : 	joypad.setKeyAt(7);
							break;
				case KeyEvent.VK_8 : 	joypad.setKeyAt(8);
							break;
				case KeyEvent.VK_9 : 	joypad.setKeyAt(9);
							break;
				case KeyEvent.VK_A : 	joypad.setKeyAt(0xa);
							break;
				case KeyEvent.VK_B : 	joypad.setKeyAt(0xb);
							break;
				case KeyEvent.VK_C : 	joypad.setKeyAt(0xc);
							break;
				case KeyEvent.VK_D : 	joypad.setKeyAt(0xD);
							break;
				case KeyEvent.VK_E : 	joypad.setKeyAt(0xE);
							break;
				case KeyEvent.VK_F : 	joypad.setKeyAt(0xF);
							break;
				case KeyEvent.VK_UP:	display.increase();
							break;
				case KeyEvent.VK_DOWN:	display.decrease();
							break;
		}		
		joypad.display();
		display.keyInput();
		}
	}

	public void setJoypad(Joypad joypad){
		this.joypad = joypad;
	}
	public void keyReleased(KeyEvent ke){
		if( joypad != null){

			switch( ke.getKeyCode() ){
				case KeyEvent.VK_0 : 	joypad.setKeyReleaseAt(0);
							break;
				case KeyEvent.VK_1 : 	joypad.setKeyReleaseAt(1);
							break;
				case KeyEvent.VK_2 : 	joypad.setKeyReleaseAt(2);
							break;
				case KeyEvent.VK_3 : 	joypad.setKeyReleaseAt(3);
							break;
				case KeyEvent.VK_4 : 	joypad.setKeyReleaseAt(4);
							break;
				case KeyEvent.VK_5 : 	joypad.setKeyReleaseAt(5);
							break;
				case KeyEvent.VK_6 : 	joypad.setKeyReleaseAt(6);
							break;
				case KeyEvent.VK_7 : 	joypad.setKeyReleaseAt(7);
							break;
				case KeyEvent.VK_8 : 	joypad.setKeyReleaseAt(8);
							break;
				case KeyEvent.VK_9 : 	joypad.setKeyReleaseAt(9);
							break;
				case KeyEvent.VK_A : 	joypad.setKeyReleaseAt(0xa);
							break;
				case KeyEvent.VK_B : 	joypad.setKeyReleaseAt(0xb);
							break;
				case KeyEvent.VK_C : 	joypad.setKeyReleaseAt(0xc);
							break;
				case KeyEvent.VK_D : 	joypad.setKeyReleaseAt(0xD);
							break;
				case KeyEvent.VK_E : 	joypad.setKeyReleaseAt(0xE);
							break;
				case KeyEvent.VK_F : 	joypad.setKeyReleaseAt(0xF);
							break;
		}		
		joypad.display();
		}
	}

	public void keyTyped( KeyEvent ke){}	
}