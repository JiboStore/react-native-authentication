curl 'http://localhost:9000/manga/seeder/getfromto?startingIndex=12&endingIndex=104' > 12_104.txt
git add --all
git commit -am "[seeder] manga: 12 - 104"
git push origin HEAD:12
