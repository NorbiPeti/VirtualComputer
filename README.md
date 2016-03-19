# The Computer
A working modern computer in Minecraft made using VirtualBox, jni4net, C# and Java. No clientside mods required.

I started working on this project on 2015. december 5. However I did not write much code in that time as I had to figure out how things work.
I plan on making a series where I show the whole process of creating this project.

[![Original video](https://img.youtube.com/vi/VxSyDfxPd3s/0.jpg)](https://www.youtube.com/watch?v=VxSyDfxPd3s)

## Installation requirements
*Note: only the server host has to meet these requirements.*

### Currently compiled for (and tested on):
*If you don't meet these requirements, you will need to compile the files for yourself. Please help the community by sending the compiled version to me so more people can download it.*

* Windows 7 64-bit
* VirtualBox 5
* Spgiot/Bukkit 1.8/1.9 with Movecraft installed
* Microsoft .NET Framework 4.5
* Java 8

### Possibly works with (untested):
* Any version of Windows which supports .NET 4.5
* Other VirtualBox versions (code modifications are probably necessary)
* Other Spigot/Bukkit versions
* Microsoft .NET Framework 4 (code modifications *are* necessary)
* Other Java versions

*Due to jni4net support limitations the plugin does not support other operating systems.*

*I was too lazy to make Movecraft a soft-dependency, so currently you will need to install Movecraft for it to work.*

## Installation
**Warning:** The first loaded world on the server (the one specified in server.properties) will **lose** the first 20 maps, though it might be only a temporary effect, it might restore after the server starts up without the plugin. *(Untested.)*

* Install all the requirements
* Put the jar file into <server>/plugins directory
* Put the dll file into <server>/plugins/VirtualComputer directory - if it doesn't exist, create it
* Make sure your server is set to a world that you don't mind your maps deleted from
* Start your server

## Usage
*Please give appropriate credit and link to this page if you use this machine in one of your videos/creations/etc. You can also send me your video and I may put it here if I like it.*

### Display
*Note:* You can create as many displays as you want, but all of them will show the same.

To create a display, make a 5 wide 4 high wall of item frames, then put the maps 0-19 on them starting from the top left and going downwards. You can give the maps with this command:

    /give <player> filled_map 1 0

Where you'll need to increment 0 for each map.

It is also recommended to start the virtual machine first so you can distinguish the different parts of the display while placing it.

#### Keyboard
You can either open the chat keyboard using /computer input key, or you can use /computer key \<keyname\>. You can find key names [here](https://github.com/NorbiPeti/VirtualComputer/blob/master/VirtualComputerSender/VirtualKeys.cs). Note that not all keys are currently supported.

*I'd highly appreciate if someone could make a portable keyboard for it. :P*

#### Mouse
You can "lock" the physical mouse to the virtual one with /computer input mouse, or you can move the mouse (including scrolling) with /computer mouse \<dx\> \<dy\> \<dz\> \<dw\>, where dx specifies the amount of pixels to move right, and dy specifies the amount of pixels to move down, while dz and dw specify the scrolling vertically (positive values scroll down) and horizontally. You can also use the "mouse" Movecraft craft type to move the mouse, though it's a bit unreliable.You can press a mouse button with /computer mouse <buttonname>. For example: /computer mouse leftbutton. You can find all the button names [here](https://github.com/NorbiPeti/VirtualComputer/blob/master/VirtualComputerSender/MouseBS.cs).

#### Sounds
Due to the way it works, it automatically plays every sound from the virtual machine **on the host computer**. This is built into VBoxHeadless, it seems. In order to make the sounds play for every connected player, we'd need a clientside mod.

### Special thanks:
* The creators of VirtualBox for making it open-source and easy to use (though I had minor issues but those was because .NET had a few marshaling errors with booleans and that I needed to learn how machines work - especially keyboards)
* The creators of jni4net, after I figured it out, it was quite easy to use
* Minecraft player iie for testing and suggesting the Movecraft mouse and other ideas not related to this project
