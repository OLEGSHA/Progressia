#!/bin/bash

lst="nsis"

user=`whoami`

dpkg -l 2>/dev/null > ls.tmp

for items in $lst
do
  cmd=$(grep "\ $items\ " ls.tmp)
  if [ $? == 0 ]
    then
      echo "$items installed!"
      echo "Building..."
    else
      echo "Package $items not found! Please install $items."
      rm ls.tmp
      exit 1
  fi
done
rm ls.tmp

cd build_packages/DEB/progressia-0.1_all/
mkdir -p home/$user/Progressia

cd ../../..

cp -r build/libs/lib build_packages/DEB/progressia-0.1_all/home/$user/Progressia/
cp build/libs/Progressia.jar build_packages/DEB/progressia-0.1_all/home/$user/Progressia/
cp -r build/libs/lib build_packages/NSIS
cp build/libs/Progressia.jar build_packages/NSIS

makensis build_packages/NSIS/ProgressiaInstaller.nsi
mv build_packages/NSIS/ProgressiaInstaller.exe build_packages/Progressia.exe
fakeroot dpkg-deb --build build_packages/DEB/progressia-0.1_all
mv build_packages/DEB/progressia-0.1_all.deb build_packages/progressia-0.1_all.deb
echo "Build done!"
