package com.github.antiad.AntiAd.model;

import java.text.SimpleDateFormat;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Level;

public class Logger {


    /**
     *
     * Save the message to the log if the server have that in the config, by default it does this.
     *
     * @param player the playername of the player
     * @param type the type of the messsage (advertisement, spam etc.)
     * @param message The message the player is spamming/ advertisement)
     * @param where Where was it spammed, so we can see that for future problems.
     */
    public void log(Core core,String player,String type,String message,String where) {
        core.debug("Begin to log:" + message);
        if(core.getConfig().getLog()){
            try {
                try (BufferedWriter write = new BufferedWriter(new FileWriter("plugins/AntiAd/Log.txt", true))) {
                    write.append(now("[yyyy-MM-dd HH:mm:ss]"))
                            .append(" - ")
                            .append(player)
                            .append(" - ")
                            .append(type)
                            .append(" - ")
                            .append(where)
                            .append(" - ")
                            .append(message);
                    write.newLine();
                    write.flush();
                }
            } catch (IOException ex) {
                core.logMessage(Level.WARNING,core.getMessage("ERRORLogSave").replace("%MESSAGE%", ex.getMessage()));
            }
        }
    }

    /**
     * A Help method to get the current time-
     *
     * @param dateFormat the number format you want back ex. MMM dd,yyyy HH:mm
     * @return the String of the calendar in the dateFormat you wanted.
     */
    private static String now(String dateFormat) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(cal.getTime());

    }
}
