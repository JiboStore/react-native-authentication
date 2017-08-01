curl 'http://localhost:9000/manga/seeder/getfromto?startingIndex=16&endingIndex=104' > 16_104.txt
git add --all
git commit -am "[seeder] manga: 16 - 104"
git push origin HEAD:16
