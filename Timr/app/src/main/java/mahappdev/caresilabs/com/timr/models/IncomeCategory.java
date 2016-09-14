package mahappdev.caresilabs.com.timr.models;

import mahappdev.caresilabs.com.timr.R;

/**
 * Created by Simon on 9/10/2016.
 */
public enum IncomeCategory {
    LIFE(R.drawable.ic_icon_life), FREETIME(R.drawable.ic_icon_freetime), OTHER(R.drawable.ic_icon_other);

    private final int icon;

    IncomeCategory(int icon) {
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }
}
