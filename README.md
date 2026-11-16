# 🐾 Save a Pet Hub

**Adopt Paws & Claws: second chances for every tail.**

Save a Pet Hub is an Android app that connects abandoned pets with permanent, loving homes. Users can browse available pets, apply to adopt, donate, volunteer, surrender a pet, and report animal abuse, all in one place.

---

## ✨ Features

| Feature | Description |
|---|---|
| 🏠 **Home** | Welcome screen with a quick link to browse pets |
| 🐶 **Adopt** | Browse available pets with photos, species, age, colour and adoption status |
| 📋 **Pet Details** | Full pet profile with a large photo, breed and description |
| 📝 **Apply to Adopt** | Submit an adoption application for any available pet |
| 📂 **My Applications** | Track the status of your adoption applications |
| 💛 **Donate** | Make a donation to support the shelter |
| 🙋 **Volunteer** | Sign up as a volunteer |
| 🏡 **Surrender a Pet** | Request to surrender a pet you can no longer care for |
| 🚨 **Report Animal Abuse** | Report suspected animal abuse |
| 👤 **Profile & Account** | Register, log in, view your profile and log out |
| ⚙️ **Settings & Help** | Manage your details and get help using the app |

---

## 🛠 Built With

- **Kotlin**
- **Jetpack Compose** with **Material 3**
- **Supabase**: Auth, Postgres database and Storage
- **supabase-kt**: Supabase client for Kotlin
- **Coil 3**: image loading for pet photos
- **Android Studio**

---

## 📲 Download

Get the latest APK from the [**Releases**](../../releases) page. Open it on your Android phone and allow **"Install unknown apps"** if prompted.

---

## 🚀 Getting Started (for developers)

### Prerequisites
- Android Studio (latest stable)
- A free [Supabase](https://supabase.com) project
- An Android phone or emulator

### 1. Clone the repo
```bash
git clone https://github.com/lavelledelano/SaveAPetHub.git
```
Open the folder in Android Studio and let Gradle sync.

### 2. Connect your Supabase project
In `app/src/main/java/com/example/saveapethub/Supabase.kt`, set your project URL and **anon** key. Find both in Supabase under **Project Settings → API**.

> ⚠️ Never put your **service_role** key in the app or commit it to GitHub.

### 3. Set up the database
The app uses these tables:

| Table | Purpose |
|---|---|
| `pets` | Pets available for adoption |
| `adoption_applications` | Adoption applications (linked to `user_id` and `pet_id`) |
| `profiles` | User profile details |
| `donations` | Donations |
| `volunteers` | Volunteer sign-ups |
| `reports` | Abuse reports and surrender requests |

**`pets` table columns:**

| Column | Type |
|---|---|
| `pet_id` | int8 (primary key) |
| `name` | text |
| `species` | text |
| `breed` | text (nullable) |
| `age_years` | int4 (nullable) |
| `colour` | text (nullable) |
| `description` | text (nullable) |
| `image_url` | text (nullable) |
| `status` | text (e.g. `Available`) |

### 4. Add pet photos
1. In Supabase go to **Storage → New bucket**, name it `pet-images` and set it to **Public**.
2. Upload your pet photos.
3. Copy each photo's public URL into the pet's `image_url` column. URLs look like this:
   ```
   https://YOUR-PROJECT.supabase.co/storage/v1/object/public/pet-images/bella.jpg
   ```
Pets without a photo show the Save a Pet Hub logo instead.

### 5. Run the app
Select an emulator or connected phone and click **▶ Run** in Android Studio.

---

## 📁 Project Structure

```
app/src/main/java/com/example/saveapethub/
├── MainActivity.kt        # App entry point
├── Supabase.kt            # Supabase client and data models
├── AuthScreens.kt         # Login & registration
├── MainScreens.kt         # Navigation, Home, Adopt, Pet Details, Profile
├── ApplyScreen.kt         # Adoption application form
├── DonateScreen.kt        # Donations
├── VolunteerScreen.kt     # Volunteer sign-up
├── ReportScreens.kt       # Report abuse & surrender a pet
├── SettingsScreens.kt     # Settings & My Applications
├── HelpScreen.kt          # Help
└── ui/theme/              # Colours, typography and theme
```

---

## 👤 Author

**Lavelle Dalman**

---

## 📄 License

This project is for educational and non-commercial use.