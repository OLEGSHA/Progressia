package ru.windcorp.progressia.test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import glm.vec._4.Vec4;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.gui.Component;

public class CubeComponent extends Component {

	private Mat4 transforms[];
	
	private final double pi2 = Math.PI/2;
	private final double r3 = Math.sqrt(3);
	
	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	
	private int size = 100;
	
	public CubeComponent(String name) {
		super(name);
		transforms = new Mat4[6];
		setPreferredSize((int) Math.ceil(r3*size),(int) Math.ceil(r3*size));
		executor.scheduleAtFixedRate(() -> requestReassembly(), 1, 60, TimeUnit.MILLISECONDS);
	}
	
	// Notes to me
	// z axis is through the screen
	// y is horizontal spin
	// x is vertical spin
	

	private void computeTransforms()
	{
		transforms[0] = new Mat4(1);
		transforms[1] = new Mat4(1);
		transforms[2] = new Mat4(1);
		transforms[3] = new Mat4(1);
		transforms[4] = new Mat4(1);
		transforms[5] = new Mat4(1);
		
		long time = System.currentTimeMillis();
		
		for (int i=0;i<6;i++)
		{
			transforms[i].rotate((float) (time%(1000*6.28) )/ 1000, new Vec3(0,1,0)).rotate((float) (time%(6777*6.28) )/ 6777, new Vec3(1,0,0));
		}
		
		transforms[0] = transforms[0].translate(new Vec3(-50,-50,60));
		transforms[1] = transforms[1].translate(new Vec3(-50,-60,-50)).rotate((float) pi2, new Vec3(1,0,0));
		transforms[2] = transforms[2].translate(new Vec3(-40,-50,50)).rotate((float) pi2, new Vec3(0,1,0));
		transforms[3] = transforms[3].translate(new Vec3(-50,-50,-40));
		transforms[4] = transforms[4].translate(new Vec3(-50,40,-50)).rotate((float) pi2, new Vec3(1,0,0));
		transforms[5] = transforms[5].translate(new Vec3(60,-50,50)).rotate((float) pi2, new Vec3(0,1,0));
	}
	
	@Override
	protected void assembleSelf(RenderTarget target)
	{
		computeTransforms();
		
		int b=0;
		
		target.pushTransform(new Mat4(1).translate(new Vec3(size,size,0)));
		
		for (Mat4 tr : transforms)
		{
			target.pushTransform(tr);
			switch (b%3)
			{
			case 0:
				target.fill(0, 0, size, size, new Vec4(255,0,0,255));
				break;
			case 1:
				target.fill(0, 0, size, size, new Vec4(0,255,0,255));
				break;
			case 2:
				target.fill(0, 0, size, size, new Vec4(0,0,255,255));
				break;
			}
			
			b++;
			target.popTransform();
		}
		
		target.popTransform();
	}
}
