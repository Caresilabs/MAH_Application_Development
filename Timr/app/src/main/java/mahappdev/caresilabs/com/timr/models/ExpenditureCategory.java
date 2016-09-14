package mahappdev.caresilabs.com.timr.models;

import mahappdev.caresilabs.com.timr.R;

/**
 * Created by Simon on 9/10/2016.
 */
public enum ExpenditureCategory {
    SLEEP(R.drawable.ic_icon_sleep), SCHOOL(R.drawable.ic_icon_school), WORKOUT(R.drawable.ic_icon_workout),
        PROGRAMMING(R.drawable.ic_icon_programming), READING(R.drawable.ic_icon_reading), EATING(R.drawable.ic_icon_eat);

    private final int icon;

    ExpenditureCategory(int icon) {
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }
}
