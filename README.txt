Open bugs on server side:

	Through some series of events a comma can occasionally leave a stray comma at the beginning of the config files. Proper trimming needs to be implemented to remove this before the file is written.

	This does not seriously effect operations on the device. But can cause you to need to make 2 requests to the device on occasion.


Setup of the physical equipment:

	Items needed:

	PI ( Must be configured to auto connect to internet and be net registered or some similar measure so you can access it remotely )
	Power source for pi
	Wireless Dongle
	Ribbon cable
	GPIO Pin Breakout ( plugs into bread board )
	Bread Board or similar
	4 male to male wires
	Servo ( I used Futaba S3003 Standard Servo )
	Battery Pack (4 double A pack that produces 6 volts, on/off switch recommended )
	"Lock Stick" (Door lock installed in 2x4 with servo mounts installed)
	Items Optional
	Nomad battery pack
	Installation Instructions:

	Assumptions: Lock stick has servo installed properly and breadboard is also attached to Lock Stick.

	Step 1: install GPIO Breakout into the bread board.

	Step 2: Take leads from battery pack and place them into the breadboard in separate rows with no other pins connected. This means they should be past the end of the GPIO Pin Breakout. Do not use the power bars on the edge of the board because they are connected to the PI and this defeats the purpose of the battery pack.

	The leads are very small and can be difficult to get into the board, especially the ground wire.

	Step 2: Plug red and black male to male connectors into the rows with the matching wire from the battery pack.

	Step 3: Plug the wires in step 2 into the servo leads with colors black to brown, red to red, white to yellow.

	Step 4: Connect the second black wire to the ground on the GPIO Breakout. This can be done in a number of places. I like to use the negative side of the 3v3 output that is on the power bar. If done correctly the black lead from the battery pack should go into a row with 2 other black wires coming out of it. Those wires go to the brown lead of the servo and the ground.

	Step 5: Connect the white wire from the yellow lead on the servo to the corresponding row on the breadboard that connects to the pin on the GPIO Breakout labeled GPIO 23. In the setup i had at the demo this was on row 8 of the breadboard. This pin is known by both "Pin 16" and "GPIO 23".

	Step 6: Connect the ribbon cable to the pi and GPIO Breakout. When doing this be sure that the cable is oriented correctly. The end that goes into the Breakout has a port that will not allow you to incorrectly install it. However the PI does not. If done correctly the ribbon cable should not cross over the top of the pi. A multimeter is recommended.

	Notes: A breakout and breadboard is not required for this setup, If desired one could buy some female to male connectors and tie the servo directly to the GPIO.

	Step 7: Install wireless dongle into PI.

	Step 8: Power on Pi with desired method.

	Step 9: Toggle switch on the battery pack. If done correctly the servo should visibly and audibly twitch. If the servo does nothing then some part of the power or grounds are installed incorrectly ( Red and black wires ).

	Step 10: Wait for solid blue light on dongle to indicate that wireless is connected and begin sending commands to the pi from the mobile device.

	Step 11: Pray the network holds up.


Network issues:
	When testing at home i have had no issues with the connection to the pi. I do not know if the wireless dongle is too weak or if there is an issue somewhere in the ISU network.






Connection Methods From client to Server:
	The way the pi is currently set up it receives commands in plain text with a comma delimiter for main command and params and a semicolon delimiter for different parameters.

The different commands are as follows

	lock - Lock the device if user has permissions example input "lock,James;Pass"
		On success returns "Locked" to the device
		On Failure (User has no permissions)  return "Failed" to the device

	unlock - Unlock the device if user has permissions example input "unlock,James;Pass"
		On success returns "Unlocked" to the device
		On Failure (User has no permissions)  return "Failed" to the device

	users - If user is owner relay all of the users with permissions to open the lock example input "users,device;;James;Pass"

		Note that the device name is passed in, This can most likely be removed but allowed me to track which user made which request on the client since multiple client devices may have the same username and password.
		Example Output
		device is the same device as above.
		"device,User;Pass,User2;Pass"
		source of info is users.conf

	requests - exactly the same as users comand but input is "requests,device;;James;Pass" and sources from requests.conf


	accept - A command from the owner to accept a request for access to the device.
		Input: "accept,James:Pass"
		The username;password string will be removed from the requests.conf file and placed in users.conf. Thus allowing access.
		Returns "OK" to device

	deny - Deny a request, similar to accept
		Input: "deny,James:Pass"
		The username;password string will be removed from the requests.conf file

	request - a request from a user to get access to the machine
		Input: "request,User;Pass"
		Output "OK"
		The User;Pass string will be placed in the request.conf file.

	remove - Remove rights from the a user to access the device
		Input "remove,User;Pass"
		Output "OK"
		the user will be removed from the users.conf file.


	All of these commands are sent to the PI by opening a socket on port 8244
	They are sent in plain text. Had i known that the code i wrote for this would be passed onto other people I probably would have implemented json as the medium of communication. I'm not really proud of the way it is currently but this is how it turned out.

	The old system used a different port (8233) and will not hear any commands from the new one. even if both servers are running on the same PI. 

Requests and Users
	These items are stored in files requests.conf and users.conf. Usernames and passwords are delimited by a semicolon and different enteries are delimited by a comma. More delimiters I'm not proud of.


How the Pi Works the Servo:
	The servo code is a python script called run.py. The java simply calls this script with the proper inputs to turn the servo. To access the GPIO the command needs sudo power. This probably isn't best and is a security risk.
