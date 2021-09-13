package ru.windcorp.progressia.client.graphics.gui;

import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.texture.Texture;

public class TextureComponent extends Component {
	
	private Texture texture;

	public TextureComponent(String name, Texture texture2) {
		super(name);
		
		texture = texture2;
		setPreferredSize(texture.getSprite().getWidth(),texture.getSprite().getHeight());
	}
	
	@Override
	protected void assembleSelf(RenderTarget target)
	{
		target.drawTexture(getX(), getY(), getWidth(), getHeight(), texture);
	}

}
