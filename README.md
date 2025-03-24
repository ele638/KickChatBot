This is a chatbot KMP-based app for Kick platform.

TODO - add Readme

Required:
1) Installed PostgreSQL server
2) SSM secure parameters
* /chatbot/db_url - database URL (ex: localhost)
* /chatbot/db_port - database port (ex: 5432)
* /chatbot/db_name - name of database (lowercase only)
* /chatbot/db_user - database username (lowercase only)
* /chatbot/db_password - database password

3) System ENV vars:
* DATABASE_URL - URL to Amazon RDS 
* PGPASSWORD - password for user postgres