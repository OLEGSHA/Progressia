package ru.windcorp.progressia.client.graphics.gui;

import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;

import glm.mat._4.Mat4;
import glm.vec._2.i.Vec2i;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.flat.RenderTarget;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.client.graphics.input.bus.InputListener;

public class RadioButton extends Interactable {
	private RadioManager manager;
	private boolean isSelected;

	public RadioButton(String name, Label textLabel, Consumer<RadioButton> onSelect, RadioManager myManager)
	{
		super(name, textLabel);
		setPreferredSize(textLabel.getPreferredSize().x+23,textLabel.getPreferredSize().y);
		manager = myManager;
		manager.addOption(this);
		
		addListener((Class<KeyEvent>) KeyEvent.class, (InputListener<KeyEvent>) e -> {if ((e.isLeftMouseButton() && containsCursor()) || (e.getKey()==GLFW.GLFW_KEY_ENTER && isFocused()) )
    	{
    		isClicked = e.isPress();
    		if (!isDisabled() && !isClicked)
    		{
    			onSelect.accept(this);
    			manager.selectSelf(this);
    		}
    		requestReassembly();
    		return true;
    	}
	  	return false;});
	}
	
	public boolean isSelected()
	{
		return isSelected;
	}
	
	public void setSelected(boolean selected)
	{
		isSelected = selected;
	}
	
	protected void assembleSelf(RenderTarget target) {
		if (isDisabled())
		{
			target.fill(getX()+getWidth()-getHeight(), getY(), getHeight(), getHeight(), 0xFFE5E5E5);
		}
		else if (isClicked() || isHovered() || isFocused())
		{
			target.fill(getX()+getWidth()-getHeight(), getY(), getHeight(), getHeight(), 0xFF37A2E6);
		}
		else
		{
			target.fill(getX()+getWidth()-getHeight(), getY(), getHeight(), getHeight(), 0xFFCBCBD0);
		}
		//Inside area
		if (!isClicked() && isHovered() && !isDisabled())
		{
			target.fill(getX()+getWidth()-getHeight()+2, getY()+2, getHeight()-4, getHeight()-4, 0xFFC3E4F7);
		}
		else if (!isClicked() || isDisabled())
		{
			target.fill(getX()+getWidth()-getHeight()+2, getY()+2, getHeight()-4, getHeight()-4, Colors.WHITE);
		}
		if (isSelected())
		{
			if (!isDisabled())
			{
				target.fill(getX()+getWidth()-getHeight()+4, getY()+4, getHeight()-8, getHeight()-8, 0xFF37A2E6);
			}
			else
			{
				target.fill(getX()+getWidth()-getHeight()+4, getY()+4, getHeight()-8, getHeight()-8, 0xFFC3E4F7);
			}
		}
		
		target.pushTransform(new Mat4().identity().translate( getX(), getY(), 0));
		label.assembleSelf(target);
		target.popTransform();
	}
}
