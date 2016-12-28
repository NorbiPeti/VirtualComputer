Compile the Visual Studio project (VirtualComputerSender), put the DLL into the Java project folder (VirtualComputer). Install Movecraft in Maven using

    install:install-file -Dfile="<path-to-Movecraft.jar>" -DgroupId=net.countercraft -DartifactId=Movecraft -Dversion=3.0.0 -Dpackaging=jar -DgeneratePom=true

 Compile the project using Maven. The compiled JAR should have everything included.
