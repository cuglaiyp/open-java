package dogfight_Z.Ammo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.PriorityQueue;

import dogfight_Z.Aircraft;
import dogfight_Z.Effects.EngineFlame;
import graphic_Z.Interfaces.Dynamic;
import graphic_Z.Interfaces.ThreeDs;
import graphic_Z.Worlds.CharTimeSpace;
import graphic_Z.utils.GraphicUtils;

public class Decoy extends Aircraft implements Dynamic
{
	public float  velocity;
	public float  resistanceRate;
	public int   lifeLeft;
	public int   life;
	public long lifeTo;
	public boolean end;
	public float Roll_angle_Aircraft[];
	
private static ArrayList<float[]> missileModelData;
	
	static {
		missileModelData = new ArrayList<float[]>();
		
		float newPonit[];
		
		newPonit = new float[3];
		newPonit[0] = 0;
		newPonit[1] = 0;
		newPonit[2] = 40;
		missileModelData.add(newPonit);
		
		newPonit = new float[3];
		newPonit[0] = 0;
		newPonit[1] = 0;
		newPonit[2] = -40;
		missileModelData.add(newPonit);
		
		newPonit = new float[3];
		newPonit[0] = 0;
		newPonit[1] = 40;
		newPonit[2] = 0;
		missileModelData.add(newPonit);
		
		newPonit = new float[3];
		newPonit[0] = 0;
		newPonit[1] = -40;
		newPonit[2] = 0;
		missileModelData.add(newPonit);
		
		newPonit = new float[3];
		newPonit[0] = 40;
		newPonit[1] = 0;
		newPonit[2] = 0;
		missileModelData.add(newPonit);
		
		newPonit = new float[3];
		newPonit[0] = -40;
		newPonit[1] = 0;
		newPonit[2] = 0;
		missileModelData.add(newPonit);
	}
	
	public Decoy
	(
		int  campTo,
		int  lifeTime,
		float Speed,
		float Speed_aircraft,
		float resistance_rate,
		float Location[],
		float Roll_angle[],
		float Roll_angle_aircraft[],
		LinkedList<ListIterator<ThreeDs>>	   del_que,
		PriorityQueue<Dynamic> effect
	)
	{
		super(null, null, 0.0F, (short)-1, null, effect, del_que, null, null, null, "\nDecory" + GraphicUtils.random(), false);
		camp = campTo;
		specialDisplay = '@';
		location[0] = Location[0];
		location[1] = Location[1];
		location[2] = Location[2];
		
		speed = Speed_aircraft;
		Roll_angle_Aircraft = Roll_angle_aircraft;
		
		points = missileModelData;
		points_count = missileModelData.size();
		
		lifeLeft = life = lifeTime;
		lifeTo = life + System.currentTimeMillis() / 1000;
		
		end				= false;
		visible			= true;
		
		roll_angle[0] = Roll_angle[0];
		roll_angle[1] = Roll_angle[1];
		roll_angle[2] = Roll_angle[2];
		
		velocity = Speed * GraphicUtils.random() * 2;
		resistanceRate = resistance_rate;
		resistanceRate *= GraphicUtils.random();
		isAlive = true;
		lockingPriority = -128/*(short)-(2.0 + 10.0 * GraphicUtils.random())*/;
	}
	
	public Decoy
	(
		short  lifeTime,
		float Speed,
		float Speed_aircraft,
		float resistance_rate,
		float Location[],
		float Roll_angle[],
		float Roll_angle_aircraft[],
		LinkedList<ListIterator<ThreeDs>> del_que,
		PriorityQueue<Dynamic> effect
	)
	{
		this
		(
			-1, lifeTime, Speed, Speed_aircraft, 
			resistance_rate, Location, Roll_angle, 
			Roll_angle_aircraft, del_que, effect
		);
	}
	
	@Override
	public void go()
	{
		if(lifeLeft <= 0) disable();
		else
		{/*
			float x, y, z, t = GraphicUtils.cos(GraphicUtils.toRadians(roll_angle[1])) * velocity;
			
			x = GraphicUtils.tan(GraphicUtils.toRadians(roll_angle[1])) * t;
			y = GraphicUtils.sin(GraphicUtils.toRadians(roll_angle[0])) * t;
			z = GraphicUtils.cos(GraphicUtils.toRadians(roll_angle[0])) * t;
			*/

            velocity -= velocity * resistanceRate * 1.5;
            
		    float x, y, z;
			float r1 = GraphicUtils.toRadians(roll_angle[1] + Roll_angle_Aircraft[1]);
	        float r2 = GraphicUtils.toRadians(roll_angle[0] + Roll_angle_Aircraft[0]);
	        float t  = GraphicUtils.cos(r1) * velocity;
	        x  = GraphicUtils.sin(r1) * velocity;
	        y  = GraphicUtils.sin(r2) * t;
	        z  = GraphicUtils.cos(r2) * t;

            location[0] -= x;
            location[1] += y;
            location[2] += z;
            
	        r1 = GraphicUtils.toRadians(Roll_angle_Aircraft[1]);
	        r2 = GraphicUtils.toRadians(Roll_angle_Aircraft[0]);
	        t  = GraphicUtils.cos(r1) * speed;
            x  = GraphicUtils.sin(r1) * speed;
            y  = GraphicUtils.sin(r2) * t;
            z  = GraphicUtils.cos(r2) * t;
			/*
			t = GraphicUtils.cos(GraphicUtils.toRadians(Roll_angle_Aircraft[1])) * speed;
			
			x = GraphicUtils.tan(GraphicUtils.toRadians(Roll_angle_Aircraft[1])) * t;
			y = GraphicUtils.sin(GraphicUtils.toRadians(Roll_angle_Aircraft[0])) * t;
			z = GraphicUtils.cos(GraphicUtils.toRadians(Roll_angle_Aircraft[0])) * t;
			*/
			location[0]	-= x;
			location[1]	+= y;
			location[2]	+= z;

            location[0] += CharTimeSpace.g * (life - lifeLeft) * 0.0125 - (life-lifeLeft) * resistanceRate;
            
			effects.add(new EngineFlame(location, (short)25 + (int)(50 * GraphicUtils.random()), '*'));
			--lifeLeft;
		}
	}
	
	public void disable() 
	{
		lockingPriority = 0;
		visible = false;
		isAlive = false;
		end = true;
		if(myPosition != null) deleteQue.add(myPosition);
		else System.err.println("null");
	}

	@Override
	public boolean deleted()
	{
		return end;
	}

	@Override
	public int compareTo(Dynamic o)
	{
		return (int) (getLife() - o.getLife());
	}

	@Override
	public long getLife()
	{
		return lifeTo;
	}
	
	@Override
	public void getDamage(int damage, Aircraft giver, String weaponName)
	{
	}
	
	@Override
	public void randomRespawn()
	{
	}
	
	@Override
	public void pollBack()
	{
	}
	public int getHash()
	{
		// TODO 自动生成的方法存根
		return this.hashCode();
	}
}
