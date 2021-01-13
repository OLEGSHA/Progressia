#!/bin/bash

#
# Progressia
# Copyright (C)  2020-2021  Wind Corporation and contributors
# 
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.
#

echoerr() { echo "$@" 1>&2; }

buildDebianPackage() {

    # Commands that must be available to execute this action
    requiredCommands='dpkg-deb fakeroot'
    
    # Package name. Sync with control file manually!
    name='progressia-techdemo'
    # Version that the package will receive. Sync with control file manually!
    version='1.0_all'
    
    # This directory will be copied into $tmpDir
    templateDirectory="build_packages/DEB/template"
    
    # Files that must be present
    requiredFiles="$templateDirectory/DEBIAN/control"
    
    nameAndVersion="$name-$version"
    tmpDir="build_packages/DEB/$nameAndVersion"
    outputFile="build_packages/DEB/$nameAndVersion.deb"

    echo "Checking environment to build Debian package"
    
    for item in $requiredCommands; do
        if command -v "$item" &> /dev/null; then
            echo "- $item found"
        else
            echoerr "Command $item not found, cannot package"
            exit 100
        fi
    done
    
    for file in $requiredFiles; do
        if ! [ -r "$file" ]; then
            echoerr "$file is missing or not readable, cannot package"
            exit 101
        else
            echo "- $file is present and readable"
        fi
    done
    
    echo "Environment OK; packaging Debian package"
    exitCode=0
    
    {
        shareDir="$tmpDir/usr/share/progressia"
        
        mkdir -p "$tmpDir"                                           &&
        mkdir -p "$shareDir"                                         &&
        cp -r "$templateDirectory"/*      "$tmpDir"                  &&
        cp -r 'build/libs/lib'            "$shareDir/lib"            &&
        cp    'build/libs/Progressia.jar' "$shareDir/Progressia.jar" &&
        echo "------ DPKG-DEB ------"                                &&
        fakeroot dpkg-deb --build "$tmpDir"                          &&
        echo "---- DPKG-DEB END ----"                                &&
        mv "$outputFile" build_packages
    } || {
        echoerr "Could not create Debian package"
        exitCode=1
    }
    
    {
        if [ -d "$tmpDir" ]; then
            rm -r "$tmpDir"
        fi
        echo "Cleaned up"
    } || {
        echoerr "Could not clean up after packaging Debian package"
        exitCode=2
    }
    
    exit "$exitCode"
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
