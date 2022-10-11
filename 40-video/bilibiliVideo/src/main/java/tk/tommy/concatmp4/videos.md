# concat mp4

```videos.txt
file 'd:/Downloads/1/g/cat/000817.mp4'
file 'd:/Downloads/1/g/cat/224806.mp4'
file 'd:/Downloads/1/g/cat/130048.mp4'
file 'd:/Downloads/1/g/cat/002501.mp4'
```


```bash
# any folder 
# has videos.txt 

/d/tommy/github-repo/yudady/practise-workspace/40-video/bilibiliVideo/src/main/resources/ffmpeg.exe -f concat -safe 0 -i videos.txt -c copy output.mp4
```


