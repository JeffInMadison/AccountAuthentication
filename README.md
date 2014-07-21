AccountAuthentication
=====================

Android provides a secure mechanism within the os to store credentials and help automate the login/getToken process. I've never used it but wanted to see what is involved in setting it up. 

Adding AccountAuthentication requires a bunch of boilerplate code and services. This sample sets up an account to use in the system->accounts section. Selecting the + under accounts will populate the accounts available with this apps account info, and launched the defined LoginActivity from your app to handle login. Then stores the token securely in the system. You can then request it in the future.


