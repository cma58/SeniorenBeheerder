# Senioren Beheerder 🛡️

Een krachtige, moderne Android-applicatie ontworpen voor mantelzorgers om de veiligheid en het toestelgebruik van senioren op afstand te beheren via een robuust SMS-commandosysteem.

## ✨ Nieuw in deze Versie

- **Android 17 / Material 3 Design**: Een volledige visuele make-over met een professionele, card-based interface, dynamische kleuren en verbeterde typografie.
- **Geautomatiseerde Setup**: Nieuwe gebruikers worden nu begeleid door een setup-flow bij de eerste start, inclusief machtigingen-check en configuratie van het telefoonnummer.
- **Persistentie**: Belangrijke instellingen zoals het telefoonnummer van de senior worden nu veilig opgeslagen op het toestel.

## 🌟 Belangrijkste Functionaliteiten

- **Modern Dashboard**: Direct inzicht in batterijniveau, netwerkstatus en volume in een strak overzicht.
- **Locatiebeheer**: Real-time kaartweergave via OpenStreetMap (OSM) met een naadloze, moderne integratie.
- **Veiligheid & SOS**: Forceer op afstand SOS-procedures, stuur wellness checks of activeer de luidspreker.
- **Anti-Scam**: Blokkeer ongewenste oproepen van onbekende nummers op afstand.
- **Zorg & Planning**: Beheer medicijnherinneringen, agenda-afspraken en wekkers.
- **Systeembeheer**: Pas op afstand instellingen aan zoals Wi-Fi, Bluetooth en helderheid.

## 📜 Overzicht Commando's

De app ondersteunt een breed scala aan commando's:

1.  **Veiligheid & Locatie**: `#WAAR`, `#SOS_NU`, `#PING`, `#BEL_TERUG`, `#SPEAKER`, `#VEILIG ON/OFF`.
2.  **Systeem & Connectiviteit**: `#WIFI`, `#BT`, `#STIL`, `#VOLUME`, `#HELDER`, `#RESTART`.
3.  **UI Beheer**: `#LETTER [1-5]`, `#THEMA [1-3]`, `#SLOT ON/OFF`, `#BERICHT [tekst]`.
4.  **Zorg & Planning**: `#MEDICIJN`, `#AGENDA`, `#WEKKER`, `#RADIO_STOP`.
5.  **Diagnose**: `#STATUS`, `#INFO_PLUS`, `#PRIVACY`, `#NETWERK`.

## 🛠️ Technieken

- **Taal**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Architectuur**: MVVM (ViewModel & State)
- **Kaarten**: OpenStreetMap via `osmdroid`
- **Persistentie**: SharedPreferences voor configuratiebeheer
- **CI/CD**: GitHub Actions voor automatische APK generatie

## 🚀 Installatie & Gebruik

Bij de eerste start controleert de app automatisch op de benodigde machtigingen (SMS, Telefoon status). Vervolgens helpt de setup-dialoog u bij het instellen van het telefoonnummer van de senior. Daarna is de app direct klaar voor gebruik.

---
*Ontwikkeld voor maximale rust en veiligheid voor zowel de senior als de verzorger.*
