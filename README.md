# Project Quackstagram Group 14

# About Quackstagram
Quackstagram is a social media app built using Java and JavaFX. Users can sign up, log in, upload pictures, like photos, and follow friends. It's a simple, enjoyable platform designed for easily sharing and interacting with photos.

### Setting up Git (optional):
To configure your Git identity locally just for this project, run:

``````
git config --local user.name "(your name)"
git config --local user.email "(your email)"

``````
Just do this once.

### Adding changes: 
```
git add .
git commit -m "Here's a list of changes"
git push -u origin main 
```

## How to Run the project:
1. Clone the repository:
run this command

`````
git clone https://github.com/FMGY0nko/Quackstagram_Code.git

cd Quackstagram

git checkout main
`````
2. Open the project in your IDE (we are using VSCode mostly).

3. You need to have JavaFX configured properly in your IDE to run this project. When you have that set up, go to SignIpUI.java and click on the run button(traingle button) at the top right corner. 

# Project Layout
**managers**:
This is where most of the application's logic lives:
-	**CredentialsManager.java** manages logging in and signing up.
-	**ImageUploadManager.java** takes care of uploading and saving images.
-	**ImageLikesManager.java** records image likes.
-	**UserRelationshipManager.java** handles following and unfollowing other users.

**models**:
Basic data structures representing users and images.

**ui**:
The user interfaces created using JavaFX:
-	**SignInUI.java** and SignUpUI allow users to create an account and log in.
-	**QuackstagramHomeUI.java** shows users their main page and feed.
-	**ExploreUI.java** lets users discover new images and accounts.
-	**InstagramProfileUI.java** displays user profiles.
-	**NotificationsUI.java** handles notifications.
-	**ImageUploadUI.java** lets users upload images.

**utils**:
Helper classes like TimeUtils.java make handling dates easier.

**img**:
Contains icons and images used in the application.

**data**:
Stores data like credentials and notifications.

# Authors:
Kristian Bek
Keti Javakhishvili
Nisrine Ouchen
