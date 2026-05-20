# Senioren Beheerder 🛡️

Een krachtige, moderne Android-applicatie ontworpen voor mantelzorgers om de veiligheid en het toestelgebruik van senioren op afstand te beheren via een robuust SMS-commandosysteem.

## ✨ Nieuw in deze Versie (V1.1)

- **Modern Material 3 Design**: Een volledige visuele update met een professionele, card-based interface, geoptimaliseerd voor Android 15+.
- **Intelligente Setup Flow**: Gebruikers worden nu direct bij de eerste start begeleid bij het toekennen van machtigingen en het instellen van het telefoonnummer.
- **Data Persistentie**: Het telefoonnummer van de senior wordt nu veilig opgeslagen op het toestel, zodat de app direct klaar is voor gebruik bij elke herstart.
- **Live Status Widgets**: Direct overzicht van batterijniveau, netwerksterkte en volume in een overzichtelijk dashboard.
- **Locatie Integratie**: Verbeterde OpenStreetMap (OSM) weergave met interactieve kaart en directe focus op de locatie van de senior.

## 🌟 Werkende Functionaliteiten (100%)

- **Dashboard**: Real-time statusoverzicht van het toestel van de senior.
- **Setup-Wizard**: Begeleiding bij machtigingen en initiële configuratie.
- **Locatie Opvragen**: Directe weergave van de GPS-positie op de kaart via het `#WAAR` commando.
- **Snelle Interactie**: Directe knoppen voor "Bel mij terug" en "Zoek toestel (Roepen)".
- **Afstandsbediening Hulpmiddelen**:
    - Zaklamp aan/uit/knipperen.
    - Tekstberichten direct op het scherm van de senior tonen.
    - Synchronisatie van alle statusgegevens met één druk op de knop.

## 📜 Geteste Commando's

De volgende commando's zijn volledig operationeel en getest:

1.  **Locatie**: `#WAAR` (Toont positie op kaart).
2.  **Status**: `#STATUS` (Update batterij, volume en netwerk).
3.  **Hulp**: `#BEL_TERUG` (Laat de senior u bellen).
4.  **Audio**: `LAUN_ZOEK` (Activeert geluidssignaal).
5.  **Tools**: `#LAMP ON/OFF`, `#BERICHT [tekst]`.

## 🛠️ Gebruikte Technieken

- **Jetpack Compose**: Voor een moderne en snelle gebruikersinterface.
- **Material 3**: Volgens de nieuwste Google design-standaarden.
- **SharedPreferences**: Voor betrouwbare lokale opslag van instellingen.
- **Osmdroid**: Voor privacy-vriendelijke kaartweergave zonder Google-trackers.

## 🚀 Aan de slag

Bij de eerste keer opstarten doorloopt u twee korte stappen:
1.  **Machtigingen**: Geef toegang tot SMS en Telefoon-status (nodig voor beheer op afstand).
2.  **Configuratie**: Voer het nummer van de senior in.

*De app is nu volledig operationeel voor beheer op afstand.*

---
*Ontwikkeld voor maximale rust en veiligheid voor zowel de senior als de verzorger.*
