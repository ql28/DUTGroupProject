CREATE TABLE Plan_de_salle (
	nom VARCHAR(100) NOT NULL,
	data MEDIUMTEXT NOT NULL,
	valide TINYINT(1) NOT NULL,
	PRIMARY KEY (nom)
)
ENGINE=INNODB;

CREATE TABLE Objet (
	nom VARCHAR(100) NOT NULL,
	data MEDIUMTEXT NOT NULL,
	image BLOB NOT NULL,
	valide TINYINT(1) NOT NULL,
	PRIMARY KEY (nom)
)
ENGINE=INNODB;


DESCRIBE Plan_de_salle;
DESCRIBE Objet;


CREATE USER 'admin'@'%' IDENTIFIED BY 'azerty';
GRANT SELECT, 
      UPDATE, 
      DELETE, 
      INSERT
ON adec56.Objet
TO 'admin'@'%' IDENTIFIED BY 'azerty';
GRANT SELECT, 
      UPDATE, 
      DELETE, 
      INSERT
ON adec56.Plan_de_salle
TO 'admin'@'%' IDENTIFIED BY 'azerty';

CREATE USER 'user'@'%' IDENTIFIED BY 'azerty';
GRANT SELECT,
      INSERT
ON adec56.Objet
TO 'user'@'%' IDENTIFIED BY 'azerty';
GRANT SELECT,
      INSERT
ON adec56.Plan_de_salle
TO 'user'@'%' IDENTIFIED BY 'azerty';
