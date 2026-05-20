# Senioren Beheerder 🛡️

Een krachtige Android-applicatie ontworpen voor mantelzorgers om de veiligheid en het toestelgebruik van senioren op afstand te beheren via een robuust SMS-commandosysteem.

## 🌟 Belangrijkste Functionaliteiten

- **Dashboard**: Direct inzicht in batterijniveau, netwerkstatus en volume.
- **Locatiebeheer**: Real-time kaartweergave via OpenStreetMap (OSM) zonder dat gebruikers API-keys nodig hebben.
- **Veiligheid & SOS**: Forceer op afstand SOS-procedures, stuur wellness checks (#PING) of activeer de luidspreker.
- **Anti-Scam**: Blokkeer ongewenste oproepen van onbekende nummers op afstand.
- **Zorg & Planning**: Beheer medicijnherinneringen, agenda-afspraken en wekkers.
- **Systeembeheer**: Pas op afstand instellingen aan zoals Wi-Fi, Bluetooth, helderheid en tekstgrootte.
- **Automatisering**: Volledig geautomatiseerde builds en releases via GitHub Actions.

## 📜 Overzicht Commando's

De app ondersteunt meer dan 35 commando's, onderverdeeld in:

1.  **Veiligheid & Locatie**: `#WAAR`, `#SOS_NU`, `#PING`, `#BEL_TERUG`, `#SPEAKER`, `#VEILIG ON/OFF`, `#BLOKKEER [nr]`.
2.  **Systeem & Connectiviteit**: `#WIFI`, `#BT`, `#STIL`, `#VOLUME`, `#HELDER`, `#SCHERM_TIJD`, `#RESTART`.
3.  **UI Beheer**: `#LETTER [1-5]`, `#THEMA [1-3]`, `#SLOT ON/OFF`, `#PIN [code]`.
4.  **Zorg & Planning**: `#MEDICIJN`, `#VOORRAAD`, `#AGENDA`, `#WEKKER`, `#RADIO_STOP`.
5.  **Diagnose**: `#STATUS`, `#INFO_PLUS`, `#PRIVACY`, `#LAATSTE_OPROEP`, `#APP_LIJST`, `#NETWERK`.

## 🛠️ Technieken

- **Taal**: Kotlin
- **UI Framework**: Jetpack Compose met Material 3
- **Architectuur**: MVVM (ViewModel & State)
- **Kaarten**: OpenStreetMap via `osmdroid` met geoptimaliseerde touch-afhandeling.
- **CI/CD**: GitHub Actions voor automatische APK generatie.
- **Communicatie**: Android Telephony SDK (SMS Manager & BroadcastReceivers)

## 🚀 Installatie & Gebruik

De app is ontworpen voor de Play Store en vereist geen ingewikkelde configuratie. Na installatie vraagt de app om de benodigde SMS-machtigingen om correct te kunnen functioneren als beheerderstoestel.

---
*Ontwikkeld voor maximale rust en veiligheid voor zowel de senior als de verzorger.*
