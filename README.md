# Senioren Beheerder 🛡️

[![Open Source Love](https://badges.frapsoft.com/os/v1/open-source.svg?v=103)](https://github.com/cma58/SeniorenBeheerder)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Android 15+](https://img.shields.io/badge/Android-15%2B-green.svg)](https://developer.android.com)

Een krachtige, moderne Android-applicatie ontworpen voor mantelzorgers om de veiligheid en het toestelgebruik van senioren op afstand te beheren via een robuust SMS-commandosysteem. 

**100% Gratis & Open Source – Geen trackers, geen abonnementen, gewoon pure rust voor de familie.**

---

## ✨ Nieuw in Versie 1.1

*   **Modern Material 3 Design**: Een volledige visuele update met een professionele interface, geoptimaliseerd voor de nieuwste Android toestellen.
*   **Intelligente Setup Flow**: Gebruikers worden direct bij de eerste start begeleid bij machtigingen en configuratie.
*   **Data Persistentie**: Instellingen worden veilig lokaal opgeslagen.
*   **Live Dashboard**: Direct overzicht van batterij, netwerk en volume.
*   **Locatie Integratie**: Interactieve OpenStreetMap (OSM) weergave met directe focus.

## 🌟 Werkende Functionaliteiten (100%)

- **Dashboard**: Real-time statusoverzicht van het toestel van de senior.
- **Setup-Wizard**: Snelle configuratie van machtigingen en telefoonnummer.
- **Locatie Opvragen**: Directe weergave van de GPS-positie op de kaart (`#WAAR`).
- **Snelle Interactie**: Directe knoppen voor "Bel mij terug" en "Zoek toestel (Roepen)".
- **Afstandsbediening**:
    - Zaklamp bediening (Aan/Uit/Knipperen).
    - Tekstberichten direct op het scherm van de senior tonen.
    - Volledige status synchronisatie met één druk op de knop.

## 📜 Geteste Commando's

| Commando | Actie |
| :--- | :--- |
| `#WAAR` | Toont de actuele GPS-positie op de kaart |
| `#STATUS` | Update batterij, volume en netwerkgegevens |
| `#BEL_TERUG` | Laat de senior automatisch naar u bellen |
| `LAUN_ZOEK` | Activeert een luid geluidssignaal op het toestel |
| `#LAMP ON/OFF` | Schakelt de zaklamp op afstand aan of uit |
| `#BERICHT [tekst]` | Toont een pop-up bericht op het scherm van de senior |

## 🛠️ Gebruikte Technieken

- **Jetpack Compose**: Voor een snelle en vloeiende gebruikerservaring.
- **Material 3**: Volgens de modernste Google design-standaarden.
- **SharedPreferences**: Voor veilige lokale opslag.
- **Osmdroid**: Privacy-vriendelijke kaarten zonder Google-tracking.

## 🚀 Aan de slag

1.  **Machtigingen**: Geef toegang tot SMS en Telefoon-status bij de eerste start.
2.  **Configuratie**: Voer het nummer van de senior in via de wizard.
3.  **Beheer**: U bent klaar om de senior op afstand te ondersteunen.

---

## ❤️ Ondersteun dit project

Senioren Beheerder is en blijft **100% gratis en open source**. Wij geloven dat veiligheid voor ouderen een recht is, geen luxe.

Vindt u de app nuttig? Overweeg dan een kleine donatie om de verdere ontwikkeling te steunen. Elke bijdrage wordt enorm gewaardeerd!

[![Donate via PayPal](https://img.shields.io/badge/Donate-PayPal-blue.svg?style=for-the-badge&logo=paypal)](https://www.paypal.com/donate/?business=amine.chtaiti@gmail.com&no_recurring=0&currency_code=EUR)

---
*Ontwikkeld met zorg voor maximale rust en veiligheid voor zowel de senior als de verzorger.*
