package ru.windcorp.progressia.client.graphics.gui;

import glm.mat._4.Mat4;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.font.Font;

import java.util.function.Supplier;

public class DynamicLabel extends Component {

	private Font font;
	private Supplier<CharSequence> contents;

	public DynamicLabel(String name, Font font, Supplier<CharSequence> contents, int width) {
		super(name);
		this.font = font;
		this.contents = contents;
		setPreferredSize(width, font.getHeight("", Integer.MAX_VALUE) * 2);
	}

	public Font getFont() {
		return font;
	}

	public Supplier<CharSequence> getContentSupplier() {
		return contents;
	}

	@Override
	protected void assembleSelf(RenderTarget target) {
		target.pushTransform(new Mat4().identity().translate(getX(), getY(), -1000).scale(2));
		target.addCustomRenderer(font.assembleDynamic(getContentSupplier()));
		target.popTransform();
	}

}
