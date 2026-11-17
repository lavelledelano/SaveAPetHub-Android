# 🐾 Save a Pet Hub

**Adopt Paws & Claws: second chances for every tail.**

Save a Pet Hub is an Android application designed to connect abandoned and homeless pets with permanent, loving homes. The application provides users with a centralised platform where they can browse available pets, apply to adopt, donate, volunteer, surrender a pet and report suspected animal abuse.

The project aims to encourage responsible pet adoption, promote animal welfare and make it easier for individuals to become involved in supporting animals in need.

---

## 🎯 Purpose of the Project

### 1. Background and Problem Statement

Animal abandonment and homelessness remain important animal welfare concerns. Many animals are left without permanent homes, proper care or protection. Animal welfare organisations and shelters play an important role in caring for these animals and helping them find suitable homes.

However, connecting potential adopters, donors and volunteers with animals in need can be challenging when information and support opportunities are not easily accessible.

Save a Pet Hub was developed as a digital platform to help address these challenges by bringing together people who want to support animal welfare and animals that need care, protection and permanent homes.

### 2. Main Purpose

The main purpose of Save a Pet Hub is to provide an accessible platform that connects abandoned and homeless pets with individuals who are interested in adopting, supporting or assisting animals in need.

The project aims to make the process of discovering available pets and participating in animal welfare activities more convenient. Through its website and Android application, users can access information about pets, submit adoption applications, make donations, register as volunteers, surrender pets and report suspected animal abuse.

The platform is intended to support animal welfare efforts by making relevant services and information available through a centralised digital solution.

### 3. Project Objectives

The main objectives of Save a Pet Hub are to:

- **Promote pet adoption:** Allow users to browse available pets and access relevant information to help them make informed adoption decisions.
- **Simplify adoption applications:** Provide users with a way to submit adoption applications and monitor their application progress.
- **Encourage donations:** Allow individuals to contribute towards supporting animal welfare activities.
- **Support volunteering:** Provide an opportunity for interested individuals to register as volunteers and assist with animal welfare initiatives.
- **Facilitate responsible pet surrender:** Allow individuals who can no longer care for their pets to submit surrender requests.
- **Promote animal protection:** Give users a platform to report suspected cases of animal abuse or mistreatment.
- **Improve accessibility:** Make animal welfare services and information accessible through both a web-based platform and an Android mobile application.

### 4. Target Users

Save a Pet Hub is intended for individuals and groups who are interested in animal welfare. Its target users include:

- **Potential adopters:** Individuals or families interested in providing abandoned or homeless pets with permanent homes.
- **Donors:** Individuals who wish to contribute towards supporting animal welfare activities.
- **Volunteers:** People interested in offering their time and assistance to support animal welfare initiatives.
- **Pet owners:** Individuals who need assistance with surrendering a pet responsibly.
- **Concerned members of the public:** Individuals who want to report suspected animal abuse or mistreatment.
- **Administrators:** Individuals responsible for managing platform information, user activities and animal welfare-related submissions.

### 5. Expected Impact

Save a Pet Hub aims to contribute positively to animal welfare by improving access to pet adoption information and providing users with convenient ways to participate in supporting animals in need.

By combining a website and an Android application, the project provides users with different ways to access its services. It aims to encourage responsible pet adoption, increase opportunities for community involvement and make it easier for individuals to contribute towards animal welfare.

Ultimately, the project seeks to support the goal of giving abandoned and homeless animals a better opportunity to find safe, caring and permanent homes.

---

## ✨ Features

| Feature | Description |
|---|---|
| 🏠 **Home** | Welcome screen with a quick link to browse pets. |
| 🐶 **Adopt** | Browse available pets with photos, species, age, colour and adoption status. |
| 📋 **Pet Details** | Full pet profile with a large photo, breed and description. |
| 📝 **Apply to Adopt** | Submit an adoption application for any available pet. |
| 📂 **My Applications** | Track the status of your adoption applications. |
| 💛 **Donate** | Make a donation to support the shelter. |
| 🙋 **Volunteer** | Sign up as a volunteer. |
| 🏡 **Surrender a Pet** | Request to surrender a pet you can no longer care for. |
| 🚨 **Report Animal Abuse** | Report suspected animal abuse. |
| 👤 **Profile & Account** | Register, log in, view your profile and log out. |
| ⚙️ **Settings & Help** | Manage your details and get help using the app. |

---

## 🛠 Built With

The Android application was developed using the following technologies and tools:

