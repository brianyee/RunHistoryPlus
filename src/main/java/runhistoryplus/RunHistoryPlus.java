package runhistoryplus;

import basemod.BaseMod;
import basemod.ModPanel;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.UIStrings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import runhistoryplus.patches.NeowBonusLog;

import static com.megacrit.cardcrawl.core.Settings.GameLanguage;
import static com.megacrit.cardcrawl.core.Settings.language;

@SpireInitializer
public class RunHistoryPlus implements
        PostInitializeSubscriber,
        EditStringsSubscriber {
    private static final Logger logger = LogManager.getLogger(RunHistoryPlus.class.getName());

    public RunHistoryPlus() {
        BaseMod.subscribe(this);
    }

    public static void initialize() {
        new RunHistoryPlus();
    }

    @Override
    public void receivePostInitialize() {
        Texture badgeTexture = new Texture("runhistoryplus/images/RunHistoryPlusBadge.png");
        BaseMod.registerModBadge(badgeTexture, "Run History Plus", "modargo", "Adds additional information to run history.", new ModPanel());

        BaseMod.addSaveField(NeowBonusLog.SaveKey, new NeowBonusLog());
    }

    private static String makeLocPath(Settings.GameLanguage language, String filename)
    {
        String ret = "localization/";
        switch (language) {
            default:
                ret += "eng";
                break;
        }
        return "runhistoryplus/" + ret + "/" + filename + ".json";
    }

    private void loadLocFiles(GameLanguage language)
    {
        BaseMod.loadCustomStringsFile(UIStrings.class, makeLocPath(language, "RunHistoryPlus-ui"));
    }

    @Override
    public void receiveEditStrings()
    {
        loadLocFiles(GameLanguage.ENG);
        if (language != GameLanguage.ENG) {
            loadLocFiles(language);
        }
    }

    public static String uiImage(String id) {
        return "runhistoryplus/images/ui/" + removeModId(id) + ".png";
    }

    public static String removeModId(String id) {
        if (id.startsWith("RunHistoryPlus:")) {
            return id.substring(id.indexOf(':') + 1);
        } else {
            logger.warn("Missing mod id on: " + id);
            return id;
        }
    }
}