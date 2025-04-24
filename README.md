**Administrator Credentials for Admin Login:**
- Email: admin@gmail.com
- Password: admin123
   
Demo to use the app is provided in EAMSrecording.mp4.
Project objective details are provided in Final Report pdf.

**Setup Instructions**
1. Clone this repository
- git clone [https://github.com/your-username/eams-android-app.git](https://github.com/uOttawaSEG/project-group-36.git)
- cd eams-android-app

2. Open with Android Studio, choose "Open an existing project" and select the cloned folder.

3. Connect to Firebase
Go to Tools > Firebase in Android Studio.
Set up Firebase Authentication and Cloud Firestore.
Download the google-services.json file from Firebase Console and place it in:
app/google-services.json
Enable Email/Password authentication in the Firebase Console.
In Firestore, create these collections:
- admin
- attendee
- events
- organiser
- users

4. Select an emulator or connected device, click run

**Group Members (Name, Student Number):**
1. Rachel Suriawidjaja, 300332168
2. James Attia, 300353040
3. Amani Mohamed, 300349984
4. Anima Mehraj Mehrin, 300278018
