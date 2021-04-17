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
import ru.windcorp.progressia.client.graphics.gui.event.FocusEvent;
import ru.windcorp.progressia.client.graphics.gui.event.HoverEvent;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutAlign;
import ru.windcorp.progressia.client.graphics.input.bus.InputListener;
import ru.windcorp.progressia.client.graphics.input.InputEvent;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;

public class Button extends Component {

	private Vec2i currentSize;
	private boolean isDisabled;
	private boolean isClicked;
	private Label label;
	private LayoutAlign align;
	
	public <T extends InputEvent> Button(String name, Label textLabel, Consumer<Button> onClick) {//, InputListener<T> onClick, Class<? extends T> onClickClass) {
		super(name);
		label = textLabel;
		align = new LayoutAlign();
		addChild(textLabel);
		setPreferredSize(107,34);
        Button inButton = (Button) setFocusable(true);
		
        addListener(new Object() {
            @Subscribe
            public void onHoverChanged(HoverEvent e) {
                requestReassembly();
            }
        });
        
        addListener(new Object() {
            @Subscribe
            public void onFocusChanged(FocusEvent e) {
            	inButton.setText(new Label("dummy",new Font().withColor(Colors.BLACK),e.getNewState() ? "Is Focused" : "Isn't focused"));
                requestReassembly();
            }
        });
        
        addListener((Class<KeyEvent>) KeyEvent.class, (InputListener<KeyEvent>) e -> {if (e.isLeftMouseButton() && inButton.containsCursor())
        	{
        		isClicked = e.isPress();
        		if (!inButton.isDisabled())
        		{
        			onClick.accept(inButton);
        		}
        		requestReassembly();
        	}
        	else if (e.isLeftMouseButton())
        	{
        		setFocused(false);
        	}
		  	return true;});
        							
	}
	
	public boolean isClicked()
	{
		return containsCursor() && isClicked;
	}
	
	public void setDisable(boolean isDisabled)
	{
		this.isDisabled = isDisabled;
        setFocusable(isDisabled);
	}
	
	public boolean isDisabled()
	{
		return isDisabled;
	}
	
	public void setText(Label newText)
	{
		removeChild(label);
		label = newText;
		addChild(label);
		requestReassembly();
	}
	
	@Override
	protected void assembleSelf(RenderTarget target) {
		//Border
		if (isDisabled())
		{
			target.fill(getX(), getY(), getWidth(), getHeight(), 0xFFE5E5E5);
		}
		else if (isClicked() || isHovered() || isFocused())
		{
			target.fill(getX(), getY(), getWidth(), getHeight(), 0xFF37A2E6);
		}
		else
		{
			target.fill(getX(), getY(), getWidth(), getHeight(), 0xFFCBCBD0);
		}
		//Inside area
		if (!isClicked() && isHovered() && !isDisabled())
		{
			target.fill(getX()+2, getY()+2, getWidth()-4, getHeight()-4, 0xFFC3E4F7);
		}
		else if (!isClicked() || isDisabled())
		{
			target.fill(getX()+2, getY()+2, getWidth()-4, getHeight()-4, Colors.WHITE);
		}
		Font tempFont = new Font().withColor(Colors.BLACK);
		if (isDisabled())
		{
			tempFont = tempFont.withColor(Colors.GRAY_A);
		}
		else if (isClicked())
		{
			tempFont = tempFont.withColor(Colors.WHITE);
		}
		
		target.pushTransform(new Mat4().identity().translate( getX()+.5f*getWidth()-.5f*label.getPreferredSize().x, getY(), 0));
		label = new Label(label.getName(), tempFont, label.getContentSupplier());
		label.assembleSelf(target);
		target.popTransform();
	}
}
