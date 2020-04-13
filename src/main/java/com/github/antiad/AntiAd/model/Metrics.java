package com.github.antiad.AntiAd.model;

import org.bstats.bukkit.Metrics.SimplePie;

public class Metrics {
    public Metrics(Core core) {
        int pluginId = 7090;
        org.bstats.bukkit.Metrics metrics = new org.bstats.bukkit.Metrics(Core.instance().getPlugin(), pluginId))
        metrics.addCustomChart(new SimplePie("spam_detection", () -> core.getConfig().isSpamDetection() ? "yes" : "No"));
        metrics.addCustomChart(new SimplePie("spam_detection", () -> core.getConfig().isSpamDetection() ? "yes" : "No"));
        metrics.addCustomChart(new SimplePie("notify_public", () -> core.getConfig().isNotifyMessage() ? "yes" : "No"));
        metrics.addCustomChart(new SimplePie("ip_detection", () -> core.getConfig().isIPDetection() ? "yes" : "No"));
        metrics.addCustomChart(new SimplePie("check_word_length", () -> core.getConfig().isCheckWordLenght() ? "yes" : "No"));
        metrics.addCustomChart(new SimplePie("numbers", () -> core.getConfig().getNumbers() + ""));
        metrics.addCustomChart(new SimplePie("procentalCapital", () -> core.getConfig().getProcentCapital() + ""));
        metrics.addCustomChart(new SimplePie("language", () -> core.getConfig().getLanguage()));
    }
}
