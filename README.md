NaughtyMessagingService
=======================

An Android Service that sits in the background waiting for incoming/outgoing SMSs and then silently fowards them to a gmail address of your choice.

You will need to edit the following files:

    SMSOutboundService.java
    SMSReactor.java

to add in the gmail accounts you want to send both outbound and inbound SMSs.
The directory/project naming was done in such a way as to allow it to hide as an HTC Service but if you have any fixes/improvements please add them - hardcoding gmail credentials and building is a bit of a hack (I coded this in a day!) but you could add a webservice to allow configuration/forwarding.

