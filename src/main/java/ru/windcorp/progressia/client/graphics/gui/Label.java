package ru.windcorp.progressia.client.graphics.gui;

import glm.mat._4.Mat4;
import glm.vec._2.i.Vec2i;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.font.Font;
import ru.windcorp.progressia.client.localization.MutableString;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class Label extends Component {
	
	private Font font;
	private String currentText;
	private Vec2i currentSize;
	private Supplier<String> contents;
	
	private MutableString.Listener mutableStringListener = null;
	
	private float maxWidth = Float.POSITIVE_INFINITY;
	
	public Label(String name, Font font, Supplier<String> contents) {
		super(name);
		this.font = font;
		this.contents = contents;
		update();
	}
	
	public Label(String name, Font font, String contents) {
		this(name, font, () -> contents);
	}
	
	public Label(String name, Font font, MutableString contents) {
		// Not the most elegant solution
		
		this(name, font, () -> {
			contents.update();
			return contents.get();
		});
		
		AtomicBoolean isUpdating = new AtomicBoolean();
		
		this.mutableStringListener = () -> {
			if (isUpdating.compareAndSet(false, true)) {
				this.update();
				isUpdating.set(false);
			}
		};
		contents.addListener(mutableStringListener);
	}

	public void update() {
		currentText = contents.get();
		currentSize = font.getSize(currentText, maxWidth, null).mul(2);
		requestReassembly();
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
		float startX = getX() + font.getAlign() * (getWidth() - currentSize.x);
		
		target.pushTransform(
				new Mat4().identity().translate(startX, getY(), -1000) // TODO wtf is this magic <---
				.scale(2)
		);
		
		target.addCustomRenderer(font.assemble(currentText, maxWidth));
		
		target.popTransform();
	}

}
