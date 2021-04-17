package ru.windcorp.progressia.client.graphics.gui;

import java.util.function.Supplier;

import glm.mat._4.Mat4;
import glm.vec._2.i.Vec2i;
import org.lwjgl.glfw.GLFW;
import ru.windcorp.progressia.client.graphics.backend.InputTracker;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.font.Font;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.input.bus.InputListener;
import ru.windcorp.progressia.client.graphics.input.InputEvent;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;

public class Button extends Component {

	private Font font;
	private String currentText;
	private Vec2i currentSize;
	private String text;
	private boolean isDisabled;
	
	public <T extends InputEvent> Button(String name, Font font, String text) {//, InputListener<T> onClick, Class<? extends T> onClickClass) {
		super(name);
		this.font = font;
		this.text = text;
		setPosition(400, 400);
		setSize(107,34);
		//super.addListener(onClickClass, onClick);
	}
	
	public boolean isClicked()
	{
		return super.containsCursor() && InputTracker.isKeyPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT);
	}
	
	public void setDisable(boolean isDisabled)
	{
		this.isDisabled = isDisabled;
	}
	
	public boolean isDisabled()
	{
		return isDisabled;
	}
	
	@Override
	protected void assembleSelf(RenderTarget target) {
		//Border
		if (isClicked() || isHovered() || isFocused())
		{
			target.fill(getX(), getY(), getWidth(), getHeight(), 0xFF37A2E6);
		}
		else if (!isDisabled())
		{
			target.fill(getX(), getY(), getWidth(), getHeight(), 0xFFCBCBD0);
		}
		else
		{
			target.fill(getX(), getY(), getWidth(), getHeight(), 0xFFE5E5E5);
		}
		//Inside area
		if (isHovered())
		{
			target.fill(getX()+1, getY()+1, getWidth()-2, getHeight()-2, 0xFFC3E4F7);
		}
		else if (!isClicked())
		{
			target.fill(getX()+1, getY()+1, getWidth()-2, getHeight()-2, Colors.WHITE);
		}
		//text
		Font tempFont;
		if (isClicked())
		{
			tempFont = font.withColor(Colors.WHITE);
		}
		else if (!isDisabled())
		{
			tempFont = font.withColor(Colors.BLACK);
		}
		else
		{
			tempFont = font.withColor(Colors.GRAY_A);
		}
		target.pushTransform(new Mat4().identity().translate(getX(), getY(), -1000).scale(2));
		target.addCustomRenderer(tempFont.assemble( (CharSequence) this.text, this.getWidth()));
		target.popTransform();
	}
}
