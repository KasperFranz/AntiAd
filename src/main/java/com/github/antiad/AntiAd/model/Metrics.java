package com.github.antiad.AntiAd.model;

public class Metrics {
    public Metrics(){

        org.bstats.bukkit.Metrics metrics = new org.bstats.bukkit.Metrics(Core.instance().getPlugin());


        metrics.addCustomChart(new org.bstats.bukkit.Metrics.SimplePie("spam_detection", () -> Core.instance().getConfig().isSpamDetection() ? "yes" : "No"));
        metrics.addCustomChart(new org.bstats.bukkit.Metrics.SimplePie("spam_detection", () -> Core.instance().getConfig().isSpamDetection() ? "yes" : "No"));
        metrics.addCustomChart(new org.bstats.bukkit.Metrics.SimplePie("notify_public", () -> Core.instance().getConfig().isNotifyMessage() ? "yes" : "No"));
        metrics.addCustomChart(new org.bstats.bukkit.Metrics.SimplePie("ip_detection", () -> Core.instance().getConfig().isIPDetection() ? "yes" : "No"));
        metrics.addCustomChart(new org.bstats.bukkit.Metrics.SimplePie("check_word_length", () -> Core.instance().getConfig().isCheckWordLenght() ? "yes" : "No"));
        metrics.addCustomChart(new org.bstats.bukkit.Metrics.SimplePie("numbers", () -> Core.instance().getConfig().getNumbers()+ ""));
        metrics.addCustomChart(new org.bstats.bukkit.Metrics.SimplePie("procentalCapital", () -> Core.instance().getConfig().getProcentCapital()+ ""));
        metrics.addCustomChart(new org.bstats.bukkit.Metrics.SimplePie("language", () -> Core.instance().getConfig().getLanguage()));
    }
}
