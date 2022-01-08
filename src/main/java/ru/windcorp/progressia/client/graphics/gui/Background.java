package ru.windcorp.progressia.client.graphics.gui;

import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.texture.Texture;

public class Background extends GUILayer {

	protected Texture backgroundTexture;
	
	public Background(String name, Layout layout, Texture inTexture) {
		super(name, layout);
		
		backgroundTexture = inTexture;
	}
	
	@Override
	protected void assemble(RenderTarget target) {
		getRoot().setBounds(0, 0, getWidth(), getHeight());
		getRoot().invalidate();
		target.pushTransform(new Mat4(1).translate(new Vec3(0,0,500)));
		target.drawTexture(0, 0, getWidth(), getHeight(), backgroundTexture);
		target.popTransform();
		getRoot().assemble(target);
	}

}
