package runhistoryplus.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.blue.GeneticAlgorithm;
import com.megacrit.cardcrawl.cards.colorless.RitualDagger;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.metrics.Metrics;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.screens.runHistory.RunHistoryScreen;
import com.megacrit.cardcrawl.screens.runHistory.TinyCard;
import com.megacrit.cardcrawl.screens.stats.RunData;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtField;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImprovableCardRunHistoryPatch {
    private static Map<String, List<Integer>> improvableCardsLog;
    private static final ArrayList<String> improvableCards = new ArrayList<String>() {{
        add(GeneticAlgorithm.ID);
        add(RitualDagger.ID);
    }};

    @SpirePatch(clz = CardCrawlGame.class, method = SpirePatch.CONSTRUCTOR)
    public static class ImprovableCardsField {
        @SpireRawPatch
        public static void addImprovableCardsField(CtBehavior ctBehavior) throws NotFoundException, CannotCompileException {
            CtClass runData = ctBehavior.getDeclaringClass().getClassPool().get(RunData.class.getName());
            String fieldSource = "public java.util.Map improvable_cards;";
            CtField field = CtField.make(fieldSource, runData);
            runData.addField(field);
        }
    }

    @SpirePatch(clz = Metrics.class, method = "gatherAllData")
    public static class GatherAllDataPatch {
        @SpirePostfixPatch
        public static void gatherAllDataPatch(Metrics __instance, boolean death, boolean trueVictor, MonsterGroup monsters) {
            improvableCardsLog = new HashMap<>();
            for (final AbstractCard card: AbstractDungeon.player.masterDeck.group) {
                if (improvableCards.contains(card.cardID)) {
                    if (!improvableCardsLog.containsKey(card.name)) {
                        improvableCardsLog.put(card.name, new ArrayList<>());
                    }
                    improvableCardsLog.get(card.name).add(card.misc);
                }
            }
            ReflectionHacks.privateMethod(Metrics.class, "addData", Object.class, Object.class)
                    .invoke(__instance, "improvable_cards", improvableCardsLog);
        }
    }

    @SpirePatch(clz = RunHistoryScreen.class, method = "update")
    public static class RunHistoryScreenUpdatePatch {
        @SpireInsertPatch(locator = Locator.class, localvars = "addMe")
        public static void updateCardsWithFinalValues(RunHistoryScreen __instance, RunData ___viewedRun, TinyCard addMe) throws NoSuchFieldException, IllegalAccessException {
            if (improvableCards.contains(addMe.card.cardID)) {
                Field improvableCardsField = ___viewedRun.getClass().getField("improvable_cards");
                Map<String, List<Double>> improvableCardsLog = (Map<String, List<Double>>)improvableCardsField.get(___viewedRun);
                if (improvableCardsLog != null && improvableCardsLog.containsKey(addMe.card.name)) {
                    Integer value = Collections.max(improvableCardsLog.get(addMe.card.name)).intValue();
                    addMe.card.baseDamage = value;
                    addMe.card.baseBlock = value;
                }
            }
        }

        public static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher matcher = new Matcher.MethodCallMatcher(CardGroup.class, "addToTop");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<>(), matcher);
            }
        }
    }
}
