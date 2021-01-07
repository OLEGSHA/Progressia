#!/bin/bash

echoerr() { echo "$@" 1>&2; }

buildDebianPackage() {

    # Commands that must be available to execute this action
    requiredCommands='dpkg-deb fakeroot'
    
    # Version that the package will receive
    version='0.1_all'
    
    directory="build_packages/DEB/progressia-$version"
    
    # .deb control file that must be present
    configurationFile="$directory/DEBIAN/control"
    
    outputFile="build_packages/DEB/progressia-$version.deb"

    echo "Checking environment to build Debian package"
    
    for item in $requiredCommands; do
        if command -v "$item" &> /dev/null; then
            echo "- $item found"
        else
            echoerr "Command $item not found, cannot package"
            exit 100
        fi
    done
    
    if ! [ -r "$configurationFile" ]; then
        echoerr "$configurationFile is missing or not readable, cannot package"
        exit 101
    else
        echo "- $configurationFile is present and readable"
    fi
    
    echo "Environment OK; packaging Debian package"
    exitCode=0
    
    {
        user=`whoami`
        homeDir="$directory/home/$user/Progressia/"
        
        mkdir -p "$homeDir"                                         &&
        cp -r 'build/libs/lib'            "$homeDir/lib"            &&
        cp    'build/libs/Progressia.jar' "$homeDir/Progressia.jar" &&
        echo "------ DPKG-DEB ------"                               &&
        fakeroot dpkg-deb --build "$directory"                      &&
        echo "---- DPKG-DEB END ----"                               &&
        mv "$outputFile" build_packages
    } || {
        echoerr "Could not create Debian package"
        exitCode=1
    }
    
    {
        if [ -d "$homeDir" ]; then
            rm -r "$homeDir"
        fi
        echo "Cleaned up"
    } || {
        echoerr "Could not clean up after packaging Debian package"
        exitCode=2
    }
}

buildWindowsInstaller() {

    # Commands that must be available to execute this action
    requiredCommands='makensis'
    
    # NSIS configuration file that must be present
    configurationFile='build_packages/NSIS/ProgressiaInstaller.nsi'
    
    # File that will be output
    outputFile='build_packages/NSIS/ProgressiaInstaller.exe'

    echo "Checking environment to build Windows installer"
    
    for item in $requiredCommands; do
        if command -v "$item" &> /dev/null; then
            echo "- $item found"
        else
            echoerr "Command $item not found, cannot build"
            exit 100
        fi
    done
    
    if ! [ -r "$configurationFile" ]; then
        echoerr "$configurationFile is missing or not readable, cannot build"
        exit 101
    else
        echo "- $configurationFile is present and readable"
    fi
    
    echo "Environment OK; building Windows installer"
    exitCode=0
    
    {
        cp -r 'build/libs/lib'            'build_packages/NSIS/lib'            &&
        cp    'build/libs/Progressia.jar' 'build_packages/NSIS/Progressia.jar' &&
        echo "------ NSIS ------"                                              &&
        makensis "$configurationFile"                                          &&
        echo "---- NSIS END ----"                                              &&
        mv "$outputFile" build_packages
    } || {
        echoerr "Could not build Windows installer"
        exitCode=1
    }
    
    {
        if [ -d 'build_packages/NSIS/lib' ]; then
            rm -r 'build_packages/NSIS/lib'
        fi
        if [ -e 'build_packages/NSIS/Progressia.jar' ]; then
            rm 'build_packages/NSIS/Progressia.jar'
        fi
        echo "Cleaned up"
    } || {
        echoerr "Could not clean up after building Windows installer"
        exitCode=2
    }
    
    exit "$exitCode"
}

printUsage() {
    echoerr "Usage: $0 TARGET"
    echoerr "    where TARGET is 'debian' or 'windows'"
}

if [ -n "$2" ]; then
    echoerr "Too many arguments."
    printUsage
    exit 202
fi

case "$1" in
"debian")
    buildDebianPackage
    ;;
"windows")
    buildWindowsInstaller
    ;;
"")
    echoerr "No action specified"
    printUsage
    exit 200
    ;;
"--help" | "-help" | "help" | "?")
    printUsage
    ;;
*)
    echoerr "Unknown action '$1'"
    printUsage
    exit 201
    ;;
esac
