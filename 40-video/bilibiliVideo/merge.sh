#!/bin/bash

[ -e list.txt ] && rm list.txt
for f in *.mp4
do
   echo "file $f" >> list.txt
done

ffmpeg.exe -f concat -i list.txt -c copy joined-out.mp4 && rm list.txt