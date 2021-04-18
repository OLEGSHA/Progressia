package ru.windcorp.progressia.client.graphics.gui;

import com.google.common.eventbus.Subscribe;

import ru.windcorp.progressia.client.graphics.gui.event.FocusEvent;
import ru.windcorp.progressia.client.graphics.gui.event.HoverEvent;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutAlign;

public class Interactable extends Component {
	
	private Vec2i currentSize;
	protected boolean isDisabled;
	protected boolean isClicked;
	protected Label label;
	
	public Interactable(String name, Label textLabel)
	{
		super(name);
		label = textLabel;
		addChild(textLabel);
		
		addListener(new Object() {
            @Subscribe
            public void onHoverChanged(HoverEvent e) {
                requestReassembly();
            }
        });
        
        addListener(new Object() {
            @Subscribe
            public void onFocusChanged(FocusEvent e) {
            	//inButton.setText(new Label("dummy",new Font().withColor(Colors.BLACK),e.getNewState() ? "Is Focused" : "Isn't focused"));
                requestReassembly();
            }
        });
	}
	
	public boolean isClicked()
	{
		return isClicked;
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
}
