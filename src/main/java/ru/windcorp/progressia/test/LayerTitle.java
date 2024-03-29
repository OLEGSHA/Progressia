package ru.windcorp.progressia.test;

import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.GUI;
import ru.windcorp.progressia.client.graphics.font.Font;
import ru.windcorp.progressia.client.graphics.gui.BasicButton;
import ru.windcorp.progressia.client.graphics.gui.Button;
import ru.windcorp.progressia.client.graphics.gui.GUILayer;
import ru.windcorp.progressia.client.graphics.gui.Group;
import ru.windcorp.progressia.client.graphics.gui.Label;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutAlign;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutVertical;
import ru.windcorp.progressia.client.localization.MutableString;
import ru.windcorp.progressia.client.localization.MutableStringLocalized;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.server.ServerState;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class LayerTitle extends GUILayer {
	
	private final BasicButton resetButton;

	public LayerTitle(String name) {
		super(name, new LayoutAlign(0.5f, 0.7f, 15));
		Group content = new Group("Layer" + name + ".Group", new LayoutVertical(15));

		MutableString title = new MutableStringLocalized("Layer" + name + ".Title");
		Font titleFont = new Font().deriveBold().withColor(Colors.BLACK).withAlign(0.5f);
		content.addChild(new Label(name + ".Title", titleFont, title));

		Font buttonFont = titleFont.deriveNotBold();
		MutableString playText = new MutableStringLocalized("Layer" + name + ".Play");
		content.addChild(new Button(name + ".Play", new Label(name + ".Play", buttonFont, playText)).addAction(this::startGame));
		
		MutableString resetText = new MutableStringLocalized("Layer" + name + ".Reset");
		this.resetButton = new Button(name + ".Reset", new Label(name + ".Reset", buttonFont, resetText)).addAction(this::resetWorld);
		content.addChild(resetButton);
		
		updateResetButton();

		MutableString quitText = new MutableStringLocalized("Layer" + name + ".Quit");
		content.addChild(new Button(name + "Quit", new Label(name + ".Quit", buttonFont, quitText)).addAction(b -> {
			System.exit(0);
		}));
		
		getRoot().addChild(content);
	}

	private void updateResetButton() {
		resetButton.setEnabled(Files.exists(Paths.get("tmp_world")));
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
	
	private void resetWorld(BasicButton basicButton) {
		Path rootPath = Paths.get("tmp_world");

		try {
			Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			throw CrashReports.report(e, "Could not reset world");
		}
		
		updateResetButton();
	}

}
