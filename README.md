
# SkillHub – Skilled Hiring Platform

![App Screenshot](https://blogger.googleusercontent.com/img/a/AVvXsEhMj0fyqLM0iskdGyVFXPDdjlEhhgLaFWXL8u15TCERTmAiO8PKiZ57bErsUeuV_twc0a8JZpG72_31K_87im4BYLz5qlpe0-B-ZepM6JtA4FZZPe-zWox4tzNRfXfSLiLUe2f3it-IGtogBQmU9W_pJu6vTX12o4CQJjc2LrXAWQGb_gyFm0Te7Wj1gSk)

SkillHub is a comprehensive Android application designed to bridge the gap between clients needing skilled labor and skilled workers seeking job opportunities. The platform enables efficient hiring, scheduling aimed primarily at tradespeople such as electricians, plumbers, drivers, and mechanics. This app was developed as part of a group project by a 7 team of undergraguates in Faculty of Science, University of Ruhuna.

## Table of Contents
- [Project Overview](#project-overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Getting Started](#getting-started)
- [Installation](#installation)
- [Usage](#usage)
- [Architecture](#architecture)
- [Future Development](#future-development)
- [Contributors](#contributors)

## Project Overview
SkillHub addresses the need for a platform where clients can connect with skilled workers based on availability, expertise, and proximity. The app supports three user roles:
1. **Client** - Users needing skilled services.
2. **Worker** - Skilled individuals offering services.
3. **Admin** - Manages platform operations, including user verification and data analytics.

\
The platform utilizes Firebase for backend services, providing real-time data synchronization, authentication, firestore , and cloud storage.

## Features
- **User Registration and Authentication**: Users can register as clients or workers, complete profiles, and securely log in with Firebase authentication.
- **Worker Profile Management**: Workers can showcase skills, update availability, and receive reviews.
- **Service Search & Matching**: Clients can search and filter workers by skill, location, and ratings.
- **Scheduling**: Clients can check worker availability and check worker schedule.
- **Admin Dashboard**: Admins can manage users, perform verifications, and monitor platform activity.
- **Data Analytics**: Usage reports for insights into user activity and system performance.

## Technology Stack
- **Android Studio** - The official IDE for Android development, used for the frontend development and project structure.
- **Firebase** - For user authentication, real-time database, and cloud storage.
- **Java** - The primary programming language for Android app logic.
- **GitHub** - Version control and collaborative development.
- **Draw.io** - Used for system design diagrams.
- **Canva** - For creating UI and visual assets.

## Getting Started
1. **Prerequisites**: 
   - Android Studio installed.
   - Firebase project set up with API keys for authentication and database services.
2. **Clone the Repository**:
   ```bash
   git clone https://github.com/pavithrailankoon/skillshub.git

3. **Firebase Configuration**:
Download the `google-services.json` file from Firebase Console and place it in the `app` directory.

3. **Build the Project**:
Open the project in Android Studio and sync dependencies.

## Installation
1. Download the APK from the [Releases](https://github.com/pavithrailankoon/skillshub/releases) section.
2. Install the APK on your Android device.

## Usage
1. **Sign Up** as a client or worker, complete profile information, and set your preferences.
2. **Filter** for skilled workers based on main skill, sub skill, district, and city.
3. **Scheduling** worker add note on his profile and clients can see the schedules that worker saved.
4. **Rate & Review** workers post-service to help maintain quality standards.

## Architecture
SkillHub uses a clean, modular architecture:

- **Frontend**: Android UI components with Activities, ListViews, RecyclerViews, Fragments, and Dialogs...
- **Backend**: Firebase for Firestore, authentication, and cloud storage.
- **Data Management**: Separate classes for handling Firestore CRUD operations, enabling easy maintenance and scalability.

## Database Structure
The Firebase Firestore database is designed to store user profiles, service categories, ratings, and job scheduling information. Each user has a unique document, and real-time data synchronization is implemented for client-worker interactions.

## Future Development
1. **Geolocation & Real-Time Updates**: Enhanced proximity-based search and worker availability.
2. **Skill Verification**: Collaborations with institutions to offer certifications for workers.
3. **Subscription Plans**: Implement subscription models to cater to different user needs.

## Screenshots
1.![1](https://github.com/user-attachments/assets/e750aca4-5fd6-46ea-b4f1-5288b8393166)
2.![2](https://github.com/user-attachments/assets/d237ec03-57c6-4119-a16d-7f03e1d75e0b)
3.![3](https://github.com/user-attachments/assets/8fa9ccf9-d850-4e96-a5b2-f135425f0a72)
4.![4](https://github.com/user-attachments/assets/4def73b8-3467-4c8e-a2d9-aa3ef83a0ed7)
5.![5](https://github.com/user-attachments/assets/7f0c30af-149c-451c-aee4-685b7bd1b52b)
6.![6 Client Registration form 1](https://github.com/user-attachments/assets/e2c69516-01a0-458e-b62c-365d76ab2c5e)
7.![7 Client Registration 2](https://github.com/user-attachments/assets/d6063c1a-e762-439b-9490-2263f0cef6f4)
8.![Worker Registration 3](https://github.com/user-attachments/assets/17e7459e-5a0d-430a-afbd-738981525d03)
9.![Worker Registration 3 Continue](https://github.com/user-attachments/assets/3f23c8ca-48f9-41f7-8ecd-a8b06311f866)

## Contributors
- Pavithra Ilankoon
- Prasad Maduranga
- Pranithye Gunasekara
- Abimani Dasunika
- Greshani Navodya
- Sahasra Manahara
- Vijithan
