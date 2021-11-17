package graphic_Z.HUDs;

import graphic_Z.utils.GraphicUtils;

public class CharDynamicHUD extends CharImage
{
	public double  angle;
	public boolean transparentAtSpace;
	
	public CharDynamicHUD
	(
		String HUDImgFile, 
		char[][] frapsBuffer, 
		short HUDLayer, 
		short[] scrResolution,
		short size_X,
		short size_Y,
		short Location_X,
		short Location_Y,
		double Angle_X,
		boolean transparent_at_space
	)
	{
		super(HUDImgFile, frapsBuffer, size_X, size_Y, Location_X, Location_Y, HUDLayer, scrResolution, true);
		transparentAtSpace = transparent_at_space;
		angle	= Angle_X;
	}
	
	public CharDynamicHUD
	(
		String HUDImgFile, 
		char[][] frapsBuffer, 
		short HUDLayer, 
		short[] scrResolution,
		short size_X,
		short size_Y,
		short Location_X,
		short Location_Y
	) {this(HUDImgFile, frapsBuffer, HUDLayer, scrResolution, size_X, size_Y, Location_X, Location_Y, 0.0, true);}
	
	public CharDynamicHUD
	(
		String HUDImgFile, 
		char[][] frapsBuffer, 
		short HUDLayer, 
		short[] scrResolution,
		short size_X,
		short size_Y
	) {this(HUDImgFile, frapsBuffer, HUDLayer, scrResolution, size_X, size_Y, (short)0, (short)0, 0.0, true);}
	
	protected double distance(double x0, double y0, int x2, int y2)
	{
		x2 -= x0;
		y2 -= y0;
		return Math.sqrt(x2*x2 + y2*y2);
	}
	
	@Override
	public void printNew()
	{
		if(visible)
		{
			angle %= 360;
			double x0, y0;
			double r, X, Y, tmp;
			
			for(int y=0 ; y<size[1] ; ++y)
			{
				for(int x=0 ; x<size[0] ; ++x)
				{
					if(HUDImg[y][x] != ' ' || !transparentAtSpace)
					{
						x0 = x-centerX;
						y0 = y-centerY;
						
						if(angle != 0)
						{
							r  = distance(x0, y0, 0, 0);
							
							tmp = Math.atan(y0/x0)+Math.toRadians(angle);
							X = GraphicUtils.cos(tmp) * r;
							Y = GraphicUtils.sin(tmp) * r;
							y0 = (short) ((x0<0)?(-Y):Y);
							x0 = (short) ((x0<0)?(-X):X);
						}
						x0 += location[0];
						y0 += location[1];
						
						if(x0 >= 0 && y0 >= 0 && x0 < resolution[0]	&&	y0 < resolution[1])
							fraps_buffer[(int) y0][(int) x0] = HUDImg[y][x];
					}
				}
			}
		}
	}
}
