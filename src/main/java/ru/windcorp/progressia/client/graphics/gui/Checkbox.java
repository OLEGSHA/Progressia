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

public class Checkbox extends Interactable {

	private boolean isActive;
	
	public <T extends InputEvent> Checkbox(String name, Label textLabel, Consumer<Checkbox> onSet, Consumer<Checkbox> onReset) {//, InputListener<T> onClick, Class<? extends T> onClickClass) {
		super(name, textLabel);
		setPreferredSize(44 + textLabel.getPreferredSize().x,textLabel.getPreferredSize().y);
        //Checkbox inCheck = (Checkbox) setFocusable(true);
        
        addListener((Class<KeyEvent>) KeyEvent.class, (InputListener<KeyEvent>) e -> {if (e.isLeftMouseButton() && containsCursor()|| (e.getKey()==GLFW.GLFW_KEY_ENTER && isFocused()))
        	{
        		isClicked = e.isPress();
        		if (!isDisabled())
        		{
        			if (!isClicked && !isActive)
        			{
        				onSet.accept(this);
            			isActive = !isActive;
        			}
        			else if (!isClicked && isActive)
        			{
        				onReset.accept(this);
            			isActive = !isActive;
        			}
        			else if (isClicked)
        			{
        				takeFocus();
        			}
        		}
        		requestReassembly();
        		return true;
        	}
		  	return false;});			
	}
	
	public boolean isActive()
	{
		return isActive;
	}
	
	@Override
	protected void assembleSelf(RenderTarget target) {
		//Border
		if (isDisabled())
		{
			target.fill(getX()+label.getPreferredSize().x, getY(), getWidth()-label.getPreferredSize().x, getHeight(), 0xFFE5E5E5);
		}
		else if (isClicked() || isHovered() || isFocused())
		{
			target.fill(getX()+label.getPreferredSize().x, getY(), getWidth()-label.getPreferredSize().x, getHeight(), 0xFF37A2E6); // blue
		}
		else
		{
			target.fill(getX()+label.getPreferredSize().x, getY(), getWidth()-label.getPreferredSize().x, getHeight(), 0xFFCBCBD0);
		}
		//Inside area
		if (!isClicked() && isHovered() && !isDisabled())
		{
			target.fill(getX()+2+label.getPreferredSize().x, getY()+2, getWidth()-label.getPreferredSize().x-4, getHeight()-4, 0xFFC3E4F7); // light blue
		}
		else if (!isClicked() || isDisabled())
		{
			target.fill(getX()+2+label.getPreferredSize().x, getY()+2, getWidth()-label.getPreferredSize().x-4, getHeight()-4, Colors.WHITE);
		}
		if (isActive() && !isClicked())
		{
			if (isDisabled())
			{
				target.fill(getX()+getWidth()-getHeight()+4, getY()+4, getHeight()-8, getHeight()-8, 0xFFB3D7EF);
			}
			else
			{
				target.fill(getX()+getWidth()-getHeight()+4, getY()+4, getHeight()-8, getHeight()-8, 0xFF37A2E6); // blue
			}
		}
		else if (!isClicked())
		{
			if (isDisabled())
			{
				target.fill(getX()+label.getPreferredSize().x+4, getY()+4, getHeight()-8, getHeight()-8, 0xFFE5E5E5);
			}
			else if (isFocused() || isHovered())
			{
				target.fill(getX()+label.getPreferredSize().x+4, getY()+4, getHeight()-8, getHeight()-8, 0xFF37A2E6); // blue
			}
			else
			{
				target.fill(getX()+label.getPreferredSize().x+4, getY()+4, getHeight()-8, getHeight()-8, 0xFFCBCBD0);
			}
			target.fill(getX()+label.getPreferredSize().x+6, getY()+6, getHeight()-12, getHeight()-12, Colors.WHITE);
		}
		target.pushTransform(new Mat4().identity().translate( getX(), getY(), 0));
		label.assembleSelf(target);
		target.popTransform();
	}
}
