# meeting-room-reservation-system

## Greetings!
This is a simple meeting room booking system with user authorities and crud of the reservations. Please read finish as it is important for some AWS credentials.

## Table of contents
* [Technologies]{#technologies}
* [Prerequisite](#prerequisite)
* [Emails Available for AWS SES](#emails-available-for-aws-ses)
* [Links and Domains](#links-and-domains)
* [Application Info](#application-info)
* [Future Enhancement](#future-enhancement)

## Technologies
  - Spring v3
  - Spring Security with Json Web Token (JWT)
  - AWS RDS with mySQL
  - AWS SES
  - AWS EC2
  - Docker
  - Mockito v5

## Prerequisite
Roles available: ROLE_ADMIN, ROLE_USER
* ROLE_ADMIN user credentials {jolyn.ling.gan@gmail.com | pwd: Jolyn3131}
* ROLE_USER user credentials {jolyngan2@gmail.com | pwd: Jolyn3131}

Postman Json Collections provided in directory
* please take note that the system database is using UTC timemzone, the application has yet to manage the timezone.


## Emails Available for AWS SES
Note that I am using the free tier of AWS SES (and all of the Amazon Services used), there are many restrictions as I wasn't grant to exit the sandbox environment. One of the restriction is emails address of receiver must be verified to successfully send an email to the receiver. To lessen the hustle, I have created a few dummy emails and verified them for testing purposes.

If you wish to use your own email, do let me know so I can add your email addresses for verification.

* dummy.meeting.1@gmail.com |  pwd: Test@123
* dummy.meeting.2@gmail.com |  pwd: Test@123
* dummy.meeting.3@gmail.com |  pwd: Test@123
* dummy.meeting.4@gmail.com |  pwd: Test@123
* dummy.meeting.5@gmail.com |  pwd: Test@123

## Links and Domains
AWS RDS HOST: meeting-room-database.cwzpkinz3ndu.ap-southeast-2.rds.amazonaws.com
AWS RDS PORT: 3306
DATABASE: meeting_room
DATABASE USERNAME: admin
DATABASE PASSWORD: Jolyn3131

AWS EC2 Instance IP: http://3.26.7.118:80

Docker Image: meownicorn/meeting-room-reservation-system
https://hub.docker.com/r/meownicorn/meeting-room-booking-system


## Application Info
General Public
  - Login                  Post Request --> /api/auth/login
  - Register               Post Request --> /api/auth/register
  - Forget Password        Post Request --> /api/users/forget-password?email=

User Management (ROLE_ADMIN)
  - Read All Users         Get Request --> /api/users/all
  - Read User by ID        Get Request --> /api/users/{id}
  - Create User            Post Request --> /api/users/create
  - Update User            Put Request --> /api/users/update
  - Delete User            Post Request --> 

Reservation Management (ROLE_ADMIN, ROLE_USER)
  - Read All Reservation   Get Request --> /api/reservations/all
  - Find By User           Get Request --> /api/reservations/find-by-email?email=
  - Today's Reservation    Get Request --> /api/reservations/today-reservations
  - Create Reservation     Post Request --> /api/reservations/create
  - Update Reservation     Put Request --> /api/reservations/update
  - Delete Reservation     Post Request --> /api/reservations/delete/{id}

Change Password (ROLE_ADMIN, ROLE_USER)
  - Change Password        Post Requeest --> /api/users/change-password
  
## Future Enhancement
  - Reactjs Front End
  - Handle timezone issue
  - Github ec2/ecs deployment
  - Delete user binded with reservations


