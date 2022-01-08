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

	private final Mat4[] transforms;
	private final Vec4[] normals;
	private final long startTime;

	private final double r3 = Math.sqrt(3+.01);

	private final int size;

	public CubeComponent(String name, int size) {
		super(name);
		this.size = size;
		transforms = new Mat4[6];
		normals = new Vec4[6];
		setPreferredSize((int) Math.ceil(r3*size),(int) Math.ceil(r3*size));
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(this::requestReassembly, 1, 60, TimeUnit.MILLISECONDS);
		startTime = System.currentTimeMillis();
	}
	
	// Notes to me
	// z axis is through the screen
	// y is horizontal spin
	// x is vertical spin
	

	private void computeTransforms()
	{
		//Creates all of the sides
		transforms[0] = new Mat4(1);
		transforms[1] = new Mat4(1);
		transforms[2] = new Mat4(1);
		transforms[3] = new Mat4(1);
		transforms[4] = new Mat4(1);
		transforms[5] = new Mat4(1);

		//Gets time since creation(for rotation amount)
		long time = System.currentTimeMillis()-startTime;

		//Initializes the way each face is facing
		normals[0] = new Vec4(0,0,-1,0);
		normals[1] = new Vec4(0,1,0,0);
		normals[2] = new Vec4(1,0,0,0);
		normals[3] = new Vec4(0,0,1,0);
		normals[4] = new Vec4(0,-1,0,0);
		normals[5] = new Vec4(-1,0,0,0);
		
		for (int i=0;i<6;i++)
		{
			//Rotates given side with the time one first, then ot get it on its off axis, then gets the image of each axis under the given rotation
			//The rotate functions do change the transforms, but the multiplication does not
			normals[i] = transforms[i].rotate((float) (time%(6000*6.28) )/ 6000, new Vec3(0,1,0)).rotate((float) 24, new Vec3(1,.5,0)).mul_(normals[i]);
		}
		double pi2 = Math.PI / 2;

		//Move and rotate the sides from the middle of the cube to the appropriate edges
		transforms[0].translate(new Vec3(-size/2f,-size/2f,size/2f));
		transforms[1].translate(new Vec3(-size/2f,-size/2f,-size/2f)).rotate((float) pi2, new Vec3(1,0,0));
		transforms[2].translate(new Vec3(-size/2f,-size/2f,size/2f)).rotate((float) pi2, new Vec3(0,1,0));
		transforms[3].translate(new Vec3(-size/2f,-size/2f,-size/2f));
		transforms[4].translate(new Vec3(-size/2f,size/2f,-size/2f)).rotate((float) pi2, new Vec3(1,0,0));
		transforms[5].translate(new Vec3(size/2f,-size/2f,size/2f)).rotate((float) pi2, new Vec3(0,1,0));

		for (int i=0;i<6;i++) // I have no clue why this is necessary, without it the sides of the cube mess up; may need to be changed if the title screen changes position.
		{
			transforms[i] = transforms[i].translate(new Vec3(0,0,17.5-3*(i<2 ? 1 : 0)));
		}

	}
	
	@Override
	protected void assembleSelf(RenderTarget target)
	{
		computeTransforms();
		
		setPosition(750,780);
		
		target.pushTransform(new Mat4(1).translate(new Vec3(getX()+size*r3/2,getY()-size*r3/2,0))); //-size*r3/2
		
		for (int b=0; b<6;b++)
		{
			target.pushTransform(transforms[b]);
			
			float dot = normals[b].dot(new Vec4(-1,0,0,0)); //Gets the "amount" the given side is pointing in the -x direction

			Vec4 color = new Vec4(.4+.3*dot, .4+.3*dot, .6+.4*dot,1.0); //More aligned means brighter color

			target.fill(0,0, size, size, color);
			
			target.popTransform();
		}
		
		target.popTransform();
	}
}
