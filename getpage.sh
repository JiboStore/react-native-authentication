curl 'http://localhost:9000/manga/seeder/imagefromto?startingIndex=0&endingIndex=100' > img_0_100.txt
git add --all
git commit -am "[seeder] img: 0 - 100"
git push origin HEAD:IMG_0
