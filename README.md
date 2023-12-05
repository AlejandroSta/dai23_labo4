# DAI lab: SMTP

## Description

This programm lets you send emails to a list of persons, the list of emails and the content of said emails is to be found in txt files, each line alternates between subject and body, if you want to add line breaks you have to type them as \n in the body. A email.txt file and content.txt file have been added to the repository as examples.

## Set up

The programm is very straightforward, it will first ask you to provide the emails list file, then the content file, the number of groups, when you press send it will then ask you to provide the IP address of the email server \(reminder : 127.0.0.1 is localhost\) and the destination port.

## Instructions

Except for what has already been said, it is to note that the first email of a group is the one used to connect to the mail server, also by default the sender that the receiver emails will see is shakira@music.com.

## Implementation

The programm is mainly divided in four parts.

### App

This file is the one to be executed, it will handle every needed process, as well as read and check if the provided files have the right format.

### FileManager

This file contains every needed method used to read files \(as well as write into them and many other methods that aren't used in this programm\). Methods from this class can throw a MyFileException, which is defined in another file.

### Popups

This file contains the code that creates popups, which is the main way that the user interacts with the app.

### SocketManager

This file manage the connection to the mail server, creating correctly sized groups and sending the emails.
