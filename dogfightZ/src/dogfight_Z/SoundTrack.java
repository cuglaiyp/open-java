package dogfight_Z;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import jmp123.demo.MiniPlayer;
import jmp123.output.Audio;

public class SoundTrack implements Runnable
{
	private ArrayList<String> soundTrack;
	private int currentPlayingIndex;
	
	private MiniPlayer player;
	private boolean interruptFlag;
	private GameManagement gameManager;
	
	public SoundTrack(GameManagement gameManager, String sountrack_info_file)
	{
	    this.gameManager = gameManager;
	    interruptFlag = false;
		soundTrack = new ArrayList<String>();
		soundTrack.clear();
		currentPlayingIndex = 0;
		player = null;
		try(InputStreamReader reader = new InputStreamReader(new FileInputStream(sountrack_info_file), "GBK"))
		{
			String tmp;
			int ch;
			
			while(true)
			{
				tmp = "";
				while((ch = reader.read()) != '\n' && ch != -1)
					tmp = tmp + (char)ch;
				
				if(!tmp.equals(""))
					soundTrack.add(tmp);
				if(ch == -1) break;
			}
			
		}	catch(IOException exc){}
	}
	
	public void switchNext() {
		if(++currentPlayingIndex == soundTrack.size()) currentPlayingIndex = 0;
	}
	
	public void switchPrevious() {
		if(currentPlayingIndex-- == 0) currentPlayingIndex = soundTrack.size()-1;
	}
	
	public void interrupt() {
	    if(player != null  &&  soundTrack.size() > 0) {
	        player.setInterrupted(interruptFlag = true);
	    }
	}
	
	@Override
	public void run()
	{
	    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		if(soundTrack.size() > 0)
		{
			player = new MiniPlayer(new Audio());
			
			try
			{
				while(gameManager.isRunning())
				{
					player.open(soundTrack.get(currentPlayingIndex));
					player.run();
					if(!interruptFlag) {
					    switchNext();
					    interruptFlag = false;
					}
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, e.toString());
				e.printStackTrace();
			}
		}
	}
}
