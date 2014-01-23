openfire-extendedjdbc
=====================

Example based on dovecot user database

1) upload extended-jdbc-xxx.jar to openfire/lib
2) upload database driver of choice to openfire/lib (mysql is already there)
3) configure openfire to use ExtendedJdbcAuthProvider:

3.1) jdbcProvider.driver=com.mysql.jdbc.Driver
3.2) jdbcProvider.connectionString=jdbc:mysql://localhost:3306/postfixdb?user=dbuser&password=dbpassword
3.3) extendedJdbcAuthProvider.passwordCheckSql=SELECT count(*) FROM postfix.mailbox u WHERE u.username=CONCAT(?,"@example.com") AND u.password=ENCRYPT(?, SUBSTRING_INDEX(u.password, "$", 3))

4) If you'd like to connect dovecot 
4.1) jdbcAuthProvider.passwordSQL=SELECT u.password FROM postfix.mailbox u WHERE u.username=CONCAT(?,"@example.com")
4.1) jdbcUserProvider.allUsersSQL=SELECT SUBSTRING_INDEX(u.username, "@", 1) `username` FROM postfix.mailbox u WHERE u.active=1
4.1) jdbcUserProvider.loadUserSQL=SELECT u.name, u.username `email` FROM postfix.mailbox u WHERE u.username=CONCAT(?, "@example.com")
4.1) jdbcUserProvider.searchSQL=SELECT SUBSTRING_INDEX(u.username, "@", 1) `username` FROM postfix.mailbox u WHERE u.active=1 AND 
4.1) jdbcUserProvider.nameField=u.name
4.1) jdbcUserProvider.emailField=u.username
4.1) jdbcUserProvider.userCountSQL=SELECT count(*) FROM postfix.mailbox u WHERE active=1
4.1) jdbcUserProvider.usernameField=SUBSTRING_INDEX(u.username, "@", 1)

5) Note: by default openfire allows username "admin" to login to webinterface, so make sure dovecot db has such user

