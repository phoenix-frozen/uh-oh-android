uh-oh
=====
Uhoh is an app that helps reassure you when walking alone at night and alerts friends and family to your position if you are worried about your safety.

It has two features

Talk To Me: Select the amber speech icon and be guided through a simulated conversation while your GPS position is recorded and stored on a secure server – just in case something should happen. The idea of this is to keep you talking and calm, making any potential unfriendlies believe you are on the phone and therefore less of a target. 
Call My Friends: Select the red call icon to discreetly and directly alert and call three of your friends or family. When this option is selected the app will automatically send an sms message to all three of your listed friends and call your friends in turn until one answers. If they don't answer it will default to the Talk To Me simulated conversation

Native apps have been create for Android and Iphone, a webservice has been created to securely store location data and transmit text messages

Android app: https://github.com/phoenix-frozen/uh-oh-android
iOS app https://github.com/rottedfrog/uhoh-ios
Webservice/website https://github.com/josephfinlayson/uh-oh-webservice

The status of the tech is below, with an estimation of the work needed to be done:

The Android (Java) app is 80% feature complete, instead of using the webservice we need to send text messages directly from the app, additionally it would be nice if the user didn't have to type the emergency numbers in manually, they were instead available using address book manager (15 hours)

The iPhone (Swift) app is 50% feature complete, the UI is finished and the 'talk to me' feature is complete. Location data needs to be marshalled into a JSON format and be transmitted to the webservice so it can send text messages. Currently the iPhone doesn't allow text messages to be sent natively in a discreet way. (25 hours)

The Webservice (NodeJS) is 100% feature complete, it accepts HTTP post request from arbitrary sources and sends predefined text messages via the Twilio API if someone is in trouble. This information is then plotted onto a map, so police/government can see where people feel most unsafe. The code needs to be better organised, and more work needs to be done to ensure anonymity of users (10 hours)
