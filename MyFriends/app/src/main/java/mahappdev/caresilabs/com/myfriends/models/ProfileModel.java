package mahappdev.caresilabs.com.myfriends.models;

/**
 * Created by Simon on 10/5/2016.
 */

public class ProfileModel {

    public enum Language {
         ENGLISH("en"), SWEDISH("sv");

        private String code;

        Language(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    public String alias;

    public Language language;
}
