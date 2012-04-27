The database teamrocket has been created with meowth as the controlling user. A password of xuguHN has been assigned. You could connect to the database by:

mysql -hmysql.wpi.edu -umeowth -pxuguHN teamrocket

dlevents (id, numChoices, numRounds, eventQuestion, dateCreated, eventType, moderator)
  users (name, password, isModerator, userIndex)
  choices (choiceIndex, choiceName)
  edges (leftChoice, rightChoice, height)
  
id is the first 13 characters of the UUID

CREATE TABLE DLEvents (
  id varchar(13) NOT NULL default '',
  numChoices INTEGER NOT NULL,
  numRounds INTEGER NOT NULL,
  eventQuestion varchar(32) NOT NULL default 'My Question',
  dateCreated DATE NOT NULL default '1900-01-01',
  isOpen BOOLEAN NOT NULL,
  acceptingUsers BOOLEAN NOT NULL, 
  moderator varchar(32) NOT NULL,
  isComplete BOOLEAN NOT NULL,
    
  PRIMARY KEY  (id)
) ENGINE=MyISAM DEFAULT CHARSET=latin1

CREATE TABLE users (
  id varchar(13) NOT NULL default '',
  name varchar(32) NOT NULL default '',
  password varchar(32) NOT NULL default '',
  isModerator BOOLEAN NOT NULL,
  userIndex INTEGER NOT NULL,
  
  PRIMARY KEY  (id, name)
) ENGINE=MyISAM DEFAULT CHARSET=latin1

CREATE TABLE choices (
  id varchar(13) NOT NULL default '',
  choiceIndex INTEGER NOT NULL,
  choiceName varchar(32) NOT NULL default '',
  
  PRIMARY KEY  (id, choiceName)
) ENGINE=MyISAM DEFAULT CHARSET=latin1

CREATE TABLE edges (
  id varchar(13) NOT NULL default '',
  leftChoice INTEGER NOT NULL,
  rightChoice INTEGER NOT NULL,
  height INTEGER NOT NULL,
  
  PRIMARY KEY  (id, height)
) ENGINE=MyISAM DEFAULT CHARSET=latin1