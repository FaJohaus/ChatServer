Chatserver + Kommandozeilenclient + (grafischer Client)



Funktionen:
	- RickRoll-Detektor <br>
	- User login (mit Datenbank) (Fabian guckt es sich an, wenns ihm zu blöd ist lässt er es Jonas machen :)) )<br>
	- Write to user<br>
	- Gruppe erstellen<br>
	- Gruppen auflisten<br>
	- Broadcast erstellen<br>
	- Broadcasts auflisten<br>
	- Online/Offline/last Online Liste (Fabian)<br>
	- (gespeicherter Chatverlauf? Viel Datenbankscheiss tho)<br>
	- Change nick (Fabian guckt es sich an, wenns ihm zu blöd ist lässt er es Jonas machen :)) )<br>
	- Change pwd (Fabian guckt es sich an, wenns ihm zu blöd ist lässt er es Jonas machen :)) )<br>
	- Change Color<br>
	- (iwie schön machen mit ascii-art)<br>

<h2>Befehle:</h2><ul>
- Create<ul>
    - create user *username* *password*<br>
    - create group </ul>
- Login<ul>
    - login *username* *password*</ul>
- WhoAmI<ul>
	- whoami</ul>
- Change<ul>
    - change user name *password* *new_username*<br>
    - change user pwd *password* *new_password*<br>
    - change group</ul>
- Delete<ul>
    - delete user *password*<br>
    - delete group</ul>
- List<ul>
    - list users</ul>
- Send<ul>
    - send *message*</ul>
- Sendto<ul>
    - sendto *username* *message*<br>
    - sendto group *groupname* *message*