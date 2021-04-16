package ru.windcorp.progressia.client.graphics.gui;

import java.util.function.Supplier;

import glm.mat._4.Mat4;
import glm.vec._2.i.Vec2i;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.font.Font;
import ru.windcorp.progressia.client.graphics.input.bus.InputListener;
import ru.windcorp.progressia.client.graphics.input.InputEvent;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;

public class Button extends Component {

	private Font font;
	private String currentText;
	private Vec2i currentSize;
	private String text;
	
	public <T extends InputEvent> Button(String name, Font font, String text, InputListener<T> onClick, Class<? extends T> onClickClass) {
		super(name);
		this.font = font;
		this.text = text;
		super.addListener(onClickClass, onClick);
	}
	
	@Override
	protected void assembleSelf(RenderTarget target) {
		target.pushTransform(new Mat4().identity().translate(getX(), getY(), -1000).scale(2));
		target.addCustomRenderer(font.assembleDynamic(getContentSupplier(), Float.POSITIVE_INFINITY));
		target.popTransform();
	}
}
