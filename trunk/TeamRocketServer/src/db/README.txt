The database teamrocket has been created with meowth as the controlling user. A password of xuguHN has been assigned. You could connect to the database by:

mysql -hmysql.wpi.edu -umeowth -pxuguHN teamrocket

decisionLinesEvent (id, numChoices, numRounds, eventQuestion, dateCreated, eventType)
  users (name, password, isModerator, userIndex)
  choices (choiceIndex, choiceName, name)
  edges (leftChoice, rightChoice, height)
  
id is the first 13 characters of the UUID

CREATE TABLE decisionLinesEvent (
  id varchar(13) NOT NULL default '',
  numChoices INTEGER NOT NULL,
  numRounds INTEGER NOT NULL,
  eventQuestion varchar(32) NOT NULL default 'My Question',
  dateCreated varchar(4) NOT NULL default '',
  eventType varchar(4) NOT NULL default '',
    
  PRIMARY KEY  (id)
) ENGINE=MyISAM DEFAULT CHARSET=latin1

CREATE TABLE users (
  id varchar(13) NOT NULL default '',
  name varchar(32) NOT NULL default '',
  password varchar(32) NOT NULL default '',
  isModerator TINYINT(1) NOT NULL,
  userIndex INTEGER NOT NULL,
  
  PRIMARY KEY  (id)
) ENGINE=MyISAM DEFAULT CHARSET=latin1

CREATE TABLE choices (
  id varchar(13) NOT NULL default '',
  choiceIndex INTEGER NOT NULL,
  choiceName varchar(32) NOT NULL default '',
  
  PRIMARY KEY  (id)
) ENGINE=MyISAM DEFAULT CHARSET=latin1

CREATE TABLE edges (
  id varchar(13) NOT NULL default '',
  leftChoice INTEGER NOT NULL,
  rightChoice INTEGER NOT NULL,
  height INTEGER NOT NULL,
  
  PRIMARY KEY  (id, user)
) ENGINE=MyISAM DEFAULT CHARSET=latin1