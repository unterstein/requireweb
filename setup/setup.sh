#!/bin/bash
rm -rf bin
mkdir bin

# download
cd bin
http://downloads.typesafe.com/play/2.2.1/play-2.2.1.zip


# install play
unzip play-2.2.1.zip
rm play-2.2.1.zip

# update .bashrc
echo "export PATH=\$PATH:/$PWD/play-2.2.1" >> ~/.bashrc
