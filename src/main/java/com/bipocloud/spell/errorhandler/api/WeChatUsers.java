package com.bipocloud.spell.errorhandler.api;

import java.util.Map;
import static java.util.Map.entry;

public final class WeChatUsers {
    private static final Map<String, String> IDS = Map.ofEntries(
        entry("sky.wang@biposervice.com", "13564037236"),
        entry("breaker.yan@biposervice.com", "13545356953"),
        entry("drummond.zhuang@biposervice.com", "18190741065"),
        entry("damu.duan@biposervice.com", "15735881003"),
        entry("feng.feng@biposervice.com", "15935144396"),
        entry("fran.chen@biposervice.com", "15202165902"),
        entry("harry.liu.by@biposervice.com", "15620970529"),
        entry("ivan.huang@biposervice.com", "17602179306"),
        entry("jeffry.yu@biposervice.com", "16619976840"),
        entry("mario.wang@biposervice.com", "15802186834"),
        entry("mouse.wang@biposervice.com", "19521269702"),
        entry("saco.song@biposervice.com", "13636481245")
    );

    private WeChatUsers() {}

    public static String userId(String email) {
        return IDS.getOrDefault(email, "");
    }
}
