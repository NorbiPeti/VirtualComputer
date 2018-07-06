# The Computer
A working modern computer in Minecraft made using VirtualBox and Java. No clientside mods required.

I started working on this project on 2015. december 5. However I did not write much code in that time as I had to figure out how things work.
I plan on making a series where I show the whole process of creating this project.

## Video

[![Original video](https://img.youtube.com/vi/VxSyDfxPd3s/0.jpg)](https://www.youtube.com/watch?v=VxSyDfxPd3s)

## Installation requirements
*Note: only the server host has to meet these requirements.*

### Tested versions:
*If you don't meet these requirements, you may need to compile the files for yourself. If you do please send the compiled version to me so more people can download it.*

* OS X; Ubuntu 16.04/18.04
* VirtualBox 5.2
* Spgiot/Bukkit 1.8/1.9/1.12
* Java 8

### Untested:
* Any Linux versions
* Other VirtualBox versions (code modifications are probably necessary)
* Other Spigot/Bukkit versions
* Other Java versions

*Due to VirtualBox Java binding support limitations the plugin does not support Windows currently.*

## Installation
**Warning:** The first loaded world on the server (the one specified in server.properties) will temporarily **lose** the first 20 maps, although it will be back to normal if the server is loaded without the plugin.

* Install VirtualBox
* Add a virtual PC if you don't have any (the first one will be started)
    * Install an OS and Guest Additions, if you haven't already
* Put the jar file into <server>/plugins directory
* Start your server

## Usage
*Please give appropriate credit and link to this page if you use this machine in one of your videos/creations/etc.*

### Display
*Note:* You can create as many displays as you want, but all of them will show the same currently.

To create a display, make a 5 wide 4 high wall of item frames, then put the maps 0-19 on them starting from the top left and going downwards. You can give the maps with this command:

    /give <player> filled_map 1 0

Where you'll need to increment 0 for each map.

It is also recommended to start the virtual machine first so you can distinguish the different parts of the display while placing it.

#### Keyboard
You can either open the chat keyboard using /computer input key, or you can use /computer key \<keyname\>. You can find key names [here](https://github.com/NorbiPeti/VirtualComputer/blob/directvb/VirtualComputer/src/sznp/virtualcomputer/Scancode.java).

#### Mouse
You can "lock" the physical mouse to the virtual one with /computer input mouse, or you can move the mouse (including scrolling) with /computer mouse \<dx\> \<dy\> \<dz\> \<dw\>, where dx specifies the amount of pixels to move right, and dy specifies the amount of pixels to move down, while dz and dw specify the scrolling vertically (positive values scroll down) and horizontally.

To set the locked mouse speed, use /computer input mspeed \<integer\>. The difference in look position will be multiplied with this number. Recommended values are between 1 and 10 (inclusive). Default: 1.

You can press a mouse button with /computer mouse <buttonname>. For example: /computer mouse leftbutton. You can find all the button names [here](https://github.com/NorbiPeti/VirtualComputer/blob/master/VirtualComputerSender/MouseBS.cs).

#### Sounds
Due to the way it works, it automatically plays every sound from the virtual machine **on the host computer**. This is built into the VM, it seems. In order to make the sounds play for every connected player, we'd need a clientside mod.

### Special thanks:
* The creators of VirtualBox for making it open-source and kind of easy to use (though I had minor issues as the documentation doesn't really tell me how to write a new frontend :P and that I needed to learn how machines work - especially keyboards)
* @iiegit for testing and more testing for the non-Windows version
