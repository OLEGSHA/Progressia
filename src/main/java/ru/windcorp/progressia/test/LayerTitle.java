package ru.windcorp.progressia.test;

import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.GUI;
import ru.windcorp.progressia.client.graphics.font.Font;
import ru.windcorp.progressia.client.graphics.gui.BasicButton;
import ru.windcorp.progressia.client.graphics.gui.Button;
import ru.windcorp.progressia.client.graphics.gui.GUILayer;
import ru.windcorp.progressia.client.graphics.gui.Label;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutVertical;
import ru.windcorp.progressia.client.localization.MutableString;
import ru.windcorp.progressia.client.localization.MutableStringLocalized;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.server.ServerState;

import java.io.IOException;

public class LayerTitle extends GUILayer {

	public LayerTitle(String name) {
		super(name, new LayoutVertical(20, 10));

		MutableString title = new MutableStringLocalized("Layer" + name + ".Title");
		Font titleFont = new Font().deriveBold().withColor(Colors.BLACK).withAlign(0.5f);
		getRoot().addChild(new Label(name + ".Title", titleFont, title));

		Font buttonFont = titleFont;
		MutableString playText = new MutableStringLocalized("Layer" + name + ".Play");
		getRoot().addChild(new Button(name + ".Play", new Label(name + ".Play", buttonFont, playText)).addAction(this::startGame));

		MutableString quitText = new MutableStringLocalized("Layer" + name + ".Quit");
		getRoot().addChild(new Button(name + "Quit", new Label(name + ".Quit", buttonFont, quitText)).addAction(b -> {
			System.exit(0);
		}));
	}

	private void startGame(BasicButton basicButton) {
		GUI.removeLayer(this);
		try {
			ServerState.startServer();
			ClientState.connectToLocalServer();
		} catch (IOException e) {
			throw CrashReports.report(e, "Problem with loading server");
		}
	}

}
