package ru.windcorp.progressia.test;

import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.GUI;
import ru.windcorp.progressia.client.graphics.font.Font;
import ru.windcorp.progressia.client.graphics.gui.*;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutAlign;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutVertical;
import ru.windcorp.progressia.client.graphics.texture.SimpleTextures;
import ru.windcorp.progressia.client.localization.Localizer;
import ru.windcorp.progressia.client.localization.MutableString;
import ru.windcorp.progressia.client.localization.MutableStringLocalized;

import java.util.List;

public class LayerOptions extends Background {
    public LayerOptions(String name) {
        super(name, new LayoutAlign(0, 1f, 15), SimpleTextures.get("title/background"));

        Group content = new Group("Layer" + name + ".Group", new LayoutVertical(15));

        Font font = new Font().withColor(Colors.BLUE).withAlign(0.5f);

        MutableString languageText = new MutableStringLocalized("Layer" + name + ".Language");
        content.addChild(new Button(name + ".Language", new Label(name + ".Language", font, languageText)).addAction(this::toggleLanguage));

        MutableString playText = new MutableStringLocalized("Layer" + name + ".Return");
        content.addChild(new Button(name + ".Return", new Label(name + ".Return", font, playText)).addAction(this::openTitle));

        getRoot().addChild(content);
    }

    private void openTitle(BasicButton basicButton) {
        GUI.removeLayer(this);
        GUI.addTopLayer(new LayerTitle("Title"));
    }

    private void toggleLanguage(BasicButton basicButton)
    {
        String curLang = Localizer.getInstance().getLanguage();
        List<String> allLangs = Localizer.getInstance().getLanguages();
        int pos = allLangs.indexOf(curLang);
        pos++;
        if (pos >= allLangs.size())
        {
            Localizer.getInstance().setLanguage(allLangs.get(0));
        }
        else
        {
            Localizer.getInstance().setLanguage(allLangs.get(pos));
        }
    }
}
