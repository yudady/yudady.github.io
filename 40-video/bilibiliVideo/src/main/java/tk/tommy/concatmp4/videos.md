# concat mp4
## 1
```shell
for f in *.mp4; do echo "file '$f'"; done > videos.txt
```


```videos.txt
file '000817.mp4'
file '224806.mp4'
file '130048.mp4'
file '002501.mp4'
```


```bash
# any folder 
# has videos.txt 

/d/tommy/github-repo/yudady/practise-workspace/40-video/bilibiliVideo/src/main/resources/ffmpeg.exe -f concat -safe 0 -i videos.txt -c copy output.mp4
```

