#!/bin/bash

for f in *.mp4; do echo "file '$f'"; done > videos.txt

/d/tommy/github-repo/yudady/practise-workspace/40-video/bilibiliVideo/src/main/resources/ffmpeg.exe -f concat -safe 0 -i videos.txt -c copy output.mp4