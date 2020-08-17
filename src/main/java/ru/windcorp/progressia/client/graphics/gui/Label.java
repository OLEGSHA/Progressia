package ru.windcorp.progressia.client.graphics.gui;

import java.util.function.Supplier;

import glm.mat._4.Mat4;
import glm.vec._2.i.Vec2i;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.font.Font;

public class Label extends Component {
	
	private Font font;
	private String currentText;
	private Vec2i currentSize;
	private Supplier<String> contents;
	
	public Label(String name, Font font, Supplier<String> contents) {
		super(name);
		this.font = font;
		this.contents = contents;
		update();
	}
	
	public Label(String name, Font font, String contents) {
		this(name, font, () -> contents);
	}

	public void update() {
		currentText = contents.get();
		currentSize = font.getSize(currentText, Integer.MAX_VALUE, null).mul(2);
	}
	
	@Override
	public synchronized Vec2i getPreferredSize() {
		return currentSize;
	}
	
	public Font getFont() {
		return font;
	}
	
	public String getCurrentText() {
		return currentText;
	}
	
	public Supplier<String> getContentSupplier() {
		return contents;
	}
	
	@Override
	protected void assembleSelf(RenderTarget target) {
		target.pushTransform(
				new Mat4().identity().translate(getX(), getY(), -1000)
				.scale(2)
		);
		
		target.addCustomRenderer(font.assemble(currentText, Integer.MAX_VALUE));
		
		target.popTransform();
	}

}
