package graphic_Z.Managers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JTextArea;

import graphic_Z.Cameras.CharFrapsCamera;
import graphic_Z.HUDs.CharDynamicHUD;
import graphic_Z.HUDs.CharHUD;
import graphic_Z.HUDs.CharImage;
import graphic_Z.HUDs.CharLabel;
import graphic_Z.HUDs.CharLoopingScrollBar;
import graphic_Z.HUDs.CharProgressBar;
import graphic_Z.HUDs.HUD;
import graphic_Z.Interfaces.Dynamic;
import graphic_Z.Interfaces.ThreeDs;
import graphic_Z.Worlds.CharWorld;
import graphic_Z.utils.HzController;

public class CharVisualManager extends VisualManager<CharWorld> implements Runnable
{
	public		char	                     point[];			//点样式
	protected	char	                     blank;				//空白样式
	public		char	                     fraps_buffer[][];			//帧缓冲，实体
	public      char                         emptyLine[];
	public      static final int             POINTLEVEL = 19;
	protected	List<CharFrapsCamera>        cameras;
	protected	JTextArea	                 mainScr;		//在主屏幕引用
	public		List<Iterable<ThreeDs>>      staticObjLists;
	public		List<Iterable<Dynamic>>      dynamicObjLists;
	public		List<PriorityQueue<Dynamic>> selfDisposable;
	public		Object	                     mainCameraFeedBack;
	private     StringBuilder                scr_show;
	private     Thread                       tmpThread;
	private     ExecutorService              epool;
	//private Thread staticObjExposureThread;
	
	public CharVisualManager(int resolution_X, int resolution_Y, CharWorld inWhichWorld, JTextArea main_scr)
	{
		super(resolution_X, resolution_Y, inWhichWorld);
		
		mainCameraFeedBack = null;
		scr_show        = new StringBuilder(resolution_X * resolution_Y);
		refreshHz       = inWorld.refreshHz;
		staticObjLists	= inWorld.objectsManager.staticObjLists;
		dynamicObjLists	= inWorld.objectsManager.dynamicObjLists;
		selfDisposable  = inWorld.objectsManager.selfDisposable;

		hzController    = new HzController(refreshHz);
		//point = '*';					//default
		point = new char[POINTLEVEL + 1];
		point[0]  = '@';
		point[1]  = '$';
		point[2]  = '0';
		point[3]  = 'G';
		point[4]  = 'Q';
		point[5]  = 'O';
		point[6]  = 'o';
		point[7]  = '*';
		point[8]  = '+';
		point[9]  = '=';
		point[10] = '^';
		point[11] = ';';
		point[12] = '"';
		point[13] = ':';
		point[14] = '~';
		point[15] = '-';
		point[16] = ',';
		point[17] = '`';
		point[18] = '\'';
		point[19] = '.';
		
		blank = ' ';					//default
		
		fraps_buffer = new char[resolution_Y][];
		emptyLine    = new char[resolution_X];
		
		for(short i=0 ; i<resolution_Y ; ++i)
			fraps_buffer[i] = new char[resolution_X];

		for(short i=0 ; i<resolution_X ; ++i)
			emptyLine[i] = blank;
		
		cameras = new ArrayList<CharFrapsCamera>();

		mainScr = main_scr;
		
		epool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	}
	
	public void reSizeScreen(int x, int y) {
		
		fraps_buffer = new char[y][];
		emptyLine    = new char[x];
		
		for(int i=0 ; i<y ; ++i)
			fraps_buffer[i] = new char[x];

		for(int i=0 ; i<x ; ++i)
			emptyLine[i] = blank;
		
		scr_show = new StringBuilder(x * y);
		
		resolution[0] = x;
		resolution[1] = y;
		
		CharHUD ahud = null;
		for(HUD hud : HUDs) {
			ahud = (CharHUD) hud;
			ahud.reSizeScreen(resolution, fraps_buffer);
		}
	}

	@Override
	protected void finalize() throws Throwable { 
		epool.shutdownNow();
	}
	
	public void newCamera()
	{
		CharFrapsCamera newCamera = new CharFrapsCamera(1.0F, 1000.0F, resolution, fraps_buffer, inWorld, staticObjLists);
		cameras.add(newCamera);
		//staticObjExposureThread = new Thread(newCamera);
	}
	
	public CharFrapsCamera newCamera(float FOV)
	{
		CharFrapsCamera newCma = new CharFrapsCamera(FOV, 1000.0F, resolution, fraps_buffer, inWorld, staticObjLists);
		cameras.add(newCma);
		//staticObjExposureThread = new Thread(newCma);
		return newCma;
	}
	
	public CharFrapsCamera newCamera(float FOV, float visibility)
	{
		CharFrapsCamera newCma = new CharFrapsCamera(FOV, visibility, resolution, fraps_buffer, inWorld, staticObjLists);
		cameras.add(newCma);
		//staticObjExposureThread = new Thread(newCma);
		return newCma;
	}
	
	public CharFrapsCamera newCamera(CharFrapsCamera newCma)
	{
		cameras.add(newCma);
		//staticObjExposureThread = new Thread(newCma);
		return newCma;
	}
	
