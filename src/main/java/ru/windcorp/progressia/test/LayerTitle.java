package ru.windcorp.progressia.test;

import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.GUI;
import ru.windcorp.progressia.client.graphics.font.Font;
import ru.windcorp.progressia.client.graphics.gui.Background;
import ru.windcorp.progressia.client.graphics.gui.BasicButton;
import ru.windcorp.progressia.client.graphics.gui.Button;
import ru.windcorp.progressia.client.graphics.gui.Group;
import ru.windcorp.progressia.client.graphics.gui.Label;
import ru.windcorp.progressia.client.graphics.gui.TextureComponent;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutAlign;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutEdges;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutVertical;
import ru.windcorp.progressia.client.graphics.texture.SimpleTextures;
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

public class LayerTitle extends Background {
	
	private final BasicButton resetButton;

	public LayerTitle(String name) {
		super(name, new LayoutAlign(0, 1f, 15), SimpleTextures.get("title/background"));
		Group content = new Group("Layer" + name + ".Group", new LayoutVertical(15));
		Group info = new Group("Layer"+name+".InfoGroup", new LayoutEdges(30));
		Group buttonContent = new Group("Layer" + name + ".ButtonGroup", new LayoutColumn(15, 320));

		Font titleFont = new Font().deriveBold().withColor(Colors.BLUE).withAlign(0.5f);
		content.addChild(new TextureComponent(name + ".Title", SimpleTextures.get("title/progressia")));

		info.addChild(new Label(
				"About",
				titleFont,
				new MutableStringLocalized("LayerAbout.Title")
			)
		);

		info.addChild(
				new Label(
						"Version",
						titleFont,
						new MutableStringLocalized("LayerAbout.Version").format(LayerAbout.version)
				)
		);
		content.addChild(info);

		Font buttonFont = titleFont.deriveNotBold();
		MutableString playText = new MutableStringLocalized("Layer" + name + ".Play");
		buttonContent.addChild(new Button(name + ".Play", new Label(name + ".Play", buttonFont, playText)).addAction(this::startGame));
		
		MutableString resetText = new MutableStringLocalized("Layer" + name + ".Reset");
		this.resetButton = new Button(name + ".Reset", new Label(name + ".Reset", buttonFont, resetText)).addAction(this::resetWorld);
		buttonContent.addChild(resetButton);
		
		updateResetButton();

		MutableString quitText = new MutableStringLocalized("Layer" + name + ".Quit");
		buttonContent.addChild(new Button(name + "Quit", new Label(name + ".Quit", buttonFont, quitText)).addAction(b -> {
			System.exit(0);
		}));
		
		content.addChild(buttonContent);
		getRoot().addChild(content);
		buttonContent.setPreferredSize(500, 1000);
		
		CubeComponent cube = new CubeComponent(name+".Cube",300);
		
		getRoot().addChild(cube);
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
