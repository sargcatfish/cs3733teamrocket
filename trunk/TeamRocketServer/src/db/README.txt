The database when2meet has been created with when2meetmanager as the controlling user. A password of XAPn.b has been assigned. You could connect to the database by:

mysql -hmysql.wpi.edu -uwhen2meetmanager -pXAPn.b when2meet

Meetings (id, startH, numColumns, numRows)
  participants (names,passwords)
  availability (c, r, boolean)
  
id is the first 13 characters of the UUID  

CREATE TABLE meetings (
  id varchar(13) NOT NULL default '',
  name varchar(32) NOT NULL default '',
  startH INTEGER NOT NULL ,
  numColumns INTEGER NOT NULL,
  numRows INTEGER NOT NULL,
  
  PRIMARY KEY  (id)
) ENGINE=MyISAM DEFAULT CHARSET=latin1

CREATE TABLE participants (
  id varchar(13) NOT NULL default '',
  user varchar(32) NOT NULL default '',
  password varchar(32) NOT NULL default '',
  
  PRIMARY KEY  (id, user)
) ENGINE=MyISAM DEFAULT CHARSET=latin1

// make note: If no tuple that defaults to NOT AVAILABLE
CREATE TABLE availability (
   id varchar(13) NOT NULL default '',
   user varchar(32) NOT NULL default '',
   col INTEGER NOT NULL,
   row INTEGER NOT NULL,
   PRIMARY KEY  (id, user, col, row)
)
