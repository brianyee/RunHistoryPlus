package runhistoryplus.ui;

import com.megacrit.cardcrawl.screens.runHistory.RunPathElement;

public class RunTooltipHooks {
    public static void replacingCardChoices(RunPathElement element, StringBuilder sb) {
        MultipleCardRewardsTooltip.buildChoices(element, sb);
    }
    public static void replacingCardSkips(RunPathElement element, StringBuilder sb) {
        MultipleCardRewardsTooltip.buildSkips(element, sb);
    }

    public static void withSkippedRewards(RunPathElement element, StringBuilder sb) {
        RewardsSkippedTooltip.build(element, sb);
    }

    public static void atEnd(RunPathElement element, StringBuilder sb) {
        FallingOptionsTooltip.build(element, sb);
        ShopContentsTooltip.build(element, sb);
        BlueKeyRelicSkippedTooltip.build(element, sb);
        PotionUseAndDiscardTooltip.build(element, sb);
        FloorExitPlaytimeTooltip.build(element, sb);
    }
}