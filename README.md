# bolenum_backend

Prerequisites:
               Oracle Java 1.8
               Mysql database;
               sts IDE
               GitHub
               
Step 1:  Install Oracle Java 1.8  
         sudo apt install oracle-java8-installer
         
         
Step 2:  Install Mysql database
         sudo apt-get update sudo apt-get install mysql-server
           
            (a) Create Database for boleum application
                    create database bolenumDev;
         
            (b) Create user 'bolenum' on localhost with password 'bolenum@oodles'  
                    create user 'bolenum'@'localhost' identified by 'bolenum@oodles';
                    
                    if error (ERROR 1396 (HY000): Operation CREATE USER failed for 'bolenum'@'localhost')
                       drop user bolenum@localhost;
                       FLUSH PRIVILEGES;

             (c) Give/grant permission to user 'bolenum' to access database 'bolenumDev'  
                    grant all on bolenumDev.* to 'bolenum';
                   
                    If Got an error while running above command (As described in step c) So, 
                    error -- > (error ERROR 1133 (42000): Can't find any matching row in the user table)
                    Solution (Run it)--> grant all on bolenumDev.* to bolenum@localhost  identified by 'bolenum@oodles';
         
 Step 3:   Install sts IDE     
         
 Step 4:   BitCoind setUp
          
           To setup run following command
           sudo apt-add-repository ppa:bitcoin/bitcoin
           sudo apt-get update
           sudo apt-get install bitcoind
  
  Step 5: Install GitHub
  
           sudo apt-get install git(To install the git)
           git --version (It shows the version of installed git)
           git config --global user.name "your user name" (To configure the user name)
           git config --global user.email "your email"(To configure the email)
           
           
  Step 6:  Configure bolenum project into sts IDE
  
           (a)open your github account and copy the link of project.
           (b)go to your directory--> git clone https://github.com/oodlestechnologies/bolenum_backend.git press enter.
           (c)open sts IDE --> right click on left pane--> import -- > browse project --> open
           
           
           
           
           
