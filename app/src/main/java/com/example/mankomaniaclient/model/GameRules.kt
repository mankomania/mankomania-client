package com.example.mankomaniaclient.model

data class GameRules(
    val title: String,
    val sections: List<RuleSection>,
    // val gameComponents: List<String>
) {
    data class RuleSection(
        val heading: String,
        val content: String
    )
/*
    companion object {
        fun defaultRules(): GameRules {
            return GameRules(
                title = "Mankomania - Spielanleitung",
                gameComponents = listOf(
                    "Spielbrett", "Spielgeld", "Aktienzertifikate",
                    "4 Hotels", "2 Würfel", "Roulette-Rad", "Pferderennbahn", "Lotterie"
                ),
                // NOCH ZU ÜBERARBEITEN !!
                sections = listOf(
                    RuleSection("Ziel des Spiels", "Als erster Spieler eine Million verjubeln"),
                    RuleSection("Spielaufbau", "1. Setzen Sie den Plastikeinsatz"),
                    RuleSection("Aktien", "Kurzschluss-Versorgungs-AG, Trockenöl-AG, Bruchstahl-AG"),
                    RuleSection("Spielablauf", "1. In jedem Durchgang werden beide Würfel geworfen..."),
                    RuleSection("Die Aktien-Börse", "Wer zur Aktien-Börse kommt..."),
                    RuleSection("Das Pferderennen", "Wetten zwischen 5.000,- und 50.000,-...")
                )
            )
        }
    }*/
}