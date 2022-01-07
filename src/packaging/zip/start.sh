#!/bin/sh

jvmFlags=""

case "$OSTYPE" in
	"darwin"*)
		# On MacOS, use -XstartOnFirstThread to resolve an issue with OpenGL contexts
		jvmFlags="$jvmFlags -XstartOnFirstThread"
esac

java $jvmFlags -jar "@mainJarFile@"
