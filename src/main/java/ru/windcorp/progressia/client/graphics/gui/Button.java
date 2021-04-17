package ru.windcorp.progressia.client.graphics.gui;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.eventbus.Subscribe;

import glm.mat._4.Mat4;
import glm.vec._2.i.Vec2i;
import org.lwjgl.glfw.GLFW;
import ru.windcorp.progressia.client.graphics.backend.InputTracker;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.font.Font;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.gui.event.HoverEvent;
import ru.windcorp.progressia.client.graphics.input.bus.InputListener;
import ru.windcorp.progressia.client.graphics.input.InputEvent;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;

public class Button extends Component {

	private Font font;
	private String currentText;
	private Vec2i currentSize;
	private String text;
	private boolean isDisabled;
	private boolean isClicked;
	
	public <T extends InputEvent> Button(String name, Font font, String text, Consumer<Button> onClick) {//, InputListener<T> onClick, Class<? extends T> onClickClass) {
		super(name);
		this.font = font;
		this.text = text;
		setPreferredSize(107,34);
		//super.addListener(onClickClass, onClick);
		
        addListener(new Object() {
            @Subscribe
            public void onHoverChanged(HoverEvent e) {
                requestReassembly();
            }
        });
        
        Button inButton = this;
        
        addListener(new Object() {
            @Subscribe
            public void onLeftClick(KeyEvent e) {
            	if (e.isLeftMouseButton())
            	{
            		isClicked = e.isPress();
            		onClick.accept(inButton);
                	requestReassembly();
            	}
            }
        });
	}
	
	public boolean isClicked()
	{
		return containsCursor() && isClicked;
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
			target.fill(getX()+2, getY()+2, getWidth()-4, getHeight()-4, 0xFFC3E4F7);
		}
		else if (!isClicked())
		{
			target.fill(getX()+2, getY()+2, getWidth()-4, getHeight()-4, Colors.WHITE);
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
