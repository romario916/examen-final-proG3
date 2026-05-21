
CREATE DATABASE "techadvisor_db";


CREATE USER "techadvisor_manager" WITH PASSWORD '123456';


GRANT ALL PRIVILEGES ON DATABASE techadvisor_db TO techadvisor_manager;


ALTER DATABASE techadvisor_db OWNER TO techadvisor_manager;