- **Kotlin** – Main programming language.
- **Jetpack Compose** with **Material 3** – Used to develop the application's user interface.
- **Supabase** – Provides authentication, PostgreSQL database and storage services.
- **supabase-kt** – Supabase client for Kotlin.
- **Coil 3** – Used for loading and displaying pet photos.
- **Android Studio** – Development environment.

---

## 🎥 Video Demonstration

A demonstration video of the Save a Pet Hub Android application

**YouTube application demonstration link:**

https://youtu.be/e5iuCET_R8g?si=XIlo0BTtgPzPPZhH 

---

## 📲 Download

Get the latest APK from the [**Releases**](../../releases) page.

Open the downloaded APK on your Android phone and allow **"Install unknown apps"** if prompted.

---

## 🚀 Getting Started (For Developers)

### Prerequisites

Before running the application, ensure that you have:

- Android Studio (latest stable version).
- A free [Supabase](https://supabase.com) project.
- An Android phone or emulator.

### 1. Clone the Repository

Clone the project using the following command:

```bash
git clone https://github.com/lavelledelano/SaveAPetHub.git
```

Open the downloaded project folder in Android Studio and allow Gradle to synchronise.

### 2. Connect Your Supabase Project

In the following file:

`app/src/main/java/com/example/saveapethub/Supabase.kt`

Set your Supabase project URL and **anon key**.

You can find both under:

**Supabase → Project Settings → API**

> ⚠️ Never put your **service_role** key in the application or commit it to GitHub.

### 3. Set Up the Database

The application uses the following database tables:

| Table | Purpose |
|---|---|
| `pets` | Stores information about pets available for adoption. |
| `adoption_applications` | Stores adoption applications linked to `user_id` and `pet_id`. |
| `profiles` | Stores user profile details. |
| `donations` | Stores donation information. |
| `volunteers` | Stores volunteer sign-ups. |
| `reports` | Stores abuse reports and pet surrender requests. |

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

Ensure that the database tables and their relationships match the application's implementation. Appropriate database access policies should also be configured.

### 4. Add Pet Photos

To add pet photos using Supabase Storage:

1. In Supabase, navigate to **Storage → New Bucket**.
2. Name the bucket `pet-images` and set it to **Public** if public image access is required by the application.
3. Upload the pet photos.
4. Copy each photo's public URL into the relevant pet's `image_url` column.

Example image URL:

```text
https://YOUR-PROJECT.supabase.co/storage/v1/object/public/pet-images/bella.jpg
```

Pets without a photo display the Save a Pet Hub logo instead.

### 5. Run the Application

1. Open the project in Android Studio.
2. Select an Android emulator or connect an Android phone.
3. Allow the project to build and synchronise.
4. Click **▶ Run** to install and launch the application.

---

## 📁 Project Structure

The Android application's source code is organised as follows:

```text
app/src/main/java/com/example/saveapethub/
├── MainActivity.kt        # App entry point
├── Supabase.kt            # Supabase client and data models
├── AuthScreens.kt         # Login and registration
├── MainScreens.kt         # Navigation, Home, Adopt, Pet Details, Profile
├── ApplyScreen.kt         # Adoption application form
├── DonateScreen.kt        # Donations
├── VolunteerScreen.kt     # Volunteer sign-up
├── ReportScreens.kt       # Report abuse and surrender a pet
├── SettingsScreens.kt     # Settings and My Applications
├── HelpScreen.kt          # Help
└── ui/theme/              # Colours, typography and theme
```

---

## 🧪 Testing

The Save a Pet Hub Android application was tested using a physical Android phone.

Testing on a physical device allowed the application to be accessed and evaluated outside the Android Studio development environment. It also provided an opportunity to observe how the application appeared and operated on an actual mobile device.

The application's main functionality includes:

- User registration and login.
- Browsing available pets.
- Viewing individual pet details.
- Submitting adoption applications.
- Viewing adoption applications.
- Accessing donation and volunteer features.
- Submitting pet surrender requests.
- Reporting suspected animal abuse.
- Accessing profile and settings features.

---

## 🔐 Security

The application uses Supabase for authentication and data management.

When configuring the application:

- Use the Supabase anon key for the Android client.
- Never expose the Supabase service_role key in the application.
- Configure appropriate Row Level Security (RLS) policies to protect user and application data.
- Avoid committing sensitive credentials or private configuration details to the GitHub repository.

---

## 👥 Contributors

**Lavelle Delano Dalman**

**Mahlatse Chokwe**

---

## 📄 License

This project is intended for educational and non-commercial use.
