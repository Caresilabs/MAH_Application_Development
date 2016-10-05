package mahappdev.caresilabs.com.myfriends;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

import mahappdev.caresilabs.com.myfriends.models.ProfileModel;

/**
 * Created by Simon on 10/5/2016.
 */

public class LocaleHelper {

    private static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";

    public static void onCreate(Context context, ProfileModel.Language language) {
        updateResources(context, language);
    }

    private static void updateResources(Context context, ProfileModel.Language language) {
        if (language == null)
            return;

        try {
            Locale locale = new Locale(language.getCode());
            Locale.setDefault(locale);

            Resources resources = context.getResources();

            Configuration configuration = resources.getConfiguration();
            configuration.setLocale(locale);

            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        } catch (Exception e) {

        }
    }
}