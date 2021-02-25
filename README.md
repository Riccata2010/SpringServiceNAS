# SpringServiceNAS
Exploring SpringBoot, Angular and Bootstrap. With JPA and some AOP.

This simple project is an example of using SpringBoot, Angular and Bootstrap. With the complication of 
JPA and a brief mention of AOP (aspect oriented programming).

The basic idea is to have one or more micro services running on the home NAS. One of these is used to 
upload photos and videos directly to the NAS via web application. Then the photos and videos are 
reorganized according to the <year> / <month> logic.


Two micro services are performed that communicate with each other with synchronous messages via AOP. 
One micro service takes care of saving information in an H2 db, while the other uploads files, 
typically photos and videos. There is also a third micro service used to present a simple angular web app to actually upload.



![image info](./pictures/image.png)
