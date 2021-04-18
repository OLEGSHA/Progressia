package ru.windcorp.progressia.client.graphics.gui;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RadioManager {
	private List<RadioButton> options;
	private int selectedOption;
	
	public RadioManager()
	{
		options = Collections.synchronizedList(new CopyOnWriteArrayList<>());
	}
	
	public void addOption(RadioButton option)
	{
		options.add(option);
	}
	
	public int getSelected()
	{
		return selectedOption;
	}
	
	public void selectSelf(RadioButton option)
	{
		if (!options.contains(option))
		{
			return;
		}
		options.get(selectedOption).setSelected(false);
		selectedOption = options.indexOf(option);
		option.takeFocus();
		option.setSelected(true);
	}
}
