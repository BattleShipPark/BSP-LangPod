package com.battleshippark.bsp_langpod.presentation;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 */

public class EpisodeDateFormat extends SimpleDateFormat {
    public EpisodeDateFormat() {
        super("MM/dd", Locale.US);
        setTimeZone(TimeZone.getDefault());
    }
}
