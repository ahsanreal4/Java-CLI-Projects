This is Group Chat project made by Socket Programming in Java. <br>By the use of Multi Threading and input output streams communication between client and server is established.

### Server

- Server will communicate with clients and also broadcast client messages to other clients. <br>
- Server is created and it has multiple threads for listening to multiple clients. <br>
- Threads grow as the clients size grow but max connections are limited so server will wait until a client disconnects.
- One server thread is always free to listen for new clients.<br>

### Client

- Client can register their username and enter the chat.<br>
- Client can send messages to other clients.<br>
- Client can send stop message to exit chat.<br>
- Client will receive other clients joining and leaving events in the group chat

### Steps to Run

- Run the <b>ServerDriver.java</b> file to run server ( It can only be run once )<br>
- Run the <b>ClientDriver.java</b> file to run client ( You can run multiple clients at the same time )

### Configuration
You can change Settings like <b>MAX_CONNECTIONS</b> and <b>PORT</b> in package constants
