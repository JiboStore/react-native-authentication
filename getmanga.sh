curl 'http://localhost:9000/manga/seeder/getfromto?startingIndex=3001&endingIndex=4000' > 3001_4000.txt
git add --all
git commit -am "[seeder] manga: 3001 - 4000"
git push origin HEAD:3001
