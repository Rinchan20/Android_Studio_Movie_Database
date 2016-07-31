package data;

/**
 * Created by Kiara on 6/4/2016.
 */
public class MovieContract {
    public static final class Review
    {
        public static final String TABLE_NAME = "reviews";

        public static final String _ID = "id";

        public static final String DISPLAY_TITLE = "title";

        public static final String SUMMARY_SHORT = "overview";

        public static final String PUBLISH_DATE = "release_date";

        public static final String REVIEW_SCORE = "vote_average";
    }
}
