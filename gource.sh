#gource --file-idle-time 0 --camera-mode overview --title "Minecraft Coder Pack" -s 2 --hide-filenames --stop-at-end --disable-progress  --output-ppm-stream  - | ffmpeg -y -r 60 -b 6000K -f image2pipe -vcodec ppm -i - -vcodec libtheora  out.ogg
gource --file-idle-time 0 --camera-mode overview --title "Waila" -s 2 --hide-filenames --stop-at-end --disable-progress  --output-ppm-stream  - | ffmpeg -y -f image2pipe -vcodec ppm -i - -vpre libx264-medium -vcodec libx264 out_small.mkv