	public CharHUD newHUD(String HUDImgFile, int HUDLayer, boolean transparentAtSpace)
	{
		CharHUD newHud = new CharHUD(HUDImgFile, fraps_buffer, HUDLayer, resolution, transparentAtSpace);
		HUDs.add(newHud);
		return newHud;
	}
	
	public CharHUD newHUD(String HUDImgFile, int HUDLayer)
	{
		return newHUD(HUDImgFile, HUDLayer, true);
	}
	
	public CharDynamicHUD newDynamicHUD(String HUDImgFile, int HUDLayer, int size_X, int size_Y)
	{
		CharDynamicHUD newHud = new CharDynamicHUD
		(
			HUDImgFile, fraps_buffer, 
			HUDLayer, resolution, 
			size_X, size_Y, 
			(short)(resolution[0]/2), (short)(resolution[1]/2)
		);
		
		HUDs.add(newHud);
		return newHud;
	}
	
	public void newDynamicHUD(CharDynamicHUD newHud)
	{
		HUDs.add(newHud);
	}
	
	public void newImage(CharImage newHud)
	{
		HUDs.add(newHud);
	}
	
	public CharImage newImage(String HUDImgFile, int HUDLayer, int size_X, int size_Y, int locatX, int locatY) {
		CharImage newHud = new CharImage (
			HUDImgFile, 
			fraps_buffer, 
			size_X, size_Y,
			locatX, locatY,
			HUDLayer, resolution, true
		);
		
		HUDs.add(newHud);
		return newHud;
	}
	
	public CharLoopingScrollBar newLoopingScrollBar
	(
		String HUDImgFile, int HUDLayer, 
		int size_X, int size_Y,
		int size_Show, CharLoopingScrollBar.Direction direction
	)
	{
		CharLoopingScrollBar newHud = new CharLoopingScrollBar
		(
			HUDImgFile, fraps_buffer, 
			HUDLayer, resolution, 
			size_X, size_Y, 
			(resolution[0]>>1), (resolution[1]>>1),
			direction, 0, size_Show
		);
		
		HUDs.add(newHud);
		return newHud;
	}
	
	public CharLabel newLabel(String Text, int location_X,  int location_Y, int HUDLayer)
	{
		CharLabel newLbl = 
			new CharLabel(fraps_buffer, HUDLayer, resolution, Text, location_X, location_Y);
		HUDs.add(newLbl);
		return newLbl;
	}
	
	public void newLabel(CharLabel newLbl)
	{
		HUDs.add(newLbl);
	}
	
	public CharProgressBar newProgressBar
	(
		int	   location_X,  
		int	   location_Y, 
		int	   HUDLayer,
		int	   size,
		char   visual,
		CharProgressBar.Direction direction,
		float value
	)
	{
		CharProgressBar newBar = 
			new CharProgressBar(fraps_buffer, HUDLayer, resolution, location_X, location_Y, size, visual, direction, value);
		HUDs.add(newBar);
		return newBar;
	}
	
	public CharProgressBar newProgressBar
	(
		int	location_X,  
		int	location_Y, 
		int	HUDLayer,
		int	size,
		char	visual,
		CharProgressBar.Direction direction
	)
	{
		CharProgressBar newBar = 
			new CharProgressBar(fraps_buffer, HUDLayer, resolution, location_X, location_Y, size, visual, direction);
		HUDs.add(newBar);
		return newBar;
	}
	
	public void run() {
		refresh();
		//try{Thread.sleep(refreshDelay);} catch(InterruptedException e) {}
	}
	
	public void refresh()
	{
		for(CharFrapsCamera aCamera : cameras) {
			//new Thread(aCamera).start();

			for(Iterable<Dynamic> eachList:dynamicObjLists)
				aCamera.exposure(eachList, 0);
			for(Iterable<Dynamic> eachList:selfDisposable)
				aCamera.exposure(eachList, 0);
			
			mainCameraFeedBack = aCamera.exposure();
		}
		
		for	(
			Iterator<HUD> iter = HUDs.iterator();
			iter.hasNext();
			iter.next().printNew()
		);
	}
	
	public void buff() {
		
		tmpThread = new Thread(hzController);
		tmpThread.setPriority(Thread.MAX_PRIORITY);
		tmpThread.start();
		for(int i=0 ; i<resolution[1] ; ++i)
			System.arraycopy(emptyLine, 0, fraps_buffer[i], 0, resolution[0]);
		
		for(CharFrapsCamera aCamera : cameras) {
			epool.execute(aCamera);
			//new Thread(aCamera).start();
		}
	}
	
	public void printNew()	//关于颜色、多摄像机的改进待做(包括裸眼3D)
	{
		if(tmpThread == null) return;
		
		refresh();

		try{tmpThread.join();} catch (InterruptedException e) {}
		tmpThread = null;
		
		boolean firstInLine, firstLine;
		
		scr_show.delete(0, scr_show.length());
		firstLine = true;
		
		for(char y[]:fraps_buffer)
		{
			firstInLine = true;
			if(!firstLine) scr_show.append('\n');
			
			for(char x:y)
			{
				if(!firstInLine) scr_show.append(' ');
				scr_show.append(x);
				firstInLine = false;
			}
			
			firstLine = false;
		}

		mainScr.setText(scr_show.toString());
	}

}
