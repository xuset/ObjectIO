ObjectIO
========
ObjectIO is a framework with the purpose of simplifying the process of communicating to another computer and interpreting the received data. Have a look at the chat server/client demo in the [wiki](https://github.com/xuset/ObjectIO/wiki/Server-Client-chat-demo) for an introduction into ObjectIO.

In objectIO, a Connection is able to send messages to another Connection instance, and receive messages as well. The messages used for communicating are MarkupMsg objects. The MarkupMsg is ideal for communicating in a meaningful way because they are very versatile. They can be assigned a name, attributes, content, and nest other MarkupMsg objects. The receiver of a message can then use these fields to determine what the message represents, and what is contained within.

A message can be sent across different kinds of mediums. A MarkupMsg message could be sent across a TCP connection via the TcpCon connection. A message could be read or sent to file using the FileCon connection.

ObjectIO also provides a means for interpreting the received messages. The netObj package is used for exactly this. The most prominent classes are the NetVar class and the NetFunc class. NetVar acts a variable; when this variable changes, it produces a MarkupMsg object that can be sent across a Connection. The receiver can then apply the message to a NetVar with the same id, and the two NetVar objects will share the same value. A NetFunc object acts a function, and does not store a value. When a NetFunc is 'called', a MarkupMsg object is produced which can be sent to a Connection. When the receiver applies the message to the NetFunc predefined code executes. So a NetVar provides a way to store information across a Connection and a NetFunc allows functionality between a Connection instances.

For information on specific parts of the code have a look at the [docs](http://xuset.github.io/objectIO/docs/v1.0/).
