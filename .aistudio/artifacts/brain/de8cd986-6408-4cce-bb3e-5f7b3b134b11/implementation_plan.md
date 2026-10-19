# Genomförandeplan: Doseringsschema

Denna plan beskriver hur vi lägger till knappen **Doseringsschema** på startsidan samt bygger en dedikerad schemaskärm som ger en tydlig daglig översikt indelad i **morgon, middag, kväll och natt**.

---

### Bekräftade val från dialogen

> [!IMPORTANT]
> **Bekräftade val:**
> - **Placering av knapp**: Exakt mellan *”Mina läkemedel”* och *”Lägg till manuellt”* på startsidan.
> - **Knappens namn**: **Doseringsschema**.
> - **Huvudfunktion**: En strukturerad daglig tidsöversikt över doser fördelade på **Morgon**, **Middag**, **Kväll** och **Natt / Sänggående**.

---

### 1. Startsidan: Ny knapplayout

Startsidan får följande vertikala ordning bland huvudknapparna:
1. **Skanna läkemedel** *(Kamera & EAN-13)*
2. **Mina läkemedel** *(Inventering, sök, förpackningar)*
3. 🆕 **Doseringsschema** *(Daglig översikt för morgon, middag, kväll och natt)*
   - Ikon: Tids-/klockikon (`Icons.Default.AccessTime` eller `Schedule`)
   - Accentfärg: Indigo/blå ton som harmonierar med apotekspaletten
4. **Lägg till manuellt** *(Formulär utan skanning)*
5. *(Avgränsare)* **Snart utgångna** *(Bäst-före-bevakning)*

---

### 2. Ny skärm: Doseringsschema (`ScheduleScreen.kt`)

Skärmen struktureras i fyra tydliga tidskort för dagen:

```
┌────────────────────────────────────────────────────────┐
│  ← Doseringsschema                                     │
├────────────────────────────────────────────────────────┤
│                                                        │
│  🌅 MORGON                                             │
│  ┌──────────────────────────────────────────────────┐  │
│  │ Alvedon 500 mg • 1 tablett                       │  │
│  │ Tas efter frukost                                │  │
│  └──────────────────────────────────────────────────┘  │
│                                                        │
│  ☀️ MIDDAG / LUNCH                                     │
│  ┌──────────────────────────────────────────────────┐  │
│  │ Pronaxen 250 mg • 1 tablett                      │  │
│  └──────────────────────────────────────────────────┘  │
│                                                        │
│  🌆 KVÄLL                                              │
│  ┌──────────────────────────────────────────────────┐  │
│  │ Omeprazol 20 mg • 1 kapsel                       │  │
│  └──────────────────────────────────────────────────┘  │
│                                                        │
│  🌙 NATT / SÄNGGÅENDE                                  │
│  ┌──────────────────────────────────────────────────┐  │
│  │ (Inga läkemedel inlagda för natten)              │  │
│  └──────────────────────────────────────────────────┘  │
│                                                        │
│  [ + Lägg till läkemedel i schemat ]                   │
└────────────────────────────────────────────────────────┘
```

#### Funktioner på skärmen:
- **Tydliga tidssektioner**: Morgon, Middag, Kväll och Natt med passande symboler och färger.
- **Enkelt att lägga till**: En dialog/bottensheet där användaren väljer ett befintligt läkemedel från skåpet, väljer tidpunkt (morgon/middag/kväll/natt) samt anger dos (t.ex. *1 tablett*).
- **Redigera & ta bort**: Möjlighet att justera dos eller ta bort en rad ur schemat.
- **Tom-vy**: Om inga schemarader finns visas en pedagogisk guide och en knapp för att lägga till första dosen.

---

### 3. Datastruktur & Lokal lagring

- Ny Room-entitet: `ScheduleEntryEntity`
  - `id`: Long (primärnyckel)
  - `medicineId`: Long (koppling till läkemedel i skåpet)
  - `medicineName`: String
  - `timeOfDay`: String (`"MORNING"`, `"NOON"`, `"EVENING"`, `"NIGHT"`)
  - `dose`: String (t.ex. `"1 tablett"`)
  - `note`: String? (t.ex. `"Tas med mat"`)
- Ny DAO: `ScheduleDao` med reaktiva Flow-frågor.
- Utökning i `AppDatabase` och `MedicineViewModel` för att hämta och spara schemat sömlöst och 100 % lokalt.

---

### 4. Navigering & Versionshantering

- Lägg till `Screen.Schedule` i `Screen.kt`.
- Koppla navigering mellan `HomeScreen` och `ScheduleScreen` i `MainActivity.kt`.
- Uppdatera appen till **Version 1.6** (`versionCode = 7`, `versionName = "1.6"`).
- Uppdatera infodialogen med information om doseringsschemat.
